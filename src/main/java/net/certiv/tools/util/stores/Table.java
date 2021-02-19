/*******************************************************************************
 * Copyright (c) 2017, 2019 Certiv Analytics. All rights reserved.
 * Use of this file is governed by the Eclipse Public License v1.0
 * that can be found in the LICENSE.txt file in the project root,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package net.certiv.tools.util.stores;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

/**
 * A table data-structure implemented as a LinkedHashMap with LinkedHashMap
 * instance values.
 */
public class Table<R, C, V> {

	private final LinkedHashMap<R, LinkedHashMap<C, V>> table;

	public Table() {
		table = new LinkedHashMap<>();
	}

	public Table(Map<R, LinkedHashMap<C, V>> keyMap) {
		this();
		this.table.putAll(keyMap);
	}

	public LinkedHashMap<C, V> get(R row) {
		return table.get(row);
	}

	public V get(R row, C col) {
		LinkedHashMap<C, V> r = table.get(row);
		if (r == null) return null;
		return r.get(col);
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

	public Set<R> rowSet() {
		return table.keySet();
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
