package net.certiv.common.grid;

/** Column layout policy */
public enum Flow {

	/** Auto: 'width' value is a hint; 0..n */
	AUTO("A"),
	/** Fixed: 'width' value is final */
	FIXED("F"),
	/** Minimum: 'width' value is a constrained hint; width..n */
	MIN("M"),
	/**
	 * Value: 'width' value will be computed from the maximum width of the column actual
	 * contents before being used as a constrained hint; width..n
	 */
	VALUE("V");

	public final String mark;

	Flow(String mark) {
		this.mark = mark;
	}

	/**
	 * Return the mark for this Flow enum.
	 *
	 * @return mark
	 */
	public String mark() {
		return mark;
	}

	/**
	 * Returns whether the given text exactly matches an Flow mark.
	 *
	 * @param txt text to evaluate
	 * @return {@code true} if the text is an Flow mark
	 */
	public static boolean isMark(String txt) {
		if (txt.length() == 1) {
			for (Flow flow : values()) {
				if (flow.mark.equals(txt)) return true;
			}
		}
		return false;
	}

	/**
	 * Returns whether the given text exactly matches a Flow mark or name.
	 *
	 * @param txt text to evaluate
	 * @return {@code true} if the text is an Flow mark
	 */
	public static boolean is(String txt) {
		for (Flow flow : values()) {
			if (flow.mark.equals(txt) || flow.name().equals(txt)) return true;
		}
		return false;
	}

	/**
	 * Converts the given text to an Flow enum. Requires an exact match a Flow mark or
	 * name. Defaults to {@link Flow#MIN}.
	 *
	 * @param txt text to evaluate
	 * @return an Flow value
	 */
	public static Flow of(String txt) {
		for (Flow flow : values()) {
			if (flow.mark.equals(txt) || flow.name().equals(txt)) return flow;
		}
		return Flow.MIN;
	}
}
