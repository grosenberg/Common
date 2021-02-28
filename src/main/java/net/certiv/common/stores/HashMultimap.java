/*******************************************************************************
 * Copyright (c) 2017, 2018 Certiv Analytics. All rights reserved.
 * Use of this file is governed by the Eclipse Public License v1.0
 * that can be found in the LICENSE.txt file in the project root,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package net.certiv.common.stores;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/** Multimap implemented as a LinkedHashMap with LinkedHashMap instance values. */
public class HashMultimap<R, C, V> {

	private final LinkedHashMap<R, LinkedHashMap<C, V>> map;

	public HashMultimap() {
		map = new LinkedHashMap<>();
	}

	public HashMultimap(Map<R, LinkedHashMap<C, V>> keyMap) {
		this();
		this.map.putAll(keyMap);
	}

	public LinkedHashMap<C, V> get(R row) {
		return map.get(row);
	}

	public V get(R row, C col) {
		LinkedHashMap<C, V> sub = map.get(row);
		if (sub == null) return null;
		return sub.get(col);
	}

	public V put(R row, C col, V value) {
		LinkedHashMap<C, V> values = get(row);
		if (values == null) {
			values = new LinkedHashMap<>();
			map.put(row, values);
		}
		return values.put(col, value);
	}

	// public boolean put(K row, Collection<V> values) {
	// LinkedHashMap<S, V> list = map.get(row);
	// if (list == null) {
	// list = new LinkedHashMap<>();
	// map.put(row, list);
	// }
	// return list.putAll(row, values);
	// }

	public void removeKey(R row) {
		map.remove(row);
	}

	public boolean containsKey(R row) {
		return map.containsKey(row);
	}

	// public boolean containsEntry(K row, V value) {
	// LinkedHashMap<S, V> values = get(row);
	// if (values == null) return false;
	// return values.contains(value);
	// }

	public Set<R> keySet() {
		return map.keySet();
	}

	/** Returns a list of the value lists held in this HashMultimap. */
	public List<LinkedHashMap<C, V>> valuesList() {
		return new ArrayList<>(map.values());
	}

	// /** Returns a list of all of the values held in this HashMultimap. */
	// public LinkedHashMap<S, V> values() {
	// LinkedHashMap<S, V> values = new ArrayList<>();
	// for (LinkedHashMap<S, V> subList : map.values()) {
	// values.addAll(subList);
	// }
	// return values;
	// }

	// public void sort(Comparator<V> comp) {
	// for (K row : map.keySet()) {
	// map.get(row).sort(comp);
	// }
	// }

	public boolean isEmpty() {
		return map.isEmpty();
	}

	public int keySize() {
		return map.size();
	}

	/** Returns the size (total number of held values) of this HashMultimap. */
	public int size() {
		int cnt = 0;
		for (LinkedHashMap<C, V> values : map.values()) {
			cnt += values.size();
		}
		return cnt;
	}

	public void clear() {
		for (R row : keySet()) {
			map.get(row).clear();
		}
		map.clear();
	}
}
