package net.certiv.common.stores.sparse;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.concurrent.ConcurrentSkipListMap;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import net.certiv.common.stores.UniqueList;

public class SparseMapSet<K, V> extends SparseMap<K, SparseSet<V>> {

	public SparseMapSet() {
		super(new ConcurrentSkipListMap<>());
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
	 * Returns an {@link Entry} containing the key and value stored against the given key,
	 * or {@code null} if the store does not contain the key.
	 *
	 * @param key store key
	 * @return set for the given key, or {@code null} if not found
	 * @throws IllegalArgumentException if the given key is {@code null}
	 */
	public SparseSet<V> get(K key) {
		chkArg(key, KEY);
		if (!store.containsKey(key)) return null;	// no prior
		return store.get(key);						// has prior
	}

	/**
	 * Returns the set of values stored against the given key, or a set containing the
	 * given default value if the store does not contain the key.
	 *
	 * @param key store key
	 * @return set for the given key or containing the default value if not found
	 * @throws IllegalArgumentException if the given key is {@code null}
	 */
	public SparseSet<V> getOrDefault(K key, V def) {
		SparseSet<V> set = get(key);
		return set != null ? set : new SparseSet<>(Set.of(def));
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
	public SparseSet<V> get(K key, Predicate<SparseSet<? super V>> cond) {
		SparseSet<V> set = get(key);
		if (cond == null) return set;
		if (set != null && cond.test(set)) return set;
		return null;
	}

	/**
	 * Returns the existing entries in the store corresponding to the given keys.
	 *
	 * @param keys store keys
	 * @return all unique list of corresponding entries
	 * @throws IllegalArgumentException if the given key list is {@code null}
	 */
	public LinkedList<Entry<K, SparseSet<V>>> getAll(List<K> keys) {
		chkArg(keys, KEYS);
		LinkedList<Entry<K, SparseSet<V>>> entries = new UniqueList<>();
		for (K key : keys) {
			SparseSet<V> set = get(key);
			if (set != null) {
				entries.add(Map.entry(key, set));
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
	public LinkedList<Entry<K, SparseSet<V>>> getAll(List<K> keys,
			Predicate<Entry<? super K, SparseSet<V>>> cond) {
		LinkedList<Entry<K, SparseSet<V>>> entries = getAll(keys);
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
	 * @return {@code true} if this set did not already contain the specified element
	 * @throws IllegalArgumentException if the given key is {@code null}
	 */
	public boolean put(K key, V value) {
		chkArg(key, KEY);

		lock.lock();
		try {
			SparseSet<V> set = store.get(key);
			if (set == null) {
				set = new SparseSet<>();
				store.put(key, set);
			}
			return set.add(value);

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
	public final SparseSet<V> remove(K key) {
		if (!containsKey(key)) return null;			// no prior
		return store.remove(key);					// has prior
	}

	/**
	 * Removes the entries for the given keys. Returns a list of the prior existing
	 * entries whose keys match any of the given entry keys. The list will be empty if
	 * none of the given entry keys preexisted in the store.
	 *
	 * @param keys reference keys
	 * @return list of prior existing entries
	 */
	public final LinkedList<Entry<K, SparseSet<V>>> removeAll(Collection<? extends K> keys) {
		chkArg(keys, KEYS);
		LinkedList<Entry<K, SparseSet<V>>> priors = new UniqueList<>();
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
