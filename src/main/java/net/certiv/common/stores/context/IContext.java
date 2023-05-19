package net.certiv.common.stores.context;

import java.util.UUID;

/**
 * Interface definition for a key-value store supporting multiple scopes.
 * <p>
 * Keys are value typed: {@code Key<V>}. Values are held in typed value holders:
 * {@code Value<V>}.
 */
public interface IContext extends IKVStore {

	/**
	 * Merges all scopes the given {@code IKVStore} as as the scopes starting at the given
	 * index of this store.
	 * <p>
	 * Typical use is, within some transaction, multiple contexts are successively merged
	 * into this context; each is merged at successive offsets.
	 * <p>
	 * merge KVStore -> KVStore :: puts ({@code idx = 0}) or putsIfAbsent
	 * ({@code idx > 0}) the given KVStore values into this store.
	 * <p>
	 * merge KVStore -> Context :: inserts the KVStore as a new scope {@code idx} in this
	 * context.
	 * <p>
	 * merge Context -> Context :: inserts all scopes of the given context at scope
	 * {@code idx} in this context.
	 *
	 * @param idx   the target scope index (0-based)
	 * @param store the store to merge into this store
	 * @return the store identifier for the pre-merge scope at the given index
	 */
	UUID merge(int idx, IKVStore store);

	/**
	 * Restores the given {@code Context} by removing the top-level (scope 0) store until
	 * the scope having the given mark becomes the top-level (scope 0) store.
	 * <p>
	 * The {@code mark} is typically obtained the from the initial merge in some
	 * transactional operation involving the context.
	 *
	 * @param mark identifies the scope to retain as the top-level (scope 0) store
	 * @return {@code true } if the restore is sucessful; {@code false} typically
	 *         indicates the marked store was pushed off the bottom of the scope stack.
	 */
	boolean restore(UUID mark);

	/**
	 * Returns a deep copy of the given number of scopes of this store, counting from the
	 * top-most scope. A {@code depth} of {@code 1} corresponds to the top-most scope.
	 */
	IContext dup(int depth);
}
