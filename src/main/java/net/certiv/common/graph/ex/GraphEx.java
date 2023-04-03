package net.certiv.common.graph.ex;

import net.certiv.common.check.ex.IAssertException.Test;

public class GraphEx {

	public static GraphException of() {
		return new GraphException();
	}

	public static GraphException of(Test type) {
		return new GraphException(type);
	}

	public static GraphException of(String msg) {
		return new GraphException(Test.OTHER, msg);
	}

	public static GraphException of(Test type, String msg) {
		return new GraphException(type, msg);
	}

	public static GraphException of(String fmt, Object... args) {
		return new GraphException(Test.OTHER, String.format(fmt, args));
	}

	public static GraphException of(Test type, String fmt, Object... args) {
		return new GraphException(type, String.format(fmt, args));
	}

	public static GraphException of(Throwable cause) {
		return new GraphException(Test.OTHER, cause);
	}

	public static GraphException of(Test type, Throwable cause) {
		return new GraphException(type, cause);
	}

	public static GraphException of(Throwable cause, String msg) {
		return new GraphException(Test.OTHER, msg, cause);
	}

	public static GraphException of(Test type, Throwable cause, String msg) {
		return new GraphException(type, msg, cause);
	}

	public static GraphException of(Throwable cause, String fmt, Object... args) {
		return new GraphException(Test.OTHER, String.format(fmt, args), cause);
	}

	public static GraphException of(Test type, Throwable cause, String fmt, Object... args) {
		return new GraphException(type, String.format(fmt, args), cause);
	}
}
