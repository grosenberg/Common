package net.certiv.common.ex;

import net.certiv.common.util.MsgBuilder;

public class NotImplEx extends NotImplementedException {

	/**
	 * Creates an {@link NotImplementedException} with the given message.
	 *
	 * @param msg message
	 * @return {@link NotImplementedException}
	 */
	public static NotImplEx of(String msg) {
		return new NotImplEx(msg);
	}

	/**
	 * Creates an {@link NotImplementedException} with a message formatted with
	 * {@link String#format(String,Object...)}.
	 *
	 * @param t   the throwable cause
	 * @param msg message
	 * @return {@link NotImplementedException}
	 */
	public static NotImplEx of(Throwable t, String msg) {
		return new NotImplEx(msg, t);
	}

	/**
	 * Creates an {@link NotImplementedException} with a message formatted with
	 * {@link String#format(String,Object...)}.
	 *
	 * @param fmt  See {@link String#format(String,Object...)}
	 * @param args See {@link String#format(String,Object...)}
	 * @return {@link NotImplementedException}
	 */
	public static NotImplEx of(String fmt, Object... args) {
		return new NotImplEx(String.format(fmt, args));
	}

	/**
	 * Creates an {@link NotImplementedException} with a message formatted with
	 * {@link String#format(String,Object...)}.
	 *
	 * @param t    the throwable cause
	 * @param fmt  See {@link String#format(String,Object...)}
	 * @param args See {@link String#format(String,Object...)}
	 * @return {@link NotImplementedException}
	 */
	public static NotImplEx of(Throwable t, String fmt, Object... args) {
		return new NotImplEx(String.format(fmt, args), t);
	}

	// --------------------------------

	private String msg;

	private NotImplEx(String msg) {
		super(msg);
	}

	private NotImplEx(String msg, Throwable t) {
		super(msg, t);
	}

	public NotImplEx formatted(Object... args) {
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
