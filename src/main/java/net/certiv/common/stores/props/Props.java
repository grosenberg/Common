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

	@SuppressWarnings("unchecked")
	public <K, V> V get(K key) {
		return (V) propMap.get(key);
	}

	public <K, V> V get(K key, V def) {
		V val = get(key);
		return val != null ? val : def;
	}

	public <K, V> V get(K key, RuntimeException ex) {
		V val = get(key);
		if (val == null) throw ex;
		return val;
	}

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
