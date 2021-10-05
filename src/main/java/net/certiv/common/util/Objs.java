package net.certiv.common.util;

public class Objs {

	private Objs() {}

	public static boolean empty(Object arg) {
		return arg == null || arg.toString().isEmpty();
	}

	public static boolean empty(Object[] args) {
		return args == null || args.length == 0;
	}

	/**
	 * Returns the given {@code arg} value if not {@code null}, or the given default
	 * value.
	 */
	public static <V> V nonNull(V arg, V def) {
		return arg != null ? arg : def;
	}
}
