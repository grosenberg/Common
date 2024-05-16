/*******************************************************************************
 * Copyright (c) 2016 - 2017 Certiv Analytics and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package net.certiv.common.stores;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.Map.Entry;
import java.util.Set;
import java.util.TreeMap;

import net.certiv.common.stores.sparse.SparseStore;

/**
 * @param <V>
 * @deprecated use {@link SparseStore}
 */
@Deprecated
public class NearMap<V> {

	private final TreeMap<Integer, V> map = new TreeMap<>();

	public NearMap() {}

	public V put(Integer key, V value) {
		if (key == null) return null;
		return map.put(key, value);
	}

	public V get(int key) {
		return map.get(key);
	}

	/**
	 * Returns the value associated with the greatest key less than or equal to the given
	 * key, or {@code null} if there is no such key.
	 *
	 * @param key the key
	 * @return the value associated with the greatest key less than or equal to
	 *         {@code key}, or {@code null} if there is no such key
	 * @throws ClassCastException   if the specified key cannot be compared with the keys
	 *                              currently in the map
	 * @throws NullPointerException if the specified key is null and this map does not
	 *                              permit null keys
	 */
	public V getFloor(int key) {
		Entry<Integer, V> entry = map.floorEntry(key);
		if (entry == null) return null;
		return entry.getValue();
	}

	public Integer lowerKey(Integer key) {
		return map.lowerKey(key);
	}

	public Integer higherKey(Integer key) {
		return map.higherKey(key);
	}

	/**
	 * Returns the key value pair for the first of the given terminal values found where
	 * the key is the greatest key strictly less than the given key, or null if there is
	 * no such pair exists.
	 */
	@SuppressWarnings("unchecked")
	public Pair<Integer, V> before(int key, V... terminals) {
		Set<V> tvalues = new HashSet<>(Arrays.asList(terminals));
		int idx = key;
		Entry<Integer, V> entry = map.lowerEntry(idx);
		while (entry != null && !tvalues.contains(entry.getValue())) {
			idx = entry.getKey();
			entry = map.lowerEntry(idx);
		}
		return entry != null ? Pair.of(idx, entry.getValue()) : null;
	}

	/**
	 * Returns the key value pair for the first of the given terminal values found where
	 * the key is the lowest key strictly greater than the given key, or null if there is
	 * no such pair exists.
	 */
	@SuppressWarnings("unchecked")
	public Pair<Integer, V> after(int key, V... terminals) {
		Set<V> tvalues = new HashSet<>(Arrays.asList(terminals));
		int idx = key;
		Entry<Integer, V> entry = map.higherEntry(idx);
		while (entry != null && !tvalues.contains(entry.getValue())) {
			idx = entry.getKey();
			entry = map.higherEntry(idx);
		}
		return entry != null ? Pair.of(idx, entry.getValue()) : null;
	}

	public Integer firstKey() {
		return map.firstKey();
	}

	public Integer lastKey() {
		return map.lastKey();
	}

	public Set<Integer> keySet() {
		return map.keySet();
	}

	public Set<Entry<Integer, V>> entrySet() {
		return map.entrySet();
	}

	public Collection<V> values() {
		return map.values();
	}

	/**
	 * Returns {@code true} if this map contains a floor entry identified by the given key
	 * whose value also contains the key.
	 *
	 * @throws IllegalArgumentException if the value does not implement {@code IExtent}
	 */
	@SuppressWarnings("unchecked")
	public boolean contains(Integer key) {
		Entry<Integer, V> entry = map.floorEntry(key);
		if (entry == null) return false;

		V value = entry.getValue();
		if (value instanceof IExtent<?>) {
			return ((IExtent<Integer>) value).contains(key);
		}
		throw new IllegalArgumentException("Value does not implement IExtent");
	}

	public boolean containsKey(Integer key) {
		return map.containsKey(key);
	}

	public boolean containsValue(V value) {
		return map.containsValue(value);
	}

	public boolean isEmpty() {
		return map.isEmpty();
	}

	public int size() {
		return map.size();
	}

	public void clear() {
		map.clear();
	}

	@Override
	public String toString() {
		return map.toString();
	}
}
