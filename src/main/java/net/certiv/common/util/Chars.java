package net.certiv.common.util;

import java.io.File;

public final class Chars {

	private Chars() {}

	public static final char[] EMPTY_CHARS = {};

	public static final char ACCENT = '\'';
	public static final char AT = '@';
	public static final char BANG = '!';
	public static final char DOT = '.';
	public static final char COMMA = ',';
	public static final char COLON = ':';
	public static final char DASH = '-';
	public static final char HASH = '#';
	public static final char LOWDASH = '_';
	public static final char MARK = '\'';
	public static final char PIPE = '|';
	public static final char PLUS = '+';
	public static final char QMARK = '?';
	public static final char QUOTE = '"';
	public static final char SEMI = ';';
	public static final char SLASH = '/';
	public static final char STAR = '*';
	public static final char SP = ' ';
	public static final char TIC = '`';
	public static final char TILDE = '~';

	public static final char LBRACK = '[';
	public static final char RBRACK = ']';
	public static final char LANGLE = '<';
	public static final char RANGLE = '>';
	public static final char LBRACE = '{';
	public static final char RBRACE = '}';

	public static final char ESC = '\\';
	public static final char TAB = '\t';
	public static final char RET = '\r';
	public static final char NL = '\n';

	public static final char EOP = File.separatorChar; // path separator

	/**
	 * Returns {@code true} if the given text contains one or more whitespace characters.
	 */
	public static boolean isWhitespace(CharSequence text) {
		return ((String) text).matches("\\s+");
	}

	/** Returns {@code true} if a character is punctuation, and false otherwise. */
	public static boolean isPunctuation(char c) {
		switch (Character.getType(c)) {
			case Character.START_PUNCTUATION:
			case Character.END_PUNCTUATION:
			case Character.OTHER_PUNCTUATION:
			case Character.CONNECTOR_PUNCTUATION:
			case Character.DASH_PUNCTUATION:
			case Character.INITIAL_QUOTE_PUNCTUATION:
			case Character.FINAL_QUOTE_PUNCTUATION:
				return true;
		}
		return false;
	}

	/** Returns {@code true} if a character is a symbol, and false otherwise. */
	public static boolean isSymbol(char c) {
		switch (Character.getType(c)) {
			case Character.MATH_SYMBOL:
			case Character.CURRENCY_SYMBOL:
			case Character.MODIFIER_SYMBOL:
			case Character.OTHER_SYMBOL:
				return true;
		}
		return false;
	}

	/**
	 * Returns {@code true} if a character is a control character, and false otherwise.
	 */
	public static boolean isControl(char c) {
		return Character.getType(c) == Character.CONTROL;
	}

	public static boolean isSeparator(char ch) {
		switch (Character.getType(ch)) {
			case Character.SPACE_SEPARATOR:
			case Character.LINE_SEPARATOR:
			case Character.PARAGRAPH_SEPARATOR:
				return true;
		}
		return false;
	}
}
