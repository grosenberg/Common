package net.certiv.common.graph.demo;

import net.certiv.common.graph.Edge;
import net.certiv.common.graph.id.Id;

public class DemoEdge extends Edge<Id, DemoNode, DemoEdge> {

	public DemoEdge(DemoNode beg, DemoNode end) {
		super(beg, end);
	}
}
