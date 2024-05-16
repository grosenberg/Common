/*******************************************************************************
 * Copyright (c) 2016 - 2024 Certiv Analytics and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package net.certiv.common.grid.sparse;

import java.util.Arrays;
import java.util.Collection;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.SortedMap;
import java.util.TreeSet;
import java.util.concurrent.ConcurrentSkipListMap;
import java.util.stream.Collectors;

import net.certiv.common.ex.IllegalArgsEx;
import net.certiv.common.stores.UniqueList;
import net.certiv.common.stores.range.PositionRange;
import net.certiv.common.stores.sparse.SparseMap;
import net.certiv.common.stores.sparse.SparseStore;

/**
 * A sparse, scalable, concurrent, ordered data grid, intended for 2D, table-like
 * operations. Supports non-overlapping grid cells that may span multiple, contiguous
 * columns and/or rows.
 * <p>
 * Implemented on a {@link SparseStore}. Instances are not safe for use by multiple
 * threads.
 * <p>
 * The grid is sorted according to the {@link Comparable natural ordering} of its keys, or
 * by a {@link Comparator} provided at map creation time, depending on the constructor
 * used for grid creation.
 * <p>
 * The grid does not allow {@code null} keys; all keys must implement {@link Region}; keys
 * must implement {@link Comparable} if a separate {@link Comparator} is not provided.
 *
 * @param <K> key type; must implement {@link Region}
 * @param <V> value type
 * @see PositionRange
 */
public class SparseGrid<K extends Region, V> extends SparseMap<K, V> {

	protected static final String WHERE = "Where";

	public enum Where {

		/** Existing region matches the key region. */
		MATCH,
		/** Existing region is within the key region. */
		WITHIN,
		/** Existing region contains the key region. */
		CONTAINS,
		/** Existing region intersects the key region. */
		INTERSECTS,
		/** Existing region X range intercepts that of the key region. */
		X_INTERCEPTS,
		/** Existing region Y range intercepts that of the key region. */
		Y_INTERCEPTS;
	}

	// --------------------------------

	/**
	 * Constructs a new, empty sparse grid, using the natural key ordering.
	 * <p>
	 * All inserted keys must implement the {@link Comparable} interface.
	 */
	public SparseGrid() {
		super(new ConcurrentSkipListMap<>());
	}

	/**
	 * Constructs a new, empty sparse grid, ordered according to the given comparator.
	 *
	 * @param comp grid order comparator; {@code null} to specify a natural key ordering
	 */
	public SparseGrid(Comparator<? super K> comp) {
		super(new ConcurrentSkipListMap<>(comp));
	}

	/**
	 * Constructs a new sparse grid, containing the same mappings as the given map,
	 * ordered according to the natural ordering of its keys.
	 *
	 * @param map map whose mappings will be placed in this grid
	 * @throws NullPointerException if the given map is {@code null}
	 */
	public SparseGrid(Map<K, ? extends V> map) {
		super(new ConcurrentSkipListMap<>(map));
	}

	/**
	 * Constructs a new sparse grid containing the same mappings and using the same
	 * ordering as the given map.
	 *
	 * @param map sorted map whose mappings will be placed in this grid, and whose
	 *            comparator is to be used to sort this grid
	 * @throws NullPointerException if the given map is {@code null}
	 */
	public SparseGrid(SortedMap<K, ? extends V> map) {
		super(new ConcurrentSkipListMap<>(map));
	}

	// --------------------------------

	/**
	 * Returns whether, relative to the given key, a key exists in the grid that meets any
	 * of the given conditions.
	 *
	 * @param key   reference key
	 * @param conds where conditions
	 * @return {@code true} if a key exists
	 */
	public boolean has(K key, Where... conds) {
		chkArg(key, KEY);

		for (Where cond : chkConds(conds)) {
			switch (cond) {
				case MATCH:
					if (!getMatches(key).isEmpty()) return true;
					break;

				case WITHIN:
					if (!getWithin(key).isEmpty()) return true;
					break;

				case CONTAINS:
					if (!getContains(key).isEmpty()) return true;
					break;

				case INTERSECTS:
					if (!getIntersecting(key).isEmpty()) return true;
					break;

				case X_INTERCEPTS:
					if (!getXIntercepting(key).isEmpty()) return true;
					break;

				case Y_INTERCEPTS:
					if (!getYIntercepting(key).isEmpty()) return true;
					break;
			}

		}
		return false;
	}

	public List<K> getKeys(K key, Where... conds) {
		chkArg(key, KEY);

		List<K> results = new UniqueList<>();
		for (Where cond : chkConds(conds)) {
			switch (cond) {
				default:
				case MATCH:
					results.addAll(getMatches(key));
					break;

				case WITHIN:
					results.addAll(getWithin(key));
					break;

				case CONTAINS:
					results.addAll(getContains(key));
					break;

				case INTERSECTS:
					results.addAll(getIntersecting(key));
					break;

				case X_INTERCEPTS:
					results.addAll(getXIntercepting(key));
					break;

				case Y_INTERCEPTS:
					results.addAll(getYIntercepting(key));
					break;
			}
		}
		return results;
	}

	public List<Entry<K, V>> getAll(K key, Where... conds) {
		chkArg(key, KEY);

		List<Entry<K, V>> results = new UniqueList<>();
		for (Where cond : chkConds(conds)) {
			switch (cond) {
				default:
				case MATCH:
					results.addAll(entries(getMatches(key)));
					break;

				case WITHIN:
					results.addAll(entries(getWithin(key)));
					break;

				case CONTAINS:
					results.addAll(entries(getContains(key)));
					break;

				case INTERSECTS:
					results.addAll(entries(getIntersecting(key)));
					break;

				case X_INTERCEPTS:
					results.addAll(entries(getXIntercepting(key)));
					break;

				case Y_INTERCEPTS:
					results.addAll(entries(getYIntercepting(key)));
					break;
			}
		}

		return results;
	}

	/**
	 * Installs the given value with the given key in this grid in replacement of any
	 * existing entries whose keys intersect the given key. Returns the replaced entries.
	 *
	 * @param key   grid key
	 * @param value grid value
	 * @return entries intersected by the given key
	 * @throws NullPointerException if the given key is {@code null}
	 */
	public LinkedList<Entry<K, V>> put(K key, V value) {
		return putAll(Map.of(key, value));
	}

	/**
	 * Installs all entries from the given map into this store. Returns those existing
	 * entries that would be intersected by any of the new entry keys.
	 *
	 * @param map mappings to be installed into this store
	 * @return all prior entries replaced in the store; {@code empty} if none preexisted
	 * @throws IllegalArgumentException if the given map is {@code null}
	 */
	public LinkedList<Entry<K, V>> putAll(Map<? extends K, ? extends V> map) {
		return putAll(map, Where.INTERSECTS);
	}

	/**
	 * Installs all entries from the given map into this store after removing those
	 * entries whose key meet any of the given conditions relative to the keys of the
	 * given map keys. Returns any removed entries.
	 *
	 * @param map   mappings to be installed into this store
	 * @param conds where conditions
	 * @return any prior entries replaced in the store
	 * @throws IllegalArgumentException if the given map is {@code null}
	 */
	public LinkedList<Entry<K, V>> putAll(Map<? extends K, ? extends V> map, Where... conds) {
		chkArg(map, MAP);

		lock.lock();
		try {
			LinkedList<Entry<K, V>> priors = removeAll(map.keySet(), conds);
			for (Entry<? extends K, ? extends V> entry : map.entrySet()) {
				if (entry != null) {
					K key = entry.getKey();
					if (key != null) store.put(key, entry.getValue());
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
	 * <p>
	 * This is an exact key match operation.
	 *
	 * @param key reference key
	 * @return prior existing entry for the given key, or {@code null} if not present
	 * @throws IllegalArgumentException if the given key is {@code null}
	 */
	public LinkedList<Entry<K, V>> remove(K key) {
		chkArg(key, KEY);
		return removeAll(List.of(key), Where.INTERSECTS);
	}

	/**
	 * Removes from this store all existing entries whose key meets any of the given
	 * conditions relative to the given key. Returns any removed entries.
	 *
	 * @param key   reference key
	 * @param conds where conditions
	 * @return {@code true} if a key exists
	 */
	public LinkedList<Entry<K, V>> remove(K key, Where... conds) {
		chkArg(key, KEY);
		return removeAll(List.of(key), conds);
	}

	/**
	 * Removes from this store all existing entries whose key intersects with any of the
	 * given keys. Returns any removed entries.
	 *
	 * @param keys reference keys
	 * @return removed entries
	 * @throws IllegalArgumentException if the given key list is {@code null}
	 */
	public LinkedList<Entry<K, V>> removeAll(Collection<? extends K> keys) {
		return removeAll(keys, Where.INTERSECTS);
	}

	/**
	 * Removes from this store all existing entries whose key meet any of the given
	 * conditions relative to any of the given keys. Returns any removed entries.
	 *
	 * @param keys  reference keys
	 * @param conds where conditions
	 * @return {@code true} if a key exists
	 */
	public LinkedList<Entry<K, V>> removeAll(Collection<? extends K> keys, Where... conds) {
		chkArg(keys, KEYS);

		lock.lock();
		try {
			LinkedList<Entry<K, V>> results = new UniqueList<>();
			for (K key : keys) {
				if (key != null) {
					for (Where cond : chkConds(conds)) {
						switch (cond) {
							default:
							case MATCH:
								results.addAll(rmAll(getMatches(key)));
								break;

							case WITHIN:
								results.addAll(rmAll(getWithin(key)));
								break;

							case CONTAINS:
								results.addAll(rmAll(getContains(key)));
								break;

							case INTERSECTS:
								results.addAll(rmAll(getIntersecting(key)));
								break;

							case X_INTERCEPTS:
								results.addAll(rmAll(getXIntercepting(key)));
								break;

							case Y_INTERCEPTS:
								results.addAll(rmAll(getYIntercepting(key)));
								break;
						}
					}
				}
			}
			return results;

		} finally {
			lock.unlock();
		}
	}

	private Collection<Entry<K, V>> rmAll(List<K> keys) {
		LinkedList<Entry<K, V>> priors = new UniqueList<>();
		for (K key : keys) {
			if (key != null && store.containsKey(key)) {
				priors.add(Map.entry(key, store.remove(key)));
			}
		}
		return priors;
	}

	protected final TreeSet<Where> chkConds(Where[] arg) {
		try {
			TreeSet<Where> conds = new TreeSet<>(Arrays.asList(arg));
			if (conds.isEmpty()) conds.add(Where.MATCH);
			return conds;
		} catch (Exception e) {
			throw IllegalArgsEx.of(ERR_ARG).formatted(WHERE);
		}
	}

	/**
	 * Returns the existing keys that exactly matches the given key. The keys returned
	 * will be mutually unique, though in unspecified order.
	 * <p>
	 * Only one such key, at most, should exist.
	 *
	 * @param key reference key
	 * @return existing keys that exactly matches the given key
	 */
	public List<K> getMatches(K key) {
		return store.containsKey(key) ? List.of(key) : List.of();
	}

	/**
	 * Returns the keys that exist within the region defined by the given key. The keys
	 * returned will be mutually unique, though in unspecified order.
	 *
	 * @param key reference key
	 * @return existing keys within the given key
	 */
	public List<K> getWithin(K key) {
		return higher(key, true, k -> k.within(key), k -> k.xMin() > key.xMax());
	}

	/**
	 * Returns the existing keys that contain the given key. The keys returned will be
	 * mutually unique, though in unspecified order.
	 * <p>
	 * Only one such key, at most, should exist, since the existing keys should not
	 * intersect.
	 *
	 * @return existing keys that contain the given key
	 */
	public List<K> getContains(K key) {
		lock.lock();
		try {
			List<K> xkeys = lower(key, true, k -> k.contains(key), k -> k.xMax() < key.xMin());
			xkeys.addAll(higher(key, true, k -> k.contains(key), k -> k.xMin() > key.xMax()));
			return xkeys;

		} finally {
			lock.unlock();
		}
	}

	// /**
	// * Returns the existing keys that intersect the given key. The keys returned will be
	// * mutually unique, though in unspecified order.
	// *
	// * @param key reference key
	// * @return existing keys that intesect the given key
	// */
	// public List<K> getIntersecting(K key) {
	// List<K> keys = lower(key, true, k -> k.intersects(key), k ->
	// k.min().ahead(key.max()));
	// keys.addAll(higher(key, true, k -> k.intersects(key), k ->
	// k.max().behind(key.min())));
	// return keys;
	// }

	/**
	 * Returns a list of any existing keys that intersect the given key. The keys returned
	 * will be mutually unique. Key order is is unspecified.
	 * <p>
	 * Impl: searches lower, then higher in the grid relative to the given key for
	 * existing keys that intersect the given key. Each search direction terminates when
	 * there are no more keys, or early terminates when no further keys can intersect.
	 * This latter condition is detected by progresively accruing applicable X intercepts
	 * and checking whether the X intercept coverage is complete.
	 *
	 * @param key reference key
	 * @return existing keys that intesect the given key
	 */
	public List<K> getIntersecting(K key) {
		lock.lock();
		try {
			key.clearXIntercepts();
			List<K> xkeys = lower(key, true, k -> ifLower(key, k), k -> key.coveredX());
			key.clearXIntercepts();

			xkeys.addAll(higher(key, true, k -> ifHigher(key, k), k -> key.coveredX()));
			key.clearXIntercepts();
			return xkeys;

		} finally {
			lock.unlock();
		}
	}

	/** Accumulate lower X intercepts; return intersects. */
	private boolean ifLower(K key, K other) {
		int keyMin = key.yMin();
		key.interceptsX(other, true, k -> k.y().contains(keyMin) || k.y().before(keyMin));
		return key.intersects(other);
	}

	/** Accumulate higher X intercepts; // return intersects. */
	private boolean ifHigher(K key, K other) {
		int keyMax = key.yMax();
		key.interceptsX(other, true, k -> k.y().contains(keyMax) || k.y().after(keyMax));
		return key.intersects(other);
	}

	/**
	 * Returns the existing keys that intersect the X range of the given key. The keys
	 * returned will be mutually unique, though in unspecified order.
	 *
	 * @param key reference key
	 * @return existing keys that intesect the X range of the given key
	 */
	public List<K> getXIntercepting(K key) {
		if (key == null) return UniqueList.of();
		return stream().filter(k -> k.interceptsX(key)).collect(Collectors.toCollection(UniqueList::new));
	}

	/**
	 * Returns the existing keys that intersect the Y range of the given key. The keys
	 * returned will be mutually unique, though in unspecified order.
	 *
	 * @param key reference key
	 * @return existing keys that intesect the Y range of the given key
	 */
	public List<K> getYIntercepting(K key) {
		if (key == null) return UniqueList.of();
		return stream().filter(k -> k.interceptsY(key)).collect(Collectors.toCollection(UniqueList::new));
	}
}
