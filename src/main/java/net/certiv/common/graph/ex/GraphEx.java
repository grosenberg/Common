package net.certiv.common.graph.ex;

import net.certiv.common.ex.IAssertException.Test;
import net.certiv.common.ex.IGraphExType;

public class GraphEx {

	// Generalized --------------------

	public static GraphException of(IGraphExType graphExType) {
		return new GraphException(graphExType);
	}

	public static GraphException of(IGraphExType graphExType, String msg) {
		return new GraphException(graphExType, msg);
	}

	public static GraphException of(IGraphExType graphExType, String fmt, Object... args) {
		return new GraphException(graphExType, String.format(fmt, args));
	}

	public static GraphException of(IGraphExType graphExType, Throwable cause) {
		return new GraphException(graphExType, cause);
	}

	public static GraphException of(IGraphExType graphExType, Throwable cause, String msg) {
		return new GraphException(graphExType, msg, cause);
	}

	public static GraphException of(IGraphExType graphExType, Throwable cause, String fmt, Object... args) {
		return new GraphException(graphExType, String.format(fmt, args), cause);
	}

	// Legacy: specific to Test -------

	public static GraphException of() {
		return new GraphException();
	}

	public static GraphException of(String msg) {
		return new GraphException(Test.OTHER, msg);
	}

	public static GraphException of(String fmt, Object... args) {
		return new GraphException(Test.OTHER, String.format(fmt, args));
	}

	public static GraphException of(Throwable cause) {
		return new GraphException(Test.OTHER, cause);
	}

	public static GraphException of(Throwable cause, String msg) {
		return new GraphException(Test.OTHER, msg, cause);
	}

	public static GraphException of(Throwable cause, String fmt, Object... args) {
		return new GraphException(Test.OTHER, String.format(fmt, args), cause);
	}
}
