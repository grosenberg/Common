package net.certiv.tools.util;

public class ObjUtil {

	private ObjUtil() {}

	public static boolean empty(Object arg) {
		return arg == null || arg.toString().isEmpty();
	}

	public static boolean empty(Object[] args) {
		return args == null || args.length == 0;
	}
}
