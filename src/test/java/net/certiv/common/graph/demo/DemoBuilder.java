package net.certiv.common.graph.demo;

import java.util.regex.Pattern;
import java.util.stream.Collectors;

import net.certiv.common.graph.Node;
import net.certiv.common.stores.UniqueList;
import net.certiv.common.util.Strings;

public class DemoBuilder {

	private static final Pattern MARK = Pattern.compile("\\h*=>|->\\h*");
	private static final Pattern SEPR = Pattern.compile("\\h*,\\h*");

	private final DemoGraph graph;
	private final UniqueList<DemoNode> memo = new UniqueList<>();
	private final UniqueList<DemoEdge> edges = new UniqueList<>();

	public DemoBuilder(DemoGraph graph) {
		this.graph = graph;
	}

	/**
	 * builder.createAndAddEdges("[AB] => C => [Delta,Eta]");
	 *
	 * @param spec
	 * @return
	 */
	public DemoBuilder createAndAddEdges(String spec) {
		return createEdges(spec).addEdges();
	}

	public DemoBuilder createEdges(String spec) {
		parse(spec);
		return this;
	}

	public UniqueList<DemoNode> findNodes(String spec) {
		return parseNodes(spec, false).unmodifiable();
	}

	public UniqueList<DemoNode> findOrCreateNodes(String spec) {
		return parseNodes(spec, true).unmodifiable();
	}

	public UniqueList<DemoEdge> getEdges() {
		return edges;
	}

	public void setEdges(UniqueList<DemoEdge> edges) {
		this.edges.addAll(edges);
	}

	public DemoBuilder addEdges() {
		edges.forEach(e -> graph.addEdge(e));
		return this;
	}

	private void parse(String spec) {
		spec = spec.trim();

		if (MARK.matcher(spec).find()) {
			String[] parts = MARK.split(spec);
			if (parts.length > 1) {
				for (int idx = 0; idx < parts.length - 1; idx++) {
					UniqueList<DemoNode> src = parseNodes(parts[idx], true);
					UniqueList<DemoNode> dst = parseNodes(parts[idx + 1], true);

					for (DemoNode beg : src) {
						for (DemoNode end : dst) {
							edges.add(graph.createEdge(beg, end));
						}
					}
				}
			}
		}
	}

	private UniqueList<DemoNode> parseNodes(String spec, boolean create) {
		UniqueList<DemoNode> nodes = new UniqueList<>();

		spec = Strings.deQuote(spec.trim());
		String[] name = SEPR.split(spec);
		for (int idx = 0; idx < name.length; idx++) {
			String txt = name[idx];
			if (create) {
				nodes.add(findOrCreateNode(txt));
			} else {
				DemoNode node = findNode(txt);
				if (node != null) nodes.add(node);
			}
		}
		return nodes;
	}

	private DemoNode findNode(String txt) {
		DemoNode node = memo.stream() //
				.filter(n -> n.get(Node.NODE_NAME).equals(txt)) //
				.collect(Collectors.toCollection(UniqueList::new)).peek();
		if (node == null) node = graph.getNode(txt);
		return node;

	}

	private DemoNode findOrCreateNode(String txt) {
		DemoNode node = memo.stream() //
				.filter(n -> n.get(Node.NODE_NAME).equals(txt)) //
				.collect(Collectors.toCollection(UniqueList::new)).peek();
		if (node == null) node = graph.findOrCreateNode(txt);
		memo.add(node);
		return node;

	}

	public void clear() {
		memo.clear();
		edges.clear();
	}
}
