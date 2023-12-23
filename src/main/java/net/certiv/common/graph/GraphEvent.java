package net.certiv.common.graph;

import java.util.Set;

import net.certiv.common.event.IEvtType;
import net.certiv.common.event.LogDesc;
import net.certiv.common.event.TypedActionEvent.IEvtCmd;
import net.certiv.common.event.TypedEvent;
import net.certiv.common.log.Level;

/**
 * Graph events encompass both action and change events.
 */
public class GraphEvent<N extends Node<N, E>, E extends Edge<N, E>> extends TypedEvent {

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

	public static <N extends Node<N, E>, E extends Edge<N, E>> GraphEvent<N, E> log(Graph<N, E> graph,
			Level level, String msg) {
		return new GraphEvent<>(graph, GraphEvtType.Log, level, msg);
	}

	public static <N extends Node<N, E>, E extends Edge<N, E>> GraphEvent<N, E> log(Graph<N, E> graph,
			Level level, String msg, StackTraceElement loc) {
		return new GraphEvent<>(graph, GraphEvtType.Log, level, msg, loc, null);
	}

	public static <N extends Node<N, E>, E extends Edge<N, E>> GraphEvent<N, E> log(Graph<N, E> graph,
			Level level, String msg, StackTraceElement loc, Throwable e) {
		return new GraphEvent<>(graph, GraphEvtType.Log, level, msg, loc, e);
	}

	// --------------------------------

	/** Event action. */
	protected transient final Object action;
	/** Event current value. */
	protected transient final Object value;
	/** Event prior value. */
	protected transient final Object prior;

	/** Graph change event. */
	protected <V> GraphEvent(Graph<N, E> graph, IEvtType type, V value) {
		this(graph, type, value, null);
	}

	/** Graph change event w/prior. */
	protected <V> GraphEvent(Graph<N, E> graph, IEvtType type, V value, V prior) {
		super(graph, type);
		action = null;
		this.value = value;
		this.prior = prior;
	}

	/** Graph log action event. */
	protected GraphEvent(Graph<N, E> graph, IEvtType type, Level level, String msg) {
		super(graph, type, level.name());
		this.action = level.name();
		this.value = LogDesc.of(level, msg);
		prior = null;
	}

	/** Graph log action event. */
	protected GraphEvent(Graph<N, E> graph, IEvtType type, Level level, String msg, StackTraceElement loc,
			Throwable e) {
		super(graph, type, level.name());
		this.action = level.name();
		this.value = LogDesc.of(level, msg, loc, e);
		prior = null;
	}

	/**
	 * Returns the action that this event defines.
	 *
	 * @return the event action
	 */
	@SuppressWarnings("unchecked")
	public <A extends IEvtCmd> A action() {
		return (A) action;
	}

	/**
	 * Returns the value associated with this action event.
	 *
	 * @return the action value
	 */
	@SuppressWarnings("unchecked")
	public <V> V value() {
		return (V) value;
	}

	/**
	 * Returns the prior change value associated with this event.
	 *
	 * @return the prior value
	 */
	@SuppressWarnings("unchecked")
	public <V> V prior() {
		return (V) value;
	}

	@Override
	public boolean issuable() {
		return valueChanged();
	}

	/**
	 * Returns whether this event presents an actual {@code prior ==> value} change.
	 *
	 * @return {@code true} if this event presents an actual value change
	 */
	public boolean valueChanged() {
		return value != prior && (value != null || prior != null);
	}

	@Override
	public String toString() {
		GraphEvtType type = type();
		if (type.equals(GraphEvtType.Log)) {
			return String.format("%s", value());
		}
		if (type.equals(GraphEvtType.AddEdge) || type.equals(GraphEvtType.AddNode)) {
			return String.format("[%s] %s", name(), value());
		}
		if (type.equals(GraphEvtType.RmvEdge) || type.equals(GraphEvtType.RmvNode)) {
			return String.format("[%s] %s", name(), prior());
		}
		return super.toString();
	}

	// ================================

	/** Graph Event type. */
	public static class GraphEvtType implements IEvtType {

		// actions
		public static final GraphEvtType Log = new GraphEvtType("Log", true, false);

		// node changes
		public static final GraphEvtType AddNode = new GraphEvtType("Add node", false, true);
		public static final GraphEvtType RmvNode = new GraphEvtType("Rmv node", false, true);

		// edge changes
		public static final GraphEvtType AddEdge = new GraphEvtType("Add edge", false, true);
		public static final GraphEvtType RmvEdge = new GraphEvtType("Rmv edge", false, true);

		// ----------------------------

		public static Set<GraphEvtType> actionTypes() {
			return Set.of(Log);
		}

		public static Set<GraphEvtType> changeTypes() {
			return Set.of(AddNode, RmvNode, AddEdge, RmvEdge);
		}

		public static Set<GraphEvtType> allTypes() {
			return Set.of(Log, AddNode, RmvNode, AddEdge, RmvEdge);
		}

		// ----------------------------

		private final String name;
		private final boolean action;
		private final boolean change;

		protected GraphEvtType(String name, boolean action, boolean change) {
			this.name = name;
			this.action = action;
			this.change = change;
		}

		@Override
		public String name() {
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
