package net.certiv.common.ex;

import java.io.IOException;

import net.certiv.common.util.MsgBuilder;

public class IOEx extends IOException {

	/**
	 * Creates an {@link IOException} with the given message.
	 *
	 * @param msg message
	 * @return {@link IOException}
	 */
	public static IOEx of(String msg) {
		return new IOEx(msg);
	}

	/**
	 * Creates an {@link IOException} with a message formatted with
	 * {@link String#format(String,Object...)}.
	 *
	 * @param t   the throwable cause
	 * @param msg message
	 * @return {@link IOException}
	 */
	public static IOEx of(Throwable t, String msg) {
		return new IOEx(msg, t);
	}

	/**
	 * Creates an {@link IOException} with a message formatted with
	 * {@link String#format(String,Object...)}.
	 *
	 * @param fmt  See {@link String#format(String,Object...)}
	 * @param args See {@link String#format(String,Object...)}
	 * @return {@link IOException}
	 */
	public static IOEx of(String fmt, Object... args) {
		return new IOEx(String.format(fmt, args));
	}

	/**
	 * Creates an {@link IOException} with a message formatted with
	 * {@link String#format(String,Object...)}.
	 *
	 * @param t    the throwable cause
	 * @param fmt  See {@link String#format(String,Object...)}
	 * @param args See {@link String#format(String,Object...)}
	 * @return {@link IOException}
	 */
	public static IOEx of(Throwable t, String fmt, Object... args) {
		return new IOEx(String.format(fmt, args), t);
	}

	private String msg;

	private IOEx(String msg) {
		super(msg);
	}

	private IOEx(String msg, Throwable t) {
		super(msg, t);
	}

	public IOEx on(Object... args) {
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
