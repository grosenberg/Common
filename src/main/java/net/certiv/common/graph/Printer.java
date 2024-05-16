package net.certiv.common.graph;

import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import org.apache.commons.text.TextStringBuilder;

import net.certiv.common.dot.Dictionary.ON;
import net.certiv.common.dot.DotStyle;
import net.certiv.common.graph.Edge.Sense;
import net.certiv.common.graph.Walker.NodeVisitor;
import net.certiv.common.graph.id.IUId;
import net.certiv.common.stores.LinkedHashList;
import net.certiv.common.stores.Pair;
import net.certiv.common.stores.UniqueList;
import net.certiv.common.util.Strings;

/**
 * Graph pretty-printer. Supports both fully stylized Dot-syntax rendering and simple
 * text-tree dump.
 */
public class Printer<I extends IUId, N extends Node<I, N, E>, E extends Edge<I, N, E>> {

	// simple DOT name
	private static final Pattern SIMPLE = Pattern.compile("\\w+");

	// literal indent spacing used for pretty-printing
	private static final String DENT = "  ";

	private static final String GRAPH_BEG = "digraph %s {";
	private static final String GRAPH_END = "}";
	private static final String SUBGRAPH_BEG = "%ssubgraph %s {";
	private static final String SUBGRAPH_NAME = "cluster_%s";
	private static final String SUBGRAPH_END = "%s}";
	private static final String NODE = "%s%s";
	private static final String EDGE = "%s%s -> %s%s";

	private boolean debug;

	public Printer() {
		this(false);
	}

	public Printer(boolean debug) {
		this.debug = debug;
	}

	/** Enable/disable debug logging of the graph walk(s). */
	public Printer<I, N, E> debug(boolean enable) {
		this.debug = enable;
		return this;
	}

	/** Pretty print out a whole graph. */
	public String dump(final Graph<I, N, E> graph) {
		return dump(graph, graph.getRoots());
	}

	/** Pretty print out a graph beginning with the given nodes. */
	public String dump(final Graph<I, N, E> graph, UniqueList<N> nodes) {
		Walker<I, N, E> walker = graph.walker().debug(debug);
		TextStringBuilder sb = new TextStringBuilder();
		for (N node : nodes) {
			sb.appendln(String.format("// ---- %s:%s ----", graph.name(), node.name()));
			sb.appendln(dump(walker, node));
			sb.appendNewLine();
		}
		return sb.toString();
	}

	private TextStringBuilder dump(Walker<I, N, E> walker, final N node) {
		TextStringBuilder sb = new TextStringBuilder();
		walker.descend(new Dumper(sb), node);
		return sb;
	}

	private class Dumper extends NodeVisitor<N> {

		private TextStringBuilder sb;

		// key=node; value=node name beg/end column
		private Map<N, Pair<Integer, Integer>> levels = new HashMap<>();
		private N last;

		public Dumper(TextStringBuilder sb) {
			this.sb = sb;
		}

		@Override
		public boolean enter(Sense dir, LinkedHashList<N, N> visited, N parent, N node) {
			if (parent == null) {
				// starting a new flow
				String str = String.format("%s ", node.name());
				sb.append(str);

				levels.put(node, Pair.of(0, node.name().length() + 1));

			} else if (parent == last) {
				// continuing a subflow
				UniqueList<E> edges = parent.to(node);
				int edgeCnt = edges.size();
				String str = String.format("-%s-> %s ", edgeCnt, node.name());
				sb.append(str);

				Pair<Integer, Integer> pos = levels.get(parent);
				int end = pos.right + str.length();
				int beg = end - 1 - node.name().length();

				levels.putIfAbsent(node, Pair.of(beg, end));

			} else {
				// branch the subflow
				Pair<Integer, Integer> pos = levels.get(parent);
				String lead = Strings.dup(pos.left, Strings.SPACE);

				sb.appendNewLine();
				int cnt = parent.to(node).size();
				String str = String.format("%s|--%s-> %s ", lead, cnt, node.name());
				sb.append(str);

				int end = pos.right + str.length();
				int beg = end - 1 - node.name().length();

				levels.putIfAbsent(node, Pair.of(beg, end));
			}

			last = node;
			return true;
		}
	}

	// --------------------------------

	/**
	 * Render a digraph representation of the given graph.
	 *
	 * @param graph the source graph
	 * @return the digraph
	 */
	public String render(final Graph<I, N, E> graph) {
		return render(graph, new DotVisitor<>());
	}

	/**
	 * Render a digraph representation of the given graph using the given node visitor.
	 * Extend {@code DotVisitor} to, <i>e.g.</i>, constrain node selection.
	 *
	 * @param graph   the source graph
	 * @param visitor the graph node visitor
	 * @return a digraph
	 */
	public String render(Graph<I, N, E> graph, DotVisitor<I, N, E> visitor) {
		TextStringBuilder sb = new TextStringBuilder();

		sb.appendln(GRAPH_BEG, fix(graph.name()));
		sb.append(graphProperties(graph, dent(1)));

		Walker<I, N, E> walker = graph.walker().debug(debug);
		UniqueList<N> roots = graph.getRoots().dup();
		Collections.sort(roots);
		switch (roots.size()) {
			case 0:
				break;

			case 1: {
				N root = roots.getFirst();
				sb.append(nodeProperties(graph, root, dent(1)));
				sb.append(edgeProperties(graph, root, dent(1)));
				sb.appendNewLine();
				sb.append(render(walker, visitor, root, dent(2)));
			}
				break;

			default:
				for (N root : roots) {
					if (root.hasEdges(Sense.OUT, true)) {
						sb.appendNewLine();
						sb.appendln(SUBGRAPH_BEG, dent(1), fix(String.format(SUBGRAPH_NAME, root.label())));
						sb.appendln(clusterProperties(graph, root, dent(2)));
						sb.append(render(walker, visitor, root, dent(3)));
						sb.appendln(SUBGRAPH_END, dent(1));
					}
				}
		}

		sb.appendln(GRAPH_END);
		return sb.toString();
	}

	private String render(Walker<I, N, E> walker, DotVisitor<I, N, E> visitor, N beg, String dent) {
		visitor.setup(dent);
		walker.descend(visitor, beg);

		TextStringBuilder sb = new TextStringBuilder();
		visitor.nodes().forEach(node -> sb.appendln(dent + node));
		sb.appendNewLine();
		sb.appendln(visitor.edges());
		return sb.toString();
	}

	private TextStringBuilder graphProperties(Graph<I, N, E> graph, String dent) {
		TextStringBuilder sb = new TextStringBuilder();

		DotStyle ds = graph.getDotStyle();
		sb.append(ds.titledAttributes(ON.GRAPHS, dent));
		return sb;
	}

	private TextStringBuilder clusterProperties(Graph<I, N, E> graph, N root, String dent) {
		TextStringBuilder sb = new TextStringBuilder();

		DotStyle ds = graph.getDotStyle();
		sb.append(ds.titledAttributes(ON.CLUSTERS, dent));
		sb.append(ds.titledAttributes(ON.NODES, dent));
		sb.append(ds.titledAttributes(ON.EDGES, dent));
		return sb;
	}

	private TextStringBuilder nodeProperties(Graph<I, N, E> graph, N root, String dent) {
		TextStringBuilder sb = new TextStringBuilder();
		DotStyle ds = graph.getDotStyle();
		sb.append(ds.titledAttributes(ON.NODES, dent));
		return sb;
	}

	private TextStringBuilder edgeProperties(Graph<I, N, E> graph, N root, String dent) {
		TextStringBuilder sb = new TextStringBuilder();

		DotStyle ds = graph.getDotStyle();
		sb.append(ds.titledAttributes(ON.EDGES, dent));
		return sb;
	}

	public static class DotVisitor<I extends IUId, N extends Node<I, N, E>, E extends Edge<I, N, E>> extends NodeVisitor<N> {

		private TreeSet<N> nodes = new TreeSet<>();
		private TreeSet<E> edges = new TreeSet<>();

		private String dent;

		public DotVisitor() {
			super();
		}

		protected void setup(String dent) {
			this.dent = dent;
			nodes.clear();
			edges.clear();
		}

		public Set<String> nodes() {
			return nodes.stream().map(n -> style(n)).collect(Collectors.toCollection(LinkedHashSet::new));
		}

		public TextStringBuilder edges() {
			TextStringBuilder sb = new TextStringBuilder();
			edges.forEach(e -> sb.appendln(EDGE, dent, fix(e.beg().label()), fix(e.end().label()), style(e)));
			return sb;
		}

		@Override
		public boolean enter(Sense dir, LinkedHashList<N, N> visited, N parent, N node) {
			nodes.add(node);
			if (parent != null) edges.addAll(parent.to(node));
			return true;
		}

		protected String style(N node) {
			if (!node.has(DotStyle.PropName)) return fix(node.label());
			DotStyle ds = (DotStyle) node.get(DotStyle.PropName);
			return String.format(NODE, fix(node.label()), ds.inlineAttributes(ON.NODES));
		}

		protected String style(E edge) {
			if (!edge.has(DotStyle.PropName)) return Strings.EMPTY;
			DotStyle ds = (DotStyle) edge.get(DotStyle.PropName);
			return ds.inlineAttributes(ON.EDGES);
		}
	}

	/**
	 * Sanitize a name to be 'dot' compliant. Quote the given name if the name contains
	 * anything other than {@code [a-zA-Z_0-9]}.
	 */
	private static String fix(String name) {
		name = name.trim();
		if (name.isEmpty()) return Strings.UNKNOWN;
		if (SIMPLE.matcher(name).matches()) return name;
		return Strings.QUOTE + name + Strings.QUOTE;
	}

	private static String dent(int cnt) {
		return Strings.dup(cnt, DENT);
	}
}
