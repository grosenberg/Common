/*******************************************************************************
 * Copyright (c) 2017, 2018 Certiv Analytics. All rights reserved.
 * Use of this file is governed by the Eclipse Public License v1.0
 * that can be found in the LICENSE.txt file in the project root,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package net.certiv.tools.util.stores;

import java.util.Collection;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.Set;
import java.util.TreeSet;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Function;

/**
 * Multimap implemented as a LinkedHashMap with TreeSet implemented instance
 * values.
 *
 * @param <K> the key type
 * @param <V> the tree set value type
 */
public class HashTree<K, V> {

	private final LinkedHashMap<K, TreeSet<V>> map;
	private Comparator<V> comp;

	public HashTree() {
		map = new LinkedHashMap<>();
	}

	public HashTree(Map<K, TreeSet<V>> keyMap) {
		this();
		this.map.putAll(keyMap);
	}

	public void setComparator(Comparator<V> comp) {
		this.comp = comp;
	}

	public TreeSet<V> get(K key) {
		return map.get(key);
	}

	public TreeSet<V> getOrDefault(Object key, TreeSet<V> defaultValue) {
		return map.getOrDefault(key, defaultValue);
	}

	public boolean put(K key, V value) {
		TreeSet<V> values = get(key);
		if (values == null) {
			values = new TreeSet<>(comp);
			map.put(key, values);
		}
		return values.add(value);
	}

	public TreeSet<V> put(K key, TreeSet<V> value) {
		return map.put(key, value);
	}

	public TreeSet<V> set(K key, Collection<V> values) {
		TreeSet<V> list = new TreeSet<>(comp);
		list.addAll(values);
		return map.put(key, list);
	}

	public boolean putAll(K key, Collection<V> values) {
		TreeSet<V> list = map.get(key);
		if (list == null) {
			list = new TreeSet<>();
			map.put(key, list);
		}
		return list.addAll(values);
	}

	public void putAll(Map<? extends K, ? extends TreeSet<V>> m) {
		map.putAll(m);
	}

	public TreeSet<V> putIfAbsent(K key, TreeSet<V> value) {
		return map.putIfAbsent(key, value);
	}

	public TreeSet<V> remove(K key) {
		return map.remove(key);
	}

	public boolean remove(K key, V value) {
		return map.remove(key, value);
	}

	public boolean replace(K key, TreeSet<V> oldValue, TreeSet<V> newValue) {
		return map.replace(key, oldValue, newValue);
	}

	public TreeSet<V> replace(K key, TreeSet<V> value) {
		return map.replace(key, value);
	}

	public TreeSet<V> computeIfAbsent(K key, Function<? super K, ? extends TreeSet<V>> mappingFunction) {
		return map.computeIfAbsent(key, mappingFunction);
	}

	public TreeSet<V> computeIfPresent(K key,
			BiFunction<? super K, ? super TreeSet<V>, ? extends TreeSet<V>> remappingFunction) {
		return map.computeIfPresent(key, remappingFunction);
	}

	public TreeSet<V> compute(K key,
			BiFunction<? super K, ? super TreeSet<V>, ? extends TreeSet<V>> remappingFunction) {
		return map.compute(key, remappingFunction);
	}

	public TreeSet<V> merge(K key, TreeSet<V> value,
			BiFunction<? super TreeSet<V>, ? super TreeSet<V>, ? extends TreeSet<V>> remappingFunction) {
		return map.merge(key, value, remappingFunction);
	}

	public void forEach(BiConsumer<? super K, ? super TreeSet<V>> action) {
		map.forEach(action);
	}

	public void replaceAll(BiFunction<? super K, ? super TreeSet<V>, ? extends TreeSet<V>> function) {
		map.replaceAll(function);
	}

	public boolean containsKey(Object key) {
		return map.containsKey(key);
	}

	public boolean containsValue(Object value) {
		return map.containsValue(value);
	}

	public boolean containsEntry(K key, V value) {
		TreeSet<V> values = get(key);
		if (values == null) return false;
		return values.contains(value);
	}

	public Set<Entry<K, TreeSet<V>>> entrySet() {
		return map.entrySet();
	}

	public Set<K> keySet() {
		return map.keySet();
	}

	public TreeSet<K> keyTreeSet() {
		return new TreeSet<>(map.keySet());
	}

	public Collection<TreeSet<V>> values() {
		return map.values();
	}

	public TreeSet<V> valuesAll() {
		TreeSet<V> values = new TreeSet<>(comp);
		for (TreeSet<V> coll : map.values()) {
			values.addAll(coll);
		}
		return values;
	}

	public void clear(K key) {
		TreeSet<V> values = map.get(key);
		if (values != null) values.clear();
	}

	public void clear() {
		map.clear();
	}

	public int size() {
		return map.size();
	}

	public int sizeAll() {
		int cnt = 0;
		for (TreeSet<V> values : map.values()) {
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
		return Objects.hash(map);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) return true;
		if (!(obj instanceof HashTree)) return false;
		HashTree<?, ?> other = (HashTree<?, ?>) obj;
		return Objects.equals(map, other.map);
	}

	@Override
	public String toString() {
		return map.toString();
	}
}
