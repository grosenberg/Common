package net.certiv.common.check.ex;

import net.certiv.common.check.ex.IAssertException.Test;

public class AssertEx {

	public static AssertException of() {
		return new AssertException();
	}

	public static AssertException of(Test type) {
		return new AssertException(type);
	}

	public static AssertException of(String msg) {
		return new AssertException(Test.OTHER, msg);
	}

	public static AssertException of(Test type, String msg) {
		return new AssertException(type, msg);
	}

	public static AssertException of(String fmt, Object... args) {
		return new AssertException(Test.OTHER, String.format(fmt, args));
	}

	public static AssertException of(Test type, String fmt, Object... args) {
		return new AssertException(type, String.format(fmt, args));
	}

	public static AssertException of(Throwable cause) {
		return new AssertException(Test.OTHER, cause);
	}

	public static AssertException of(Test type, Throwable cause) {
		return new AssertException(type, cause);
	}

	public static AssertException of(Throwable cause, String msg) {
		return new AssertException(Test.OTHER, msg, cause);
	}

	public static AssertException of(Test type, Throwable cause, String msg) {
		return new AssertException(type, msg, cause);
	}

	public static AssertException of(Throwable cause, String fmt, Object... args) {
		return new AssertException(Test.OTHER, String.format(fmt, args), cause);
	}

	public static AssertException of(Test type, Throwable cause, String fmt, Object... args) {
		return new AssertException(type, String.format(fmt, args), cause);
	}
}
