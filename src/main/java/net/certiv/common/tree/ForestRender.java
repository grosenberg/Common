package net.certiv.common.tree;

import java.util.Map.Entry;
import java.util.TreeMap;
import java.util.regex.Pattern;

import org.apache.commons.text.TextStringBuilder;

import net.certiv.common.dot.Dictionary.ON;
import net.certiv.common.dot.DotStyle;
import net.certiv.common.util.Strings;

/**
 * Render a {@link Forest} to various formats, including Dot-syntax.
 */
public class ForestRender {

	// simple DOT name
	private static final Pattern SIMPLE = Pattern.compile("\\w+");

	// literal indent spacing used for pretty-printing
	private static final String DENT = "  ";

	private static final String GRAPH_BEG = "digraph %s {";
	private static final String GRAPH_END = "}";
	private static final String SUBGRAPH_BEG = "%ssubgraph %s {";
	private static final String SUBGRAPH_NAME = "cluster_%s";
	private static final String SUBGRAPH_END = "%s}";
	private static final String NODE = "%s%s%s";
	private static final String EDGE = "%s%s -> %s";

	private ForestRender() {}

	/**
	 * Render a textual representation of the given forest.
	 *
	 * @param forest source forest
	 * @return textual representation
	 */
	public static <T> String dump(final Forest<T> forest) {
		TextStringBuilder sb = new TextStringBuilder();

		sb.appendln(forest.label());

		for (TreeNode<T> root : forest.roots()) {
			forest.dfsStream(root).forEachOrdered(n -> {
				TreeNode<T> p = n.parent();
				if (p == null) { // root
					sb.appendNewLine();
					sb.appendln("Root %s", n.label());
				} else {
					sb.appendln(DENT + "%s => %s", p.label(), n.label());
				}
			});
		}
		return sb.toString();
	}

	/**
	 * Render a Dot-style digraph representation of the given forest.
	 *
	 * @param forest source forest
	 * @return Dot digraph
	 */
	public static <T> String toDot(final Forest<T> forest) {
		TextStringBuilder sb = new TextStringBuilder();

		sb.appendln(GRAPH_BEG, fix(forest.label()));
		sb.append(graphProperties(forest, dent(1)));

		switch (forest.roots().size()) {
			case 0:
				break;

			case 1:
				TreeNode<T> one = forest.roots().first();
				sb.append(nodeProperties(forest, one, dent(1)));
				sb.append(edgeProperties(forest, one, dent(1)));
				sb.appendNewLine();
				sb.append(dot(forest, one, dent(2)));
				break;

			default:
				for (TreeNode<T> root : forest.roots()) {
					sb.appendNewLine();
					sb.appendln(SUBGRAPH_BEG, dent(1), fix(String.format(SUBGRAPH_NAME, root.label())));
					sb.appendln(clusterProperties(forest, root, dent(2)));
					sb.append(dot(forest, root, dent(3)));
					sb.appendln(SUBGRAPH_END, dent(1));
				}
		}

		sb.appendln(GRAPH_END);
		return sb.toString();
	}

	private static <T> TextStringBuilder graphProperties(Forest<T> forest, String dent) {
		TextStringBuilder sb = new TextStringBuilder();

		DotStyle ds = forest.style();
		sb.append(ds.titledAttributes(ON.GRAPHS, dent));
		return sb;
	}

	private static <T> TextStringBuilder clusterProperties(Forest<T> forest, TreeNode<T> root, String dent) {
		TextStringBuilder sb = new TextStringBuilder();

		DotStyle ds = forest.style();
		sb.append(ds.titledAttributes(ON.CLUSTERS, dent));
		sb.append(ds.titledAttributes(ON.NODES, dent));
		sb.append(ds.titledAttributes(ON.EDGES, dent));
		return sb;
	}

	private static <T> TextStringBuilder nodeProperties(Forest<T> forest, TreeNode<T> root, String dent) {
		TextStringBuilder sb = new TextStringBuilder();
		DotStyle ds = forest.style();
		sb.append(ds.titledAttributes(ON.NODES, dent));
		return sb;
	}

	private static <T> TextStringBuilder edgeProperties(Forest<T> forest, TreeNode<T> root, String dent) {
		TextStringBuilder sb = new TextStringBuilder();

		DotStyle ds = forest.style();
		sb.append(ds.titledAttributes(ON.EDGES, dent));
		return sb;
	}

	private static <T> String dot(Forest<T> forest, TreeNode<T> beg, String dent) {
		// prettier by node num ordering: key=node num; value=node
		TreeMap<Long, TreeNode<T>> nodes = new TreeMap<>();
		forest.dfsStream(beg).forEach(n -> nodes.put(n.num, n));

		TextStringBuilder sb = new TextStringBuilder();
		sb.appendln(defineNodes(nodes, dent));
		sb.appendNewLine();
		sb.appendln(defineEdges(nodes, dent));
		return sb.toString();
	}

	private static <T> TextStringBuilder defineNodes(TreeMap<Long, TreeNode<T>> nodes, String dent) {
		TextStringBuilder sb = new TextStringBuilder();

		for (Entry<Long, TreeNode<T>> entry : nodes.entrySet()) {
			TreeNode<T> n = entry.getValue();
			sb.appendln(NODE, dent, fix(n.label()), n.style().inlineAttributes(ON.NODES));
		}
		return sb;
	}

	private static <T> TextStringBuilder defineEdges(TreeMap<Long, TreeNode<T>> nodes, String dent) {
		TextStringBuilder sb = new TextStringBuilder();

		for (Entry<Long, TreeNode<T>> entry : nodes.entrySet()) {
			TreeNode<T> n = entry.getValue();
			TreeNode<T> p = n.parent();
			if (p != null) {
				sb.appendln(EDGE, dent, fix(p.label()), fix(n.label()));
			}
		}
		return sb;
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
