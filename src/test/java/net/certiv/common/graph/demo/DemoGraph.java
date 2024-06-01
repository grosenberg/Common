package net.certiv.common.graph.demo;

import net.certiv.common.graph.Edge.Sense;
import net.certiv.common.graph.EdgeSet;
import net.certiv.common.graph.Graph;
import net.certiv.common.graph.Printer;
import net.certiv.common.graph.Walker;
import net.certiv.common.id.Id;

public class DemoGraph extends Graph<Id, DemoNode, DemoEdge> {

	public DemoGraph(Id id) {
		super(id);
	}

	@Override
	public DemoNode createNode(Id id) {
		return new DemoNode(this, id, new EdgeSet<>(Sense.IN), new EdgeSet<>(Sense.OUT));
	}

	@Override
	public DemoEdge createEdge(DemoNode beg, DemoNode end) {
		return new DemoEdge(beg, end);
	}

	@Override
	public Walker<Id, DemoNode, DemoEdge> walker() {
		return new Walker<>();
	}

	public Printer<Id, DemoNode, DemoEdge> printer() {
		return new Printer<>();
	}

	public String dump() {
		return printer().dump(this);
	}

	public String render() {
		return printer().render(this);
	}
}
