package net.certiv.common.event;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import net.certiv.common.stores.LinkedHashList;

public class TypedEventDispatcher implements ITypedEventDispatcher {

	/** Map of listeners by event type: key=type; values=listeners. */
	private LinkedHashList<IEvtType, TypedEventListener> map = new LinkedHashList<>(true);

	/**
	 * {@inheritDoc}
	 * <p>
	 * TODO: support firing on separate virtual threads.
	 */
	@Override
	public <TE extends TypedEvent> void fire(TE event) {
		if (event.issuable()) {
			TypeKey key = TypeKey.get(event);
			for (TypedEventListener listener : map.getOrDefault(key.type, List.of())) {
				if (listener.comprehends(key)) {
					listener.accept(event);
				}
			}
		}
	}

	@Override
	public void addListener(TypedEventListener listener) {
		if (listener != null) addListeners(Set.of(listener));
	}

	@Override
	public void addListeners(Collection<TypedEventListener> listeners) {
		LinkedHashList<IEvtType, TypedEventListener> dup = new LinkedHashList<>(map, true);
		for (TypedEventListener listener : listeners) {
			Set<IEvtType> types = listener.types();
			types.forEach(t -> dup.put(t, listener));
		}
		map = dup;
	}

	@Override
	public void removeListener(TypedEventListener listener) {
		LinkedHashList<IEvtType, TypedEventListener> dup = new LinkedHashList<>(map, true);
		listener.types().forEach(t -> dup.remove(t, listener));
		map = dup;
	}

	@Override
	public boolean hasListeners(IEvtType type) {
		LinkedList<TypedEventListener> listeners = map.get(type);
		return listeners != null && !listeners.isEmpty();
	}

	@Override
	public Set<IEvtType> getListenerTypes() {
		return map.keySet();
	}

	// @Override
	// public Set<TypeKey> getListenerKeys() {
	// return null;
	// }

	@Override
	public Set<TypedEventListener> getListeners() {
		return Set.copyOf(map.valuesAll());
	}

	@Override
	public int getListenerCount() {
		return map.sizeValues();
	}

	@Override
	public void clearListeners() {
		map.clear();
	}
}
