package net.certiv.common.util;

import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.LinkedList;

/** Collections related convenience methods. */
@Deprecated
public final class Make {

	private Make() {}

	@SafeVarargs
	public static <V> LinkedHashSet<V> set(V... args) {
		return new LinkedHashSet<>(Arrays.asList(args));
	}

	@SafeVarargs
	public static <V> LinkedList<V> list(V... args) {
		return new LinkedList<>(Arrays.asList(args));
	}
}
