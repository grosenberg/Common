package net.certiv.common.stores;

import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map.Entry;
import java.util.Set;
import java.util.TreeMap;

/**
 * Sorted multimap implemented as a TreeMap/TreeList table. Keys are implicitly unique.
 *
 * @param <R> row type
 * @param <C> col type
 * @param <V> the list value type
 */
public class TreeTableList<R, C, V> {

	private TreeMap<R, TreeList<C, V>> map;
	private Comparator<? super C> colComp;

	public TreeTableList() {
		this(null, null);
	}

	public TreeTableList(Comparator<? super R> rowComp) {
		this(rowComp, null);
	}

	public TreeTableList(Comparator<? super R> rowComp, Comparator<? super C> colComp) {
		this.map = new TreeMap<>(rowComp);
		this.colComp = colComp;
	}

	public TreeList<C, V> get(R row) {
		return map.get(row);
	}

	public List<V> get(R row, C col) {
		TreeList<C, V> cols = map.get(row);
		if (cols != null) {
			List<V> values = cols.get(col);
			if (values != null) return values;
		}
		return Collections.emptyList();
	}

	public void put(R row, C col, V value) {
		TreeList<C, V> cols = map.get(row);
		if (cols == null) {
			cols = new TreeList<>(colComp);
			map.put(row, cols);
		}
		cols.put(col, value);
	}

	public void putAll(R row, C col, List<V> values) {
		TreeList<C, V> cols = map.get(row);
		if (cols == null) {
			cols = new TreeList<>(colComp);
			map.put(row, cols);
		}
		cols.putAll(col, values);
	}

	public boolean contains(R row) {
		return map.containsKey(row);
	}

	public boolean contains(R row, C col) {
		TreeList<C, V> cols = map.get(row);
		if (cols == null) return false;
		return cols.containsKey(col);
	}

	public TreeList<C, V> remove(R row) {
		return map.remove(row);
	}

	public Set<R> keySet() {
		return map.keySet();
	}

	public Set<Entry<R, TreeList<C, V>>> entrySet() {
		return map.entrySet();
	}

	public List<C> cols(R row) {
		TreeList<C, V> cols = map.get(row);
		if (cols != null) {
			return new LinkedList<>(cols.keySet());
		}
		return Collections.emptyList();
	}

	public List<R> rows() {
		return new LinkedList<>(map.keySet());
	}

	public List<V> values(R row) {
		TreeList<C, V> cols = map.get(row);
		if (cols != null) {
			return cols.values();
		}
		return Collections.emptyList();
	}

	public boolean isEmpty() {
		return map.isEmpty();
	}

	public boolean isEmpty(R row) {
		TreeList<C, V> cols = map.get(row);
		return cols != null ? cols.isEmpty() : true;
	}

	public int size() {
		return map.size();
	}

	public int size(R row) {
		TreeList<C, V> cols = map.get(row);
		return cols != null ? cols.size() : 0;
	}

	public void clear() {
		for (R row : keySet()) {
			map.get(row).clear();
		}
		map.clear();
	}
}
