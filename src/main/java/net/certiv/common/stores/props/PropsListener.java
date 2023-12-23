package net.certiv.common.stores.props;

import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.function.Consumer;

import net.certiv.common.event.IEvtType;
import net.certiv.common.event.TypedEvent;
import net.certiv.common.event.TypedEventListener;

public class PropsListener extends TypedEventListener {

	public static PropsListener of(IEvtType... types) {
		PropsListener listener = new PropsListener();
		listener.register(types);
		return listener;
	}

	public static PropsListener of(Collection<IEvtType> types) {
		PropsListener listener = new PropsListener();
		listener.register(types);
		return listener;
	}

	// --------------------------------

	private final LinkedHashSet<Consumer<? super PropEvent>> actions = new LinkedHashSet<>();

	public PropsListener action(Consumer<? super PropEvent> action) {
		if (action != null) actions.add(action);
		return this;
	}

	public PropsListener addTo(Props props) {
		if (props != null) props.addListener(this);
		return this;
	}

	@Override
	protected <TE extends TypedEvent> void accept(TE event) {
		if (event instanceof PropEvent) {
			for (Consumer<? super PropEvent> action : actions) {
				action.accept((PropEvent) event);
			}
		}
	}
}
