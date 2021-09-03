package net.certiv.common.graph;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;

import org.apache.commons.text.TextStringBuilder;

import net.certiv.common.dot.DotStyle;
import net.certiv.common.graph.Edge.Sense;
import net.certiv.common.graph.Walker.NodeVisitor;
import net.certiv.common.stores.HashList;
import net.certiv.common.stores.Pair;
import net.certiv.common.util.Strings;

public class Printer<N extends Node<N, E>, E extends Edge<N, E>> {

	// literal indent spacing used for pretty-printing
	private static final String DENT = "  ";
	// fixer for literal dot names
	private static final Pattern FX = Pattern.compile("[. -]");

	private static final String GRAPH_BEG = "digraph %s {";
	private static final String GRAPH_END = "}";
	private static final String SUBGRAPH_BEG = "%ssubgraph cluster_%s {";
	private static final String SUBGRAPH_END = "%s}";
	private static final String NODE = "%s%s";
	private static final String EDGE = "%s%s -> %s%s";
	private static final String LABEL = "%slabel=\"%s\"";

	public Printer() {}

	/** Pretty print out a whole graph. */
	public String dump(final Graph<N, E> graph) {
		TextStringBuilder sb = new TextStringBuilder();
		for (N root : graph.roots) {
			sb.appendln(String.format("// ---- %s:%s ----", graph.name(), root.name()));
			sb.appendln(dump(root));
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
		public boolean enter(Sense dir, HashList<N, N> visited, N parent, N node) {
			if (parent == null) {
				// starting a new flow
				String str = String.format("%s ", node.name());
				sb.append(str);

				levels.put(node, Pair.of(0, node.name().length() + 1));

			} else if (parent == last) {
				// continuing a subflow
				Set<E> edges = parent.to(node);
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

	public String toDot(final Graph<N, E> graph) {
		TextStringBuilder sb = new TextStringBuilder();
		sb.appendln(GRAPH_BEG, fx(graph.name()));
		sb.appendln(graphProperties(graph));

		Set<N> roots = graph.roots;
		if (roots.size() == 1) {
			sb.append(toDot(graph, roots.iterator().next(), dent(1)));

		} else {
			for (N root : roots) {
				sb.appendln(SUBGRAPH_BEG, dent(1), fx(root.name()));
				sb.append(toDot(graph, root, dent(2)));
				sb.appendln(SUBGRAPH_END, dent(1));
				sb.appendNewLine();
			}
		}

		sb.appendln(GRAPH_END);
		return sb.toString();
	}

	private String graphProperties(Graph<N, E> graph) {
		if (graph.hasProperty(DotStyle.Property)) {
			DotStyle ds = (DotStyle) graph.getProperty(DotStyle.Property);
			return ds.graphAttributes(dent(1));
		}

		TextStringBuilder sb = new TextStringBuilder();
		sb.appendln(LABEL, dent(1), graph.name());
		return sb.toString();
	}

	private String toDot(final Graph<N, E> graph, final N root, final String dent) {
		Map<N, String> nodes = new LinkedHashMap<>();
		TextStringBuilder edges = new TextStringBuilder();

		Walker<N, E> walker = new Walker<>();
		walker.descend(new Dotter(graph, dent, nodes, edges), root);

		TextStringBuilder dot = new TextStringBuilder();
		nodes.values().forEach(node -> dot.appendln(dent + node));
		dot.appendNewLine();
		dot.appendln(edges);
		return dot.toString();
	}

	private class Dotter extends NodeVisitor<N> {

		private Graph<N, E> graph;
		private Map<N, String> nodes;
		private TextStringBuilder edges;
		private String dent;

		public Dotter(Graph<N, E> graph, String dent, Map<N, String> nodes, TextStringBuilder edges) {
			this.graph = graph;
			this.dent = dent;
			this.nodes = nodes;
			this.edges = edges;
		}

		@Override
		public boolean enter(Sense dir, HashList<N, N> visited, N parent, N node) {
			nodes.put(node, style(node));
			if (parent != null) {
				for (E edge : graph.findEdges(parent, node)) {
					edges.appendln(EDGE, dent, fx(parent.name()), fx(node.name()), style(edge));
				}
			}
			return true;
		}

		private String style(N node) {
			if (!node.hasProperty(DotStyle.Property)) return fx(node.name());
			DotStyle ds = (DotStyle) node.getProperty(DotStyle.Property);
			return String.format(NODE, fx(node.name()), ds.nodeAttributes());
		}

		private String style(E edge) {
			if (!edge.hasProperty(DotStyle.Property)) return Strings.EMPTY;
			DotStyle ds = (DotStyle) edge.getProperty(DotStyle.Property);
			return ds.edgeAttributes();
		}
	}

	// fix to a 'dot' compliant name
	private String fx(String name) {
		return FX.matcher(name).replaceAll(Strings.LOWDASH);
	}

	private String dent(int cnt) {
		return Strings.dup(cnt, DENT);
	}
}
