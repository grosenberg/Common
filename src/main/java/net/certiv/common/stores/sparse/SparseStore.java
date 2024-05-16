/*******************************************************************************
 * Copyright (c) 2016 - 2024 Certiv Analytics and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package net.certiv.common.stores.sparse;

import java.util.Collection;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.SortedMap;
import java.util.concurrent.ConcurrentSkipListMap;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import net.certiv.common.stores.UniqueList;

/**
 * A sparse, scalable, concurrent, ordered data store.
 * <p>
 * Implemented on a {@link ConcurrentSkipListMap}.
 * <p>
 * The store is sorted according to the {@link Comparable natural ordering} of its keys,
 * or by a {@link Comparator} provided at map creation time, depending on the constructor
 * used for store creation.
 * <p>
 * The store does not allow {@code null} keys; keys must implement {@link Comparable} if a
 * separate {@link Comparator} is not provided.
 *
 * @param <K> key type
 * @param <V> value type
 */
public class SparseStore<K, V> extends SparseMap<K, V> {

	/**
	 * Constructs a new, empty sparse store, using the natural key ordering.
	 * <p>
	 * All inserted keys must implement the {@link Comparable} interface.
	 */
	public SparseStore() {
		super(new ConcurrentSkipListMap<>());
	}

	/**
	 * Constructs a new, empty sparse store, ordered according to the given comparator.
	 *
	 * @param comp store order comparator; {@code null} to specify a natural key ordering
	 */
	public SparseStore(Comparator<? super K> comp) {
		super(new ConcurrentSkipListMap<>(comp));
	}

	/**
	 * Constructs a new sparse store, containing the same mappings as the given map,
	 * ordered according to the natural ordering of its keys.
	 *
	 * @param map map whose mappings will be placed in this store
	 * @throws ClassCastException   if the keys in the given map are not
	 *                              {@link Comparable}, or are not mutually comparable
	 * @throws NullPointerException if the given map is {@code null}
	 */
	public SparseStore(Map<K, V> map) {
		super(new ConcurrentSkipListMap<>(map));
	}

	/**
	 * Constructs a new sparse store containing the same mappings and using the same
	 * ordering as the given map.
	 *
	 * @param map sorted map whose mappings will be placed in this store, and whose
	 *            comparator is to be used to sort this store
	 * @throws NullPointerException if the given map is {@code null}
	 */
	public SparseStore(SortedMap<K, ? extends V> map) {
		super(new ConcurrentSkipListMap<>(map));
	}

	/**
	 * Returns {@code true} if this store contains an entry for the exact key.
	 * <p>
	 * This is an exact key match operation.
	 *
	 * @param key reference key
	 * @return {@code true} if this store contains an entry for the given key
	 * @throws IllegalArgumentException if the given key is {@code null}
	 */
	public final boolean containsKey(K key) {
		chkArg(key, KEY);
		return store.containsKey(key);
	}

	/**
	 * Returns {@code true} if this store contains any entry containing the given value.
	 *
	 * @param value reference value
	 * @return {@code true} if any entry containing the given value exists in the store
	 */
	public final boolean containsValue(V value) {
		return store.containsValue(value);
	}

	/**
	 * Returns the value stored against the given key, or {@code null} if the store does
	 * not contain the key.
	 *
	 * @param key store key
	 * @return value for the given key, or {@code null} if not found
	 * @throws IllegalArgumentException if the given key is {@code null}
	 */
	public V getValue(K key) {
		chkArg(key, KEY);
		return store.get(key);		// has prior
	}

	/**
	 * Returns an {@link Entry} containing the key and value stored against the given key,
	 * or {@code null} if the store does not contain the key.
	 *
	 * @param key store key
	 * @return entry for the given key, or {@code null} if not found
	 * @throws IllegalArgumentException if the given key is {@code null}
	 */
	public Entry<K, V> get(K key) {
		chkArg(key, KEY);
		if (!store.containsKey(key)) return null;	// no prior
		return Map.entry(key, store.get(key));		// has prior
	}

	/**
	 * Returns an {@link Entry} containing the key and value stored against the given key,
	 * or containing the key and given default value if the store does not contain the
	 * key.
	 *
	 * @param key store key
	 * @return entry for the given key, or given key and default value if not found
	 * @throws IllegalArgumentException if the given key is {@code null}
	 */
	public Entry<K, V> getOrDefault(K key, V def) {
		Entry<K, V> entry = get(key);
		return entry != null ? entry : Map.entry(key, def);
	}

	/**
	 * Returns an {@link Entry} containing the key and value stored against the given key,
	 * subject to the given where predicate condition, or {@code null} if not found.
	 *
	 * @param key  store key
	 * @param cond predicate condition
	 * @return entry for the given key, or {@code null} if not found
	 * @throws IllegalArgumentException if the given key is {@code null}
	 */
	public Entry<K, V> get(K key, Predicate<Entry<? super K, ? super V>> cond) {
		Entry<K, V> entry = get(key);
		if (cond == null) return entry;
		if (entry != null && cond.test(entry)) return entry;
		return null;
	}

	/**
	 * Returns the existing entries in the store corresponding to the given keys.
	 *
	 * @param keys store keys
	 * @return all unique list of corresponding entries
	 * @throws IllegalArgumentException if the given key list is {@code null}
	 */
	public LinkedList<Entry<K, V>> getAll(List<K> keys) {
		chkArg(keys, KEYS);
		LinkedList<Entry<K, V>> entries = new UniqueList<>();
		for (K key : keys) {
			Entry<K, V> entry = get(key);
			if (entry != null) {
				entries.add(Map.entry(key, store.get(key)));
			}
		}
		return entries;
	}

	/**
	 * Returns an {@link Entry} list containing the key and value stored against the given
	 * keys, subject to the given where predicate condition, or {@code null} if not found.
	 *
	 * @param keys store keys
	 * @param cond predicate condition
	 * @return entry for the given key, or {@code null} if not found
	 * @throws IllegalArgumentException if the given key is {@code null}
	 */
	public LinkedList<Entry<K, V>> getAll(List<K> keys, Predicate<Entry<? super K, ? super V>> cond) {
		LinkedList<Entry<K, V>> entries = getAll(keys);
		if (cond == null) return entries;
		return entries.stream().filter(cond).collect(Collectors.toCollection(UniqueList::new));
	}

	/**
	 * Installs the given key and value into this store. Returns any prior existing entry
	 * whose key matches the given key. Returns {@code null} if no such entry preexisted
	 * in the store.
	 *
	 * @param key   reference key
	 * @param value corresponding value
	 * @return prior existing entry for the given key, or {@code null} if none preexisted
	 * @throws IllegalArgumentException if the given key is {@code null}
	 */
	public Entry<K, V> put(K key, V value) {
		chkArg(key, KEY);
		lock.lock();
		try {
			if (store.containsKey(key)) {
				return Map.entry(key, store.put(key, value));
			}

			store.put(key, value);
			return null;

		} finally {
			lock.unlock();
		}
	}

	/**
	 * Installs the given key/value entry into this store.
	 *
	 * @param entry store key/value
	 * @return prior entry for the given key, if any; {@code null} if none preexisted
	 * @throws IllegalArgumentException if the given entry or entry key is {@code null}
	 */
	public Entry<K, V> put(Entry<K, V> entry) {
		chkArg(entry, ENTRY);
		K key = entry.getKey();
		if (store.containsKey(key)) {
			return Map.entry(key, store.put(key, entry.getValue()));
		}
		store.put(key, entry.getValue());
		return null;
	}

	/**
	 * Installs all entries from the given map into this store. Returns a list of any
	 * prior existing entries whose key matches any of the given entry keys. The list will
	 * be empty if none of the given entry keys preexisted in the store.
	 *
	 * @param map mappings to be installed into this store
	 * @return all prior entries replaced in the store; {@code empty} if none preexisted
	 * @throws IllegalArgumentException if the given map is {@code null}
	 */
	public final LinkedList<Entry<K, V>> putAll(Map<? extends K, ? extends V> map) {
		chkArg(map, MAP);

		lock.lock();
		try {
			LinkedList<Entry<K, V>> priors = new UniqueList<>();
			for (Entry<? extends K, ? extends V> entry : map.entrySet()) {
				if (entry != null) {
					K key = entry.getKey();
					if (key != null) {
						if (store.containsKey(key)) {
							priors.add(Map.entry(key, store.put(key, entry.getValue())));
						} else {
							store.put(key, entry.getValue());
						}
					}
				}
			}
			return priors;

		} finally {
			lock.unlock();
		}
	}

	/**
	 * Removes the entry stored against the given key. Returns an {@link Entry} containing
	 * any prior existing key and value, or {@code null} if the store did not contain the
	 * given key.
	 *
	 * @param key reference key
	 * @return prior existing entry for the given key, or {@code null} if not present
	 * @throws IllegalArgumentException if the given key is {@code null}
	 */
	public final Entry<K, V> remove(K key) {
		if (!containsKey(key)) return null;			// no prior
		return Map.entry(key, store.remove(key));	// has prior
	}

	/**
	 * Removes the entries for the given keys. Returns a list of the prior existing
	 * entries whose keys match any of the given entry keys. The list will be empty if
	 * none of the given entry keys preexisted in the store.
	 *
	 * @param keys reference keys
	 * @return list of prior existing entries
	 */
	public final LinkedList<Entry<K, V>> removeAll(Collection<? extends K> keys) {
		chkArg(keys, KEYS);
		LinkedList<Entry<K, V>> priors = new UniqueList<>();
		lock.lock();
		try {
			for (K key : keys) {
				if (key != null && store.containsKey(key)) {
					priors.add(Map.entry(key, store.remove(key)));
				}
			}
			return priors;

		} finally {
			lock.unlock();
		}
	}
}
