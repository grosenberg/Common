package net.certiv.common.stores;

import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
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

	private LinkedHashMap<K, LinkedHashSet<V>> map;

	public HashMultiset() {
		this.map = new LinkedHashMap<>();
	}

	public HashMultiset(LinkedHashMap<? extends K, ? extends LinkedHashSet<V>> multiset) {
		this();
		putAll(multiset);
	}

	public LinkedHashSet<V> get(K key) {
		return map.get(key);
	}

	public boolean put(K key, V value) {
		LinkedHashSet<V> set = map.get(key);
		if (set == null) {
			set = new LinkedHashSet<>();
			map.put(key, set);
		}
		return set.add(value);
	}

	public void put(K key, LinkedHashSet<V> value) {
		LinkedHashSet<V> set = map.get(key);
		if (set == null) {
			set = new LinkedHashSet<>(value);
			map.put(key, set);
		} else {
			set.addAll(value);
		}
	}

	public void putAll(LinkedHashMap<? extends K, ? extends LinkedHashSet<V>> multiset) {
		map.putAll(multiset);
	}

	public void putAll(HashMultiset<K, V> items) {
		for (Entry<K, LinkedHashSet<V>> entry : items.entrySet()) {
			put(entry.getKey(), entry.getValue());
		}
	}

	public Set<Entry<K, LinkedHashSet<V>>> entrySet() {
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

	@Override
	public String toString() {
		return map.toString();
	}
}
