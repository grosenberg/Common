package net.certiv.common.graph.ex;

import net.certiv.common.ex.IAssertException.Test;
import net.certiv.common.ex.IType;

public class GraphEx {

	// Generalized --------------------

	public static GraphException of(IType type) {
		return new GraphException(type);
	}

	public static GraphException of(IType type, String msg) {
		return new GraphException(type, msg);
	}

	public static GraphException of(IType type, String fmt, Object... args) {
		return new GraphException(type, String.format(fmt, args));
	}

	public static GraphException of(IType type, Throwable cause) {
		return new GraphException(type, cause);
	}

	public static GraphException of(IType type, Throwable cause, String msg) {
		return new GraphException(type, msg, cause);
	}

	public static GraphException of(IType type, Throwable cause, String fmt, Object... args) {
		return new GraphException(type, String.format(fmt, args), cause);
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
