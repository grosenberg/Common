package net.certiv.common.stores;

import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/** Table implemented with two levels of {@link ConcurrentHashMap}. */
public class ConcurrentTable<R, C, V> {

	private final Map<R, ConcurrentHashMap<C, V>> map;

	/** Constructs a new, empty table. */
	public ConcurrentTable() {
		this.map = new ConcurrentHashMap<>();
	}

	/**
	 * Constructs a new map containing the same mappings as the given map, sorted
	 * according to the {@linkplain Comparable natural ordering} of the keys.
	 *
	 * @param m the map whose mappings are to be placed in this map
	 * @throws ClassCastException if the keys in {@code m} are not
	 *             {@link Comparable}, or are not mutually comparable
	 * @throws NullPointerException if the specified map or any of its keys or
	 *             values are null
	 */
	public ConcurrentTable(ConcurrentTable<R, C, V> m) {
		this();
		putAll(m);
	}

	public ConcurrentHashMap<C, V> get(R row) {
		return map.get(row);
	}

	public V get(R row, C col) {
		ConcurrentHashMap<C, V> cols = map.get(row);
		if (cols == null) return null;
		return cols.get(col);
	}

	public boolean contains(R row) {
		return map.containsKey(row);
	}

	public boolean contains(R row, C col) {
		ConcurrentHashMap<C, V> cols = map.get(row);
		if (cols == null) return false;
		return cols.get(col) != null;
	}

	public boolean contains(R row, C col, V value) {
		ConcurrentHashMap<C, V> cols = map.get(row);
		V val = cols.get(col);
		if (val == null) return value == null;
		return val.equals(value);
	}

	public V put(R row, C col, V val) {
		ConcurrentHashMap<C, V> cols = map.get(row);
		if (cols != null) return cols.put(col, val);
		synchronized (this) {
			cols = new ConcurrentHashMap<>();
			map.put(row, cols);
			return cols.put(col, val);
		}
	}

	public void putAll(ConcurrentTable<R, C, V> t) {
		for (Entry<R, ConcurrentHashMap<C, V>> e : t.entrySet()) {
			R row = e.getKey();
			for (Entry<C, V> m : e.getValue().entrySet()) {
				put(row, m.getKey(), m.getValue());
			}
		}
	}

	public Set<Entry<R, ConcurrentHashMap<C, V>>> entrySet() {
		return map.entrySet();
	}

	public Set<R> rowSet() {
		return map.keySet();
	}

	public Set<C> colSet() {
		Set<C> cols = new LinkedHashSet<>();
		for (R row : map.keySet()) {
			cols.addAll(map.get(row).keySet());
		}
		return cols;
	}

	public Set<C> colSet(R row) {
		Map<C, V> cols = map.get(row);
		return new LinkedHashSet<>(cols.keySet());
	}

	public Set<V> values() {
		Set<V> values = new LinkedHashSet<>();
		for (ConcurrentHashMap<C, V> cols : map.values()) {
			for (V val : cols.values()) {
				values.add(val);
			}
		}
		return values;
	}

	public ConcurrentHashMap<C, V> remove(R row) {
		return map.remove(row);
	}

	public V remove(R row, C col) {
		ConcurrentHashMap<C, V> cols = map.get(row);
		if (cols == null) return null;
		return cols.remove(col);
	}

	public void clear() {
		map.clear();
	}

	public boolean isEmpty() {
		return map.isEmpty();
	}

	public int size() {
		return map.size();
	}

	@Override
	public int hashCode() {
		return Objects.hash(map);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) return true;
		if (!(obj instanceof ConcurrentTable)) return false;
		ConcurrentTable<?, ?, ?> other = (ConcurrentTable<?, ?, ?>) obj;
		return Objects.equals(map, other.map);
	}
}
