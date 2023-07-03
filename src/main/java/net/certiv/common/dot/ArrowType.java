package net.certiv.common.dot;

import java.util.stream.Stream;

public enum ArrowType implements IDotStr {
	box,
	crow,
	curve,
	icurve,
	diamond,
	dot,
	odot,
	ediamond,
	empty,
	inv,
	invdot,
	invodot,
	invempty,
	none,
	normal,
	open,
	tee,
	vee,
	o,
	l,
	r;

	private static String[] valArray;

	public static String[] toArray() {
		if (valArray == null) {
			valArray = Stream.of(values()).map(ArrowType::name).toArray(String[]::new);
		}
		return valArray;
	}
}
