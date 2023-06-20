package net.certiv.common.stores.context;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;
import java.util.function.BiConsumer;
import java.util.stream.Stream;

import net.certiv.common.check.Assert;

/**
 * A single-level constraint-typed key/value store. While the store is untyped, each store
 * {@code Key} and {@code Value} pair is consistently typed.
 *
 * @see {@code Key}
 * @see {@code Value}
 */
public class KVStore implements IKVStore {

	protected final Map<Key<?>, Value<?>> store = new LinkedHashMap<>();

	public KVStore() {
		store.put(MARK, Value.of(randomUUID()));
	}

	/**
	 * Returns a randomly selected store UUID. Will not equal
	 * {@link IKVMinStore#NO_STORE}.
	 *
	 * @return a unique store UUID
	 */
	public static UUID randomUUID() {
		UUID uuid = UUID.randomUUID();
		if (uuid.equals(NO_STORE)) {
			uuid = UUID.randomUUID();
		}
		return uuid;
	}

	@Override
	@SuppressWarnings("unchecked")
	public <V> V get(Key<V> key) {
		Assert.notNull(key);
		if (!store.containsKey(key)) return null;
		return (V) store.get(key).value();
	}

	@Override
	@SuppressWarnings("unchecked")
	public <V> V get(Key<V> key, V def) {
		Assert.notNull(key);
		if (!store.containsKey(key)) return def;
		return (V) store.get(key).value();
	}

	@Override
	@SuppressWarnings("unchecked")
	public <V> Value<V> getValue(Key<V> key) {
		return (Value<V>) store.get(key);
	}

	@Override
	@SuppressWarnings("unchecked")
	public <V> Value<V> getValue(Key<V> key, Value<V> def) {
		if (!store.containsKey(key)) return def;
		return (Value<V>) store.get(key);
	}

	@Override
	public Map<Key<?>, Value<?>> getAll() {
		return Collections.unmodifiableMap(store);
	}

	@Override
	@SuppressWarnings("unchecked")
	public <V> V put(Key<V> key, V value) {
		Assert.notNull(key, value);
		if (MARK == key) return null;
		return (V) store.put(key, Value.of(value));
	}

	@Override
	@SuppressWarnings("unchecked")
	public <V> V put(Key<V> key, V value, String unit) {
		Assert.notNull(key, value);
		if (MARK == key) return null;
		return (V) store.put(key, Value.of(value, unit));
	}

	@Override
	@SuppressWarnings("unchecked")
	public <V> V putValue(Key<V> key, Value<V> value) {
		Assert.notNull(key, value);
		if (MARK == key) return null;
		return (V) store.put(key, value);
	}

	@Override
	public <V> void putIfAbsent(Key<V> key, V value) {
		putIfAbsent(key, value, null);
	}

	@Override
	public <V> void putIfAbsent(Key<V> key, V value, String unit) {
		Assert.notNull(key);
		if (!contains(key)) put(key, value, unit);
	}

	@Override
	public <V> void putValueIfAbsent(Key<V> key, Value<V> value) {
		Assert.notNull(key, value);
		if (!contains(key)) store.put(key, value);
	}

	@Override
	public void putAll(IKVStore store) {
		Assert.isTrue(store != null);
		store.forEach((k, v) -> this.store.put(k, v));
	}

	@Override
	public void putAllIfAbsent(IKVStore store) {
		store.getAll().forEach((k, v) -> this.store.putIfAbsent(k, v));
	}

	@Override
	public <V> V putIfNotNull(Key<V> key, V value) {
		if (value != null) return put(key, value);
		return get(key);
	}

	@Override
	public Stream<Key<?>> keyStream() {
		return store.keySet().stream();
	}

	@Override
	public void forEach(BiConsumer<? super Key<?>, ? super Value<?>> action) {
		store.forEach(action);
	}

	@SuppressWarnings("unchecked")
	@Override
	public <V> V remove(Key<V> key) {
		Assert.isTrue(ERR_MARK, MARK != key);
		return (V) store.remove(key);
	}

	@Override
	public boolean contains(Key<?> key) {
		return store.containsKey(key);
	}

	@Override
	public void clear() {
		store.clear();
	}

	@Override
	public int size() {
		return store.size();
	}

	@Override
	public int depth() {
		return 1;
	}

	@Override
	public boolean isEmpty() {
		return store.isEmpty();
	}

	@Override
	public Set<Key<?>> keys() {
		return new LinkedHashSet<>(store.keySet());
	}

	@Override
	public KVStore dup() {
		KVStore dup = new KVStore();
		store.forEach((k, v) -> dup.store.put(k, v));
		return dup;
	}

	@Override
	public KVStore delta() {
		return dup();
	}

	@Override
	public UUID mergeFirst(IKVStore store) {
		UUID mark = get(MARK);
		store.forEach((k, v) -> this.store.put(k, v));
		return mark;
	}

	@Override
	public UUID mergeLast(IKVStore store) {
		UUID mark = get(MARK);
		store.forEach((k, v) -> this.store.putIfAbsent(k, v));
		return mark;
	}

	@Override
	public int hashCode() {
		return Objects.hash(store);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) return true;
		if (!(obj instanceof KVStore)) return false;
		KVStore other = (KVStore) obj;
		return Objects.equals(store, other.store);
	}

	@Override
	public String toString() {
		return store.toString();
	}
}
