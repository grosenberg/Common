package net.certiv.common.util;

import java.util.Collection;
import java.util.Iterator;

public class CompareUtil {

	/**
	 * Compares the two given classes. A {@code null} class is ordered last.
	 * Otherwise, the classes are ordered based on {@code Class#getName}.
	 *
	 * @param ref the reference class
	 * @param tgt the target class
	 * @return a relative ordering indicator
	 */
	public static int compare(Class<?> ref, Class<?> tgt) {
		if (ref == null && tgt == null) return 0;
		if (ref != null && tgt == null) return -1;
		if (ref == null && tgt != null) return 1;
		return ref.getName().compareTo(tgt.getName());
	}

	/**
	 * Compare {@code Collection}s for order. Returns a negative integer, zero, or a
	 * positive integer if the reference collection is ordered less than, equal to,
	 * or greater than that of the target collection.
	 * <p>
	 * The collections must
	 * <ol>
	 * <li>not be {@code null}
	 * <li>have well-defined iteration order
	 * <li>contain only {@code Comparable} elements
	 * </ol>
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
