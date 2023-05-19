package net.certiv.common.stores.context;

import java.util.ConcurrentModificationException;
import java.util.Map;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.stream.Stream;

/**
 * Interface definition for a key-value store having a single scope, {@code KVStore}, or
 * multiple scopes, {@code Context}.
 * <p>
 * Keys are value typed: {@code Key<V>}. Values are held in typed value holders:
 * {@code Value<V>}.
 */
public interface IKVStore extends IKVMinStore {

	/**
	 * Returns the value holder for the given key or {@code null} if not present.
	 */
	<V> Value<V> getValue(Key<V> key);

	/** Returns the value holder for the given key or {@code def} if not present. */
	<V> Value<V> getValue(Key<V> key, Value<V> def);

	/** Returns a {@code Map} containing all key/value entries. */
	Map<Key<?>, Value<?>> getAll();

	/**
	 * Puts the given typed key/value pair in the top-most scope of this store. Replaces
	 * any existing same-key instance in the top-most scope. Hides any same-key instance
	 * that may exist in any deeper scope.
	 */
	<V> V putValue(Key<V> key, Value<V> value);

	/**
	 * Puts the given key/value pair in the top-most scope of this store if no same-key
	 * instance pre-exists in any scope.
	 */
	<V> void putValueIfAbsent(Key<V> key, Value<V> value);

	/**
	 * Puts the key/value pairs of the given {@code Context} in the top-most scope of this
	 * {@code Context}.
	 */
	<V> void putAll(IKVStore store);

	/**
	 * Puts the key/value pairs of the given store in the top-most scope of this
	 * {@code Context} if no same-key instance pre-exists in any scope.
	 */
	<V> void putAllIfAbsent(IKVStore delta);

	/** Returns a stream of the unique keys present in this store. */
	Stream<Key<?>> keyStream();

	/**
	 * Performs the given action for each visible entry in this store until all such
	 * entries have been processed, or the action throws an exception.
	 * <p>
	 * Exceptions thrown by the action are rethrown to the caller.
	 *
	 * @param action The action to be performed for each entry
	 * @throws NullPointerException            if the specified action is null
	 * @throws ConcurrentModificationException if an entry is found removed during
	 *                                         iteration
	 */
	void forEach(BiConsumer<? super Key<?>, ? super Value<?>> action);

	/**
	 * Returns the unique set of keys stored in this store, inclusive of all scopes.
	 */
	Set<Key<?>> keys();

	/** Returns {@code true} if no keys exist in this store. */
	boolean isEmpty();

	/**
	 * Returns the number of unique keys stored in this store, inclusive of all scopes.
	 */
	int size();

	/**
	 * Returns the total depth of this store. Equivalent to the number of scopes.
	 */
	int depth();

	/** Returns a shallow copy of this store including all scopes. */
	IKVStore dup();

	/** Clear all scopes of this store. */
	void clear();
}
