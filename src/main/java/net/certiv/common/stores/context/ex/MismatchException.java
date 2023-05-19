package net.certiv.common.stores.context.ex;

@Deprecated
public class MismatchException extends RuntimeException {

	public MismatchException(String message) {
		super(message);
	}

	public MismatchException(String fmt, Object... args) {
		super(String.format(fmt, args));
	}

	public MismatchException(Throwable cause) {
		super(cause);
	}

	public MismatchException(Throwable cause, String fmt, Object... args) {
		super(String.format(fmt, args), cause);
	}
}
