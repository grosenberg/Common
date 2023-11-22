package net.certiv.common.graph.demo;

import java.util.EnumSet;

import net.certiv.common.graph.GraphEvent.GraphEvtType;
import net.certiv.common.graph.GraphListener;

public class DemoListener extends GraphListener<DemoNode, DemoEdge> {

	public static DemoListener of(GraphEvtType... types) {
		DemoListener listener = new DemoListener();
		listener.register(types);
		return listener;
	}

	public static DemoListener of(EnumSet<GraphEvtType> types) {
		DemoListener listener = new DemoListener();
		listener.register(types);
		return listener;
	}
}
