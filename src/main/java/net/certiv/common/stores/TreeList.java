package net.certiv.common.stores;

import java.util.Collection;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.NavigableMap;
import java.util.NavigableSet;
import java.util.Objects;
import java.util.Set;
import java.util.TreeMap;
import java.util.function.BiConsumer;

/**
 * Sorted multimap implemented as a {@link TreeMap} with {@link LinkedList} implemented
 * instance values. Keys are implicitly unique.
 *
 * @param <K> the key type
 * @param <V> the list value type
 */
public class TreeList<K, V> {

	private final TreeMap<K, LinkedList<V>> map;
	private boolean unique;

	/**
	 * Constructs a new, empty treelist, using the natural ordering of its keys. All keys
	 * inserted into the treelist must implement the {@link Comparable} interface.
	 * Furthermore, all such keys must be <em>mutually comparable</em>:
	 * {@code k1.compareTo(k2)} must not throw a {@code ClassCastException} for any keys
	 * {@code k1} and {@code k2} in the treelist. If the user attempts to put a key into
	 * the treelist that violates this constraint (for example, the user attempts to put a
	 * string key into a treelist whose keys are integers), the
	 * {@code put(Object key, Object value)} call will throw a {@code ClassCastException}.
	 */
	public TreeList() {
		this.map = new TreeMap<>();
	}

	/**
	 * Constructs a new, empty treelist, ordered according to the given comparator. All
	 * keys inserted into the treelist must be <em>mutually comparable</em> by the given
	 * comparator: {@code comparator.compare(k1, k2)} must not throw a
	 * {@code ClassCastException} for any keys {@code k1} and {@code k2} in the treelist.
	 * If the user attempts to put a key into the treelist that violates this constraint,
	 * the {@code put(Object key, Object value)} call will throw a
	 * {@code ClassCastException}.
	 *
	 * @param comp the comparator that will be used to order this treelist. If
	 *             {@code null}, the {@linkplain Comparable natural ordering} of the keys
	 *             will be used.
	 */
	public TreeList(Comparator<? super K> comp) {
		this.map = new TreeMap<>(comp);
	}

	/**
	 * Constructs a new tree map containing the same mappings as the given map, ordered
	 * according to the <em>natural ordering</em> of its keys. All keys inserted into the
	 * new map must implement the {@link Comparable} interface. Furthermore, all such keys
	 * must be <em>mutually comparable</em>: {@code k1.compareTo(k2)} must not throw a
	 * {@code ClassCastException} for any keys {@code k1} and {@code k2} in the map. This
	 * method runs in n*log(n) time.
	 *
	 * @param m the map whose mappings are to be placed in this map
	 * @throws ClassCastException   if the keys in m are not {@link Comparable}, or are
	 *                              not mutually comparable
	 * @throws NullPointerException if the specified map is null
	 */
	public TreeList(TreeList<? extends K, ? extends V> m) {
		this.map = new TreeMap<>();
		putAll(m);
	}

	public K firstKey() {
		return map.firstKey();
	}

	public K lastKey() {
		return map.lastKey();
	}

	public LinkedList<V> firstValue() {
		return !map.isEmpty() ? firstEntry().getValue() : new LinkedList<>();
	}

	public LinkedList<V> lastValue() {
		return !map.isEmpty() ? lastEntry().getValue() : new LinkedList<>();
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

	/**
	 * Adds the given value to the end of the list identified by the given key. If
	 * {@link enforceUniqueValues} is set to {@code true}, the value is added only if
	 * value is not already present (based on object equality).
	 */
	public boolean add(K key, V value) {
		LinkedList<V> values = map.get(key);
		if (values == null) {
			values = new LinkedList<>();
			map.put(key, values);
		}
		if (unique && values.contains(value)) return false;
		return values.add(value);
	}

	/**
	 * Puts the given value at the beginning of the value list identified by the given
	 * key.
	 */
	public void put(K key, V value) {
		LinkedList<V> values = map.get(key);
		if (values == null) {
			values = new LinkedList<>();
			map.put(key, values);
		}
		if (!unique || !values.contains(value)) values.push(value);
	}

	/**
	 * Puts the given values at the beginning of the value list identified by the given
	 * key. The order of the individual added values is preserved.
	 */
	public void putAll(K key, Collection<? extends V> values) {
		values.forEach(v -> put(key, v));
	}

	public void putAll(Map<? extends K, List<? extends V>> map) {}

	/**
	 * Copies all of the mappings from the specified map to this map. These mappings
	 * replace any mappings that this map had for any of the keys currently in the
	 * specified map.
	 *
	 * @param map mappings to be stored in this map
	 * @throws ClassCastException   if the class of a key or value in the specified map
	 *                              prevents it from being stored in this map
	 * @throws NullPointerException if the specified map is null or the specified map
	 *                              contains a null key and this map does not permit null
	 *                              keys
	 */
	public void putAll(TreeList<? extends K, ? extends V> map) {
		map.entrySet().forEach(e -> putAll(e.getKey(), e.getValue()));
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

	public void forEach(BiConsumer<? super K, ? super LinkedList<V>> action) {
		map.forEach(action);
	}

	public NavigableSet<K> descendingKeySet() {
		return map.descendingKeySet();
	}

	public NavigableMap<K, LinkedList<V>> descendingMap() {
		return map.descendingMap();
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

	public NavigableSet<K> keySet() {
		return map.navigableKeySet();
	}

	/** Returns a consolidated list of all values. */
	public List<V> values() {
		List<V> results = new LinkedList<>();
		for (List<V> values : map.values()) {
			results.addAll(values);
		}
		return results;
	}

	public void enforceUniqueValues(boolean unique) {
		this.unique = unique;
	}

	public boolean isenforcingUniqueValues() {
		return unique;
	}

	public List<V> remove(K key) {
		return map.remove(key);
	}

	public boolean remove(K key, V value) {
		LinkedList<V> values = map.get(key);
		if (values == null) return false;
		return values.remove(value);
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
		return Objects.hash(map, unique);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) return true;
		if (!(obj instanceof TreeList)) return false;
		TreeList<?, ?> other = (TreeList<?, ?>) obj;
		return unique == other.unique && Objects.equals(map, other.map);
	}

	@Override
	public String toString() {
		return map.toString();
	}
}
