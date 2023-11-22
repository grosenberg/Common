package net.certiv.common.stores.context;

import java.util.ConcurrentModificationException;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.function.BiConsumer;
import java.util.stream.Stream;

import net.certiv.common.util.GsonUtil.Convertable;

/**
 * Interface definition for a key-value store having a single scope, {@code KVScope}, or
 * multiple scopes, {@code Context}.
 * <p>
 * Keys are value typed: {@code Key<V>}. Values are held in typed value holders:
 * {@code Value<V>}.
 */
public interface IKVScope extends Convertable {

	String ErrNotDef = "Key [%s] not defined.";
	RuntimeException ErrMark = new UnsupportedOperationException("MARK is inviolable.");
	RuntimeException ErrNotImpl = new UnsupportedOperationException("Not implemented.");

	/** Unique (UUID-based) mark value identifing non-existance of a scope. */
	UUID EOS = new UUID(0, 0);

	// ---- Basic Ops -----------------

	/** Returns {@code true} if the given key exists in any scope in this store. */
	boolean contains(Key<?> key);

	/** Returns the underlying value for the given key or {@code null}. */
	<V> V get(Key<V> key);

	/**
	 * Returns the underlying value for the given key or {@code def} if the key does not
	 * exist in any scope.
	 */
	<V> V get(Key<V> key, V def);

	/**
	 * Puts the given key and given raw value in the top-most scope of this store.
	 * Replaces any existing same-key instance in the top-most scope. Hides any same-key
	 * instance that may exist in any deeper scope.
	 * <p>
	 * Removes the key if the value is {@code null}.
	 */
	<V> V put(Key<V> key, V value);

	/** Assign the given {@code Key} the given underlying raw value and unit. */
	<V> V put(Key<V> key, V value, String unit);

	/**
	 * Puts the given key and given raw value, if non-null, in the top-most scope of this
	 * store. Replaces any existing same-key instance in the top-most scope. Hides any
	 * same-key instance that may exist in any deeper scope.
	 * <p>
	 * Has no effect if the value is {@code null}; returns the present value associated
	 * with the given key, or {@code null}.
	 *
	 * @return the prior existing value associated with the given key, or {@code null}
	 */
	<V> V putIfNotNull(Key<V> key, V value);

	/**
	 * Puts the given key and raw value in the top-most scope of this store if no same-key
	 * instance pre-exists in any scope.
	 */
	<V> void putIfAbsent(Key<V> key, V value);

	/**
	 * Puts the given key and raw value and unit in the top-most scope of this store if no
	 * same-key instance pre-exists in any scope.
	 */
	<V> void putIfAbsent(Key<V> key, V value, String unit);

	/**
	 * Removes the given key:value instance that exists in this scope. Returns the removed
	 * value or {@code null} if no instance existed. Has no affect on any same-key
	 * instance that may exist in any deeper scope. Does not remove the scope identifying
	 * MARK.
	 *
	 * @param key the key identifying the key:value pair to remove
	 */
	<V> V remove(Key<V> key);

	/** Returns a deep copy of just the top-most scope of this store. */
	KVScope delta();

	/**
	 * Merges all scopes contained within the given {@code IKVStore} as the first/top-most
	 * scope(s) of the this store.
	 * <p>
	 * merge KVScope -> KVScope :: puts the given KVScope values into this store
	 * superceding any existing values having the same key.
	 * <p>
	 * merge KVScope -> Context :: inserts the KVScope as a new scope 0 in this Context.
	 * The depth of this Context is increased as necessary to accommodate the merger.
	 * <p>
	 * merge Context -> Context :: inserts the collection of scopes of the given Context
	 * beginning at scope 0 in this Context. The depth of this Context is increased as
	 * necessary to accommodate the merger.
	 *
	 * @param store the store to merge into this store
	 * @return the store marker identifying the pre-merge top-level (scope 0) store
	 * @see IContext#restore
	 */
	UUID mergeFirst(IKVScope store);

	/**
	 * Merges (appends) all scopes contained within the given {@code IKVStore} as the
	 * last/bottom-most scope(s) of the this store.
	 * <p>
	 * merge KVScope -> KVScope :: puts the given KVScope values into this store if the
	 * corresponding key is absent.
	 * <p>
	 * merge KVScope -> Context :: appends the KVScope after the bottom-most scope in this
	 * Context. The depth of this Context is increased as necessary to accommodate the
	 * merger.
	 * <p>
	 * merge Context -> Context :: appends the collection of scopes of the given Context
	 * after the bottom-most scope in this Context. The depth of this Context is increased
	 * as necessary to accommodate the merger.
	 *
	 * @param store the store to merge into this store
	 * @return the store marker identifying the first scope of the appended store
	 * @see IContext#restore
	 */
	UUID mergeLast(IKVScope store);

	// ---- Value Ops ----------------

	/**
	 * Returns the value holder for the given key or {@code null} if not present.
	 */
	default <V> Value<V> getValue(Key<V> key) {
		throw ErrNotImpl;
	}

	/** Returns the value holder for the given key or {@code def} if not present. */
	default <V> Value<V> getValue(Key<V> key, Value<V> def) {
		throw ErrNotImpl;
	}

	/** Returns a {@code Map} containing all key:value entries. */
	default Map<Key<?>, Value<?>> getAll() {
		throw ErrNotImpl;
	}

	/**
	 * Puts the given typed key:value pair in the top-most scope of this store. Replaces
	 * any existing same-key instance in the top-most scope. Hides any same-key instance
	 * that may exist in any deeper scope.
	 */
	default <V> V putValue(Key<V> key, Value<V> value) {
		throw ErrNotImpl;
	}

	/**
	 * Puts the given key:value pair in the top-most scope of this store if no same-key
	 * instance pre-exists in any scope.
	 */
	default <V> void putValueIfAbsent(Key<V> key, Value<V> value) {
		throw ErrNotImpl;
	}

	// ---- Extended Ops --------------

	/**
	 * Puts the key:value pairs of the given {@code Context} in the top-most scope of this
	 * {@code Context}.
	 */
	default <V> void putAll(IKVScope store) {
		throw ErrNotImpl;
	}

	/**
	 * Puts the key:value pairs of the given store in the top-most scope of this
	 * {@code Context} if no same-key instance pre-exists in any scope.
	 */
	default <V> void putAllIfAbsent(IKVScope delta) {
		throw ErrNotImpl;
	}

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
	default void forEach(BiConsumer<? super Key<?>, ? super Value<?>> action) {
		throw ErrNotImpl;
	}

	/** Returns the unique set of keys stored in this scope. */
	default Set<Key<?>> keys() {
		throw ErrNotImpl;
	}

	/** Returns a stream of the unique keys present in this scope. */
	default Stream<Key<?>> keyStream() {
		throw ErrNotImpl;
	}

	/**
	 * Returns the total depth of this store; same as number of scopes. Defaults to a
	 * depth of {@code 1}.
	 */
	default int depth() {
		return 1;
	}

	/**
	 * Returns a shallow copy of the visible key:value pairs in this store. The returned
	 * scope has a unique MARK.
	 */
	default IKVScope dup() {
		throw ErrNotImpl;
	}

	/** Clear this store. */
	default void clear() {
		throw ErrNotImpl;
	}

	// --------------------------------

	/** Returns {@code true} if no keys exist in this store. */
	boolean isEmpty();

	/** Returns the number of unique keys present in this store. */
	int size();

}
