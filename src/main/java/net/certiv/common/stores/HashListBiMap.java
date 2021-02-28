package net.certiv.common.stores;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map.Entry;
import java.util.Set;

/**
 * Forward store is 1:n. Reverse store is 1:1.
 */
public class HashListBiMap<K, V> {

	private final HashList<K, V> forward = new HashList<>();
	private final HashMap<V, K> reverse = new LinkedHashMap<>();

	public HashListBiMap() {}

	public List<V> get(K key) {
		return forward.get(key);
	}

	public K getRev(V value) {
		return reverse.get(value);
	}

	public boolean put(K key, V value) {
		reverse.put(value, key);
		return forward.put(key, value);
	}

	public LinkedList<V> putAll(K key, List<V> values) {
		for (V value : values) {
			reverse.put(value, key);
		}
		return forward.put(key, values);
	}

	public List<V> remove(K key) {
		List<V> removed = forward.remove(key);
		for (V value : removed) {
			reverse.remove(value, key);
		}
		return removed;
	}

	public boolean remove(K key, V value) {
		reverse.remove(value, key);
		return forward.remove(key, value);
	}

	public boolean containsKey(K key) {
		return forward.containsKey(key);
	}

	public boolean containsValue(V value) {
		return reverse.containsKey(value);
	}

	public boolean containsEntry(K key, V value) {
		return forward.containsEntry(key, value);
	}

	public Set<Entry<K, LinkedList<V>>> entrySet() {
		return forward.entrySet();
	}

	public Set<Entry<V, K>> entrySetInv() {
		return reverse.entrySet();
	}

	public Set<K> keySet() {
		return forward.keySet();
	}

	public Set<V> valueSet() {
		return reverse.keySet();
	}

	public void clear() {
		forward.clear();
		reverse.clear();
	}

	public int size() {
		return forward.size();
	}

	public int sizeValues() {
		return reverse.size();
	}

	public boolean isEmpty() {
		return forward.isEmpty();
	}

	@Override
	public String toString() {
		return forward.toString();
	}
}
