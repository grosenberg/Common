package net.certiv.common.stores;

import java.util.LinkedList;
import java.util.List;
import java.util.Map.Entry;
import java.util.Set;

@Deprecated
public class BiHashList<K, V> {

	private final LinkedHashList<K, V> forward = new LinkedHashList<>();
	private final LinkedHashList<V, K> reverse = new LinkedHashList<>();

	public BiHashList() {}

	public List<V> get(K key) {
		return forward.get(key);
	}

	public List<K> getRev(V value) {
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

	public Set<Entry<V, LinkedList<K>>> entrySetInv() {
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
		return forward.sizeKeys();
	}

	public int sizeValues() {
		return reverse.sizeKeys();
	}

	public boolean isEmpty() {
		return forward.isEmpty();
	}

	@Override
	public String toString() {
		return forward.toString();
	}
}
