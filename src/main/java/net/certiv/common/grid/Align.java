package net.certiv.common.grid;

/** Column alignment policy */
public enum Align {
	
	/** Align left */
	LEFT("L"),
	/** Align center */
	CENTER("C"),
	/** Align right */
	RIGHT("R");

	public final String mark;

	Align(String mark) {
		this.mark = mark;
	}

	/**
	 * Return the mark for this Align enum.
	 * 
	 * @return mark
	 */
	public String mark() {
		return mark;
	}

	/**
	 * Returns whether the given text exactly matches an Align mark.
	 *
	 * @param txt text to evaluate
	 * @return {@code true} if the text is an Align mark
	 */
	public static boolean isMark(String txt) {
		if (txt.length() == 1) {
			for (Align align : values()) {
				if (align.mark.equals(txt)) return true;
			}
		}
		return false;
	}

	/**
	 * Returns whether the given text exactly matches an Align mark or name.
	 *
	 * @param txt text to evaluate
	 * @return {@code true} if the text is an Align mark
	 */
	public static boolean is(String txt) {
		for (Align align : values()) {
			if (align.mark.equals(txt) || align.name().equals(txt)) return true;
		}
		return false;
	}

	/**
	 * Converts the given text to an Align enum. Requires an exact match an Align mark or
	 * name. Defaults to {@link Align#LEFT}.
	 *
	 * @param txt text to evaluate
	 * @return an Align value
	 */
	public static Align of(String txt) {
		for (Align align : values()) {
			if (align.mark.equals(txt) || align.name().equals(txt)) return align;
		}
		return Align.LEFT;
	}
}
