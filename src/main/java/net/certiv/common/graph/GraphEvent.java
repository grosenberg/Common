package net.certiv.common.graph;

import java.util.EnumSet;

import net.certiv.common.event.LogDesc;
import net.certiv.common.event.TypedChangeEvent;
import net.certiv.common.log.Level;

public class GraphEvent<N extends Node<N, E>, E extends Edge<N, E>> extends TypedChangeEvent {

	public static <N extends Node<N, E>, E extends Edge<N, E>> GraphEvent<N, E> addNode(Graph<N, E> graph,
			N node) {
		return new GraphEvent<>(graph, GraphEvtType.AddNode, node);
	}

	public static <N extends Node<N, E>, E extends Edge<N, E>> GraphEvent<N, E> rmvNode(Graph<N, E> graph,
			N node) {
		return new GraphEvent<>(graph, GraphEvtType.RmvNode, node);
	}

	public static <N extends Node<N, E>, E extends Edge<N, E>> GraphEvent<N, E> addEdge(Graph<N, E> graph,
			E edge) {
		return new GraphEvent<>(graph, GraphEvtType.AddEdge, edge);
	}

	public static <N extends Node<N, E>, E extends Edge<N, E>> GraphEvent<N, E> rmvEdge(Graph<N, E> graph,
			E edge) {
		return new GraphEvent<>(graph, GraphEvtType.RmvEdge, edge);
	}

	public static <N extends Node<N, E>, E extends Edge<N, E>> GraphEvent<N, E> of(Graph<N, E> graph,
			Level level, String msg) {
		return new GraphEvent<>(graph, GraphEvtType.Log, level, msg);
	}

	public static <N extends Node<N, E>, E extends Edge<N, E>> GraphEvent<N, E> of(Graph<N, E> graph,
			Level level, String msg, StackTraceElement loc, Throwable e) {
		return new GraphEvent<>(graph, GraphEvtType.Log, level, msg, loc, e);
	}

	@Override
	public String toString() {
		switch ((GraphEvtType) type()) {
			case AddEdge:
			case AddNode:
				return String.format("[%s] %s", name(), value());
			case RmvEdge:
			case RmvNode:
				return String.format("[%s] %s", name(), prior());
			case Log:
				return String.format("%s", value());
			default:
				return super.toString();
		}
	}

	// --------------------------------

	protected <V> GraphEvent(Graph<N, E> graph, IEvtType type, V value) {
		super(graph, type, value, null);
	}

	protected GraphEvent(Graph<N, E> core, IEvtType type, Level level, String msg) {
		super(core, type, level.name(), LogDesc.of(level, msg));
	}

	protected GraphEvent(Graph<N, E> core, IEvtType type, Level level, String msg, StackTraceElement loc,
			Throwable e) {
		super(core, type, level.name(), LogDesc.of(level, msg, loc, e));
	}

	/** Graph Event type. */
	public enum GraphEvtType implements IEvtType {

		// actions
		Log("Log", true, false),

		// node changes
		AddNode("Add node", false, true),
		RmvNode("Remove node", false, true),

		// edge changes
		AddEdge("Add edge", false, true),
		RmvEdge("Remove edge", false, true);

		// ----------------------------

		public static EnumSet<GraphEvtType> actionTypes() {
			return EnumSet.of(Log);
		}

		public static EnumSet<GraphEvtType> changeTypes() {
			return EnumSet.of(AddNode, RmvNode, AddEdge, RmvEdge);
		}

		// ----------------------------

		private final String name;
		private final boolean action;
		private final boolean change;

		GraphEvtType(String name, boolean action, boolean change) {
			this.name = name;
			this.action = action;
			this.change = change;
		}

		@Override
		public String typeName() {
			return name;
		}

		@Override
		public boolean isActionType() {
			return action;
		}

		@Override
		public boolean isChangeType() {
			return change;
		}
	}
}
