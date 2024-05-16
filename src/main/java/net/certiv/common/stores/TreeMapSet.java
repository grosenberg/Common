package net.certiv.common.stores;

import java.util.Comparator;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.stream.Collectors;

/**
 * Sorted multimap implemented as a {@link TreeMap} with {@link TreeSet} implemented
 * instance values. Keys and values are implicitly unique.
 *
 * @param <K> the key type
 * @param <V> the list value type
 */
public class TreeMapSet<K, V> {

	private final TreeMap<K, Set<V>> map;
	private final Comparator<? super V> valComp;

	public TreeMapSet() {
		this(null, null);
	}

	public TreeMapSet(Comparator<? super K> keyComp) {
		this(keyComp, null);
	}

	public TreeMapSet(Comparator<? super K> keyComp, Comparator<? super V> valComp) {
		this.map = new TreeMap<>(keyComp);
		this.valComp = valComp;
	}

	public Set<V> get(K key) {
		return new LinkedHashSet<>(map.get(key));
	}

	public boolean put(K key, V value) {
		TreeSet<V> set = (TreeSet<V>) map.get(key);
		if (set == null) {
			set = new TreeSet<>(valComp);
			map.put(key, set);
		}
		return set.add(value);
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

	public Set<K> keys() {
		return new LinkedHashSet<>(map.keySet());
	}

	public Set<V> values() {
		return map.values().stream() //
				.flatMap(s -> s.stream()) //
				.collect(Collectors.toCollection(LinkedHashSet::new));
	}

	public Set<V> remove(K key) {
		if (!map.containsKey(key)) return new LinkedHashSet<>();
		return new LinkedHashSet<>(map.remove(key));
	}

	public boolean remove(K key, V value) {
		if (!map.containsKey(key)) return false;
		boolean ok = map.get(key).remove(value);
		if (map.get(key).isEmpty()) map.remove(key);
		return ok;
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
}
