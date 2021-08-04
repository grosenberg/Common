package net.certiv.common.util;

import java.util.Collection;
import java.util.Iterator;

public class CompareUtil {

	/**
	 * Compare {@code Collection}s for order. The collections must have well-defined
	 * iteration order and contain only {@code Comparable} elements.
	 * <p>
	 * Returns a negative integer, zero, or a positive integer if the reference
	 * collection is ordered less than, equal to, or greater than that of the target
	 * collection.
	 *
	 * @param <E> the underlying element type
	 * @param ref the reference element collection
	 * @param tgt the target element collection
	 * @return a relative ordering indicator
	 */
	public static <E extends Comparable<E>> int compare(Collection<E> ref, Collection<E> tgt) {
		for (Iterator<E> a = ref.iterator(), b = tgt.iterator(); a.hasNext() || b.hasNext();) {
			E e1, e2;
			if (a.hasNext()) {
				e1 = a.next();
			} else {
				return -1;
			}

			if (b.hasNext()) {
				e2 = b.next();
			} else {
				return 1;
			}

			int c = e1.compareTo(e2);
			if (c != 0) return c;
		}
		return 0;
	}
}
