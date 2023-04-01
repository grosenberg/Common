package net.certiv.common.util;

import java.lang.reflect.Array;
import java.util.Arrays;
import java.util.Collection;
import java.util.stream.StreamSupport;

/**
 * {@code Check} is useful for for embedding runtime sanity checks in code.
 */
public final class Check {

	private Check() {}

	/**
	 * Checks that the given single {@code arg} value is {@code null}.
	 *
	 * @param arg the simple object to check
	 * @return {@code true} if the arg object is {@code null}
	 */
	public static boolean isNull(Object arg) {
		if (arg == null) return true;
		if (arg.getClass().isArray()) {
			int len = Array.getLength(arg);
			if (len == 0) return false;
			for (int idx = 0; idx < len; idx++) {
				if (isNull(Array.get(arg, idx))) return true;
			}
			return false;
		}
		if (arg instanceof Collection<?>) return isNull((Collection<?>) arg);
		if (arg instanceof Iterable<?>) return isNull((Iterable<?>) arg);
		return false;
	}

	/**
	 * Checks that the given array or an included element is {@code null}.
	 *
	 * @param arg the array object to check
	 * @return {@code true} if the arg array is or contains {@code null}
	 */
	public static boolean isNull(Object... args) {
		if (args == null) return true;
		if (args.length == 0) return false;
		return Arrays.stream(args).anyMatch(v -> isNull(v));
	}

	/**
	 * Checks that the given collection or an included element is {@code null}.
	 *
	 * @param arg the collection to check
	 * @return {@code true} if the collection is or contains {@code null}
	 */
	public static boolean isNull(Collection<?> coll) {
		if (coll == null) return true;
		if (coll.isEmpty()) return false;
		return coll.stream().anyMatch(v -> isNull(v));
	}

	/**
	 * Checks that the given iterable or an included element is {@code null}.
	 *
	 * @param itr the iterable to check
	 * @return {@code true} if the iterable is or contains {@code null}
	 */
	public static boolean isNull(Iterable<?> itr) {
		if (itr == null) return true;
		return StreamSupport.stream(itr.spliterator(), false).anyMatch(v -> isNull(v));
	}

	/**
	 * Checks that the given simple {@code arg} value is not {@code null}.
	 *
	 * @param arg the simple object to check
	 * @return {@code true} if the arg object is not {@code null}
	 */
	public static boolean notNull(Object arg) {
		return !isNull(arg);
	}

	/**
	 * Checks that the given array and any included elements are not {@code null}.
	 *
	 * @param arg the array object to check
	 * @return {@code true} if the arg array is not {@code null}
	 */
	public static boolean notNull(Object... args) {
		return !isNull(args);
	}

	/**
	 * Checks that the given collection and any included elements are not {@code null}.
	 *
	 * @param arg the collection to check
	 * @return {@code true} if the collection is not and does not contain {@code null}
	 */
	public static boolean notNull(Collection<?> args) {
		return !isNull(args);
	}

	/**
	 * Checks that the given iterable or an included element is not {@code null}.
	 *
	 * @param itr the iterable to check
	 * @return {@code true} if the iterable is not and does not contain {@code null}
	 */
	public static boolean notNull(Iterable<?> itr) {
		return !isNull(itr);
	}

	/**
	 * Checks that the given {@code arg} is {@code null} or its string equivalent is
	 * {@code empty}.
	 *
	 * @param arg the object to test
	 * @return {@code true} if the arg object is effectively {@code empty}
	 */
	public static boolean empty(Object arg) {
		if (arg == null) return true;
		if (arg.getClass().isArray()) {
			int len = Array.getLength(arg);
			if (len == 0) return true;
			for (int idx = 0; idx < len; idx++) {
				if (empty(Array.get(arg, idx))) return true;
			}
			return false;
		}
		if (arg instanceof Collection<?>) return empty((Collection<?>) arg);
		if (arg instanceof Iterable<?>) return empty((Iterable<?>) arg);
		return arg.toString().isEmpty();
	}

	/**
	 * Checks that the given {@code arg} array is {@code null}, contains no elements, or
	 * all elements are string equivalent to {@code empty}.
	 *
	 * @param arg the args array to test
	 * @return {@code true} if the args array or contained elements are effectively
	 *         {@code empty}
	 */
	public static boolean empty(Object... args) {
		return args == null || args.length == 0 || Arrays.stream(args).anyMatch(v -> empty(v));
	}

	/**
	 * Checks that the given {@code arg} collection is {@code null}, contains no elements,
	 * or all elements are string equivalent to {@code empty}.
	 *
	 * @param arg the args collection to test
	 * @return {@code true} if the collection or contained elements are effectively
	 *         {@code empty}
	 */
	public static boolean empty(Collection<?> coll) {
		return coll == null || coll.isEmpty() || coll.stream().anyMatch(v -> empty(v));
	}

	/**
	 * Checks that the given iterable or an included element is {@code null} or
	 * {@code empty}.
	 *
	 * @param itr the iterable to check
	 * @return {@code true} if the iterable is {@code null} or {@code empty}
	 */
	public static boolean empty(Iterable<?> itr) {
		if (itr == null || !itr.iterator().hasNext()) return true;
		return StreamSupport.stream(itr.spliterator(), false).anyMatch(v -> empty(v));
	}

	/**
	 * Checks that the given {@code arg} is not {@code null} and its string equivalent is
	 * not {@code empty}.
	 *
	 * @param arg the object to test
	 * @return {@code true} if the arg object is effectively not {@code empty}
	 */
	public static boolean notEmpty(Object arg) {
		return !empty(arg);
	}

	/**
	 * Checks that the given {@code arg} array is not {@code null}, not {@code empty}, and
	 * the individual elements are {@code notEmpty}.
	 *
	 * @param arg the args array to test
	 * @return {@code true} if the args array and contained elements are effectively not
	 *         {@code empty}
	 */
	public static boolean notEmpty(Object... args) {
		return !empty(args);
	}

	/**
	 * Checks that the given collection is not {@code null}, not {@code empty}, and the
	 * individual elements are {@code notEmpty}.
	 *
	 * @param arg the args collection to test
	 * @return {@code true} if the collection and contained elements are not {@code empty}
	 */
	public static boolean notEmpty(Collection<?> coll) {
		return !empty(coll);
	}

	/**
	 * Checks that the given iterable or an included element is not {@code null}.
	 *
	 * @param itr the iterable to check
	 * @return {@code true} if the iterable is not and does not contain {@code null}
	 */
	public static boolean notEmpty(Iterable<?> itr) {
		return !empty(itr);
	}
}
