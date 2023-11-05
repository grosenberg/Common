package net.certiv.common.util;

import java.io.PrintWriter;
import java.io.StringWriter;

import org.apache.commons.lang3.exception.ExceptionUtils;

public class ExceptUtil {

	/**
	 * Returns a simple exception message extracted from the given throwable.
	 *
	 * @param ex a throwable
	 * @return a simple exception message
	 */
	public static String getMessage(Throwable ex) {
		return getMessage(ex, false);
	}

	/**
	 * Returns a simple or full form exception message extracted from the given throwable.
	 *
	 * @param ex   a throwable
	 * @param full elects to return a possibly full form message if {@code true}
	 * @return a simple or full form exception message
	 */
	public static String getMessage(Throwable ex, boolean full) {
		if (ex == null) return Strings.EMPTY;
		String msg = ex.getMessage();
		if (msg != null) return msg;

		Throwable cause = ex.getCause();
		if (cause != null) {
			msg = cause.getMessage();
			if (msg != null) {
				if (full) return msg;

				// simplify
				msg = msg.trim().split(Strings.EOL)[0].trim();
				if (msg.endsWith(Strings.COLON)) {
					msg = msg.substring(0, msg.length() - 2);
				}
				return msg;
			}
		}
		return Strings.EMPTY;
	}

	/**
	 * Returns a root cause stack trace of the given {@link Throwable}.
	 *
	 * @param t throwable to introspect
	 * @return a root cause stack trace of the given throwable
	 */
	public static String rootTrace(Throwable t) {
		return stacktrace(ExceptionUtils.getRootCause(t));
	}

	/**
	 * Returns a root cause description of the given {@link Throwable}.
	 *
	 * @param t throwable to introspect
	 * @return a root cause summary description of the given throwable
	 */
	public static String rootCause(Throwable t) {
		MsgBuilder mb = new MsgBuilder();

		Throwable cause = ExceptionUtils.getRootCause(t);
		String msg = cause.getMessage();
		mb.append("%s: %s", cause.getClass().getSimpleName(), msg != null ? msg : Strings.UNKNOWN);

		StackTraceElement[] traces = cause.getStackTrace();
		if (traces.length > 0) {
			StackTraceElement elem = traces[0];
			mb.append(" :: %s.%s @%s", //
					elem.getClassName(), elem.getMethodName(), elem.getLineNumber());
		}
		return mb.toString();
	}

	/**
	 * Returns a cause chain description of the given {@link Throwable}.
	 *
	 * @param t throwable to introspect
	 * @return a cause chain summary description of the given throwable
	 */
	public static String causes(Throwable t) {
		MsgBuilder mb = new MsgBuilder();
		for (Throwable cause : ExceptionUtils.getThrowableList(t)) {
			String msg = cause.getMessage();
			mb.nl().append("%s: %s", cause.getClass().getSimpleName(), msg != null ? msg : Strings.UNKNOWN);
			StackTraceElement[] traces = cause.getStackTrace();
			if (traces.length > 0) {
				StackTraceElement elem = traces[0];
				mb.nl().indent().append("%s.%s @%s", //
						elem.getClassName(), elem.getMethodName(), elem.getLineNumber());
			}
		}
		return mb.toString();
	}

	/**
	 * Return a printable stack trace of the given {@link Throwable}.
	 *
	 * @param e throwable to introspect
	 * @return a string representation of the stack trace of the given throwable
	 */
	public static String stacktrace(Throwable e) {
		if (e == null) return Strings.EMPTY;
		StringWriter sw = new StringWriter();
		e.printStackTrace(new PrintWriter(sw));
		return sw.toString();
	}
}
