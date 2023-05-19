package net.certiv.common.stores.context.ex;

/** Indicates a type validation problem. */
public final class TypeException extends RuntimeException {

	public TypeException() {
		super("Type Exception");
	}

	public TypeException(String msg) {
		super(msg);
	}

	public TypeException(String fmt, Object... args) {
		super(String.format(fmt, args));
	}

	public TypeException(Throwable cause) {
		super(cause);
	}

	public TypeException(Throwable cause, String msg) {
		super(msg, cause);
	}

	public TypeException(Throwable cause, String fmt, Object... args) {
		super(String.format(fmt, args), cause);
	}
}
