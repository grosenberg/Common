package net.certiv.common.stores.props;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;

public class Props {

	private final LinkedHashMap<Object, Object> propMap = new LinkedHashMap<>();

	public boolean has(Object key) {
		return propMap.containsKey(key);
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
		return (V) propMap.get(key);
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
		if (value == null) return (V) propMap.remove(key);
		return (V) propMap.put(key, value);
	}

	@SuppressWarnings("unchecked")
	public <K, V> V putIfAbsent(K key, V value) {
		if (value == null) return (V) propMap.remove(key);
		return (V) propMap.putIfAbsent(key, value);
	}

	public <K, V> void putAll(Map<K, V> props) {
		if (props != null) props.forEach((k, v) -> put(k, v));
	}

	public <K, V> void putAllIfAbsent(Map<K, V> props) {
		if (props != null) props.forEach((k, v) -> putIfAbsent(k, v));
	}

	/** Returns the properties map. */
	public Map<Object, Object> properties() {
		return Collections.unmodifiableMap(propMap);
	}

	/** Clears the structure. */
	public void clear() {
		propMap.clear();
	}

	@Override
	public int hashCode() {
		return Objects.hash(propMap);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) return true;
		if (!(obj instanceof Props)) return false;
		Props other = (Props) obj;
		return Objects.equals(propMap, other.propMap);
	}

	@Override
	public String toString() {
		return propMap.toString();
	}
}
