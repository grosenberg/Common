/*******************************************************************************
 * Copyright (c) 2016 - 2020 Certiv Analytics and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package net.certiv.common.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.text.StringEscapeUtils;

import net.certiv.common.stores.Table;

public class Strings {

	public static final Pattern PAT_NL = Pattern.compile("\\R");

	public static final String ISO_LATIN = "ISO-8859-1";
	public static final String UTF_8 = "UTF-8";
	public static final String UTC = "UTC";
	public static final String MD5 = "MD5";

	public static final String AT = "@";
	public static final String BANG = "!";
	public static final String COLON = ":";
	public static final String COLON2 = "::";
	public static final String COMMA = ",";
	public static final String DASH = "-";
	public static final String DOT = ".";
	public static final String LOWDASH = "_";
	public static final String HASH = "#";
	public static final String PERCENT = "%";
	public static final String PIPE = "|";
	public static final String PLUS = "+";
	public static final String QMARK = "?";
	public static final String SEMI = ";";
	public static final String SLASH = "/";
	public static final String STAR = "*";
	public static final String TILDE = "~";

	public static final String ACCENT = "'";
	public static final String TIC = "`";
	public static final String QUOTE = "\"";

	public static final String SPACE = " ";
	public static final String TAB = "\t";

	public static final String LPAREN = "(";
	public static final String RPAREN = ")";
	public static final String LBRACE = "{";
	public static final String RBRACE = "}";
	public static final String LBRACK = "[";
	public static final String RBRACK = "]";
	public static final String RANGLE = ">";
	public static final String LANGLE = "<";

	public static final String CsvDELIM = ", ";

	public static final String EOL = System.lineSeparator();
	public static final char EOP = File.separatorChar;	// path
	public static final String UDIR = System.getProperty("user.dir");
	public static final String UHOME = System.getProperty("user.home");

	public static final String EMPTY = ""; //$NON-NLS-1$
	public static final String[] EMPTY_STRINGS = {};
	public static final Object[] EMPTY_ARRAY = {};

	public static final String UNKNOWN = "Unknown"; //$NON-NLS-1$

	public static final String TAB_UBAR_MARK = "\u1E6F"; 	// t underbar ṯ
	public static final String DIAMOND_MARK = "\u2666";		// diamond ♦
	public static final String DOWN_TRIANGLE = "\u25BC";	// down triangle ▼
	public static final String RIGHT_TRIANGLE = "\u25B6";	// down triangle ▶
	public static final String PARA_MARK = "\u00B6";		// pillcrow ¶
	public static final String NULL_MARK = "\u2400";		// null ␀
	public static final String ACK_MARK = "\u2406";			// acknowledge ␆
	public static final String BS_MARK = "\u2408";			// backspace ␈
	public static final String TAB_MARK = "\u2409";			// horizontal tabulation ␉
	public static final String LF_MARK = "\u240A";			// line feed ␊
	public static final String CR_MARK = "\u240D";			// carriage return ␍
	public static final String RETURN_MARK = "\u23CE";		// return symbol ⏎
	public static final String SPACE_MARK = "\u2423";		// space symbol ␣
	public static final String MIDDLE_DOT = "\u00B7";		// middle dot ·
	public static final String HOLLOW_DOT = "\u25E6";		// hollow dot ◦
	public static final String RING_POINT = "\u2E30";		// ring point ⸰
	public static final String BULLET_DOT = "\u2219";		// bullet dot ∙
	public static final String ELLIPSIS_MARK = "\u2026"; 	// ellipsis …
	public static final String LARR_MARK = "\u2190";		// left arrow
	public static final String RARR_MARK = "\u2192";		// right arrow →
	public static final String RARR_BAR = "\u21E5"; 		// right arrow to bar ⇥
	public static final String RARR_DOUBLE = "\u21D2";		// double rightwards arrow ⇒
	public static final String LARR_WEAK_MARK = "\u21DC";	// weak leftwards arrow
	public static final String RARR_WEAK_MARK = "\u21DD";	// weak rightwards arrow
	public static final String GUILL_L1_MARK = "\u2039";	// left single guillemet ‹
	public static final String GUILL_R1_MARK = "\u203A";	// right single guillemet ›
	public static final String GUILL_L2_MARK = "\u00AB";	// left double guillemet «
	public static final String GUILL_R2_MARK = "\u00BB";	// right double guillemet »

	private static final String V = "[aeiouy]";
	private static final String C = "[^aeiouy]";
	private static final Pattern ShortVowel = Pattern.compile(".*" + C + V + "(" + C + ")");

	/** Reference characters */
	private static char[] R = { '$', '@', '.', '#' };

	/** Quoting character pairs */
	private static final char[][] QuotePairs = { //
			{ '\'', '\'' }, //
			{ '\"', '\"' }, //
			{ '[', ']' }, //
			{ '(', ')' }, //
			{ '{', '}' }, //
			{ '<', '>' } //
	};

	private Strings() {}

	/** Encodes WS as discrete visible characters. */
	public static List<String> encode(List<String> in) {
		if (in != null) {
			for (int idx = 0; idx < in.size(); idx++) {
				in.set(idx, encode(in.get(idx)));
			}
		}
		return in;
	}

	/** Encodes WS as discrete visible characters. */
	public static String encode(String in) {
		if (in == null) return EMPTY;
		StringBuilder sb = new StringBuilder();
		for (int idx = 0; idx < in.length(); idx++) {
			char c = in.charAt(idx);
			switch (c) {
				case Chars.SP:
					sb.append(SPACE_MARK);
					break;
				case Chars.TAB:
					sb.append(TAB_MARK);
					break;
				case Chars.RET:
					sb.append(CR_MARK);
					break;
				case Chars.NL:
					sb.append(LF_MARK);
					break;
				default:
					sb.append(c);
			}
		}
		return sb.toString();
	}

	/** Escapes the given text using Java string escape rules. */
	public static String escape(String text) {
		return StringEscapeUtils.escapeJava(text);
	}

	public static String displyEscape(String text, int maxWidth) {
		String str = text.replaceAll("\\R", PARA_MARK);
		str = StringUtils.abbreviate(str, maxWidth);
		return str;
	}

	public static String formatEscape(String text) {
		return text.replaceAll("%", "%%");
	}

	/** Returns a comparative form of a name by adding {@code -er}. */
	public static String rForm(String name) {
		if (name.endsWith("e")) return name + "r";
		if (name.endsWith("o")) return name + "r";
		if (name.endsWith("y")) return name.substring(0, name.length() - 1) + "ier";

		Matcher m = ShortVowel.matcher(name);
		if (m.matches()) {
			String c = m.group(1);
			return name + c + "er";
		}
		return name + "er";
	}

	/**
	 * Returns the given input string conditionally truncated and with an added ellipsis mark if the
	 * string exceeds the given {@code len}. If {@code len} is positive, truncation occurs at the
	 * string tail, otherwise at the head.
	 */
	public static String ellipsize(String input, int len) {
		if (len == 0) return EMPTY;
		if (input == null || input.length() < Math.abs(len)) return input;
		if (len > 0) return input.substring(0, len) + ELLIPSIS_MARK;
		return ELLIPSIS_MARK + input.substring(input.length() + len);
	}

	/** Capitalize the first letter of the given phrase & lowercase remainder. */
	public static String capitalize(String phrase) {
		if (phrase == null) return EMPTY;
		phrase = phrase.trim();
		if (phrase.isEmpty()) return phrase;
		return phrase.substring(0, 1).toUpperCase() + phrase.substring(1).toLowerCase();
	}

	/**
	 * Returns <code>true</code> if the given character is a line delimiter character.
	 *
	 * @param ch the given character
	 * @return Returns <code>true</code> if this the character is a line delimiter character,
	 *         <code>false</code> otherwise
	 */
	public static boolean isLineDelimiterChar(char ch) {
		return ch == Chars.NL || ch == Chars.RET;
	}

	public static String titleCase(String title) {
		if (title.length() < 2) return title.toUpperCase();

		StringBuilder sb = new StringBuilder(title.length());
		boolean goUp = true;
		for (int idx = 0, len = title.length(); idx < len; idx++) {
			char c = title.charAt(idx);
			if (Character.isLetterOrDigit(c) || c == Chars.MARK) {
				if (goUp) {
					sb.append(Character.toUpperCase(c));
					goUp = false;
				} else {
					sb.append(Character.toLowerCase(c));
				}
			} else {
				sb.append(c);
				goUp = true;
			}
		}

		return sb.toString();
	}

	public static String toInitials(String name) {
		StringBuilder sb = new StringBuilder();
		boolean yes = true;
		for (int i = 0; i < name.length(); i++) {
			char c = name.charAt(i);
			if (Character.isWhitespace(c)) {
				yes = true;
			} else {
				if (yes) {
					c = Character.toUpperCase(c);
					sb.append(c);
				}
				yes = false;
			}
		}

		return sb.toString();
	}

	/**
	 * Tests if a char is lower case.
	 *
	 * @param ch the char
	 * @return return true if char is lower case
	 */
	public static boolean isLowerCase(char ch) {
		return Character.toLowerCase(ch) == ch;
	}

	/**
	 * Tests if a char is upper case.
	 *
	 * @param ch the char
	 * @return return true if char is upper case
	 */
	public static boolean isUpperCase(char ch) {
		return Character.toUpperCase(ch) == ch;
	}

	public static boolean isUpperCase(CharSequence text) {
		return text.codePoints().allMatch(Character::isUpperCase);
	}

	public static boolean isLowerCase(CharSequence text) {
		return text.codePoints().allMatch(Character::isLowerCase);
	}

	public static boolean matchesNoCase(char c, char d) {
		return Character.toLowerCase(c) == Character.toLowerCase(d);
	}

	public static boolean startsWithIgnoreCase(String text, String prefix) {
		int textLength = text.length();
		int prefixLength = prefix.length();
		if (textLength < prefixLength) return false;
		for (int i = prefixLength - 1; i >= 0; i--) {
			if (Character.toLowerCase(prefix.charAt(i)) != Character.toLowerCase(text.charAt(i))) return false;
		}
		return true;
	}

	public static String normalize(String content) throws IllegalArgumentException {
		return content.replaceAll("\\R", EOL);
	}

	/**
	 * Return {@code true} if the given text is {@code null}, empty, or only contains whitespace.
	 */
	public static boolean blank(String text) {
		return text == null || text.trim().isEmpty();
	}

	/** Returns result of !null and !isEmpty test. */
	public static boolean notEmpty(String arg) {
		return arg != null && !arg.isEmpty();
	}

	/** Returns result of null or isEmpty test. */
	public static boolean empty(String arg) {
		return arg == null || arg.isEmpty();
	}

	public static boolean empty(String[] args) {
		return args == null || args.length == 0;
	}

	/**
	 * Remove one level of quotes surrounding the literal. No error if quotes are not present or are
	 * mixed.
	 */
	public static String deQuote(String literal) {
		int endIdx = literal.length() - 1;
		if (endIdx < 2) return literal;
		char beg = literal.charAt(0);
		char end = literal.charAt(endIdx);
		for (char[] element : QuotePairs) {
			if (beg == element[0] && end == element[1]) {
				return literal.substring(1, endIdx);
			}
		}
		return literal;
	}

	public static String trimQuotes(String arg) {
		if (arg == null) return null;
		if (arg.charAt(0) != Chars.ACCENT && arg.charAt(0) != Chars.QUOTE) {
			return arg;
		}
		char c = arg.charAt(arg.length() - 1);
		if (c != Chars.ACCENT && c != Chars.QUOTE) {
			return arg;
		} else {
			return arg.substring(1, arg.length() - 1);
		}
	}

	public static String trimLeadingPunctuation(String text) {
		int offset = 0;
		for (char c : text.toCharArray()) {
			if (Character.isLetterOrDigit(c)) break;
			offset++;
		}
		return text.substring(offset);
	}

	public static String trimExt(String text) {
		int mrk = text.lastIndexOf(Chars.SLASH);
		int dot = text.lastIndexOf(Chars.DOT);
		return dot > mrk ? text.substring(0, dot) : text;
	}

	public static String trimLeft(String text) {
		for (int idx = 0; idx < text.length(); idx++) {
			if (!Character.isSpaceChar(text.charAt(idx))) {
				return text.substring(idx);
			}
		}
		return EMPTY;
	}

	public static String trimRight(String text) {
		for (int idx = text.length(); idx > 0; idx--) {
			if (!Character.isWhitespace(text.charAt(idx - 1))) {
				return text.substring(0, idx);
			}
		}
		return EMPTY;
	}

	/**
	 * Trims the leading and trailing brace, if present, along with adjacent whitespace. A trailing
	 * brace is trimmed only if a leading brace is found.
	 */
	public static String trimBraces(String block) {
		if (block == null) return EMPTY;
		String str = block.trim();
		if (str.startsWith(LBRACE)) {
			str = str.substring(1);
			if (str.endsWith(RBRACE)) {
				str = str.substring(0, str.length() - 1);
			}
		}
		return str.trim();
	}

	public static String trimLeadingNewline(String s) {
		int idx = 0;
		while (idx < s.length() && (s.charAt(idx) == '\r' || s.charAt(idx) == '\n')) {
			idx++;
		}
		return s.substring(idx);
	}

	// /**
	// * Wraps the given text to lines of length less than the given limit.
	// Preserves
	// * existing hard line returns.
	// */
	// @Deprecated
	// public static String wrap(String text, int limit) {
	// StringBuilder block = new StringBuilder();
	// String[] lines = text.split("\\R");
	// for (String line : lines) {
	// block.append(_wrap(line, limit) + EOL);
	// }
	// return block.toString().trim();
	// }
	//
	// @Deprecated
	// private static String _wrap(String text, int limit) {
	// StringBuilder block = new StringBuilder();
	// StringBuilder line = new StringBuilder();
	// String[] words = text.split(SPACE);
	// for (int idx = 0; idx < words.length; idx++) {
	// line.append(words[idx]);
	// if (idx + 1 == words.length || line.length() + words[idx + 1].length() >
	// limit) {
	// block.append(line.toString() + EOL);
	// line.setLength(0);
	// } else {
	// line.append(SPACE);
	// }
	// }
	// return block.toString().trim();
	// }

	/** Unwraps the given text by replacing all hard returns with a space. */
	public static String unwrap(String text) {
		return text.replaceAll("\\R", SPACE);
	}

	public static int wordCount(String text) {
		return text.split("\\s+").length;
	}

	public static int countLeading(String text, char lead) {
		int count = 0;
		for (char s : text.toCharArray()) {
			if (s != lead) return count;
			count++;
		}
		return count;
	}

	public static int countLines(String txt) {
		if (txt == null || txt.isEmpty()) return 0;
		return PAT_NL.split(txt, -1).length;
	}

	/** Split all lines, preserving blank last line, if any. */
	public static String[] splitLines(String text) {
		return PAT_NL.split(text, -1);
	}

	public static int count(String text, String mark) {
		if (text == null || text.isEmpty()) return 0;
		return text.split(Pattern.quote(mark), -1).length - 1;
	}

	public static int lastLineLen(String txt) {
		if (txt == null || txt.isEmpty()) return 0;

		int idx = txt.lastIndexOf(Chars.NL);
		if (idx == -1) return txt.length();
		return txt.substring(idx + 1).length();
	}
	//
	// /**
	// * Returns the visual width of the given line of text.
	// *
	// * @param text the string to measure
	// * @param tabWidth the visual width of a tab
	// * @return the visual width of {@code text}
	// * @see
	// org.eclipse.jdt.ui/ui/org/eclipse/jdt/internal/ui/javaeditor/IndentUtil.java
	// */
	// @Deprecated
	// public static int measureVisualWidth(CharSequence text, int tabWidth) {
	// return measureVisualWidth(text, tabWidth, 0);
	// }
	//
	// /**
	// * Returns the visual width of the given text starting from the given offset
	// * within a line. Width is reset each time a line separator character is
	// * encountered.
	// *
	// * @param text the string to measure
	// * @param tabWidth the visual width of a tab
	// * @param from the visual starting offset of the text
	// * @return the visual width of {@code text}
	// * @see
	// org.eclipse.jdt.ui/ui/org/eclipse/jdt/internal/ui/javaeditor/IndentUtil.java
	// */
	// @Deprecated
	// public static int measureVisualWidth(CharSequence text, int tabWidth, int
	// from) {
	// if (text == null || tabWidth < 0 || from < 0) throw new
	// IllegalArgumentException();
	//
	// int width = from;
	// for (int idx = 0, len = text.length(); idx < len; idx++) {
	// switch (text.charAt(idx)) {
	// case Chars.TAB:
	// if (tabWidth > 0) width += tabWidth - width % tabWidth;
	// break;
	// case Chars.RET:
	// case Chars.NL:
	// width = 0;
	// from = 0;
	// break;
	// default:
	// width++;
	// }
	// }
	// return width - from;
	// }
	//
	// @Deprecated
	// public static String expandTabs(String text, int tabWidth) {
	// if (text == null || tabWidth < 1) throw new IllegalArgumentException();
	//
	// StringBuilder sb = new StringBuilder();
	// int width = 0;
	// for (int idx = 0, len = text.length(); idx < len; idx++) {
	// char ch = text.charAt(idx);
	// switch (ch) {
	// case Chars.TAB:
	// int delta = tabWidth - width % tabWidth;
	// width += delta;
	// sb.append(dup(delta, SPACE));
	// break;
	// case Chars.RET:
	// case Chars.NL:
	// sb.append(ch);
	// width = 0;
	// break;
	// default:
	// sb.append(ch);
	// width++;
	// }
	// }
	// return sb.toString();
	// }
	//
	// /**
	// * Allocates the given text into an array where each entry corresponds to a
	// * single visual width unit as defined by the given tabWidth. The last entry
	// * will contain any text lenth excess.
	// *
	// * @param text the text to be allocated
	// * @param tabWidth the defined visual unit with in spaces
	// * @return
	// */
	// @Deprecated
	// public static String[] allocateVisualWidth(String text, int tabWidth) {
	// if (text == null || tabWidth < 0) throw new IllegalArgumentException();
	//
	// Deque<String> res = new ArrayDeque<>();
	// String buf = EMPTY;
	//
	// for (int idx = 0, len = text.length(); idx < len; idx++) {
	// char ch = text.charAt(idx);
	// switch (ch) {
	// case Chars.TAB:
	// if (buf.length() < tabWidth) {
	// res.add(buf + TAB);
	// buf = EMPTY;
	// } else {
	// if (!buf.isEmpty()) res.add(buf);
	// res.add(TAB);
	// buf = EMPTY;
	// }
	// break;
	//
	// case Chars.RET:
	// case Chars.NL:
	// res.clear();
	// buf = EMPTY;
	// break;
	//
	// default:
	// if (buf.length() < tabWidth) {
	// buf += ch;
	// } else {
	// res.add(buf);
	// buf = EMPTY + ch;
	// }
	// }
	// }
	//
	// if (res.size() > 1) {
	// String ultm = res.peekLast();
	// if (!ultm.contains(TAB) && ultm.length() < tabWidth) {
	// res.removeLast();
	// String pent = res.removeLast();
	// res.add(pent + ultm);
	// }
	// }
	//
	// return res.toArray(new String[res.size()]);
	// }

	/** Returns a string containing {@code count} spaces. */
	public static String spaces(int count) {
		return dup(count, SPACE);
	}

	public static String dup(int cnt, char... c) {
		return dup(cnt, String.valueOf(c));
	}

	// row=dup cnt; col=dup value; value=dup'd result
	private static final Table<Integer, String, String> DUPS = new Table<>();

	public static String dup(int cnt, String value) {
		cnt = Math.max(0, cnt);
		if (!DUPS.contains(cnt, value)) {
			StringBuilder sb = new StringBuilder();
			for (int idx = 0; idx < cnt; idx++) {
				sb.append(value);
			}
			DUPS.put(cnt, value, sb.toString());
		}
		return DUPS.get(cnt, value);
	}

	public static List<String> dupList(int cnt, String value) {
		List<String> sb = new ArrayList<>();
		for (int idx = 0; idx < cnt; idx++) {
			sb.add(value);
		}
		return sb;
	}

	// /**
	// * Returns the given number of spaces.
	// * <p>
	// * Use #dup
	// */
	// @Deprecated
	// public static String getNSpaces(int spaces) {
	// return dup(spaces, Chars.SP);
	// }
	//
	// /**
	// * Returns {@code count} copies of the given character.
	// * <p>
	// * Use #dup
	// */
	// @Deprecated
	// public static String getNChars(int count, char ch) {
	// StringBuffer buf = new StringBuffer(count);
	// for (int i = 0; i < count; i++)
	// buf.append(ch);
	// return buf.toString();
	//
	// }

	// -----

	/**
	 * Returns a separator delimited string representation of the given list values. The returned
	 * string will not include a trailing separator.
	 *
	 * @param values   ordered list of string values
	 * @param asPrefix if {@code true}, the separator is positioned as a prefix to each list value,
	 *                 otherwise as a suffix
	 * @param sep      the string literal to be used as a list string separator
	 * @return separator delimited string
	 */
	public static String asString(List<String> values, boolean asPrefix, String sep) {
		StringBuilder sb = new StringBuilder();
		String pf = asPrefix ? sep : EMPTY;
		String sf = asPrefix ? EMPTY : sep;
		for (String value : values) {
			sb.append(pf + value + sf);
		}
		if (!asPrefix) { // remove trailing sep
			int beg = sb.length() - 1 - sep.length();
			sb.delete(beg, sb.length());
		}
		return sb.toString();
	}

	public static boolean numeric(String value) {
		if (value.length() == 0) return false; // empty string is a string
		try {
			Double.parseDouble(value);
		} catch (NumberFormatException e) {
			return false;
		}
		return true;
	}

	public static boolean isPunctuation(char ch) {
		switch (Character.getType(ch)) {
			case Character.DASH_PUNCTUATION:
			case Character.START_PUNCTUATION:
			case Character.END_PUNCTUATION:
			case Character.CONNECTOR_PUNCTUATION:
			case Character.OTHER_PUNCTUATION:
			case Character.MATH_SYMBOL:
			case Character.CURRENCY_SYMBOL:
			case Character.MODIFIER_SYMBOL:
			case Character.OTHER_SYMBOL:
			case Character.INITIAL_QUOTE_PUNCTUATION:
			case Character.FINAL_QUOTE_PUNCTUATION:
				return true;
			default:
				return false;
		}
	}

	// @Deprecated
	// public static String csvList(List<String> strs) {
	// if (strs == null) return EMPTY;
	// StringBuilder sb = new StringBuilder();
	// for (String str : strs) {
	// sb.append(str + CsvDELIM);
	// }
	// int len = sb.length();
	// sb.delete(len - 2, len);
	// return sb.toString();
	// }

	/**
	 * Returns the string representation of the given objects joined using the CSV delimiter.
	 */
	public static String join(Collection<?> objs) {
		return join(CsvDELIM, objs);
	}

	/**
	 * Returns the string representation of the given objects joined using the given delimiter.
	 */
	public static String join(CharSequence delimiter, Collection<?> objs) {
		List<String> list = new ArrayList<>();
		for (Object obj : objs) {
			list.add(obj.toString());
		}
		return String.join(delimiter, list);
	}

	public static String join(CharSequence delim, int wrap, Collection<?> objs) {
		if (wrap < 1) return join(delim, objs);

		StringBuilder sb = new StringBuilder();
		int cnt = 0;
		for (Object obj : objs) {
			sb.append(obj.toString());

			sb.append(delim);
			cnt++;
			if (cnt >= wrap) {
				sb.append(EOL);
				cnt = 0;
			}
		}
		if (sb.length() > 0) sb.setLength(sb.length() - delim.length());
		return sb.toString();
	}

	/**
	 * Remove leading reference identifier. No error if the identifier is not present.
	 */
	public static String varName(String varRef) {
		for (char element : R) {
			if (varRef.charAt(0) == element) {
				return varRef.substring(1);
			}
		}
		return varRef;
	}

	/**
	 * Concatenate the given strings into one string using the passed line delimiter as a delimiter.
	 * No delimiter is added to the last line.
	 *
	 * @param lines     the lines
	 * @param delimiter line delimiter
	 * @return the concatenated lines
	 */
	public static String concatenate(String[] lines, String delimiter) {
		String buffer = String.join(delimiter, lines);
		return buffer;
	}

	/**
	 * Returns a new array adding the second array at the end of first array. It answers null if the
	 * first and second are null. If the first array is null or if it is empty, then a new array is
	 * created with second. If the second array is null, then the first array is returned. <br>
	 * <br>
	 * For example:
	 * <ol>
	 * <li>
	 *
	 * <pre>
	 *    first = null
	 *    second = "a"
	 *    => result = {"a"}
	 * </pre>
	 *
	 * <li>
	 *
	 * <pre>
	 *    first = {"a"}
	 *    second = null
	 *    => result = {"a"}
	 * </pre>
	 *
	 * </li>
	 * <li>
	 *
	 * <pre>
	 *    first = {"a"}
	 *    second = {"b"}
	 *    => result = {"a", "b"}
	 * </pre>
	 *
	 * </li>
	 * </ol>
	 *
	 * @param first  the first array to concatenate
	 * @param second the array to add at the end of the first array
	 * @return a new array adding the second array at the end of first array, or null if the two
	 *         arrays are null.
	 */
	public static String[] arrayConcat(String[] first, String second) {
		if (second == null) return first;
		if (first == null) return new String[] { second };

		int length = first.length;
		if (first.length == 0) {
			return new String[] { second };
		}

		String[] result = new String[length + 1];
		System.arraycopy(first, 0, result, 0, length);
		result[length] = second;
		return result;
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

	/////////////////////////////////////////////////////////

	public static String removeNewLine(String message) {
		StringBuffer result = new StringBuffer();
		int current = 0;
		int index = message.indexOf('\n', 0);
		while (index != -1) {
			result.append(message.substring(current, index));
			if (current < index && index != 0) result.append(' ');
			current = index + 1;
			index = message.indexOf('\n', current);
		}
		result.append(message.substring(current));
		return result.toString();
	}

	/**
	 * Returns {@code true} if the given string only consists of white spaces according to Java. If
	 * the string is empty, <code>true
	 * </code> is returned.
	 *
	 * @param s the string to test
	 * @return {@code true} if the string only consists of white spaces; otherwise {@code false} is
	 *         returned
	 * @see java.lang.Character#isWhitespace(char)
	 */
	public static boolean containsOnlyWhitespaces(String s) {
		int size = s.length();
		for (int i = 0; i < size; i++) {
			if (!Character.isWhitespace(s.charAt(i))) return false;
		}
		return true;
	}

	public static String[] removeTrailingEmptyLines(String[] sourceLines) {
		int lastNonEmpty = findLastNonEmptyLineIndex(sourceLines);
		String[] result = new String[lastNonEmpty + 1];
		for (int i = 0; i < result.length; i++) {
			result[i] = sourceLines[i];
		}
		return result;
	}

	private static int findLastNonEmptyLineIndex(String[] sourceLines) {
		for (int i = sourceLines.length - 1; i >= 0; i--) {
			if (!sourceLines[i].trim().equals(EMPTY)) return i;
		}
		return -1;
	}

	public static boolean equals(String s, char[] c) {
		if (s.length() != c.length) return false;

		for (int i = c.length; --i >= 0;) if (s.charAt(i) != c[i]) return false;
		return true;
	}

	public static String removeLeadingCharacters(String text, char toRemove) {
		int idx = 0;
		int len = text.length();
		while (idx < len && text.charAt(idx) == toRemove) {
			idx++;
		}
		return text.substring(idx);
	}

	public static String removeTrailingCharacters(String text, char toRemove) {
		int size = text.length();
		int end = size;
		for (int i = size - 1; i >= 0; i--) {
			char c = text.charAt(i);
			if (c == toRemove) {
				end = i;
			} else {
				break;
			}
		}
		if (end == size) return text;
		else if (end == 0) return EMPTY;
		else return text.substring(0, end);
	}
}
