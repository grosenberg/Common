package net.certiv.common.diff;

import java.util.Arrays;
import java.util.List;
import java.util.function.BiFunction;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.text.TextStringBuilder;

import com.github.difflib.text.DiffRow;
import com.github.difflib.text.DiffRow.Tag;
import com.github.difflib.text.DiffRowGenerator;
import com.github.difflib.text.DiffRowGenerator.Builder;

import net.certiv.common.util.Strings;

/**
 * Produces a table providing a side-by-side difference comparison between two texts.
 * <p>
 * Use {@link #out} to print to standard out or {@link #str} to return the difference as a
 * string.
 *
 * <pre>
 * Differ.diff(name, ori, txt).sdiff(true, 120).out();
 * Log.debug("Difference\n%s", Differ.diff(name, ori, txt).sdiff(true, 120).str());
 * </pre>
 *
 * @see org.eclipse.jdt.internal.junit.ui.CompareResultDialog
 */
public class Differ {

	private static final Pattern ANSI = Pattern.compile("\033\\[.+?m");
	private static final int ELLIP_LEN = Strings.ELLIPSIS_MARK.length();

	private static final String HDR = "===== %s - %s =====";

	private static final BiFunction<Tag, Boolean, String> RED = //
			(tag, f) -> f ? Ansi.RED_BOLD.code : Ansi.RESET.code;
	private static final BiFunction<Tag, Boolean, String> BLUE =//
			(tag, f) -> f ? Ansi.BLUE_BOLD.code : Ansi.RESET.code;

	private static final String START = "Start";
	private static final String DONE = "Done";

	/** display name */
	private String name;
	/** whether diff processing produced a change */
	private boolean diff;
	/** diff produced rows */
	private List<DiffRow> rows;
	/** diff display results */
	private String result;

	public static Differ diff(String name, String src, String ref) {
		return new Differ(name, src, ref, true);
	}

	public static Differ diff(String name, String src, String ref, int width, boolean ansi) {
		return new Differ(name, src, ref, ansi);
	}

	private Differ(String name, String src, String ref, boolean ansi) {
		this.name = name;
		rows = generate(src, ref, ansi);
		diff = rows.stream().anyMatch(r -> !r.getNewLine().equals(r.getOldLine()));
	}

	public boolean hasDiff() {
		return diff;
	}

	/**
	 * Produces a side-by-side difference display if a difference exists.
	 *
	 * @param header {@code true} to produce minimal header and trailer lines
	 * @param width  the production display width
	 * @return this instance
	 */
	public Differ sdiff(boolean header, int width) {
		sdiff(header, width, false);
		return this;
	}

	/**
	 * Produces a side-by-side difference display if a difference exists or the
	 * {@code force} flag is {@code true}.
	 *
	 * @param header {@code true} to produce minimal header and trailer lines
	 * @param width  the production display width
	 * @param force  {@code true} to force a production even if there is no difference
	 * @return this instance
	 */
	public Differ sdiff(boolean header, int width, boolean force) {
		result = Strings.EMPTY;
		if (diff || force) {
			TextStringBuilder sb = new TextStringBuilder();
			if (header) sb.appendln(Strings.padr(String.format(HDR, name, START), width, "="));
			for (DiffRow row : rows) {
				RowDisplay rd = new RowDisplay(row, width);
				sb.appendln(rd);
			}
			if (header) sb.appendln(Strings.padr(String.format(HDR, name, DONE), width, "="));
			result = sb.toString();
		}
		return this;
	}

	/** Print the results of {@link #sdiff} to {@code System.out} */
	public void out() {
		System.out.println(result);
	}

	/** Print the results of {@link #sdiff} to {@code System.err} */
	public void err() {
		System.err.println(result);
	}

	/** @return the results of {@link #sdiff} */
	public String str() {
		return result;
	}

	private List<DiffRow> generate(String src, String ref, boolean ansi) {
		Builder builder = DiffRowGenerator //
				.create() //
				.ignoreWhiteSpaces(true) //
				.showInlineDiffs(true) //
				.inlineDiffByWord(true) //
		;

		if (ansi) {
			builder.oldTag(RED).newTag(BLUE);
		} else {
			builder.oldTag(f -> "~").newTag(f -> "**");
		}

		DiffRowGenerator gen = builder.build();
		return gen.generateDiffRows(split(src), split(ref));
	}

	private List<String> split(String content) {
		if (content == null) return List.of(Strings.EMPTY);
		return Arrays.asList(content.split(Strings.EOL));
	}

	/** Pad right. */
	static String padr(String txt, int delta) {
		return txt + Strings.SPACE.repeat(delta);
	}

	/**
	 * Trim the given text to the given visual width.
	 *
	 * @param txt   a text string, potentially including ANSI sequences
	 * @param width a visual length limit
	 * @return a portion of the text string adjusted to fit within the given width
	 */
	static String trim(String txt, int width) {
		width = Math.max(ELLIP_LEN, width - ELLIP_LEN);

		int fdot = 0; // cursor in full string
		int pdot = 0; // cursor in plain string
		Matcher m = ANSI.matcher(txt);
		while (m.find()) {
			int len = m.start() - fdot; // subsequence plain text length

			// if trim point before this code?
			if (pdot + len >= width) {
				int frac = width - pdot; // partial subsequence length
				String str = txt.substring(0, fdot + frac);
				boolean inAnsiSpan = m.group().equals(Ansi.RESET.code);
				if (inAnsiSpan) {
					str += Ansi.RESET.code;
				}
				str += Strings.ELLIPSIS_MARK;
				return str;
			}

			// trim point is after this code
			pdot += len;
			fdot = m.end();
		}

		// trim point is after last code
		int frac = width - pdot; // partial subsequence length
		String str = txt.substring(0, fdot + frac) + Strings.ELLIPSIS_MARK;
		return str;
	}

	static String filter(String txt) {
		return ANSI.matcher(txt).replaceAll(Strings.EMPTY);
	}

	private class RowDisplay {

		private final String marginBeg = "|  ";
		private final String marginEnd = "  |";
		private final String allchg = "  |   ";
		private final String oldchg = "  <   ";
		private final String newchg = "  >   ";
		private final String no_chg = "      ";

		private String beg;
		private String end;
		private String sep;

		RowDisplay(DiffRow row, int width) {

			beg = fix(row.getOldLine());
			end = fix(row.getNewLine());

			int begLen = filter(beg).length();
			int endLen = filter(end).length();

			sep = delta(beg, begLen, end, endLen);

			int _width = (width - 12) / 2;
			beg = adjust(beg, begLen, _width);
			end = adjust(end, endLen, _width);
		}

		String adjust(String txt, int len, int width) {
			if (len < width) return padr(txt, width - len);
			if (len > width) return trim(txt, width);
			return txt;
		}

		private String delta(String beg, int begLen, String end, int endLen) {
			boolean begChg = beg.length() != begLen;
			boolean endChg = end.length() != endLen;

			if (begChg && !endChg) return oldchg;
			if (!begChg && endChg) return newchg;
			if (begChg && endChg) return allchg;
			return no_chg;
		}

		private String fix(String txt) {
			return txt.replace("&lt;", "<").replace("&gt;", ">");
		}

		@Override
		public String toString() {
			return marginBeg + beg + sep + end + marginEnd;
		}
	}

	// ================================

	// /**
	// * Trim a string, potentially including embedded ANSI sequences, to a given
	// width.
	// * <p>
	// * If no ANSI sequences, just trim. Otherwise, increment through the stream to
	// * find the longest substring that fits within the given width.
	// *
	// * @param txt the string with embedded ANSI to trim
	// * @param len the string length without embedded ANSI
	// * @param width the target width without embedded ANSI
	// * @param delta difference between original length and target width
	// * @return the trimmed string
	// */
	// private String trim(String txt, int len, int width, int delta) {
	//
	// // just ellipsize if no ANSI present
	// int dot = txt.lastIndexOf(Ansi.RESET.code);
	// if (dot == -1) return Strings.ellipsize(txt, width);
	//
	// // will ellipsizing intersect with the last ansi seq?
	// int mark = dot + Ansi.RESET.code.length();
	// int tail = txt.length() - mark; // length of tailing plain text
	//
	// // not if in tail
	// if (tail >= delta + ELLIP_LEN) return Strings.ellipsize(txt, width);
	//
	// // have to it the hard (incremental) way
	// int trunc = 0;
	// Matcher m = ANSI.matcher(txt);
	// while (m.find()) {
	// if (m.end() < delta + ELLIP_LEN) {
	// trunc = m.end();
	// }
	// }
	// txt = txt.substring(0, trunc) + Ansi.RESET.code + Strings.ELLIPSIS_MARK;
	// int rem = filter(txt).length();
	// return pad(txt, rem, width);
	// }

	// ================================

	// private String format(List<DiffRow> diffRows) {
	// TextStringBuilder sb = new TextStringBuilder();
	// for (DiffRow row : diffRows) {
	// sb.append("| ");
	// sb.appendFixedWidthPadRight(row.getOldLine().replace("&gt;", ">"), 80, Chars.SP);
	// sb.appendFixedWidthPadRight(row.getNewLine().replace("&gt;", ">"), 80, Chars.SP);
	// sb.appendln(" |");
	// }
	// return sb.toString();
	// }

	// public static String diffParts(String src, String ref) {
	// TextStringBuilder sb = new TextStringBuilder();
	// Patch<String> patch = DiffUtils.diff(src, ref, null);
	// for (AbstractDelta<String> delta : patch.getDeltas()) {
	// sb.appendln(delta);
	// }
	// return sb.toString();
	// }
}
