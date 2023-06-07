package net.certiv.common.stores.context;

import java.util.UUID;

/** Defines the essential operations supported by a typed key/value store. */
public interface IKVMinStore {

	/** Unique (UUID-based) store identifying mark. */
	Key<UUID> MARK = Key.of("kvstore.uuid");

	RuntimeException ERR_MARK = new UnsupportedOperationException("MARK is inviolable.");

	String ErrNotDef = "Key [%s] not defined.";

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
	 * Removes the given key/value instance that exists in the top-most scope of this
	 * store. Returns the removed value or {@code null} if no instance existed. Has no
	 * affect on any same-key instance that may exist in any deeper scope.
	 */
	<V> V remove(Key<V> key);

	/** Returns a deep copy of just the top-most scope of this store. */
	KVStore delta();

	/**
	 * Merges all scopes contained within the given {@code IKVStore} as the first/top-most
	 * scope(s) of the this store.
	 * <p>
	 * merge KVStore -> KVStore :: puts the given KVStore values into this store
	 * superceding any existing values having the same key.
	 * <p>
	 * merge KVStore -> Context :: inserts the KVStore as a new scope 0 in this Context.
	 * The depth of this Context is increased as necessary to accommodate the merger.
	 * <p>
	 * merge Context -> Context :: inserts the collection of scopes of the given Context
	 * beginning at scope 0 in this Context. The depth of this Context is increased as
	 * necessary to accommodate the merger.
	 *
	 * @param store the store to merge into this store
	 * @return the store marker identifying the pre-merge top-level (scope 0) store
	 * @see IContext#restore(UUID)
	 */
	UUID mergeFirst(IKVStore store);

	/**
	 * Merges (appends) all scopes contained within the given {@code IKVStore} as the
	 * last/bottom-most scope(s) of the this store.
	 * <p>
	 * merge KVStore -> KVStore :: puts the given KVStore values into this store if the
	 * corresponding key is absent.
	 * <p>
	 * merge KVStore -> Context :: appends the KVStore after the bottom-most scope in this
	 * Context. The depth of this Context is increased as necessary to accommodate the
	 * merger.
	 * <p>
	 * merge Context -> Context :: appends the collection of scopes of the given Context
	 * after the bottom-most scope in this Context. The depth of this Context is increased
	 * as necessary to accommodate the merger.
	 *
	 * @param store the store to merge into this store
	 * @return the store marker identifying the first scope of the appended store
	 * @see IContext#restore(UUID)
	 */
	UUID mergeLast(IKVStore store);
}