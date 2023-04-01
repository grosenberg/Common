/*******************************************************************************
 * Copyright (c) 2017, 2023 Certiv Analytics. All rights reserved.
 * Use of this file is governed by the Eclipse Public License v1.0
 * that can be found in the LICENSE.txt file in the project root,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package net.certiv.common.stores;

import java.util.Collection;
import java.util.Comparator;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.Set;
import java.util.function.BiConsumer;

/**
 * Multimap store supporing 1:N relations. Implemented as a LinkedHashMap with LinkedList
 * implemented instance values. Supports restriction of the value list to unique values on
 * a per-key basis.
 *
 * @param <K> the key type
 * @param <V> the list value type
 */
public class LinkedHashList<K, V> {

	private final LinkedHashMap<K, LinkedList<V>> map = new LinkedHashMap<>();
	private boolean unique = false;

	public LinkedHashList() {}

	public LinkedHashList(LinkedHashList<K, V> data) {
		for (K key : data.keys()) {
			put(key, data.get(key));
		}
	}

	public LinkedHashList(Map<K, List<V>> data) {
		for (Entry<K, List<V>> entry : data.entrySet()) {
			put(entry.getKey(), entry.getValue());
		}
	}

	/**
	 * Returns the value list mapped to the specified key, or {@code null} if this map
	 * contains no mapping for the key.
	 */
	public LinkedList<V> get(K key) {
		LinkedList<V> list = map.get(key);
		if (list == null) return null;
		return new LinkedList<>(list);
	}

	public LinkedList<V> getOrDefault(K key, Collection<V> defaults) {
		LinkedList<V> res = map.get(key);
		if (res != null) return new LinkedList<>(res);
		return new LinkedList<>(defaults);
	}

	/**
	 * Adds the given value to the list of values identified by the given key. If
	 * {@link unique} is {@code true}, the value is added only if not already present
	 * (based on object equality).
	 */
	public boolean put(K key, V value) {
		LinkedList<V> list = map.get(key);
		if (list == null) {
			list = new LinkedList<>();
			map.put(key, list);
		}
		if (unique && list.contains(value)) return false;
		return list.add(value);
	}

	/**
	 * Appends the given values to the list of values identified by the given key. If
	 * {@link unique} is set to {@code true}, each value is added only if value is not
	 * already present (based on object equality).
	 */
	public LinkedList<V> put(K key, Collection<V> values) {
		for (V value : values) {
			put(key, value);
		}
		return new LinkedList<>(map.get(key));
	}

	/**
	 * Puts the given values as the list of values identified by the given key if no such
	 * value list preexists. If {@link unique} is set to {@code true}, each value in the
	 * value list filtered to being unique (based on object equality).
	 */
	public LinkedList<V> putIfAbsent(K key, Collection<V> values) {
		LinkedList<V> list = map.get(key);
		if (list == null) return put(key, values);
		// return map.get(key);
		return null; // TODO: return result or null signalling not absent?
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
		List<V> values = map.get(key);
		if (values == null) return false;
		return values.contains(value);
	}

	public Set<Entry<K, LinkedList<V>>> entrySet() {
		return map.entrySet();
	}

	public Set<K> keySet() {
		return new HashSet<>(map.keySet());
	}

	public LinkedList<K> keys() {
		return new LinkedList<>(map.keySet());
	}

	/** Returns a list of the value lists held in this LinkedHashList. */
	public LinkedList<LinkedList<V>> values() {
		return new LinkedList<>(map.values());
	}

	/** Returns a list of all values held in this LinkedHashList. */
	public LinkedList<V> valuesAll() {
		LinkedList<V> values = new LinkedList<>();
		for (LinkedList<V> subList : map.values()) {
			values.addAll(subList);
		}
		return new LinkedList<>(values);
	}

	/** Remove all values associated with the given key. */
	public LinkedList<V> remove(K key) {
		return map.remove(key);
	}

	public boolean remove(K key, V value) {
		LinkedList<V> list = map.get(key);
		if (list == null) return false;
		boolean ok = list.remove(value);
		if (ok && list.isEmpty()) map.remove(key);
		return ok;
	}

	public boolean isUniqueValued() {
		return unique;
	}

	public void setUniqueValued(boolean unique) {
		this.unique = unique;
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

	public int sizeKeys() {
		return map.size();
	}

	/** Returns the total size (total number of held values) of this LinkedHashList. */
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
		if (!(obj instanceof LinkedHashList)) return false;
		LinkedHashList<?, ?> other = (LinkedHashList<?, ?>) obj;
		return unique == other.unique && Objects.equals(map, other.map);
	}

	@Override
	public String toString() {
		return map.toString();
	}
}
