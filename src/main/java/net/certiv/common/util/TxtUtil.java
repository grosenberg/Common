package net.certiv.common.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.text.TextStringBuilder;

public class TxtUtil {

	private static final Pattern NL = Pattern.compile(".*?(\\R)");

	private TxtUtil() {}

	public static String createIndent(int tabWidth, boolean useTabs, int indents) {
		if (indents < 1) return Strings.EMPTY;

		StringBuilder sb = new StringBuilder();
		String indent = useTabs ? Strings.TAB : Strings.dup(tabWidth, Chars.SP);
		for (int i = 0; i < indents; i++) {
			sb.append(indent);
		}
		return sb.toString();
	}

	public static String createVisualWs(int tabWidth, int from, int to) {
		if (tabWidth < 1) tabWidth = 1;
		if (from < 0 || from > to) {
			throw new IllegalArgumentException(String.format("%d:%d", from, to));
		}

		int ftabs = from / tabWidth;
		int fspcs = from % tabWidth;

		int ttabs = to / tabWidth;
		int tspcs = to % tabWidth;

		int tabs = ttabs - ftabs;
		int spcs = tabs > 0 ? tspcs : tspcs - fspcs;
		return Strings.dup(tabs, Chars.TAB) + Strings.dup(spcs, Chars.SP);
	}

	/**
	 * Allocates the given text into an array where each entry corresponds to a
	 * single visual width unit as defined by the given tabWidth. The last entry
	 * will contain any text lenth excess.
	 *
	 * @param text the text to be allocated
	 * @param tabWidth the defined visual unit with in spaces
	 * @return
	 */
	public static String[] allocateVisualWidth(String text, int tabWidth) {
		if (text == null || tabWidth < 0) throw new IllegalArgumentException();

		Deque<String> res = new ArrayDeque<>();
		String buf = Strings.EMPTY;

		for (int idx = 0, len = text.length(); idx < len; idx++) {
			char ch = text.charAt(idx);
			switch (ch) {
				case Chars.TAB:
					if (buf.length() < tabWidth) {
						res.add(buf + Strings.TAB);
						buf = Strings.EMPTY;
					} else {
						if (!buf.isEmpty()) res.add(buf);
						res.add(Strings.TAB);
						buf = Strings.EMPTY;
					}
					break;

				case Chars.RET:
				case Chars.NL:
					res.clear();
					buf = Strings.EMPTY;
					break;

				default:
					if (buf.length() < tabWidth) {
						buf += ch;
					} else {
						res.add(buf);
						buf = Strings.EMPTY + ch;
					}
			}
		}

		if (res.size() > 1) {
			String ultm = res.peekLast();
			if (!ultm.contains(Strings.TAB) && ultm.length() < tabWidth) {
				res.removeLast();
				String pent = res.removeLast();
				res.add(pent + ultm);
			}
		}

		return res.toArray(new String[res.size()]);
	}

	public static String expandTabs(String text, int tabWidth) {
		if (text == null || tabWidth < 1) throw new IllegalArgumentException();

		StringBuilder sb = new StringBuilder();
		int width = 0;
		for (int idx = 0, len = text.length(); idx < len; idx++) {
			char ch = text.charAt(idx);
			switch (ch) {
				case Chars.TAB:
					int delta = tabWidth - width % tabWidth;
					width += delta;
					sb.append(Strings.dup(delta, Chars.SP));
					break;
				case Chars.RET:
				case Chars.NL:
					sb.append(ch);
					width = 0;
					break;
				default:
					sb.append(ch);
					width++;
			}
		}
		return sb.toString();
	}

	public static String indentBlock(String ci, String block) {
		if (block == null) return "<Error: indent block is null>";
		StringReader sr = new StringReader(block);
		BufferedReader buf = new BufferedReader(sr);
		StringBuilder sb = new StringBuilder();
		String s;
		try {
			while ((s = buf.readLine()) != null) {
				sb.append(ci + s + Strings.EOL);
			}
			sb.setLength(sb.length() - Strings.EOL.length());
		} catch (IOException e) {
			sb.append("<Error indenting block: " + e.getMessage() + ">");
		}
		return sb.toString();
	}

	/**
	 * Returns the indentation of the given line in indentation units. Odd spaces
	 * are not counted. This method only analyzes the content of <code>line</code>
	 * up to the first non-whitespace character.
	 *
	 * @param line the string to measure the indent of
	 * @param tabWidth the width of one tab character in space equivalents
	 * @param indentWidth the width of one indentation unit in space equivalents
	 * @return the number of indentation units that line is indented by
	 * @exception IllegalArgumentException if:
	 *                <ul>
	 *                <li>the given <code>indentWidth</code> is lower or equals to
	 *                zero</li>
	 *                <li>the given <code>tabWidth</code> is lower than zero</li>
	 *                <li>the given <code>line</code> is null</li>
	 *                </ul>
	 */
	public static int measureIndentUnits(CharSequence line, int tabWidth, int indentWidth) {
		if (indentWidth <= 0 || tabWidth < 0 || line == null) {
			throw new IllegalArgumentException();
		}

		int visualLength = measureIndentInSpaces(line, tabWidth);
		return visualLength / indentWidth;
	}

	/**
	 * Returns the indentation of the given line in space equivalents.
	 * <p>
	 * Tab characters are counted using the given <code>tabWidth</code> and every
	 * other indent character as one. This method analyzes the content of
	 * <code>line</code> up to the first non-whitespace character.
	 * </p>
	 *
	 * @param line the string to measure the indent of
	 * @param tabWidth the width of one tab in space equivalents
	 * @return the measured indent width in space equivalents
	 * @exception IllegalArgumentException if:
	 *                <ul>
	 *                <li>the given <code>line</code> is null</li>
	 *                <li>the given <code>tabWidth</code> is lower than zero</li>
	 *                </ul>
	 */
	public static int measureIndentInSpaces(CharSequence line, int tabWidth) {
		if (tabWidth < 0 || line == null) throw new IllegalArgumentException();

		int length = 0;
		for (int idx = 0, max = line.length(); idx < max; idx++) {
			char ch = line.charAt(idx);
			if (ch == Chars.TAB) {
				length += tabWidth - (length % tabWidth);
			} else if (isHWs(ch)) {
				length++;
			} else {
				return length;
			}
		}
		return length;
	}

	/**
	 * Returns the visual width of the given line of text.
	 *
	 * @param text the string to measure
	 * @param tabWidth the visual width of a tab
	 * @return the visual width of <code>text</code>
	 * @see org.eclipse.jdt.ui/ui/org/eclipse/jdt/internal/ui/javaeditor/IndentUtil.java
	 */
	public static int measureVisualWidth(CharSequence text, int tabWidth) {
		return measureVisualWidth(text, tabWidth, 0);
	}

	/**
	 * Returns the visual width of the given text starting from the given offset
	 * within a line. Width is reset each time a line separator character is
	 * encountered.
	 *
	 * @param text the string to measure
	 * @param tabWidth the visual width of a tab
	 * @param from the visual starting offset of the text
	 * @return the visual width of <code>text</code>
	 * @see org.eclipse.jdt.ui/ui/org/eclipse/jdt/internal/ui/javaeditor/IndentUtil.java
	 */
	public static int measureVisualWidth(CharSequence text, int tabWidth, int from) {
		if (text == null || tabWidth < 0 || from < 0) throw new IllegalArgumentException();

		int width = from;
		for (int idx = 0, len = text.length(); idx < len; idx++) {
			switch (text.charAt(idx)) {
				case Chars.TAB:
					if (tabWidth > 0) width += tabWidth - width % tabWidth;
					break;

				case Chars.RET:
				case Chars.NL:
					width = 0;
					from = 0;
					break;

				default:
					width++;
			}
		}
		return width - from;
	}

	/**
	 * Returns the column of the tab stop equal to or larger than the given column.
	 */
	public static int nextTabCol(int col, int tabWidth) {
		int rem = col % tabWidth;
		if (rem == 0) return col;
		return col + tabWidth - rem;
	}

	/** Returns the tab column of the nearest tab stop to the given column. */
	public static int nearestTabCol(int col, int tabWidth) {
		int rem = col % tabWidth;
		if (rem / 2 >= tabWidth / 2) return col + tabWidth - rem;
		return col - rem;
	}

	/**
	 * Returns the given string trimmed of any trailing HWS.
	 *
	 * @param str the string to check
	 * @return the string without tailing HWS
	 */
	public static String trimTrailinglHWs(String str) {
		int idx = lastNonHWs(str);
		if (idx == -1) return "";
		return str.substring(0, idx + 1);
	}

	/**
	 * Returns <code>true</code> if the given character is an indentation character.
	 * Indentation character are all whitespace characters except the line delimiter
	 * characters.
	 *
	 * @param ch the given character
	 * @return Returns <code>true</code> if this the character is a indent
	 *             character, <code>false</code> otherwise
	 */
	public static boolean isHWs(char ch) {
		return Character.isWhitespace(ch) && !Strings.isLineDelimiterChar(ch);
	}

	public static int countVWS(String txt) {
		if (txt == null || txt.isEmpty()) return 0;

		int cnt = 0;
		Matcher m = NL.matcher(txt);
		while (m.find()) {
			cnt++;
		}
		return cnt;
	}

	/**
	 * Returns the leading HWS contained in the given string.
	 *
	 * @param str the string to check
	 * @return the leading HWS
	 */
	public static String leadHWs(String str) {
		int idx = firstNonHWs(str);
		return idx > -1 ? str.substring(0, idx) : "";
	}

	/**
	 * Returns the trailing HWS contained in the given string.
	 *
	 * @param str the string to check
	 * @return the tailing HWS
	 */
	public static String trailingHWs(String str) {
		int idx = lastNonHWs(str);
		return idx != -1 ? str.substring(idx + 1) : str;
	}

	/**
	 * Returns the index of the first non-horizontal whitespace character in the
	 * given string.
	 *
	 * @param str the string to check
	 * @return index of the first non-whitespace character
	 */
	public static int firstNonHWs(String str) {
		if (str == null || str.isEmpty()) return -1;

		for (int col = 0; col < str.length(); col++) {
			if (!isHWs(str.charAt(col))) return col;
		}
		return -1;
	}

	/**
	 * Returns the index of the last non-horizontal whitespace character in the
	 * given string.
	 *
	 * @param str the string to check
	 * @return index of the last non-whitespace character
	 */
	public static int lastNonHWs(String str) {
		if (str == null || str.isEmpty()) return -1;

		for (int col = str.length() - 1; col >= 0; col--) {
			if (!isHWs(str.charAt(col))) return col;
		}
		return -1;
	}

	/**
	 * Wraps the given text defined by the given format and args to lines of up to
	 * the given length. Uses the system newline string as the line return terminal.
	 * Preserves existing internal returns.
	 *
	 * @param len the target maximum line length
	 * @param fmt the text format
	 * @param args the format arguments
	 * @return the resulting text
	 */
	public static String wrap(int len, String fmt, Object... args) {
		return wrap(Strings.EOL, len, fmt, args);
	}

	/**
	 * Wraps the text defined by the given format and args to lines of up to the
	 * given length. Existing hard internal returns are preserved & converted to the
	 * given newline terminal.
	 *
	 * @param terminal the newline delimiter
	 * @param len the target maximum line length
	 * @param fmt the text format
	 * @param args the format arguments
	 * @return the resulting text
	 */
	public static String wrap(String terminal, int len, String fmt, Object... args) {
		TextStringBuilder block = new TextStringBuilder();
		block.setNewLineText(terminal);
		String[] lines = String.format(fmt, args).split("\\R");
		for (int idx = 0; idx < lines.length; idx++) {
			block.appendln(_wrap(terminal, len, lines[idx]));
		}
		return trim(block, terminal).toString();
	}

	private static TextStringBuilder _wrap(String terminal, int len, String text) {
		TextStringBuilder block = new TextStringBuilder();
		TextStringBuilder line = new TextStringBuilder();
		block.setNewLineText(terminal);

		String[] words = text.split(Strings.SPACE);
		for (int idx = 0; idx < words.length; idx++) {
			line.append(words[idx]);
			if (idx + 1 == words.length || line.length() + words[idx + 1].length() > len) {
				block.appendln(line.toString());
				line.setLength(0);
			} else {
				line.append(Strings.SPACE);
			}
		}
		return trim(block, terminal);
	}

	private static TextStringBuilder trim(TextStringBuilder sb, String terminal) {
		while (sb.startsWith(terminal)) {
			sb.deleteFirst(terminal);
		}
		while (sb.endsWith(terminal)) {
			sb.setLength(sb.length() - terminal.length());
		}
		return sb;
	}

	/**
	 * Wrap the given text according to the given parameters.
	 *
	 * @param txt the text to wrap
	 * @param col the wrap column
	 * @param prefix a leading prefix to provide on each line
	 * @param terminal the line terminal
	 * @param splits a regex used to split the text
	 * @return the wrapped text
	 */
	@Deprecated
	public static String wrap(String prefix, String terminal, String splits, int col, String txt) {
		if (txt == null || txt.isEmpty()) return Strings.EMPTY;
		if (prefix == null) prefix = Strings.EMPTY;
		if (terminal == null) terminal = Strings.EOL;
		if (splits == null || splits.isEmpty()) splits = Strings.SPACE;
		if (col < 1) col = 80;

		final Pattern splitter = Pattern.compile(splits);
		int len = txt.length();
		StringBuilder sb = new StringBuilder(len);
		sb.append(prefix);

		int beg = 0;
		while (beg < len) {
			int wrapAt = -1;
			int end = Math.min((int) Math.min(Integer.MAX_VALUE, beg + col + 1L), len);
			Matcher matcher = splitter.matcher(txt.substring(beg, end));
			if (matcher.find()) {
				if (matcher.start() == 0) {
					beg += matcher.end();
					continue;
				}
				wrapAt = matcher.start() + beg;
			}
			// only last line without leading spaces is left
			if (len - beg <= col) break;

			while (matcher.find()) {
				wrapAt = matcher.start() + beg;
			}

			if (wrapAt >= beg) { // normal case
				sb.append(txt, beg, wrapAt);
				sb.append(terminal + prefix);
				beg = wrapAt + 1;

			} else {
				matcher = splitter.matcher(txt.substring(beg + col));
				if (matcher.find()) {
					wrapAt = matcher.start() + beg + col;
				}

				if (wrapAt >= 0) {
					sb.append(txt, beg, wrapAt);
					sb.append(terminal);
					beg = wrapAt + 1;
				} else {
					sb.append(txt, beg, txt.length());
					beg = len;
				}
			}
		}

		sb.append(txt, beg, txt.length()); // append remainder
		return sb.toString();
	}
}
