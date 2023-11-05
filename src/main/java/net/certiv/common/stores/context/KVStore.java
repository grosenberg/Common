package net.certiv.common.stores.context;

import net.certiv.common.event.ITypedEventDispatcher;
import net.certiv.common.event.TypedEventDispatcher;

/**
 * A single-level constraint-typed key:value store. That is, each stored {@link Key} and
 * {@link Value} pair is consistently typed, while the store itself is untyped.
 */
public class KVStore extends KVScope {

	/**
	 * Creates a new {@code KVStore}, initialized from the given store, for storing
	 * consistently typed key:value pairs.
	 *
	 * @param scope a {@link IKVScope} containing the initial values
	 * @return an initialized store
	 */
	public static KVStore of(IKVScope scope) {
		return new KVStore(scope);
	}

	// --------------------------------

	/**
	 * Creates a new {@code KVStore} for storing consistently typed key:value pairs.
	 */
	public KVStore() {
		super();
	}

	/**
	 * Creates a new {@code KVStore}, initialized from the given store, for storing
	 * consistently typed key:value pairs.
	 *
	 * @param scope a {@link IKVScope} containing the initial values
	 */
	public KVStore(IKVScope scope) {
		KVStore store = new KVStore();
		store.mergeFirst(scope);
	}

	/**
	 * Marks this KVStore as reactive by enabling the dispatcher. Adds a new
	 * {@link TypedEventDispatcher} if no other dispatcher has been set on this KVStore.
	 *
	 * @return this
	 */
	public KVStore reactive() {
		return reactive(true);
	}

	/**
	 * Marks this KVStore as reactive by enabling the dispatcher depending on the given
	 * enable state. Adds a new {@link TypedEventDispatcher} if no other dispatcher has
	 * been set on this KVStore.
	 *
	 * @param enable reactivity state
	 * @return this
	 */
	@Override
	public KVStore reactive(boolean enable) {
		this.reactive = enable;
		if (reactive && dispatcher == null) dispatcher = new TypedEventDispatcher();
		return this;
	}

	/**
	 * Marks this KVStore as reactive by adding and enabling the given dispatcher.
	 * <p>
	 * Removes any existing dispatcher, and disables reactivity, if the given dispatcher
	 * is {@code null}.
	 *
	 * @param dispatcher a dispatcher, or {@code null} to remove
	 * @return this
	 */
	@Override
	public KVStore reactive(ITypedEventDispatcher dispatcher) {
		return reactive(dispatcher, true);
	}

	/**
	 * Marks this KVStore as reactive by adding and enabling the given dispatcher
	 * depending on the given enable state.
	 * <p>
	 * Removes any existing dispatcher, and disables reactivity, if the given dispatcher
	 * is {@code null}.
	 *
	 * @param enable     reactivity state
	 * @param dispatcher a dispatcher, or {@code null} to remove
	 * @return this
	 */
	@Override
	public KVStore reactive(ITypedEventDispatcher dispatcher, boolean enable) {
		this.dispatcher = dispatcher;
		this.reactive = dispatcher != null ? enable : false;
		return this;
	}
}
