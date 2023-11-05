package net.certiv.common.stores.props;

import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

import net.certiv.common.event.ITypedEventDispatcher;
import net.certiv.common.event.TypedEvent;
import net.certiv.common.event.TypedEvent.IEvtType;
import net.certiv.common.event.TypedEventDispatcher;
import net.certiv.common.event.TypedEventListener;

/**
 * A propery key/value store.
 *
 * @implNote {@code null} values are not allowed in the property map
 */
public class Props implements ITypedEventDispatcher {

	private final LinkedHashMap<Object, Object> map = new LinkedHashMap<>();
	private final ITypedEventDispatcher dispatcher;

	public Props() {
		dispatcher = new TypedEventDispatcher();
	}

	public Props(ITypedEventDispatcher dispatcher) {
		this.dispatcher = dispatcher;
	}

	/**
	 * Returns whether this property map contains a actual value mapped to the given key.
	 *
	 * @param key a property key
	 * @return {@code true} if a value corresponds to the property key
	 */
	public <K> boolean has(K key) {
		return map.containsKey(key);
	}

	/**
	 * Returns the property value mapped to the given key, or {@code null} if the given
	 * key has no property value.
	 * <p>
	 * Use the {@link #has} method to distinguish between no property value or a
	 * {@code null} property value.
	 *
	 * @param key a property key
	 * @return the value corresponding to the property key or {@code null}
	 */
	@SuppressWarnings("unchecked")
	public <K, V> V get(K key) {
		return (V) map.get(key);
	}

	/**
	 * Returns the property value mapped to the given key, or the given default value if
	 * the given key has no property value.
	 * <p>
	 * Use the {@link #has} method to distinguish between no property value or the given
	 * default property value.
	 *
	 * @param key a property key
	 * @param def a default property value
	 * @return the value corresponding to the property key or the default value
	 */
	public <K, V> V get(K key, V def) {
		V val = get(key);
		return val != null ? val : def;
	}

	/**
	 * Returns the property value mapped to the given key, or throws the given exception
	 * if the given key has no property value.
	 * <p>
	 * Use the {@link #has} method to check if a property value exists for the given key.
	 *
	 * @param key a property key
	 * @param ex  exception to throw if no value is mapped to the property key
	 * @return the value corresponding to the property key or throws the given exception
	 */
	public <K, V> V get(K key, RuntimeException ex) {
		V val = get(key);
		if (val == null) throw ex;
		return val;
	}

	/**
	 * Associates the given value with the specified key in this map. Replaces any
	 * previously contained mapping for the key. If the value is {@code null}, the mapping
	 * is removed.
	 *
	 * @param key   property map key
	 * @param value property value
	 * @return the prior value associated with {@code key}, or {@code null} if none
	 *         existed.
	 */
	@SuppressWarnings("unchecked")
	public <K, V> V put(K key, V value) {
		if (value == null) {
			V prior = (V) map.remove(key);
			fire(PropEvent.of(this, key, value, prior));
			return prior;
		}

		V prior = (V) map.put(key, value);
		fire(PropEvent.of(this, key, value, prior));
		return prior;
	}

	/**
	 * Associates the given key with the given a value, provided the key is not already
	 * associated with a value. If the value is {@code null}, the mapping is removed.
	 *
	 * @param key   key with which the specified value is to be associated
	 * @param value value to be associated with the specified key
	 * @return the previous value associated with the specified key, or {@code null} if
	 *         there was no prior mapping for the key.
	 */
	@SuppressWarnings("unchecked")
	public <K, V> V putIfAbsent(K key, V value) {
		if (value == null) {
			V prior = (V) map.remove(key);
			fire(PropEvent.of(this, key, value, prior));
			return prior;
		}

		if (map.containsKey(key)) return (V) map.get(key);

		V prior = (V) map.put(key, value);
		fire(PropEvent.of(this, key, value, prior));
		return prior;
	}

	public <K, V> void putAll(Map<K, V> props) {
		if (props != null) props.forEach((k, v) -> put(k, v));
	}

	public <K, V> void putAllIfAbsent(Map<K, V> props) {
		if (props != null) props.forEach((k, v) -> putIfAbsent(k, v));
	}

	/** Returns an unmodifiable view of the properties map. */
	public Map<Object, Object> properties() {
		return Collections.unmodifiableMap(map);
	}

	/** Clears the structure. */
	public void clear() {
		Set<Object> keys = Set.copyOf(map.keySet());
		keys.forEach(k -> put(k, null));
	}

	// ---- Dispatcher Delegates ------

	public ITypedEventDispatcher dispatcher() {
		return dispatcher;
	}

	@Override
	public void fire(TypedEvent event) {
		if (dispatcher != null) dispatcher.fire(event);
	}

	@Override
	public void addListener(TypedEventListener listener) {
		if (dispatcher != null) dispatcher.addListener(listener);
	}

	@Override
	public void addListeners(Collection<TypedEventListener> listeners) {
		if (dispatcher != null) dispatcher.addListeners(listeners);
	}

	@Override
	public boolean hasListeners(IEvtType type) {
		return dispatcher != null ? dispatcher.hasListeners(type) : false;
	}

	@Override
	public Set<TypedEventListener> getListeners() {
		return dispatcher != null ? dispatcher.getListeners() : Set.of();
	}

	@Override
	public Set<IEvtType> getListenerTypes() {
		return dispatcher != null ? dispatcher.getListenerTypes() : Set.of();
	}

	@Override
	public int getListenerCount() {
		return dispatcher != null ? dispatcher.getListenerCount() : 0;
	}

	@Override
	public void removeListener(TypedEventListener listener) {
		if (dispatcher != null) dispatcher.removeListener(listener);
	}

	@Override
	public void clearListeners() {
		if (dispatcher != null) dispatcher.clearListeners();
	}

	// --------------------------------

	@Override
	public int hashCode() {
		return Objects.hash(map);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) return true;
		if (!(obj instanceof Props)) return false;
		Props other = (Props) obj;
		return Objects.equals(map, other.map);
	}

	@Override
	public String toString() {
		return map.toString();
	}
}
