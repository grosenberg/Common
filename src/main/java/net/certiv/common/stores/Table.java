/*******************************************************************************
 * Copyright (c) 2017, 2019 Certiv Analytics. All rights reserved.
 * Use of this file is governed by the Eclipse Public License v1.0
 * that can be found in the LICENSE.txt file in the project root,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package net.certiv.common.stores;

import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.function.BiConsumer;

/**
 * A table data-structure implemented as a LinkedHashMap with LinkedHashMap instance
 * values.
 */
public class Table<R, C, V> {

	private final LinkedHashMap<R, LinkedHashMap<C, V>> table;

	public Table() {
		table = new LinkedHashMap<>();
	}

	public Table(Table<R, C, V> table) {
		this();
		this.table.putAll(table.rowMap());
	}

	public Table(Map<R, LinkedHashMap<C, V>> keyMap) {
		this();
		table.putAll(keyMap);
	}

	public LinkedHashMap<C, V> get(R row) {
		return table.get(row);
	}

	public V get(R row, C col) {
		LinkedHashMap<C, V> r = table.get(row);
		if (r == null) return null;
		return r.get(col);
	}

	/**
	 * Get the row keys for the rows that contain the given column value.
	 *
	 * @param col a column value
	 * @return set of row keys for rows containing the given column value
	 */
	public Set<R> getRows(C col) {
		Set<R> values = new LinkedHashSet<>();
		for (Entry<R, LinkedHashMap<C, V>> row : table.entrySet()) {
			LinkedHashMap<C, V> map = row.getValue();
			if (map.containsKey(col)) values.add(row.getKey());
		}
		return values;

	}

	/**
	 * Copies all of the mappings from the given map to this map to the col/value mappings
	 * identified by the given row key. The copied mappings will be added to or replace,
	 * as appropriate, any prior existing col/value mappings.
	 *
	 * @throws NullPointerException if the row or map is {@code null}
	 */
	public void putRow(R row, Map<C, V> map) {
		LinkedHashMap<C, V> values = table.get(row);
		if (values == null) {
			values = new LinkedHashMap<>();
			table.put(row, values);
		}
		values.putAll(map);
	}

	/**
	 * Replaces the col/value mappings identified by the given row key with the given map,
	 * or adds the mappings at the given row key.
	 *
	 * @throws NullPointerException if the row or map is {@code null}
	 */
	public void replaceRow(R row, Map<C, V> map) {
		LinkedHashMap<C, V> values = table.get(row);
		if (values == null) {
			values = new LinkedHashMap<>();
			table.put(row, values);
		}
		values.clear();
		values.putAll(map);
	}

	public V put(R key, C sel, V value) {
		LinkedHashMap<C, V> values = get(key);
		if (values == null) {
			values = new LinkedHashMap<>();
			table.put(key, values);
		}
		return values.put(sel, value);
	}

	public boolean contains(R row) {
		return table.containsKey(row);
	}

	public boolean contains(R row, C col) {
		LinkedHashMap<C, V> r = table.get(row);
		if (r == null) return false;
		return r.containsKey(col);
	}

	/** Returns a list view of all values contained in this table. */
	public LinkedList<V> values() {
		LinkedList<V> values = new LinkedList<>();
		for (LinkedHashMap<C, V> x : table.values()) {
			values.addAll(x.values());
		}
		return values;
	}

	public Set<R> rowSet() {
		return table.keySet();
	}

	public LinkedHashMap<R, LinkedHashMap<C, V>> rowMap() {
		return table;
	}

	public Set<Entry<R, LinkedHashMap<C, V>>> entrySet() {
		return table.entrySet();
	}

	public void forEach(BiConsumer<? super R, ? super LinkedHashMap<C, V>> action) {
		table.forEach(action);
	}

	public void remove(R row) {
		table.remove(row);
	}

	public void remove(R row, C col) {
		LinkedHashMap<C, V> r = table.get(row);
		if (r != null) r.remove(col);
	}

	public boolean isEmpty() {
		return size() == 0;
	}

	/** Returns the number of rows in this Table. */
	public int rowSize() {
		return table.size();
	}

	/** Returns the size of this Table (total number of values held). */
	public int size() {
		int cnt = 0;
		for (Map<C, V> rows : table.values()) {
			cnt += rows.size();
		}
		return cnt;
	}

	public void clear() {
		for (R row : rowSet()) {
			table.get(row).clear();
		}
		table.clear();
	}
}
