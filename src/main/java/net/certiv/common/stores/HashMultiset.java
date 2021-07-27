package net.certiv.common.stores;

import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.Set;

/**
 * Multimap implemented as a LinkedHashMap with LinkedHashSet implemented
 * instance values.
 *
 * @param <K> the key type
 * @param <V> the value type
 */
public class HashMultiset<K, V> {

	private Map<K, Set<V>> map;

	public HashMultiset() {
		this.map = new HashMap<>();
	}

	public HashMultiset(Map<? extends K, ? extends Set<V>> multiset) {
		this();
		putAll(multiset);
	}

	public Set<V> get(K key) {
		return map.get(key);
	}

	public boolean put(K key, V value) {
		Set<V> set = map.get(key);
		if (set == null) {
			set = new LinkedHashSet<>();
			map.put(key, set);
		}
		return set.add(value);
	}

	public void put(K key, Set<V> value) {
		Set<V> set = map.get(key);
		if (set == null) {
			set = new HashSet<>(value);
			map.put(key, set);
		} else {
			set.addAll(value);
		}
	}

	public void putAll(Map<? extends K, ? extends Set<V>> multiset) {
		map.putAll(multiset);
	}

	public void putAll(HashMultiset<K, V> items) {
		for (Entry<K, Set<V>> entry : items.entrySet()) {
			put(entry.getKey(), entry.getValue());
		}
	}

	public Set<Entry<K, Set<V>>> entrySet() {
		return map.entrySet();
	}

	public boolean containsKey(K key) {
		return map.containsKey(key);
	}

	public Set<K> keySet() {
		return map.keySet();
	}

	public boolean containsValue(K key, V value) {
		Set<V> set = map.get(key);
		if (set != null) return set.contains(value);
		return false;
	}

	public Set<V> values() {
		Set<V> results = new LinkedHashSet<>();
		for (Set<V> set : map.values()) {
			results.addAll(set);
		}
		return results;
	}

	public Set<V> remove(K key) {
		return map.remove(key);
	}

	public boolean remove(K key, V value) {
		Set<V> set = map.get(key);
		if (set != null) {
			boolean ok = set.remove(value);
			if (set.isEmpty()) map.remove(key);
			return ok;
		}
		return false;
	}

	public void clear() {
		map.clear();
	}

	public boolean isEmpty() {
		return map.isEmpty();
	}

	public int size() {
		return map.size();
	}

	@Override
	public int hashCode() {
		return Objects.hash(map);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) return true;
		if (!(obj instanceof HashMultiset)) return false;
		HashMultiset<?, ?> other = (HashMultiset<?, ?>) obj;
		return Objects.equals(map, other.map);
	}
}
