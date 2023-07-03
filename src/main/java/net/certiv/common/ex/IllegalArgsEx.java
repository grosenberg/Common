package net.certiv.common.ex;

public class IllegalArgsEx extends IllegalArgumentException {

	/**
	 * Creates an {@link IllegalArgumentException} with a message formatted with
	 * {@link String#format(String,Object...)}.
	 *
	 * @param fmt  See {@link String#format(String,Object...)}
	 * @param args See {@link String#format(String,Object...)}
	 * @return {@link IllegalArgumentException}
	 */
	public static IllegalArgumentException of(String fmt, Object... args) {
		return new IllegalArgumentException(String.format(fmt, args));
	}

	/**
	 * Creates an {@link IllegalArgumentException} with a message formatted with
	 * {@link String#format(String,Object...)}.
	 *
	 * @param t    the throwable cause
	 * @param fmt  See {@link String#format(String,Object...)}
	 * @param args See {@link String#format(String,Object...)}
	 * @return {@link IllegalArgumentException}
	 */
	public static IllegalArgumentException of(Throwable t, String fmt, Object... args) {
		return new IllegalArgumentException(String.format(fmt, args), t);
	}

}
