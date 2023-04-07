package net.certiv.common.graph.demo;

import java.util.Map;

import net.certiv.common.graph.Builder;
import net.certiv.common.graph.Edge.Sense;
import net.certiv.common.graph.EdgeSet;

public class DemoBuilder extends Builder<DemoGraph, DemoNode, DemoEdge> {

	public DemoBuilder(DemoGraph graph) {
		super(graph);
	}

	@Override
	protected DemoNode createNode() {
		return new DemoNode(graph, new EdgeSet<DemoNode, DemoEdge>(Sense.IN),
				new EdgeSet<DemoNode, DemoEdge>(Sense.OUT));
	}

	@Override
	protected DemoNode createNode(String name) {
		return new DemoNode(graph, new EdgeSet<DemoNode, DemoEdge>(Sense.IN),
				new EdgeSet<DemoNode, DemoEdge>(Sense.OUT), name);
	}

	@Override
	protected DemoNode createNode(Map<Object, Object> props) {
		return new DemoNode(graph, new EdgeSet<DemoNode, DemoEdge>(Sense.IN),
				new EdgeSet<DemoNode, DemoEdge>(Sense.OUT), props);
	}
}
