package net.certiv.common.graph;

import net.certiv.common.event.TypedChangeEvent;

public class GraphEvent<N extends Node<N, E>, E extends Edge<N, E>> extends TypedChangeEvent {

	public static <N extends Node<N, E>, E extends Edge<N, E>> GraphEvent<N, E> install(Graph<N, E> graph,
			N node) {
		return new GraphEvent<>(graph, GraphChgType.INSTALL, node);
	}

	public static <N extends Node<N, E>, E extends Edge<N, E>> GraphEvent<N, E> uninstall(Graph<N, E> graph,
			N node) {
		return new GraphEvent<>(graph, GraphChgType.UNINSTALL, node);
	}

	public static <N extends Node<N, E>, E extends Edge<N, E>> GraphEvent<N, E> add(Graph<N, E> graph,
			E edge) {
		return new GraphEvent<>(graph, GraphChgType.ADD, edge);
	}

	public static <N extends Node<N, E>, E extends Edge<N, E>> GraphEvent<N, E> remove(Graph<N, E> graph,
			E edge) {
		return new GraphEvent<>(graph, GraphChgType.REMOVE, edge);
	}

	@Override
	public String toString() {
		switch ((GraphChgType) type()) {
			case ADD:
			case INSTALL:
				return String.format("[%s] %s", name(), value());
			case REMOVE:
			case UNINSTALL:
				return String.format("[%s] %s", name(), prior());
			default:
				return super.toString();
		}
	}

	// --------------------------------

	private <V> GraphEvent(Graph<N, E> graph, GraphChgType type, V value) {
		super(graph, type, value, null);
	}

	/** Graph Event type. */
	public enum GraphChgType implements IEvtType {

		// nodes
		INSTALL("Install node", false, true),
		UNINSTALL("Uninstall node", false, true),

		// edges
		ADD("Add edge", false, true),
		REMOVE("Remove edge", false, true);

		private final String name;
		private final boolean action;
		private final boolean change;

		GraphChgType(String name, boolean action, boolean change) {
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
