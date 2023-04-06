package net.certiv.common.graph.demo;

import java.util.Map;

import net.certiv.common.graph.Edge.Sense;
import net.certiv.common.graph.EdgeSet;
import net.certiv.common.graph.Graph;
import net.certiv.common.graph.Printer;
import net.certiv.common.graph.Walker;

public class DemoGraph extends Graph<DemoNode, DemoEdge> {

	public static final Walker<DemoNode, DemoEdge> WALKER = new Walker<>();
	public static final Printer<DemoNode, DemoEdge> PRINTER = new Printer<>();

	public DemoGraph(String name) {
		super(name);
	}

	@Override
	public DemoNode createNode() {
		return new DemoNode(this, new EdgeSet<DemoNode, DemoEdge>(Sense.IN),
				new EdgeSet<DemoNode, DemoEdge>(Sense.OUT));
	}

	@Override
	public DemoNode createNode(String name) {
		return new DemoNode(this, new EdgeSet<DemoNode, DemoEdge>(Sense.IN),
				new EdgeSet<DemoNode, DemoEdge>(Sense.OUT), name);
	}

	@Override
	public DemoNode createNode(Map<Object, Object> props) {
		return new DemoNode(this, new EdgeSet<DemoNode, DemoEdge>(Sense.IN),
				new EdgeSet<DemoNode, DemoEdge>(Sense.OUT), props);
	}

	@Override
	protected DemoEdge createEdge(DemoNode beg, DemoNode end) {
		return new DemoEdge(beg, end);
	}

	public String dump() {
		return PRINTER.dump(this);
	}

	public String dot() {
		return PRINTER.render(this);
	}
}
