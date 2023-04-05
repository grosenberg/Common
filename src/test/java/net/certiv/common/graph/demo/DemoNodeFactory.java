package net.certiv.common.graph.demo;

import java.util.Map;

import net.certiv.common.graph.Edge.Sense;
import net.certiv.common.graph.NodeFactory;

public class DemoNodeFactory extends NodeFactory<DemoGraph, DemoNode, DemoEdge> {

	public DemoNodeFactory(DemoGraph graph) {
		super(graph);
	}

	@Override
	public DemoNode createNode() {
		return new DemoNode(graph, new DemoEdgeSet(Sense.IN), new DemoEdgeSet(Sense.OUT));
	}

	@Override
	public DemoNode createNode(String name) {
		return new DemoNode(graph, new DemoEdgeSet(Sense.IN), new DemoEdgeSet(Sense.OUT), name);
	}

	@Override
	public DemoNode createNode(Map<Object, Object> props) {
		return new DemoNode(graph, new DemoEdgeSet(Sense.IN), new DemoEdgeSet(Sense.OUT), props);
	}
}
