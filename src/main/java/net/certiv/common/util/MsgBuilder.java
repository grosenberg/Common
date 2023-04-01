package net.certiv.common.util;

public class MsgBuilder {

	private final StringBuilder sb;

	public static MsgBuilder of() {
		return new MsgBuilder();
	}

	public static MsgBuilder of(String msg) {
		return new MsgBuilder(msg);
	}

	public static MsgBuilder of(String fmt, Object... args) {
		return new MsgBuilder(fmt, args);
	}

	// --------------------------------

	public MsgBuilder() {
		sb = new StringBuilder();
	}

	public MsgBuilder(String format, Object... args) {
		this();
		if (args != null && args.length > 0) {
			sb.append(String.format(format, args));
		} else {
			sb.append(format);
		}
	}

	/**
	 * Append a formatted string.
	 *
	 * @param format the format specification
	 * @param args   supplied arguments
	 * @return this
	 */
	public MsgBuilder append(String format, Object... args) {
		sb.append(String.format(format, args));
		return this;
	}

	/**
	 * Conditionally append a formatted string.
	 *
	 * @param cond   the append enable condition
	 * @param format the format specification
	 * @param args   supplied arguments
	 * @return this
	 */
	public MsgBuilder append(boolean cond, String format, Object... args) {
		if (cond) sb.append(String.format(format, args));
		return this;
	}

	/**
	 * Append a formatted string with a leading tab character.
	 *
	 * @param format the format specification
	 * @param args   supplied arguments
	 * @return this
	 */
	public MsgBuilder indent(String format, Object... args) {
		indent();
		sb.append(String.format(format, args));
		return this;
	}

	/**
	 * Append a formatted string with a leading tab character.
	 *
	 * @param cond   the append enable condition
	 * @param format the format specification
	 * @param args   supplied arguments
	 * @return this
	 */
	public MsgBuilder indent(boolean cond, String format, Object... args) {
		if (cond) {
			indent();
			sb.append(String.format(format, args));
		}
		return this;
	}

	/**
	 * Append a tab character.
	 *
	 * @return this
	 */
	public MsgBuilder indent() {
		sb.append(Strings.TAB);
		return this;
	}

	/**
	 * Append a platform specific new line string.
	 *
	 * @return this
	 */
	public MsgBuilder nl() {
		sb.append(System.lineSeparator());
		return this;
	}

	public MsgBuilder sp() {
		sb.append(Strings.SPACE);
		return this;
	}

	public MsgBuilder clear() {
		sb.setLength(0);
		return this;
	}

	public int length() {
		return sb.length();
	}

	public void setLength(int newLength) {
		sb.setLength(newLength);
	}

	public char charAt(int index) {
		return sb.charAt(index);
	}

	public void setCharAt(int index, char ch) {
		sb.setCharAt(index, ch);
	}

	public String substring(int start) {
		return sb.substring(start);
	}

	public String substring(int start, int end) {
		return sb.substring(start, end);
	}

	@Override
	public String toString() {
		return sb.toString();
	}

	// /**
	// * Append a EOL terminal followed by a formatted string.
	// *
	// * @param format the format specification
	// * @param args supplied arguments
	// * @return this
	// */
	// public MsgBuilder appendNL(String format, Object... args) {
	// nl();
	// sb.append(String.format(format, args));
	// return this;
	// }
	//
	// /**
	// * Append a EOL terminal followed by a formatted string.
	// *
	// * @param cond the append enable condition
	// * @param format the format specification
	// * @param args supplied arguments
	// * @return this
	// */
	// public MsgBuilder appendNL(boolean cond, String format, Object... args) {
	// if (cond) {
	// nl();
	// sb.append(String.format(format, args));
	// }
	// return this;
	// }

	// /**
	// * Append a EOL terminal followed by a tab character and formatted string.
	// *
	// * @param format the format specification
	// * @param args supplied arguments
	// * @return this
	// */
	// public MsgBuilder indentNL(String format, Object... args) {
	// nl();
	// indent();
	// sb.append(String.format(format, args));
	// return this;
	// }
	//
	// /**
	// * Append a EOL terminal followed by a tab character and formatted string.
	// *
	// * @param cond the append enable condition
	// * @param format the format specification
	// * @param args supplied arguments
	// * @return this
	// */
	// public MsgBuilder indentNL(boolean cond, String format, Object... args) {
	// if (cond) {
	// nl();
	// indent();
	// sb.append(String.format(format, args));
	// }
	// return this;
	// }

}
