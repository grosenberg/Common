package net.certiv.common.graph.demo;

import net.certiv.common.graph.Builder;
import net.certiv.common.graph.Edge.Sense;
import net.certiv.common.graph.EdgeSet;

public class DemoBuilder extends Builder<String, DemoGraph, DemoNode, DemoEdge> {

	public DemoBuilder(DemoGraph graph) {
		super(graph);
	}

	@Override
	protected DemoNode createNode(String name) {
		return new DemoNode(graph, new EdgeSet<DemoNode, DemoEdge>(Sense.IN),
				new EdgeSet<DemoNode, DemoEdge>(Sense.OUT), name);
	}

	@Override
	protected String makeId(String name) {
		return name;
	}

	@Override
	protected String nameOf(String id) {
		return id;
	}

}
