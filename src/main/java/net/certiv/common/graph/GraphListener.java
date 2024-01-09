package net.certiv.common.graph;

import java.util.LinkedHashSet;
import java.util.function.Consumer;

import net.certiv.common.event.TypedEvent;
import net.certiv.common.event.TypedEventListener;
import net.certiv.common.graph.id.Id;

/**
 * Base class for graph listeners. Extend and implement constructors:
 *
 * <pre>{@code
 * public static <I extends Id, N extends Node<I, N, E>, E extends Edge<I, N, E>> GraphListener<I, N, E> of(
 * 		GraphEvtType... types) {
 * 	GraphListener<I, N, E> listener = new GraphListener<>();
 * 	listener.register(types);
 * 	return listener;
 * }
 * }</pre>
 *
 * <pre>{@code
 * public static <I extends Id, N extends Node<I, N, E>, E extends Edge<I, N, E>> GraphListener<I, N, E> of(
 * 		EnumSet<GraphEvtType> types) {
 * 	GraphListener<I, N, E> listener = new GraphListener<>();
 * 	listener.register(types);
 * 	return listener;
 * }
 * }</pre>
 *
 * @param <N> node type
 * @param <E> edge type
 */
public abstract class GraphListener<I extends Id, N extends Node<I, N, E>, E extends Edge<I, N, E>>
		extends TypedEventListener {

	private final LinkedHashSet<Consumer<? super GraphEvent<I, N, E>>> actions = new LinkedHashSet<>();

	protected GraphListener() {
		super();
	}

	@SuppressWarnings("unchecked")
	public <L extends GraphListener<I, N, E>> L action(Consumer<? super GraphEvent<I, N, E>> action) {
		if (action != null) actions.add(action);
		return (L) this;
	}

	@SuppressWarnings("unchecked")
	public <L extends GraphListener<I, N, E>> L addTo(Graph<I, N, E> graph) {
		if (graph != null) graph.addListener(this);
		return (L) this;
	}

	@SuppressWarnings("unchecked")
	public <L extends GraphListener<I, N, E>> L removeFrom(Graph<I, N, E> graph) {
		if (graph != null) graph.removeListener(this);
		return (L) this;
	}

	@Override
	@SuppressWarnings("unchecked")
	protected <TE extends TypedEvent> void accept(TE event) {
		if (event instanceof GraphEvent) {
			for (Consumer<? super GraphEvent<I, N, E>> action : actions) {
				action.accept((GraphEvent<I, N, E>) event);
			}
		}
	}

	@Override
	public void clear() {
		actions.clear();
		super.clear();
	}
}
