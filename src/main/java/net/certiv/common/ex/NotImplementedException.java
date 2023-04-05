package net.certiv.common.ex;

public class NotImplementedException extends RuntimeException {

	public NotImplementedException() {
		super();
	}

	public NotImplementedException(String message) {
		super(message);
	}

	/** Creates an exception with the given composed message. */
	public NotImplementedException(String fmt, Object... args) {
		super(String.format(fmt, args));
	}
}
