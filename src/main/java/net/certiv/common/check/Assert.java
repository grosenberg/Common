package net.certiv.common.check;

import static net.certiv.common.ex.IAssertException.ASSERT_FAILED;
import static net.certiv.common.ex.IAssertException.EMPTY_ARG;
import static net.certiv.common.ex.IAssertException.NULL_ARG;

import net.certiv.common.ex.AssertEx;
import net.certiv.common.ex.AssertException;
import net.certiv.common.ex.IAssertException;
import net.certiv.common.ex.IAssertException.Test;
import net.certiv.common.stores.Result;
import net.certiv.common.util.Reflect;
import net.certiv.common.util.Strings;

/**
 * {@code Assert} is useful for for embedding runtime sanity checks in code. The predicate
 * methods all test a condition and, if the condition does not hold, throw an unchecked
 * exception, either as given or the default {@code AssertionFailedException}.
 */
public final class Assert {

	private static final String FMT0 = "[%s]"; //$NON-NLS-1$
	private static final String FMT1 = "[%s] %s"; //$NON-NLS-1$
	private static final String FMT2 = "[%s] %s: %s"; //$NON-NLS-1$

	private Assert() {}

	/**
	 * Asserts that the given expression is {@code true}. If {@code false}, throws an
	 * unchecked exception to aid debugging.
	 *
	 * @param expr the expression to check
	 */
	public static void isTrue(boolean expr) {
		isTrue(expr, (String) null);
	}

	/**
	 * Asserts that the given expression is {@code true}. If {@code false}, throws an
	 * unchecked exception with the given message to aid debugging.
	 *
	 * @param expr the expression to check
	 * @param msg  the failed check exception message
	 */
	public static void isTrue(boolean expr, String msg) {
		if (!expr) throw exception(Test.IS_TRUE, ASSERT_FAILED, msg);
	}

	/**
	 * Asserts that the given expression is {@code true}. If {@code false}, the given
	 * unchecked exception is thrown with the given message to aid debugging.
	 *
	 * @param expr the expression to check
	 * @param fmt  the failed check exception message format
	 * @param args the message arguments for formatting
	 */
	public static void isTrue(boolean expr, String fmt, Object... args) {
		if (!expr) throw exception(Test.IS_TRUE, ASSERT_FAILED, String.format(fmt, args));
	}

	/**
	 * Asserts that the given expression is {@code true}. If {@code false}, the given
	 * unchecked exception is thrown to aid debugging.
	 *
	 * @param ex   the unchecked exception to throw on failure
	 * @param expr the expression to check
	 */
	public static <E extends RuntimeException> void isTrue(E ex, boolean expr) {
		if (!expr) {
			if (ex instanceof IAssertException) ((IAssertException) ex).setType(Test.IS_TRUE);
			throw ex;
		}
	}

	// -----------------------------

	/**
	 * Asserts that the given element is not {@code null} and that any collection-style
	 * element does not itself include any {@code null} element (deep examination). Throws
	 * the default unchecked exception on failure.
	 *
	 * @param arg the object to check
	 */
	public static void notNull(Object arg) {
		if (Check.isNull(arg)) throw exception(Test.NOT_NULL, NULL_ARG).setElement(arg);
	}

	/**
	 * Asserts that the given elements are not {@code null} and that any included
	 * collection-style element does not itself include any {@code null} element (deep
	 * examination). Throws the given unchecked exception on failure.
	 *
	 * @param args the object array to check
	 */
	public static void notNull(Object... args) {
		if (Check.isNull(args)) throw exception(Test.NOT_NULL, NULL_ARG).setElements(args);
	}

	/**
	 * Asserts that the given element is not {@code null} and that any included
	 * collection-style element does not itself include any {@code null} element (deep
	 * examination). Throws the given unchecked exception on failure.
	 *
	 * @param ex  the unchecked exception to throw
	 * @param arg the object array to check
	 */
	public static <E extends RuntimeException> void notNull(E ex, Object arg) {
		if (Check.isNull(arg)) {
			if (ex instanceof IAssertException) {
				((IAssertException) ex).setType(Test.NOT_NULL);
				((IAssertException) ex).setElement(arg);
			}
			throw ex;
		}
	}

	/**
	 * Asserts that the given elements are not {@code null} and that any included
	 * collection-style element does not itself include any {@code null} element (deep
	 * examination). Throws the given unchecked exception on failure.
	 *
	 * @param ex   the unchecked exception to throw
	 * @param args the object array to check
	 */
	public static <E extends RuntimeException> void notNull(E ex, Object... args) {
		if (Check.isNull(args)) {
			if (ex instanceof IAssertException) {
				((IAssertException) ex).setType(Test.NOT_NULL);
				((IAssertException) ex).setElement(args);
			}
			throw ex;
		}
	}

	/**
	 * Asserts that the given elements are not {@code empty} and that any included
	 * collection-style element does not itself include any {@code empty} element (deep
	 * examination). Throws the given unchecked exception on failure.
	 * <p>
	 * Empty means zero-length or {@code null}.
	 *
	 * @param arg the value to check
	 */
	public static void notEmpty(Object arg) {
		if (Check.empty(arg)) throw exception(Test.NOT_EMPTY, EMPTY_ARG).setElement(arg);
	}

	/**
	 * Asserts that the given elements are not {@code empty} and that any included
	 * collection-style element does not itself include any {@code empty} element (deep
	 * examination). Throws the given unchecked exception on failure.
	 * <p>
	 * Empty means zero-length or {@code null}.
	 *
	 * @param args the values to check
	 */
	public static void notEmpty(Object... args) {
		if (Check.empty(args)) throw exception(Test.NOT_EMPTY, EMPTY_ARG).setElements(args);
	}

	/**
	 * Asserts that the given element is not {@code empty} and that any included
	 * collection-style element does not itself include any {@code empty } element (deep
	 * examination). Throws the given unchecked exception on failure.
	 *
	 * @param ex  the unchecked exception to throw
	 * @param arg the object array to check
	 */
	public static <E extends RuntimeException> void notEmpty(E ex, Object arg) {
		if (Check.empty(arg)) {
			if (ex instanceof IAssertException) {
				((IAssertException) ex).setType(Test.NOT_EMPTY);
				((IAssertException) ex).setElement(arg);
			}
			throw ex;
		}
	}

	/**
	 * Asserts that the given elements are not {@code empty} and that any included
	 * collection-style element does not itself include any {@code empty} element (deep
	 * examination). Throws the given unchecked exception on failure.
	 * <p>
	 * Empty means zero-length or {@code null}.
	 *
	 * @param ex   the unchecked exception class
	 * @param args the object array to check
	 */
	public static <E extends RuntimeException> void notEmpty(E ex, Object... args) {
		if (Check.empty(args)) {
			if (ex instanceof IAssertException) {
				((IAssertException) ex).setType(Test.NOT_EMPTY);
				((IAssertException) ex).setElements(args);
			}
			throw ex;
		}
	}

	/**
	 * Make a specific {@code RuntimeException} to report assertion failures.
	 *
	 * @param cls  the unchecked exception class
	 * @param type the exception type qualifier
	 * @param fmt  the exception message (if args is empty), or message format otherwise
	 * @param args the message parameters (may be empty)
	 * @return specific unchecked exception instance
	 */
	public static RuntimeException make(Class<? extends RuntimeException> cls, Test type, String fmt,
			Object... args) {
		String msg = args != null && args.length > 0 ? String.format(fmt, args) : fmt;
		msg = String.format(FMT1, type, msg);
		Result<? extends RuntimeException> res = Reflect.make(cls, type, msg);
		return res.valid() ? res.get() : new RuntimeException(res.getErr());
	}

	private static AssertException exception(Test type, String msg) {
		if (Strings.blank(msg)) {
			msg = String.format(FMT0, type);
		} else {
			msg = String.format(FMT1, type, msg);
		}
		return AssertEx.of(type, msg);
	}

	private static AssertException exception(Test type, String reason, String msg) {
		if (Strings.blank(reason, msg)) {
			msg = String.format(FMT0, type);
		} else {
			msg = String.format(FMT2, type, reason, msg);
		}
		return AssertEx.of(type, msg);
	}
}
