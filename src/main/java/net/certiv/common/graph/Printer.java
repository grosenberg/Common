package net.certiv.common.graph;

import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;

import org.apache.commons.text.TextStringBuilder;

import net.certiv.common.dot.Dictionary.ON;
import net.certiv.common.dot.DotStyle;
import net.certiv.common.graph.Edge.Sense;
import net.certiv.common.graph.Walker.NodeVisitor;
import net.certiv.common.stores.LinkedHashList;
import net.certiv.common.stores.Pair;
import net.certiv.common.stores.UniqueList;
import net.certiv.common.util.Strings;

public class Printer<N extends Node<N, E>, E extends Edge<N, E>> {

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

	public Printer() {}

	/** Pretty print out a whole graph. */
	public String dump(final Graph<N, E> graph) {
		return dump(graph, graph.getRoots());
	}

	/** Pretty print out a graph beginning with the given nodes. */
	public String dump(final Graph<N, E> graph, UniqueList<N> nodes) {
		TextStringBuilder sb = new TextStringBuilder();
		for (N node : nodes) {
			sb.appendln(String.format("// ---- %s:%s ----", graph.name(), node.name()));
			sb.appendln(dump(node));
			sb.appendNewLine();
		}
		return sb.toString();
	}

	private TextStringBuilder dump(final N node) {
		TextStringBuilder sb = new TextStringBuilder();
		Walker<N, E> walker = new Walker<>();
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
	public String render(final Graph<N, E> graph) {
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
	public String render(Graph<N, E> graph, DotVisitor<N, E> visitor) {
		TextStringBuilder sb = new TextStringBuilder();

		sb.appendln(GRAPH_BEG, fix(graph.name()));
		sb.append(graphProperties(graph, dent(1)));

		Walker<N, E> walker = graph.walker();
		UniqueList<N> roots = graph.getRoots();
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
						sb.appendln(SUBGRAPH_BEG, dent(1), fix(String.format(SUBGRAPH_NAME, root.uname())));
						sb.appendln(clusterProperties(graph, root, dent(2)));
						sb.append(render(walker, visitor, root, dent(3)));
						sb.appendln(SUBGRAPH_END, dent(1));
					}
				}
		}

		sb.appendln(GRAPH_END);
		return sb.toString();
	}

	private String render(Walker<N, E> walker, DotVisitor<N, E> visitor, N beg, String dent) {
		visitor.setup(dent);
		walker.descend(visitor, beg);

		TextStringBuilder sb = new TextStringBuilder();
		visitor.nodes().forEach(node -> sb.appendln(dent + node));
		sb.appendNewLine();
		sb.appendln(visitor.edges());
		return sb.toString();
	}

	private TextStringBuilder graphProperties(Graph<N, E> graph, String dent) {
		TextStringBuilder sb = new TextStringBuilder();

		DotStyle ds = graph.getDotStyle();
		sb.append(ds.titledAttributes(ON.GRAPHS, dent));
		return sb;
	}

	private TextStringBuilder clusterProperties(Graph<N, E> graph, N root, String dent) {
		TextStringBuilder sb = new TextStringBuilder();

		DotStyle ds = graph.getDotStyle();
		sb.append(ds.titledAttributes(ON.CLUSTERS, dent));
		sb.append(ds.titledAttributes(ON.NODES, dent));
		sb.append(ds.titledAttributes(ON.EDGES, dent));
		return sb;
	}

	private TextStringBuilder nodeProperties(Graph<N, E> graph, N root, String dent) {
		TextStringBuilder sb = new TextStringBuilder();

		DotStyle ds = graph.getDotStyle();
		sb.append(ds.titledAttributes(ON.NODES, dent));
		return sb;
	}

	private TextStringBuilder edgeProperties(Graph<N, E> graph, N root, String dent) {
		TextStringBuilder sb = new TextStringBuilder();

		DotStyle ds = graph.getDotStyle();
		sb.append(ds.titledAttributes(ON.EDGES, dent));
		return sb;
	}

	public static class DotVisitor<N extends Node<N, E>, E extends Edge<N, E>> extends NodeVisitor<N> {

		// value=formatted node definition string
		private Set<String> nodes = new LinkedHashSet<>();
		private TextStringBuilder edges = new TextStringBuilder();

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
			return nodes;
		}

		public TextStringBuilder edges() {
			return edges;
		}

		@Override
		public boolean enter(Sense dir, LinkedHashList<N, N> visited, N parent, N node) {
			nodes.add(style(node));
			if (parent != null) {
				for (E edge : parent.to(node)) {
					edges.appendln(EDGE, dent, fix(parent.uname()), fix(node.uname()), style(edge));
				}
			}
			return true;
		}

		protected String style(N node) {
			if (!node.has(DotStyle.PropName)) return fix(node.uname());
			DotStyle ds = (DotStyle) node.get(DotStyle.PropName);
			return String.format(NODE, fix(node.uname()), ds.inlineAttributes(ON.NODES));
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
