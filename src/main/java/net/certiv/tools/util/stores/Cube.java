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
 * A 2D table data-structure implemented as a LinkedHashMap with Table instance
 * values.
 */
public class Cube<L, R, C, V> {

	private final LinkedHashMap<L, Table<R, C, V>> cube;

	public Cube() {
		cube = new LinkedHashMap<>();
	}

	public Cube(Map<L, Table<R, C, V>> keyMap) {
		this();
		cube.putAll(keyMap);
	}

	public Table<R, C, V> get(L layer) {
		return cube.get(layer);
	}

	public LinkedHashMap<C, V> get(L layer, R row) {
		Table<R, C, V> table = cube.get(layer);
		if (table == null) return null;
		return table.get(row);
	}

	public V get(L layer, R row, C col) {
		LinkedHashMap<C, V> r = get(layer, row);
		if (r == null) return null;
		return r.get(col);
	}

	public V put(L layer, R row, C col, V val) {
		Table<R, C, V> table = get(layer);
		if (table == null) {
			table = new Table<>();
			cube.put(layer, table);
		}
		return table.put(row, col, val);
	}

	public boolean contains(L layer) {
		return cube.containsKey(layer);
	}

	public boolean contains(L layer, R row) {
		if (!cube.containsKey(layer)) return false;
		return cube.get(layer).contains(row);
	}

	public boolean contains(L layer, R row, C col) {
		if (!cube.containsKey(layer)) return false;
		return cube.get(layer).contains(row, col);
	}

	public Set<L> layerSet() {
		return cube.keySet();
	}

	public void remove(L layer) {
		cube.remove(layer);
	}

	public void remove(L layer, R row) {
		Table<R, C, V> t = cube.get(layer);
		if (t != null) t.remove(row);
	}

	public void remove(L layer, R row, C col) {
		Table<R, C, V> t = cube.get(layer);
		if (t != null) t.remove(row, col);
	}

	public boolean isEmpty() {
		return cube.isEmpty();
	}

	/** Returns the number of rows in this Table. */
	public int layerSize() {
		return cube.size();
	}

	/** Returns the size of this Table (total number of values held). */
	public int size() {
		int cnt = 0;
		for (Table<R, C, V> table : cube.values()) {
			cnt += table.size();
		}
		return cnt;
	}

	public void clear() {
		for (L layer : layerSet()) {
			cube.get(layer).clear();
		}
		cube.clear();
	}
}
