package net.certiv.common.graph.ex;

/**
 * General unchecked exception representing a {@code Graph} construction failure of some
 * type.
 */
public class GraphBuildException extends RuntimeException {

	public GraphBuildException() {
		super();
	}

	public GraphBuildException(String message) {
		super(message);
	}

	/** Creates an exception with the given composed message. */
	public GraphBuildException(String fmt, Object... args) {
		super(String.format(fmt, args));
	}

	/** Creates an exception that wraps the given exception. */
	public GraphBuildException(Throwable e) {
		super(e);
	}
}
