package net.certiv.common.dot;

import java.util.stream.Stream;

public enum LineStyle implements IDotStr {
	bold,
	dashed,
	diagonals,
	dotted,
	filled,
	invis,
	radial,
	rounded,
	solid,
	striped,
	tapered,
	wedged;

	private static String[] valArray;

	public static String[] toArray() {
		if (valArray == null) {
			valArray = Stream.of(values()).map(LineStyle::name).toArray(String[]::new);
		}
		return valArray;
	}
}
