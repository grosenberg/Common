package net.certiv.common.util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.regex.Pattern;

import net.certiv.common.check.Assert;

public class CompareUtil {

	/** Single DOT pattern */
	public static final Pattern DOTTED = Pattern.compile("\\.");

	/**
	 * Compares the two given classes. A {@code null} class is ordered last. Otherwise,
	 * the classes are ordered based on {@code Class#getName}.
	 *
	 * @param ref a reference class
	 * @param tgt a target class
	 * @return a relative ordering indicator
	 */
	public static int compare(Class<?> ref, Class<?> tgt) {
		if (ref == null && tgt == null) return 0;
		if (ref != null && tgt == null) return -1;
		if (ref == null && tgt != null) return 1;
		return ref.getName().compareTo(tgt.getName());
	}

	/**
	 * Compare two potentially dotted names for relative order. Returns a negative
	 * integer, zero, or a positive integer if the reference is ordered less than, equal
	 * to, or greater than that of the given name.
	 *
	 * <pre>
	 *    , a   => -1
	 * a  , a.b => -1
	 * a.d, c.b => -1
	 * a.b, a.c => -1
	 *    ,     => 0
	 * a  , a   => 0
	 * a.b, a.b => 0
	 * a  ,     => +1
	 * b  , a.b => +1
	 * c  , a.b => +1
	 * a.b, a   => +1
	 * a.c, a.b => +1
	 * </pre>
	 *
	 * @param ref  the reference name
	 * @param name the target name
	 * @return a relative ordering indicator
	 */
	public static int compare(String ref, String name) {
		return compare(parse(ref), parse(name));
	}

	/**
	 * Compare collections of elements for relative order. Returns a negative, zero, or
	 * positive integer if the reference is to be ordered before (less than), same (equal
	 * to), or after (greater than) that of the given target.
	 * <p>
	 * The collections must:
	 * <ol>
	 * <li>not be {@code null}
	 * <li>have well-defined iteration order
	 * <li>contain only {@code Comparable} elements
	 * </ol>
	 *
	 * @param <E> the underlying element type
	 * @param ref the reference element list
	 * @param tgt the target element list
	 * @return a relative ordering indicator
	 */
	public static <E extends Comparable<E>> int compare(Collection<E> ref, Collection<E> tgt) {
		Assert.notNull(ref, tgt);
		Iterator<E> rItr = ref.iterator();
		Iterator<E> tItr = tgt.iterator();
		while (rItr.hasNext() || tItr.hasNext()) {
			E a, b;
			if (rItr.hasNext()) {
				a = rItr.next();
			} else {
				return -1;
			}

			if (tItr.hasNext()) {
				b = tItr.next();
			} else {
				return 1;
			}

			int c = a.compareTo(b);
			if (c != 0) return c;
		}
		return 0;
	}

	/**
	 * Compare two potentially dotted strings for rooted, relative order.
	 * <p>
	 * Returns an ordering indicia of:
	 * <ul>
	 * <li>-2: before (no common root)
	 * <li>-1: before (has common root)
	 * <li>+0: same (has common root)
	 * <li>+1: after (has common root)
	 * <li>+2: after (no common root)
	 * </ul>
	 * Exampes:
	 *
	 * <pre>
	 * ref: tgt
	 *    :     => 0
	 *    : ""  => -2
	 *    : a   => -2
	 *
	 * "" :     => 2
	 * "" : ""  => 0
	 * "" : a   => -2
	 *
	 * a  : a   => 0
	 * a  : b   => -2
	 * a  : a.b => -1
	 * a.b: a   => 1
	 * a.b: a   => -2
	 *
	 * b  : a   => 2
	 * b  : b   => 0
	 * b  : a.b => 2
	 * b  : b.c => -1
	 * b.c: b   => 1
	 * </pre>
	 *
	 * @param ref the reference name
	 * @param tgt the target name
	 * @return ordering indicator
	 */
	public static int within(String ref, String tgt) {
		return within(parse(ref), parse(tgt));
	}

	/**
	 * Compare two collections of elements for rooted, relative order.
	 * <p>
	 * The collections must:
	 * <ol>
	 * <li>not be {@code null}
	 * <li>have well-defined iteration order
	 * <li>contain only {@code Comparable} elements
	 * </ol>
	 * Returns an ordering indicia of:
	 * <ul>
	 * <li>-2: before (no common root)
	 * <li>-1: before (has common root)
	 * <li>+0: same (has common root)
	 * <li>+1: after (has common root)
	 * <li>+2: after (no common root)
	 * </ul>
	 * Exampes:
	 *
	 * <pre>
	 * ref: tgt
	 *    :     => 0
	 *    : ""  => -2
	 *    : a   => -2
	 *
	 * "" :     => 2
	 * "" : ""  => 0
	 * "" : a   => -2
	 *
	 * a  : a   => 0
	 * a  : b   => -2
	 * a  : a.b => -1
	 * a.b: a   => 1
	 * a.b: a   => -2
	 *
	 * b  : a   => 2
	 * b  : b   => 0
	 * b  : a.b => 2
	 * b  : b.c => -1
	 * b.c: b   => 1
	 * </pre>
	 *
	 * @param <E> the underlying element type
	 * @param ref the reference element list
	 * @param tgt the target element list
	 * @return ordering indicator
	 */
	public static <E extends Comparable<E>> int within(Collection<E> ref, Collection<E> tgt) {
		Assert.notNull(ref, tgt);
		boolean w = isWithin(ref, tgt);
		Iterator<E> rItr = ref.iterator();
		Iterator<E> tItr = tgt.iterator();
		while (rItr.hasNext() || tItr.hasNext()) {
			E a, b;
			if (rItr.hasNext()) {
				a = rItr.next();
			} else {
				return w ? -1 : -2;
			}

			if (tItr.hasNext()) {
				b = tItr.next();
			} else {
				return w ? 1 : 2;
			}

			int c = a.compareTo(b);
			if (c != 0) return w ? c : c * 2;
		}
		return 0;
	}

	/**
	 * Determines whether the target shares a common root with the reference.
	 *
	 * @param <E> the underlying element type
	 * @param ref the reference element list
	 * @param tgt the target element list
	 * @return {@code true} if both lists have a common root
	 */
	public static <E extends Comparable<E>> boolean isWithin(Collection<E> ref, Collection<E> tgt) {
		Iterator<E> rItr = ref.iterator();
		Iterator<E> tItr = tgt.iterator();

		E a = rItr.hasNext() ? rItr.next() : null;
		E b = tItr.hasNext() ? tItr.next() : null;

		return a == b || a != null && a.equals(b);
	}

	/**
	 * Parse the given element into a list of name elements using a single DOT regex
	 * pattern to split the element.
	 *
	 * @param element a source element
	 * @return a list of name elements
	 */
	public static List<String> parse(String element) {
		if (element == null || element.isBlank()) return Collections.emptyList();
		return parse(Arrays.asList(element));
	}

	/**
	 * Parse the given elements into a list of name elements using a single DOT regex
	 * pattern to split each of the elements.
	 *
	 * @param elements a sequence of elements
	 * @return a list of name elements
	 */
	public static List<String> parse(String... elements) {
		if (elements == null || elements.length == 0) return Collections.emptyList();
		return parse(Arrays.asList(elements));
	}

	/**
	 * Parse the given elements into a list of name elements using a single DOT regex
	 * pattern to split each of the elements.
	 *
	 * @param elements a list of elements
	 * @return a list of name elements
	 */
	public static List<String> parse(List<String> elements) {
		List<String> results = new ArrayList<>();
		if (elements != null) elements.forEach(e -> results.addAll(parse(DOTTED, e)));
		return results;
	}

	/**
	 * Parse the given source text into a list of name elements using the given pattern to
	 * split the source.
	 *
	 * @param pattern a regex pattern
	 * @param source  a source text
	 * @return a list of name elements
	 */
	public static List<String> parse(Pattern pattern, String source) {
		Assert.notNull(pattern, source);
		return Arrays.asList(pattern.split(source, 0));
	}
}
