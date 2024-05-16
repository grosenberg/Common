package net.certiv.common.stores;

import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map.Entry;
import java.util.Set;
import java.util.TreeMap;

/**
 * Sorted multimap implemented as a TreeMap/TreeMap table. Keys are implicitly unique.
 *
 * @param <R> row type
 * @param <C> col type
 * @param <V> value type
 */
public class TreeTable<R, C, V> {

	private TreeMap<R, TreeMap<C, V>> map;
	private Comparator<? super C> colComp;

	public TreeTable() {
		this(null, null);
	}

	public TreeTable(Comparator<? super R> rowComp) {
		this(rowComp, null);
	}

	public TreeTable(Comparator<? super R> rowComp, Comparator<? super C> colComp) {
		this.map = new TreeMap<>(rowComp);
		this.colComp = colComp;
	}

	public TreeMap<C, V> get(R row) {
		return map.get(row);
	}

	public V get(R row, C col) {
		TreeMap<C, V> cols = map.get(row);
		if (cols == null) return null;
		return cols.get(col);
	}

	public void put(R row, C col, V value) {
		TreeMap<C, V> cols = map.get(row);
		if (cols == null) {
			cols = new TreeMap<>(colComp);
			map.put(row, cols);
		}
		cols.put(col, value);
	}

	public boolean contains(R row) {
		return map.containsKey(row);
	}

	public boolean contains(R row, C col) {
		TreeMap<C, V> cols = map.get(row);
		if (cols == null) return false;
		return cols.containsKey(col);
	}

	public TreeMap<C, V> remove(R row) {
		return map.remove(row);
	}

	public Set<R> keySet() {
		return map.keySet();
	}

	public Set<Entry<R, TreeMap<C, V>>> entrySet() {
		return map.entrySet();
	}

	public List<C> cols(R row) {
		TreeMap<C, V> cols = map.get(row);
		if (cols != null) {
			return new LinkedList<>(cols.keySet());
		}
		return Collections.emptyList();
	}

	public List<R> rows() {
		return new LinkedList<>(map.keySet());
	}

	public List<V> values(R row) {
		TreeMap<C, V> cols = map.get(row);
		if (cols == null) return Collections.emptyList();
		return new LinkedList<>(cols.values());
	}

	public boolean isEmpty() {
		return map.isEmpty();
	}

	public boolean isEmpty(R row) {
		TreeMap<C, V> cols = map.get(row);
		return cols != null ? cols.isEmpty() : true;
	}

	public int size() {
		return map.size();
	}

	public int size(R row) {
		TreeMap<C, V> cols = map.get(row);
		return cols != null ? cols.size() : 0;
	}

	public void clear() {
		for (R row : keySet()) {
			map.get(row).clear();
		}
		map.clear();
	}
}
