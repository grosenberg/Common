package net.certiv.common.graph.ex;

/**
 * General unchecked exception representing a {@code Graph} transform failure of some
 * type.
 */
public class TransformException extends RuntimeException {

	public TransformException() {
		super();
	}

	public TransformException(String msg) {
		super(msg);
	}

	/** Creates an exception with the given composed message. */
	public TransformException(String fmt, Object... args) {
		super(String.format(fmt, args));
	}

	/** Creates an exception that wraps the given exception. */
	public TransformException(Throwable cause) {
		super(cause);
	}

	public TransformException(Throwable cause, String msg) {
		super(msg, cause);
	}

	public TransformException(Throwable cause, String fmt, Object... args) {
		super(String.format(fmt, args), cause);
	}
}
