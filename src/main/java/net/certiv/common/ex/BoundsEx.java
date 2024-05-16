package net.certiv.common.ex;

import net.certiv.common.util.MsgBuilder;

public class BoundsEx extends IndexOutOfBoundsException {

	/**
	 * Creates an {@link IndexOutOfBoundsException} with the given message.
	 *
	 * @param msg message
	 * @return {@link IndexOutOfBoundsException}
	 */
	public static BoundsEx of(String msg) {
		return new BoundsEx(msg);
	}

	/**
	 * Creates an {@link IndexOutOfBoundsException} with a message formatted with
	 * {@link String#format(String,Object...)}.
	 *
	 * @param fmt  See {@link String#format(String,Object...)}
	 * @param args See {@link String#format(String,Object...)}
	 * @return {@link IndexOutOfBoundsException}
	 */
	public static BoundsEx of(String fmt, Object... args) {
		return new BoundsEx(String.format(fmt, args));
	}

	private String msg;

	private BoundsEx(String msg) {
		super(msg);
	}

	public BoundsEx formatted(Object... args) {
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
