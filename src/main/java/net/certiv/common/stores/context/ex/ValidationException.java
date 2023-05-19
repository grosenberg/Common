package net.certiv.common.stores.context.ex;

/** Indicates a data validation problem. */
public final class ValidationException extends RuntimeException {

	public ValidationException() {
		super("Validation Exception");
	}

	public ValidationException(final String s) {
		super(s);
	}

	public ValidationException(String fmt, Object... args) {
		super(String.format(fmt, args));
	}

	public ValidationException(Throwable cause) {
		super(cause);
	}

	public ValidationException(Throwable cause, String msg) {
		super(msg, cause);
	}

	public ValidationException(Throwable cause, String fmt, Object... args) {
		super(String.format(fmt, args), cause);
	}
}
