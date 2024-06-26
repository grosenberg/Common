package net.certiv.common.graph.demo;

import net.certiv.common.graph.GraphEvent;
import net.certiv.common.id.Id;
import net.certiv.common.log.Level;

public class DemoEvent extends GraphEvent<Id, DemoNode, DemoEdge> {

	public static DemoEvent addNode(DemoGraph graph, DemoNode node) {
		return new DemoEvent(graph, GraphEvtType.AddNode, node);
	}

	public static DemoEvent rmvNode(DemoGraph graph, DemoNode node) {
		return new DemoEvent(graph, GraphEvtType.RmvNode, node);
	}

	public static DemoEvent addEdge(DemoGraph graph, DemoEdge edge) {
		return new DemoEvent(graph, GraphEvtType.AddEdge, edge);
	}

	public static DemoEvent rmvEdge(DemoGraph graph, DemoEdge edge) {
		return new DemoEvent(graph, GraphEvtType.RmvEdge, edge);
	}

	public static DemoEvent of(DemoGraph graph, Level level, String msg) {
		return new DemoEvent(graph, GraphEvtType.Log, level, msg);
	}

	public static DemoEvent of(DemoGraph graph, Level level, String msg, StackTraceElement loc, Throwable e) {
		return new DemoEvent(graph, GraphEvtType.Log, level, msg, loc, e);
	}

	// --------------------------------

	protected DemoEvent(DemoGraph graph, GraphEvtType type, Level level, String msg, StackTraceElement loc,
			Throwable e) {
		super(graph, type, level, msg, loc, e);
	}

	protected DemoEvent(DemoGraph graph, GraphEvtType type, Level level, String msg) {
		super(graph, type, level, msg);
	}

	protected <V> DemoEvent(DemoGraph graph, GraphEvtType type, V value) {
		super(graph, type, value);
	}
}
