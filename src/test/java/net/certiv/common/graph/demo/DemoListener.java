package net.certiv.common.graph.demo;

import java.util.Set;

import net.certiv.common.graph.GraphEvent.GraphEvtType;
import net.certiv.common.graph.GraphListener;
import net.certiv.common.graph.id.Id;

public class DemoListener extends GraphListener<Id, DemoNode, DemoEdge> {

	public static DemoListener of(GraphEvtType... types) {
		DemoListener listener = new DemoListener();
		listener.register(types);
		return listener;
	}

	public static DemoListener of(Set<GraphEvtType> types) {
		DemoListener listener = new DemoListener();
		listener.register(types);
		return listener;
	}
}
