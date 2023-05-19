package net.certiv.common.check;

import java.lang.reflect.Array;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.Collection;

/**
 * {@code Check} is useful for for embedding runtime sanity checks in code.
 */
public final class Check {

	private Check() {}

	/**
	 * Checks whether the given single {@code arg} value is {@code null}.
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
			Path.of(null);
			return false;
		}
		if (arg instanceof Collection<?>) return isNull((Collection<?>) arg);
		// if (arg instanceof Iterable<?>) return isNull((Iterable<?>) arg);
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

	// /**
	// * Checks that the given iterable or an included element is {@code null}.
	// *
	// * @param itr the iterable to check
	// * @return {@code true} if the iterable is or contains {@code null}
	// */
	// public static boolean isNull(Iterable<?> itr) {
	// if (itr == null) return true;
	// return StreamSupport.stream(itr.spliterator(), false).anyMatch(v -> isNull(v));
	// }

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

	// /**
	// * Checks that the given iterable or an included element is not {@code null}.
	// *
	// * @param itr the iterable to check
	// * @return {@code true} if the iterable is not and does not contain {@code null}
	// */
	// public static boolean notNull(Iterable<?> itr) {
	// return !isNull(itr);
	// }

	/**
	 * Performs a deep test to determine whether the given {@code arg} is either
	 * {@code null} or {@code empty}. Returns {@code true} if <strong>any</strong>
	 * {@code null} or {@code empty} is encountered.
	 *
	 * @param arg the object to test
	 * @return {@code true} if the arg object is empty
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
		// if (arg instanceof Iterable<?>) return empty((Iterable<?>) arg);
		return arg.toString().isEmpty();
	}

	/**
	 * Performs a deep test to determine whether the given array is either {@code null} or
	 * {@code empty}. Returns {@code true} if <strong>any</strong> {@code null} or
	 * {@code empty} is encountered.
	 *
	 * @param args the array to test
	 * @return {@code true} if the array is empty
	 */
	public static boolean empty(Object... args) {
		return args == null || args.length == 0 || Arrays.stream(args).anyMatch(v -> empty(v));
	}

	/**
	 * Performs a deep test to determine whether the given collection is either
	 * {@code null} or {@code empty}. Returns {@code true} if <strong>any</strong>
	 * {@code null} or {@code empty} is encountered.
	 *
	 * @param coll the collection to test
	 * @return {@code true} if the collection is empty
	 */
	public static boolean empty(Collection<?> coll) {
		return coll == null || coll.isEmpty() || coll.stream().anyMatch(v -> empty(v));
	}

	// /**
	// * Performs a deep test to determine whether the given iterable is either {@code
	// null}
	// * or {@code empty}. Returns {@code true} if <strong>any</strong> {@code null} or
	// * {@code empty} is encountered.
	// *
	// * @param itr the iterable to test
	// * @return {@code true} if the iterable is empty
	// */
	// public static boolean empty(Iterable<?> itr) {
	// if (itr == null || !itr.iterator().hasNext()) return true;
	// return StreamSupport.stream(itr.spliterator(), false).anyMatch(v -> empty(v));
	// }

	/**
	 * Performs a deep test to determine whether the given {@code arg} is both not
	 * {@code null} and not {@code empty}. Returns {@code false} if <strong>any</strong>
	 * {@code null} or {@code empty} is encountered.
	 *
	 * @param arg the object to test
	 * @return {@code true} if the arg object is not empty
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

	// /**
	// * Checks that the given iterable or an included element is not {@code null}.
	// *
	// * @param itr the iterable to check
	// * @return {@code true} if the iterable is not and does not contain {@code null}
	// */
	// public static boolean notEmpty(Iterable<?> itr) {
	// return !empty(itr);
	// }

	/**
	 * Performs a deep test to determine whether the given object is either {@code null}
	 * or {@code empty}. Returns {@code true} if <strong>all</strong> elements are
	 * {@code null} or {@code empty}.
	 *
	 * @param arg the object to test
	 * @return {@code true} if the arg object is completely empty
	 */
	public static boolean allEmpty(Object arg) {
		if (arg == null) return true;
		if (arg.getClass().isArray()) {
			int len = Array.getLength(arg);
			if (len == 0) return true;
			boolean all = true;
			for (int idx = 0; idx < len; idx++) {
				all &= allEmpty(Array.get(arg, idx));
			}
			return all;
		}
		if (arg instanceof Collection<?>) return allEmpty((Collection<?>) arg);
		// if (arg instanceof Iterable<?>) return allEmpty((Iterable<?>) arg);
		return arg.toString().isEmpty();
	}

	/**
	 * Performs a deep test to determine whether the given array is either {@code null} or
	 * {@code empty}. Returns {@code true} if <strong>all</strong> elements are
	 * {@code null} or {@code empty}.
	 *
	 * @param arg the array to test
	 * @return {@code true} if the array is completely empty
	 */
	public static boolean allEmpty(Object... args) {
		return args == null || args.length == 0 || Arrays.stream(args).allMatch(v -> allEmpty(v));
	}

	/**
	 * Performs a deep test to determine whether the given collection is either
	 * {@code null} or {@code empty}. Returns {@code true} if <strong>all</strong>
	 * elements are {@code null} or {@code empty}.
	 *
	 * @param coll the collection to test
	 * @return {@code true} if the collection is completely empty
	 */
	public static boolean allEmpty(Collection<?> coll) {
		return coll == null || coll.isEmpty() || coll.stream().allMatch(v -> allEmpty(v));
	}

	// /**
	// * Performs a deep test to determine whether the given iterable is either {@code
	// null}
	// * or {@code empty}. Returns {@code true} if <strong>all</strong> elements are
	// * {@code null} or {@code empty}.
	// *
	// * @param itr the iterable to test
	// * @return {@code true} if the iterable is completely empty
	// */
	// public static boolean allEmpty(Iterable<?> itr) {
	// if (itr == null || !itr.iterator().hasNext()) return true;
	// return StreamSupport.stream(itr.spliterator(), false).allMatch(v -> allEmpty(v));
	// }
}
