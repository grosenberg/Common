package net.certiv.common.graph.demo;

import net.certiv.common.graph.Graph;
import net.certiv.common.graph.Printer;
import net.certiv.common.graph.Walker;

public class DemoGraph extends Graph<DemoNode, DemoEdge> {

	public static final Walker<DemoNode, DemoEdge> WALKER = new Walker<>();
	public static final Printer<DemoNode, DemoEdge> PRINTER = new Printer<>();

	public DemoGraph(String name) {
		super();
		put(GRAPH_NAME, name);
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
