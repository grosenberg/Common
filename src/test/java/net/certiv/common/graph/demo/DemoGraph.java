package net.certiv.common.graph.demo;

import net.certiv.common.graph.Graph;
import net.certiv.common.graph.Printer;
import net.certiv.common.graph.Walker;

public class DemoGraph extends Graph<DemoNode, DemoEdge> {

	public static final Walker<DemoNode, DemoEdge> WALKER = new Walker<>();
	public static final Printer<DemoNode, DemoEdge> PRINTER = new Printer<>();

	public DemoGraph(String name) {
		super(name);
	}

	public DemoEdge createEdge(String parent, String child) {
		return createEdge(createNode(parent), createNode(child));
	}

	public DemoEdge createEdge(DemoNode parent, DemoNode child) {
		DemoEdge edge = new DemoEdge(parent, child);
		addEdge(edge); // add edge, including nodes, to graph
		return edge;
	}

	public DemoNode createNode(String name) {
		DemoNode node = getNode(name);
		if (node != null) return node;

		node = new DemoNode(this, name);
		return node;
	}

	public String dump() {
		return PRINTER.dump(this);
	}

	public String dot() {
		return PRINTER.render(this);
	}
}
