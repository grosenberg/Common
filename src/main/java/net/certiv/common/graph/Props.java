package net.certiv.common.graph;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;

import net.certiv.common.dot.Dictionary.ON;
import net.certiv.common.dot.DotStyle;

public class Props {

	private final LinkedHashMap<Object, Object> propMap = new LinkedHashMap<>();

	/**
	 * Returns the {@code DotStyle} store for the {@code Node} or {@code Edge} containing
	 * this properties store. Creates and adds a {@code DotStyle} store, using the given
	 * default {@code ON} category, if a store does not exist.
	 *
	 * @param category a default {@code ON} category
	 * @return the dot style store
	 */
	protected DotStyle getDotStyle(ON category) {
		DotStyle ds = (DotStyle) getProperty(DotStyle.PropName);
		if (ds == null) {
			ds = new DotStyle(category);
			putProperty(DotStyle.PropName, ds);
		}
		return ds;
	}

	protected void clearDotStyle() {
		putProperty(DotStyle.PropName, null);
	}

	/**
	 * Returns {@code true} if a property value is associated with the given key.
	 *
	 * @param key the property key
	 * @return {@code true} if a property value is associated with key
	 */
	public final boolean hasProperty(Object key) {
		return propMap.containsKey(key);
	}

	/**
	 * Returns the value of the property with the specified key. Only properties added
	 * with putProperty will return a non-null value.
	 *
	 * @param key the property key
	 * @return the property value associated with key, or {@code null} if there was no
	 *         mapping for the key
	 */
	public final Object getProperty(Object key) {
		return propMap.get(key);
	}

	/**
	 * Returns the value of the property with the specified key, or the default value if
	 * no such property key mapping exists. The default value is not retained.
	 *
	 * @param key the property key
	 * @param def the default value
	 * @return the property value associated with key, or the {@code def} value if no
	 *         mapping for the key exists
	 */
	public Object getProperty(Object key, Object def) {
		Object val = getProperty(key);
		return val != null ? val : def;
	}

	/**
	 * Adds an arbitrary key/value "property" to this edge. If value is {@code null}, the
	 * property will be removed.
	 *
	 * @param key   the property key
	 * @param value the new property value
	 * @return the previous property value associated with key, or {@code null} if there
	 *         was no mapping for the key
	 */
	public final Object putProperty(Object key, Object value) {
		if (value == null) return propMap.remove(key);
		return propMap.put(key, value);
	}

	/** Adds the given map of properties. */
	public final void putProperties(Map<Object, Object> props) {
		if (props != null) props.forEach((k, v) -> putProperty(k, v));
	}

	/** Returns the properties map. */
	public final Map<Object, Object> properties() {
		return Collections.unmodifiableMap(propMap);
	}

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
