package net.certiv.common.util;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;

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
	 * Returns a simple or full form exception message extracted from the given
	 * throwable.
	 *
	 * @param ex a throwable
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

	public static List<String> causes(Throwable t) {
		if (t == null) return List.of();

		List<String> causes = new ArrayList<>();
		causes.add(msg(t));

		Throwable cause = t.getCause();
		while (cause != null) {
			causes.add(msg(t));
			cause = cause.getCause();
		}

		return causes;
	}

	private static String msg(Throwable t) {
		String msg = t.getMessage();
		msg = msg != null ? msg : "<no detail>";
		return String.format("%s [%s]", msg, t.getClass().getSimpleName());
	}

	public static String stacktrace(Throwable t) {
		StringWriter sw = new StringWriter();
		t.printStackTrace(new PrintWriter(sw));
		return sw.toString();
	}
}
