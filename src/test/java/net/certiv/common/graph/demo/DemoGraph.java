package net.certiv.common.graph.demo;

import net.certiv.common.graph.Edge.Sense;
import net.certiv.common.graph.EdgeSet;
import net.certiv.common.graph.Graph;
import net.certiv.common.graph.Printer;
import net.certiv.common.graph.Walker;

public class DemoGraph extends Graph<DemoNode, DemoEdge> {

	public DemoGraph(String name) {
		super(name);
	}

	@Override
	public DemoNode createNode(Object id) {
		return new DemoNode(this, new EdgeSet<>(Sense.IN),
				new EdgeSet<>(Sense.OUT), (String) id);
	}

	@Override
	public DemoEdge createEdge(DemoNode beg, DemoNode end) {
		return new DemoEdge(beg, end);
	}

	@Override
	public Walker<DemoNode, DemoEdge> walker() {
		return new Walker<>();
	}

	public Printer<DemoNode, DemoEdge> printer() {
		return new Printer<>();
	}

	public String dump() {
		return printer().dump(this);
	}

	public String render() {
		return printer().render(this);
	}
}
