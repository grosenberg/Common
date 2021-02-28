package net.certiv.common.util;

import java.util.Collection;

import net.certiv.common.ex.AssertionFailedException;

/**
 * {@code Assert} is useful for for embedding runtime sanity checks in code. The
 * predicate methods all test a condition and throw some type of unchecked
 * exception if the condition does not hold.
 * <p>
 * Assertion failure exceptions, like most runtime exceptions, are thrown when
 * something is misbehaving. Assertion failures are invariably unspecified
 * behavior; consequently, clients should never rely on these being thrown (and
 * certainly should not be catching them specifically).
 */
public final class Assert {

	private static final String MSG = "%s: %s"; //$NON-NLS-1$
	private static final String FAILED = "assertion failed"; //$NON-NLS-1$
	private static final String NULL_ARG = "null argument"; //$NON-NLS-1$
	private static final String EMPTY_ARG = "empty argument"; //$NON-NLS-1$

	private Assert() {}

	/**
	 * Asserts that an argument is legal. If the given boolean is not {@code true},
	 * an {@code IllegalArgumentException} is thrown.
	 *
	 * @param expression the outcome of the check
	 * @return {@code true} if the check passes (does not return if the check fails)
	 * @exception IllegalArgumentException if the legality test failed
	 */
	public static boolean isLegal(boolean expression) {
		return isLegal(expression, null);
	}

	/**
	 * Asserts that an argument is legal. If the given boolean is not {@code true},
	 * an {@code IllegalArgumentException} is thrown. The given message is included
	 * in that exception, to aid debugging.
	 *
	 * @param expression the outcome of the check
	 * @param msg the message to include in the exception
	 * @return {@code true} if the check passes (does not return if the check fails)
	 * @exception IllegalArgumentException if the legality test failed
	 */
	public static boolean isLegal(boolean expression, String msg) {
		if (!expression) throw new IllegalArgumentException(msg);
		return expression;
	}

	/**
	 * Asserts that an argument is legal. If the given boolean is not {@code true},
	 * an {@code IllegalArgumentException} is thrown. The given message is included
	 * in that exception, to aid debugging.
	 *
	 * @param expression the outcome of the check
	 * @param format the message format
	 * @param args the message arguments for formatting
	 * @return {@code true} if the check passes (does not return if the check fails)
	 * @exception IllegalArgumentException if the legality test failed
	 */
	public static boolean isLegal(boolean expression, String format, Object... args) {
		return isLegal(expression, String.format(format, args));
	}

	/**
	 * Asserts that the given objects are not {@code null}. If this is not the case,
	 * some kind of unchecked exception is thrown.
	 *
	 * @param objects the values to test
	 */
	public static void notNull(Object... objects) {
		for (Object object : objects) {
			notNull(object, null);
		}
	}

	/**
	 * Asserts that the given object is not {@code null}. If this is not the case,
	 * some kind of unchecked exception is thrown. The given message is included in
	 * that exception, to aid debugging.
	 *
	 * @param object the value to test
	 * @param msg the message to include in the exception
	 */
	public static void notNull(Object object, String msg) {
		if (object == null) throw exception(NULL_ARG, msg);
	}

	/**
	 * Asserts that the given boolean is {@code true}. If this is not the case, some
	 * kind of unchecked exception is thrown.
	 *
	 * @param expression the outcome of the check
	 * @return {@code true} if the check passes (does not return if the check fails)
	 */
	public static boolean isTrue(boolean expression) {
		return isTrue(expression, null);
	}

	/**
	 * Asserts that the given boolean is {@code true}. If this is not the case, some
	 * kind of unchecked exception is thrown. The given message is included in that
	 * exception, to aid debugging.
	 *
	 * @param expression the outcome of the check
	 * @param msg the message to include in the exception
	 * @return {@code true} if the check passes (does not return if the check fails)
	 */
	public static boolean isTrue(boolean expression, String msg) {
		if (!expression) throw exception(FAILED, msg);
		return expression;
	}

	/**
	 * Asserts that the given object array is not {@code null} and, if applicable,
	 * not {@code empty}. If this is not the case, some kind of unchecked exception
	 * is thrown.
	 *
	 * @param object the value to test
	 * @param data the message to include in the exception
	 */
	public static void notEmpty(Object[] values) {
		notEmpty(values, null);
	}

	/**
	 * Asserts that the given object arrayis not {@code null} and, if applicable,
	 * not {@code empty}. If this is not the case, some kind of unchecked exception
	 * is thrown. The given message is included in that exception, to aid debugging.
	 *
	 * @param object the value to test
	 * @param msg the message to include in the exception
	 */
	public static void notEmpty(Object[] values, String msg) {
		if (values == null || values.length == 0) throw exception(EMPTY_ARG, msg);
		for (Object value : values) {
			notEmpty(value);
		}
	}

	/**
	 * Asserts that the given object is not {@code null} and, if applicable, not
	 * {@code empty}. If this is not the case, some kind of unchecked exception is
	 * thrown.
	 *
	 * @param object the value to test
	 * @param data the message to include in the exception
	 */
	public static void notEmpty(Object value) {
		notEmpty(value, null);
	}

	/**
	 * Asserts that the given object is not {@code null} and, if applicable, not
	 * {@code empty}. If this is not the case, some kind of unchecked exception is
	 * thrown. The given message is included in that exception, to aid debugging.
	 *
	 * @param object the value to test
	 * @param msg the message to include in the exception
	 */
	public static void notEmpty(Object value, String msg) {
		if (value == null) throw exception(EMPTY_ARG, msg);
		if (value instanceof CharSequence) {
			if (((CharSequence) value).length() == 0) throw exception(EMPTY_ARG, msg);
		}
		if (value instanceof Collection<?>) {
			if (((Collection<?>) value).isEmpty()) throw exception(EMPTY_ARG, msg);
		}
	}

	private static AssertionFailedException exception(String lead, String text) {
		String msg = lead;
		if (text != null && !text.isEmpty()) {
			msg = String.format(MSG, lead, text);
		}
		return new AssertionFailedException(msg);
	}
}
