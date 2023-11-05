package net.certiv.common.event;

import java.util.Arrays;
import java.util.Collection;
import java.util.EventListener;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import net.certiv.common.check.Assert;
import net.certiv.common.event.TypedEvent.IEvtType;
import net.certiv.common.stores.LinkedHashList;
import net.certiv.common.util.CompareUtil;
import net.certiv.common.util.Maths;
import net.certiv.common.util.Maths.RangeStyle;

public abstract class TypedEventListener implements EventListener {

	/** key=reg event type; value=reg type keys */
	private final LinkedHashList<IEvtType, TypeKey> registry = new LinkedHashList<>();

	/**
	 * Returns the event types this listener is specified as capable of handling.
	 *
	 * @return event types of the registered type keys
	 */
	public Set<IEvtType> types() {
		return registry.keySet();
	}

	/**
	 * Returns the event type keys this listener is specified as capable of handling.
	 *
	 * @return the registered type keys
	 */
	public LinkedList<TypeKey> keys() {
		return registry.valuesAll();
	}

	/**
	 * Returns the event type keys with the given type this listener is specified as
	 * capable of handling.
	 *
	 * @return the registered type keys
	 */
	public LinkedList<TypeKey> keys(IEvtType type) {
		return registry.getOrDefault(type, List.of());
	}

	/**
	 * Register the given event type and name with this listener. Establishes that events
	 * with the given event type and name can be handled by this listener.
	 *
	 * @param type event type
	 * @param name event name
	 * @return {@code this}
	 */
	protected TypedEventListener register(IEvtType type, String name) {
		Assert.notNull(type, name);
		TypeKey key = TypeKey.get(type, name);
		if (!comprehends(key)) {
			Set<TypeKey> keys = comprehendedBy(key);
			keys.forEach(k -> registry.remove(k.type, k));
		}
		registry.put(type, key);
		return this;
	}

	/**
	 * Establishes this listener as capable of handling events with the given event types.
	 *
	 * @param types event types
	 * @return {@code this}
	 */
	protected TypedEventListener register(IEvtType... types) {
		register(Arrays.asList(types));
		return this;
	}

	/**
	 * Establishes this listener as capable of handling events with the given event types.
	 *
	 * @param types event types
	 * @return {@code this}
	 */
	protected TypedEventListener register(Collection<? extends IEvtType> types) {
		Assert.notNull(types);
		types.forEach(t -> register(t, TypeKey.ANY));
		return this;
	}

	/**
	 * Register the given event type keys with this listener. Establishes that events with
	 * the given event type keys can be handled by this listener.
	 *
	 * @param keys event type keys
	 * @return {@code this}
	 */
	protected TypedEventListener registerKeys(Collection<TypeKey> keys) {
		Assert.notNull(keys);
		keys.forEach(k -> register(k.type, k.name));
		return this;
	}

	/**
	 * Returns the subset of registered keys that are comprehended by the give key.
	 *
	 * @param key type key
	 * @return the subset of registered keys that are comprehended, if any
	 */
	protected Set<TypeKey> comprehendedBy(TypeKey key) {
		if (key == null) return Set.of();
		return keys(key.type).stream().filter(k -> comprehends(k.name, key.name)) //
				.collect(Collectors.toSet());
	}

	/**
	 * Determines whether this listener can handle events corresponding to the given type
	 * key.
	 * <p>
	 * Correspondence exists when any currently registered key is of the same type and has
	 * a name that is within the scope of the name of the given key.
	 *
	 * <pre>{@code
	 * A:ANY | A:ANY => true
	 * A:ANY | A:a.b => true
	 * A:a   | A:ANY => false
	 * A:a   | A:a.b => true
	 * A:a.b | A:a.b => true
	 * A:a.b | A:a.c => false
	 * A:ANY | B:ANY => false
	 * A:a.b | B:ANY => false
	 * A:a.b | B:a.b => false
	 * }</pre>
	 *
	 * @param key type key
	 * @return {@code true} if this listener comprehends the given type key
	 */
	protected boolean comprehends(TypeKey key) {
		if (key == null) return false;
		return keys(key.type).stream().anyMatch(k -> comprehends(k.name, key.name));
	}

	/**
	 * Returns whether the reference name comprehends the given name.
	 *
	 * @param ref  a reference name
	 * @param name a target name
	 * @return {@code true} if the reference name comprehends the given name
	 * @see CompareUtil#within(String, String)
	 */
	protected boolean comprehends(String ref, String name) {
		Assert.notNull(ref, name);
		if (ref.equals(TypeKey.ANY) || ref.equals(name)) return true;
		if (name.equals(TypeKey.ANY)) return false;
		return Maths.inRange(CompareUtil.within(ref, name), -1, 0, RangeStyle.CLOSED);
	}

	/**
	 * Called when the given event is dispatched.
	 *
	 * @param event a {@link TypedEvent} object
	 */
	protected abstract <TE extends TypedEvent> void handle(TE event);
}
