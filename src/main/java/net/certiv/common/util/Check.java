package net.certiv.common.util;

import java.util.Arrays;
import java.util.Collection;

/**
 * {@code Check} is useful for for embedding runtime sanity checks in code.
 */
public final class Check {

	private Check() {}

	/**
	 * Checks that the given simple {@code arg} value is {@code null}.
	 *
	 * @param arg the simple object to check
	 * @return {@code true} if the arg object is {@code null}
	 */
	public static boolean isNull(Object arg) {
		return arg == null;
	}

	/**
	 * Checks that the given array or an included element is {@code null}.
	 *
	 * @param arg the array object to check
	 * @return {@code true} if the arg array is {@code null}
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
	 * @return {@code true} if the collection is {@code null}
	 */
	public static boolean isNull(Collection<?> args) {
		if (args == null) return true;
		if (args.isEmpty()) return false;
		return args.stream().anyMatch(v -> isNull(v));
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
	 * Checks that the given collection and any included elements are not
	 * {@code null}.
	 *
	 * @param arg the collection to check
	 * @return {@code true} if the collection is not {@code null}
	 */
	public static boolean notNull(Collection<?> args) {
		return !isNull(args);
	}

	/**
	 * Checks that the given {@code arg} is {@code null} or its string equivalent is
	 * {@code empty}.
	 *
	 * @param arg the object to test
	 * @return {@code true} if the arg object is effectively {@code empty}
	 */
	public static boolean empty(Object arg) {
		return arg == null || arg.toString().isEmpty();
	}

	/**
	 * Checks that the given {@code arg} array is {@code null}, contains no
	 * elements, or all elements are string equivalent to {@code empty}.
	 *
	 * @param arg the args array to test
	 * @return {@code true} if the args array or contained elements are effectively
	 *             {@code empty}
	 */
	public static boolean empty(Object... args) {
		return args == null || args.length == 0 || Arrays.stream(args).allMatch(v -> empty(v));
	}

	/**
	 * Checks that the given {@code arg} collection is {@code null}, contains no
	 * elements, or all elements are string equivalent to {@code empty}.
	 *
	 * @param arg the args collection to test
	 * @return {@code true} if the collection or contained elements are effectively
	 *             {@code empty}
	 */
	public static boolean empty(Collection<?> args) {
		return args == null || args.isEmpty() || args.stream().allMatch(v -> empty(v));
	}

	/**
	 * Checks that the given {@code arg} is not {@code null} and its string
	 * equivalent is not {@code empty}.
	 *
	 * @param arg the object to test
	 * @return {@code true} if the arg object is effectively not {@code empty}
	 */
	public static boolean notEmpty(Object arg) {
		return arg != null && !arg.toString().isEmpty();
	}

	/**
	 * Checks that the given {@code arg} array is not {@code null}, not
	 * {@code empty}, and the individual elements are {@code notEmpty}.
	 *
	 * @param arg the args array to test
	 * @return {@code true} if the args array and contained elements are effectively
	 *             not {@code empty}
	 */
	public static boolean notEmpty(Object... args) {
		return args != null && args.length > 0 && Arrays.stream(args).allMatch(v -> notEmpty(v));
	}

	/**
	 * Checks that the given {@code arg} collection is not {@code null}, not
	 * {@code empty}, and the individual elements are {@code notEmpty}.
	 *
	 * @param arg the args collection to test
	 * @return {@code true} if the collection and contained elements are not
	 *             {@code empty}
	 */
	public static boolean notEmpty(Collection<?> args) {
		return args != null && !args.isEmpty() && args.stream().allMatch(v -> notEmpty(v));
	}
}
