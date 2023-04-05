package net.certiv.common.graph.demo;

import net.certiv.common.graph.Edge.Sense;
import net.certiv.common.graph.EdgeSet;

public class DemoEdgeSet extends EdgeSet<DemoNode, DemoEdge> {

	public DemoEdgeSet(Sense dir) {
		super(dir);
	}
}
