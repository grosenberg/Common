package net.certiv.common.stores;

/**
 * @deprecated
 */
@Deprecated
public interface IExtent<V> {

	/** Returns {@code true} if this extent contains the given value. */
	boolean contains(V v);
}
