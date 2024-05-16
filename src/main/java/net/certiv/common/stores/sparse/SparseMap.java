package net.certiv.common.stores.sparse;

import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.NavigableSet;
import java.util.Objects;
import java.util.concurrent.ConcurrentNavigableMap;
import java.util.concurrent.ConcurrentSkipListMap;
import java.util.concurrent.locks.ReentrantLock;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import net.certiv.common.ex.IllegalArgsEx;
import net.certiv.common.stores.UniqueList;

/**
 * Abstract base class for sparse, scalable, concurrent, ordered data storage.
 * <p>
 * Implemented on a {@link ConcurrentSkipListMap}. The store is sorted according to the
 * {@linkplain Comparable natural ordering} of its keys, or by a {@link Comparator}
 * provided at map creation time, depending on the constructor used for store creation.
 * <p>
 * The store does not allow {@code null} keys; keys must implement {@link Comparable} if a
 * separate {@link Comparator} is not provided.
 * <p>
 * Intended to rationalize the {@link ConcurrentSkipListMap} methods that return a
 * {@code null} value indistinguishably indicating either a valid store entry containing a
 * {@code null} value or the absence of an entry. Rather, this class will always return an
 * {@link Entry} if the corresponding key exists in the store, leaving a {@code null}
 * return to exclusively indicate that no entry key was present.
 *
 * @param <K> key type
 * @param <V> value type
 */
public abstract class SparseMap<K, V> {

	protected static final String ERR_ARG = "%s cannot be 'null'.";

	protected static final String ENTRY = "Entry";
	protected static final String KEY = "Key";
	protected static final String KEYS = "Keys";
	protected static final String KEY2 = " key";
	protected static final String MAP = "Map";

	protected final ConcurrentSkipListMap<K, V> store;
	protected final ReentrantLock lock = new ReentrantLock();

	protected SparseMap(ConcurrentSkipListMap<K, V> store) {
		this.store = store;
	}

	/**
	 * Returns the lowest (first) key in this store.
	 *
	 * @return lowest key
	 */
	public final K lowest() {
		return store.firstKey();
	}

	/**
	 * Searches, starting with the given key, for lower keys, relative to the given key,
	 * located in the store that meet the criteria given by the given include predicate.
	 * If inclusive, the search starts, relative to the given key, with the floor key;
	 * otherwise with the lower key.
	 * <p>
	 * {@code Lower}: the greatest key less than another, <em>i.e.</em>, immediately
	 * before.<br>
	 * {@code Floor}: the greatest key less than or equal to another, <em>i.e.</em>,
	 * existing at or immediatly before.
	 *
	 * @param key       starting key
	 * @param inclusive {@code true} to include the starting key in the search
	 * @return select head keys
	 * @throws IllegalArgumentException if the given key is {@code null}
	 */
	public final List<K> lower(K key, boolean inclusive) {
		return lower(key, inclusive, null, null);
	}

	/**
	 * Searches, starting with the given key, for lower keys, relative to the given key,
	 * located in the store that meet the criteria given by the given include predicate.
	 * If inclusive, the search starts, relative to the given key, with the floor key;
	 * otherwise with the lower key.
	 * <p>
	 * A {@code null} include predicate is equivalent to a 'match all' criteria. A
	 * {@code null} stop predicate disables early termination consideration.
	 * <p>
	 * {@code Lower}: the greatest key less than another, <em>i.e.</em>, immediately
	 * before.<br>
	 * {@code Floor}: the greatest key less than or equal to another, <em>i.e.</em>,
	 * existing at or immediatly before.
	 *
	 * @param key       starting key
	 * @param inclusive {@code true} to include the starting key in the search
	 * @param include   inclusion filter condition; may be {@code null}
	 * @return select head keys
	 * @throws IllegalArgumentException if the given key is {@code null}
	 */
	public final List<K> lower(K key, boolean inclusive, Predicate<? super K> include) {
		return lower(key, inclusive, include, null);
	}

	/**
	 * Searches, starting with the given key, for lower keys, relative to the given key,
	 * located in the store that meet the criteria given by the given include predicate.
	 * If inclusive, the search starts, relative to the given key, with the floor key;
	 * otherwise with the lower key.
	 * <p>
	 * The search terminates early on the first key that meets the given stop criteria. A
	 * key that meets both the include and stop criteria is <em>not</em> included in the
	 * search results.
	 * <p>
	 * A {@code null} include predicate is equivalent to a 'match all' criteria. A
	 * {@code null} stop predicate disables early termination consideration.
	 * <p>
	 * {@code Lower}: the greatest key less than another, <em>i.e.</em>, immediately
	 * before.<br>
	 * {@code Floor}: the greatest key less than or equal to another, <em>i.e.</em>,
	 * existing at or immediatly before.
	 *
	 * @param key       starting key
	 * @param inclusive {@code true} to include the starting key in the search
	 * @param include   inclusion filter condition; may be {@code null}
	 * @param stop      search termination condition; may be {@code null}
	 * @return select head keys
	 * @throws IllegalArgumentException if the given key is {@code null}
	 */
	public final List<K> lower(K key, boolean inclusive, Predicate<? super K> include,
			Predicate<? super K> stop) {
		return lower(key, inclusive, include, stop, true);
	}

	/**
	 * Searches, starting with the given key, for lower keys, relative to the given key,
	 * located in the store that meet the criteria given by the given include predicate.
	 * If inclusive, the search starts, relative to the given key, with the floor key;
	 * otherwise with the lower key.
	 * <p>
	 * The search terminates early on the first key that meets the given stop criteria. A
	 * key that meets both the include and stop criteria is excluded from the search
	 * results dependent on the given exclude value.
	 * <p>
	 * A {@code null} include predicate is equivalent to a 'match all' criteria. A
	 * {@code null} stop predicate disables early termination consideration.
	 * <p>
	 * {@code Lower}: the greatest key less than another, <em>i.e.</em>, immediately
	 * before.<br>
	 * {@code Floor}: the greatest key less than or equal to another, <em>i.e.</em>,
	 * existing at or immediatly before.
	 *
	 * @param key       starting key
	 * @param inclusive {@code true} to include the starting key in the search
	 * @param include   inclusion filter condition; may be {@code null}
	 * @param stop      search termination condition; may be {@code null}
	 * @param exclude   {@code true} to exclude the stop element even if it matches the
	 *                  inclusion filter
	 * @return selected head keys
	 * @throws IllegalArgumentException if the given key is {@code null}
	 */
	public final List<K> lower(K key, boolean inclusive, Predicate<? super K> include,
			Predicate<? super K> stop, boolean exclude) {
		chkArg(key, KEY);
		List<K> results = new UniqueList<>();
		for (K next = inclusive ? store.floorKey(key) : store.lowerKey(key); //
				next != null; //
				next = store.lowerKey(next)) {
			if (stop != null && exclude && stop.test(next)) break;
			if (include == null || include.test(next)) results.add(next);
			if (stop != null && !exclude && stop.test(next)) break;
		}
		return results;
	}

	/**
	 * Searches, starting with the given key, for higher keys, relative to the given key,
	 * located in the store. If inclusive, the search starts, relative to the given key,
	 * with the ceiling key; otherwise with the higher key.
	 * <p>
	 * {@code Higher}: least key greater than another, <em>i.e.</em>, existing immediatly
	 * after<br>
	 * {@code Ceiling}: least key greater than or equal to another, <em>i.e.</em>,
	 * existing at or immedidatly after
	 *
	 * @param key       starting key
	 * @param inclusive {@code true} to include the starting key in the search
	 * @return select tail keys
	 * @throws IllegalArgumentException if the given key is {@code null}
	 */
	public final List<K> higher(K key, boolean inclusive) {
		return higher(key, inclusive, null, null);
	}

	/**
	 * Searches, starting with the given key, for higher keys, relative to the given key,
	 * located in the store that meet the criteria given by the given include predicate.
	 * If inclusive, the search starts, relative to the given key, with the ceiling key;
	 * otherwise with the higher key.
	 * <p>
	 * A {@code null} include predicate is equivalent to a 'match all' criteria. A
	 * {@code null} stop predicate disables early termination consideration.
	 * <p>
	 * {@code Higher}: least key greater than another, <em>i.e.</em>, existing immediatly
	 * after<br>
	 * {@code Ceiling}: least key greater than or equal to another, <em>i.e.</em>,
	 * existing at or immedidatly after
	 *
	 * @param key       starting key
	 * @param inclusive {@code true} to include the starting key in the search
	 * @param include   inclusion filter condition; may be {@code null}
	 * @return select tail keys
	 * @throws IllegalArgumentException if the given key is {@code null}
	 */
	public final List<K> higher(K key, boolean inclusive, Predicate<? super K> include) {
		return higher(key, inclusive, include, null);
	}

	/**
	 * Searches, starting with the given key, for higher keys, relative to the given key,
	 * located in the store that meet the criteria given by the given include predicate.
	 * If inclusive, the search starts, relative to the given key, with the ceiling key;
	 * otherwise with the higher key.
	 * <p>
	 * The search terminates early on the first key that meets the given stop criteria. A
	 * key that meets both the include and stop criteria is <em>not</em> included in the
	 * search results.
	 * <p>
	 * A {@code null} include predicate is equivalent to a 'match all' criteria. A
	 * {@code null} stop predicate disables early termination consideration.
	 * <p>
	 * {@code Higher}: least key greater than another, <em>i.e.</em>, existing immediatly
	 * after<br>
	 * {@code Ceiling}: least key greater than or equal to another, <em>i.e.</em>,
	 * existing at or immedidatly after
	 *
	 * @param key       starting key
	 * @param inclusive {@code true} to include the starting key in the search
	 * @param include   inclusion filter condition; may be {@code null}
	 * @param stop      search termination condition; may be {@code null}
	 * @return select tail keys
	 * @throws IllegalArgumentException if the given key is {@code null}
	 */
	public final List<K> higher(K key, boolean inclusive, Predicate<? super K> include,
			Predicate<? super K> stop) {
		return higher(key, inclusive, include, stop, true);
	}

	/**
	 * Searches, starting with the given key, for higher keys, relative to the given key,
	 * located in the store that meet the criteria given by the given include predicate.
	 * If inclusive, the search starts, relative to the given key, with the ceiling key;
	 * otherwise with the higher key.
	 * <p>
	 * The search terminates early on the first key that meets the given stop criteria. A
	 * key that meets both the include and stop criteria is excluded from the search
	 * results dependent on the given exclude value.
	 * <p>
	 * A {@code null} include predicate is equivalent to a 'match all' criteria. A
	 * {@code null} stop predicate disables early termination consideration.
	 * <p>
	 * {@code Higher}: least key greater than another, <em>i.e.</em>, existing immediatly
	 * after<br>
	 * {@code Ceiling}: least key greater than or equal to another, <em>i.e.</em>,
	 * existing at or immedidatly after
	 *
	 * @param key       starting key
	 * @param inclusive {@code true} to include the starting key in the search
	 * @param include   inclusion filter condition; may be {@code null}
	 * @param stop      search termination condition; may be {@code null}
	 * @param exclude   {@code true} to exclude the stop element even if it matches the
	 *                  inclusion filter
	 * @return selected tail keys
	 * @throws IllegalArgumentException if the given key is {@code null}
	 */
	public final List<K> higher(K key, boolean inclusive, Predicate<? super K> include,
			Predicate<? super K> stop, boolean exclude) {
		chkArg(key, KEY);
		List<K> results = new UniqueList<>();
		for (K next = inclusive ? store.ceilingKey(key) : store.higherKey(key); //
				next != null; //
				next = store.higherKey(next)) {
			if (stop != null && exclude && stop.test(next)) break;
			if (include == null || include.test(next)) results.add(next);
			if (stop != null && !exclude && stop.test(next)) break;
		}
		return results;
	}

	/**
	 * Returns the highest (last) key in this store.
	 *
	 * @return highest key
	 */
	public final K highest() {
		return store.lastKey();
	}

	/**
	 * Returns an unmodifiable {@link NavigableSet} view of the keys contained in this
	 * store.
	 * <p>
	 * The set's iterator returns the keys in ascending order.
	 *
	 * @return a navigable set view of the keys in this store
	 */
	public final NavigableSet<K> navigableKeySet() {
		return Collections.unmodifiableNavigableSet(store.navigableKeySet());
	}

	/**
	 * Returns a sequential {@code Stream} of the keys of this map as its source.
	 *
	 * @return a sequential {@code Stream} over the keys of this map
	 */
	public final Stream<K> stream() {
		return store.navigableKeySet().stream();
	}

	/**
	 * Returns a navigable head map view of the entries in this store relative to the
	 * given key.
	 *
	 * @param key       starting key
	 * @param inclusive {@code true} to include the starting key in the map
	 * @return navigable head map view
	 * @throws IllegalArgumentException if the given key is {@code null}
	 */
	public final ConcurrentNavigableMap<K, V> headMap(K key, boolean inclusive) {
		chkArg(key, KEY);
		return store.headMap(key, inclusive);
	}

	/**
	 * Returns a navigable tail map view of the entries in this store relative to the
	 * given key.
	 *
	 * @param key       starting key
	 * @param inclusive {@code true} to include the starting key in the map
	 * @return navigable tail map view
	 * @throws IllegalArgumentException if the given key is {@code null}
	 */
	public final ConcurrentNavigableMap<K, V> tailMap(K key, boolean inclusive) {
		chkArg(key, KEY);
		return store.tailMap(key, inclusive);
	}

	/**
	 * Returns a {@link UniqueList} of selected store entries.
	 * <p>
	 * The resulting list is not backed by the store, so changes to the store are not
	 * reflected in the list, and vice-versa.
	 * <p>
	 * The order of the list will nominally reflect the order of the given keys. *
	 * <p>
	 * This is an exact key match operation.
	 *
	 * @param keys entry selection keys
	 * @return list of selected store entries
	 */
	public final UniqueList<Entry<K, V>> entries(Collection<? extends K> keys) {
		chkArg(keys, KEYS);
		return keys.stream() //
				.filter(k -> k != null) //
				.map(k -> Map.entry((K) k, store.get(k))) //
				.collect(Collectors.toCollection(UniqueList::new));
	}

	/**
	 * Returns a {@link LinkedList} of the store values.
	 * <p>
	 * The resulting list is not backed by the store, so changes to the store are not
	 * reflected in the list, and vice-versa.
	 * <p>
	 * The order of the list will nominally reflect the order of entries in the store.
	 *
	 * @return list of store values
	 */
	public final LinkedList<V> values() {
		return new LinkedList<>(store.values());
	}

	/**
	 * Sanity check for {@code null}. Throws an {@link IllegalArgumentException} on
	 * failure using the {@link #ERR_ARG} message and message qualifier.
	 *
	 * @param arg argument to check
	 * @param msg failure message qualifier
	 */
	protected final void chkArg(Object arg, String msg) {
		if (arg == null) throw IllegalArgsEx.of(ERR_ARG).formatted(msg);
		if ((arg instanceof Entry e) && e.getKey() == null)
			throw IllegalArgsEx.of(ERR_ARG).formatted(msg + KEY2);
	}

	/**
	 * Returns the number of key/value mappings in this store.
	 *
	 * @return number of key/value mappings
	 */
	public int size() {
		return store.size();
	}

	/**
	 * Returns {@code true} if this store contains no key/value mappings.
	 *
	 * @return {@code true} if this store contains no key/value mappings
	 */
	public boolean isEmpty() {
		return store.isEmpty();
	}

	/**
	 * Removes all mappings.
	 */
	public void clear() {
		store.clear();
	}

	/**
	 * Return the comparator used to maintain order in this store, or {@code null} if the
	 * store is using the natural ordering the keys.
	 *
	 * @return the store comparator or {@code null}
	 */
	public Comparator<? super K> comparator() {
		return store.comparator();
	}

	@Override
	public int hashCode() {
		return Objects.hash(store);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) return true;
		if (!(obj instanceof SparseMap)) return false;
		SparseMap<?, ?> other = (SparseMap<?, ?>) obj;
		return Objects.equals(store, other.store);
	}

	@Override
	public String toString() {
		return store.toString();
	}
}
