package net.certiv.common.stores;

import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.Set;
import java.util.TreeMap;

public class TreeList<K, V> {

	private final TreeMap<K, LinkedList<V>> map;

	public TreeList() {
		this(null);
	}

	public TreeList(Comparator<? super K> keyComp) {
		this.map = new TreeMap<>(keyComp);
	}

	public K firstKey() {
		return map.firstKey();
	}

	public K lastKey() {
		return map.lastKey();
	}

	public LinkedList<V> firstValue() {
		return !map.isEmpty() ? firstEntry().getValue() : null;
	}

	public LinkedList<V> lastValue() {
		return !map.isEmpty() ? lastEntry().getValue() : null;
	}

	public Entry<K, LinkedList<V>> firstEntry() {
		return map.firstEntry();
	}

	public Entry<K, LinkedList<V>> lastEntry() {
		return map.lastEntry();
	}

	public Set<Entry<K, LinkedList<V>>> entrySet() {
		return map.entrySet();
	}

	/**
	 * Returns the value list identified by the given key or {@code null} if the key does
	 * not exist.
	 */
	public List<V> get(K key) {
		return map.get(key);
	}

	/** Adds the given value to the end of the list identified by the given key. */
	public boolean add(K key, V value) {
		LinkedList<V> values = map.get(key);
		if (values == null) {
			values = new LinkedList<>();
			map.put(key, values);
		}
		return values.add(value);
	}

	/**
	 * Puts the given value at the beginning of the list identified by the given key.
	 */
	public void put(K key, V value) {
		LinkedList<V> values = map.get(key);
		if (values == null) {
			values = new LinkedList<>();
			map.put(key, values);
		}
		values.push(value);
	}

	/**
	 * Returns the first value in the list identified by the given key or {@code null} if
	 * the list is empty.
	 */
	public V peek(K key) {
		LinkedList<V> values = map.get(key);
		if (values == null) return null;
		return values.peek();
	}

	/**
	 * Returns the last value in the list identified by the given key or {@code null} if
	 * the list is empty.
	 */
	public V peekLast(K key) {
		LinkedList<V> values = map.get(key);
		if (values == null) return null;
		return values.peekLast();
	}

	/** Returns {@code true} if a value set for the given key exists. */
	public boolean containsKey(K key) {
		return map.containsKey(key);
	}

	/**
	 * Returns {@code true} if at least one of the value sets contains the given value.
	 */
	public boolean containsValue(V value) {
		return map.containsValue(value);
	}

	/**
	 * Returns {@code true} if the value set for the given key contains the given value.
	 */
	public boolean contains(K key, V value) {
		if (!map.containsKey(key)) return false;
		return map.get(key).contains(value);
	}

	public Set<K> keySet() {
		return map.keySet();
	}

	public List<V> values() {
		List<V> results = new LinkedList<>();
		for (List<V> set : map.values()) {
			results.addAll(set);
		}
		return results;
	}

	public List<V> remove(K key) {
		return map.remove(key);
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
		if (!(obj instanceof TreeList)) return false;
		TreeList<?, ?> other = (TreeList<?, ?>) obj;
		return Objects.equals(map, other.map);
	}

	@Override
	public String toString() {
		return map.toString();
	}
}
