package net.certiv.common.util;

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
}
