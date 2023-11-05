package net.certiv.common.graph;

import java.util.EnumSet;
import java.util.LinkedHashSet;
import java.util.function.Consumer;

import net.certiv.common.event.TypedEvent;
import net.certiv.common.event.TypedEventListener;
import net.certiv.common.graph.GraphEvent.GraphChgType;

public class GraphListener extends TypedEventListener {

	public static GraphListener of(GraphChgType... types) {
		GraphListener listener = new GraphListener();
		listener.register(types);
		return listener;
	}

	public static GraphListener of(EnumSet<GraphChgType> types) {
		GraphListener listener = new GraphListener();
		listener.register(types);
		return listener;
	}

	// --------------------------------

	private final LinkedHashSet<Consumer<? super GraphEvent<?, ?>>> actions = new LinkedHashSet<>();

	public GraphListener action(Consumer<? super GraphEvent<?, ?>> action) {
		if (action != null) actions.add(action);
		return this;
	}

	public GraphListener addTo(Graph<?, ?> graph) {
		if (graph != null) graph.addListener(this);
		return this;
	}

	@Override
	protected <TE extends TypedEvent> void handle(TE event) {
		if (event instanceof GraphEvent) {
			for (Consumer<? super GraphEvent<?, ?>> action : actions) {
				action.accept((GraphEvent<?, ?>) event);
			}
		}
	}
}
