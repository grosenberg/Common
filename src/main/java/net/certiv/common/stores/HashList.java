/*******************************************************************************
 * Copyright (c) 2017, 2020 Certiv Analytics. All rights reserved.
 * Use of this file is governed by the Eclipse Public License v1.0
 * that can be found in the LICENSE.txt file in the project root,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package net.certiv.common.stores;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.Set;
import java.util.function.BiConsumer;

/**
 * Multimap implemented as a LinkedHashMap with LinkedList implemented instance
 * values.
 *
 * @param <K> the key type
 * @param <V> the list value type
 */
public class HashList<K, V> {

	private final LinkedHashMap<K, LinkedList<V>> map = new LinkedHashMap<>();
	private boolean unique = false;

	public HashList() {}

	public HashList(HashList<K, V> data) {
		for (K key : data.keys()) {
			put(key, data.get(key));
		}
	}

	public HashList(Map<K, List<V>> data) {
		for (Entry<K, List<V>> entry : data.entrySet()) {
			put(entry.getKey(), entry.getValue());
		}
	}

	/**
	 * Returns the value mapped to the specified key, or {@code null} if this map
	 * contains no mapping for the key.
	 */
	public LinkedList<V> get(K key) {
		return map.get(key);
	}

	public LinkedList<V> getOrDefault(K key, Collection<V> defValue) {
		LinkedList<V> res = map.get(key);
		if (res != null) return res;
		return new LinkedList<>(defValue);
	}

	/**
	 * Adds the given value to the list of values identified by the given key. If
	 * {@link unique} is {@code true}, the value is added only if not already
	 * present (based on object equality).
	 */
	public boolean put(K key, V value) {
		LinkedList<V> values = map.get(key);
		if (values == null) {
			values = new LinkedList<>();
			map.put(key, values);
		}
		if (unique && values.contains(value)) return false;
		return values.add(value);
	}

	/**
	 * Appends the given values to the list of values identified by the given key.
	 * If {@link unique} is set to {@code true}, each value is added only if value
	 * is not already present (based on object equality).
	 */
	public LinkedList<V> put(K key, Collection<V> values) {
		for (V value : values) {
			put(key, value);
		}
		return map.get(key);
	}

	public List<V> putIfAbsent(K key, Collection<V> values) {
		LinkedList<V> cur = get(key);
		if (cur != null) return cur;
		put(key, values);
		return null;
	}

	public void forEach(BiConsumer<? super K, ? super List<V>> action) {
		map.forEach(action);
	}

	public boolean containsKey(K key) {
		return map.containsKey(key);
	}

	public boolean containsValue(V value) {
		return map.containsValue(value);
	}

	public boolean containsEntry(K key, V value) {
		List<V> values = get(key);
		if (values == null) return false;
		return values.contains(value);
	}

	public Set<Entry<K, LinkedList<V>>> entrySet() {
		return map.entrySet();
	}

	public Set<K> keySet() {
		return map.keySet();
	}

	public List<K> keyList() {
		return new ArrayList<>(map.keySet());
	}

	public LinkedList<K> keys() {
		return new LinkedList<>(map.keySet());
	}

	/** Returns a list of the value lists held in this HashList. */
	public List<List<V>> values() {
		return new ArrayList<>(map.values());
	}

	/** Returns a list of all values held in this HashList. */
	public List<V> valuesAll() {
		List<V> values = new ArrayList<>();
		for (List<V> subList : map.values()) {
			values.addAll(subList);
		}
		return values;
	}

	/** Remove all values associated with the given key. */
	public List<V> remove(K key) {
		return map.remove(key);
	}

	public boolean remove(K key, V value) {
		List<V> values = get(key);
		if (values == null) return false;
		return values.remove(value);
	}

	public void enforceUniqueValues(boolean unique) {
		this.unique = unique;
	}

	public boolean enforcingUniqueValues() {
		return unique;
	}

	public void sort(Comparator<V> comp) {
		for (K key : map.keySet()) {
			map.get(key).sort(comp);
		}
	}

	public void clear(K key) {
		List<V> values = map.get(key);
		if (values != null) values.clear();
	}

	public void clear() {
		map.clear();
	}

	public int size() {
		return map.size();
	}

	/** Returns the size (total number of held values) of this HashList. */
	public int sizeValues() {
		int cnt = 0;
		for (List<V> values : map.values()) {
			cnt += values.size();
		}
		return cnt;
	}

	public boolean isEmpty() {
		return map.isEmpty();
	}

	@Override
	public Object clone() {
		return map.clone();
	}

	@Override
	public int hashCode() {
		return Objects.hash(map, unique);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) return true;
		if (!(obj instanceof HashList)) return false;
		HashList<?, ?> other = (HashList<?, ?>) obj;
		return unique == other.unique && Objects.equals(map, other.map);
	}

	@Override
	public String toString() {
		return map.toString();
	}
}
