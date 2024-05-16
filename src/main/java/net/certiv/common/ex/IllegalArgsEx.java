package net.certiv.common.ex;

import net.certiv.common.util.MsgBuilder;

public class IllegalArgsEx extends IllegalArgumentException {

	/**
	 * Creates an {@link IllegalArgumentException} with the given message.
	 *
	 * @param msg message
	 * @return {@link IllegalArgumentException}
	 */
	public static IllegalArgsEx of(String msg) {
		return new IllegalArgsEx(msg);
	}

	/**
	 * Creates an {@link IllegalArgumentException} with a message formatted with
	 * {@link String#format(String,Object...)}.
	 *
	 * @param t   the throwable cause
	 * @param msg message
	 * @return {@link IllegalArgumentException}
	 */
	public static IllegalArgsEx of(Throwable t, String msg) {
		return new IllegalArgsEx(msg, t);
	}

	/**
	 * Creates an {@link IllegalArgumentException} with a message formatted with
	 * {@link String#format(String,Object...)}.
	 *
	 * @param fmt  See {@link String#format(String,Object...)}
	 * @param args See {@link String#format(String,Object...)}
	 * @return {@link IllegalArgumentException}
	 */
	public static IllegalArgsEx of(String fmt, Object... args) {
		return new IllegalArgsEx(String.format(fmt, args));
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
	public static IllegalArgsEx of(Throwable t, String fmt, Object... args) {
		return new IllegalArgsEx(String.format(fmt, args), t);
	}

	private String msg;

	private IllegalArgsEx(String msg) {
		super(msg);
	}

	private IllegalArgsEx(String msg, Throwable t) {
		super(msg, t);
	}

	public IllegalArgsEx formatted(Object... args) {
		if (args != null && args.length > 0) {
			String fmt = super.getMessage();
			msg = String.format(fmt, args);
		}
		return this;
	}

	@Override
	public String getMessage() {
		if (msg != null) return msg;
		return super.getMessage();
	}

	@Override
	public String toString() {
		return MsgBuilder.of(getClass().getSimpleName()) //
				.append(" %s", getMessage()) //
				.toString();
	}
}
