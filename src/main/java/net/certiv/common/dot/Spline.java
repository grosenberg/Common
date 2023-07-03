package net.certiv.common.dot;

import java.util.stream.Stream;

public enum Spline implements IDotStr {
	True,
	False,
	none,
	line,
	spline,
	polyline,
	ortho,
	curved;

	private static String[] valArray;

	public static String[] toArray() {
		if (valArray == null) {
			valArray = Stream.of(values()).map(Spline::toString).toArray(String[]::new);
		}
		return valArray;
	}

	@Override
	public String toString() {
		return name().toLowerCase();
	}
}
