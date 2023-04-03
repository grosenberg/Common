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
 * @see org.eclipse.jdt.internal.junit.ui.CompareResultDialog
 */
public class Differ {

	private static final String HDR = "===== %s - %s =====";

	private static final BiFunction<Tag, Boolean, String> RED = //
			(tag, f) -> f ? Ansi.RED_BOLD.code : Ansi.RESET.code;
	private static final BiFunction<Tag, Boolean, String> BLUE =//
			(tag, f) -> f ? Ansi.BLUE_BOLD.code : Ansi.RESET.code;

	private static final String START = "Start";
	private static final String DONE = "Done";

	/** display name */
	private String name;
	/** diff processing produced a change */
	private boolean diff;
	/** diff produced rows */
	private List<DiffRow> rows;

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
	 * Produces a side-by-side difference display to {@code System.out} if a difference
	 * exists.
	 *
	 * @param header {@code true} to produce minimal header and trailer lines
	 * @param width  the production display width
	 */
	public void sdiff(boolean header, int width) {
		sdiff(header, width, false);
	}

	/**
	 * Produces a side-by-side difference display to {@code System.out} if a difference
	 * exists or the {@code force} flag is {@code true}.
	 *
	 * @param header {@code true} to produce minimal header and trailer lines
	 * @param width  the production display width
	 * @param force  {@code true} to force a production even if there is no difference
	 */
	public void sdiff(boolean header, int width, boolean force) {
		if (diff || force) {
			TextStringBuilder sb = new TextStringBuilder();

			if (header) sb.appendln(Strings.padr(String.format(HDR, name, START), width, "="));
			for (DiffRow row : rows) {
				RowDisplay rd = new RowDisplay(row, width);
				sb.appendln(rd);
			}
			if (header) sb.appendln(Strings.padr(String.format(HDR, name, DONE), width, "="));
			System.out.println(sb.toString());
		}
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

	private class RowDisplay {

		private final int lenELL = Strings.ELLIPSIS_MARK.length();
		private final Pattern ANSI = Pattern.compile("\033\\[.+?m");

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

		private String adjust(String txt, int len, int width) {
			if (len < width) return padr(txt, width - len);
			if (len > width) return trim(txt, len, width, len - width);
			return txt;
		}

		private String padr(String txt, int delta) {
			return txt + Strings.SPACE.repeat(delta);
		}

		private String trim(String txt, int len, int width, int delta) {
			// can just ellipsize?
			int dot = txt.lastIndexOf(Ansi.RESET.code);
			if (dot == -1) return Strings.ellipsize(txt, width);

			// will ellipsizing intersect with the last ansi seq?
			int mark = dot + Ansi.RESET.code.length();
			int tail = txt.length() - mark; // length of tailing plain text

			// not if in tail
			if (tail >= delta + lenELL) return Strings.ellipsize(txt, width);

			// have to it the hard (incremental) way
			int trunc = 0;
			Matcher m = ANSI.matcher(txt);
			while (m.find()) {
				if (m.end() < delta + lenELL) {
					trunc = m.end();
				}
			}
			txt = txt.substring(0, trunc) + Ansi.RESET.code + Strings.ELLIPSIS_MARK;
			int rem = filter(txt).length();
			return pad(txt, rem, width);
		}

		private String pad(String txt, int len, int width) {
			if (len < width) {
				txt += Strings.SPACE.repeat(len - width);
			}
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

		private String filter(String txt) {
			return txt.replace(Ansi.RED_BOLD.code, "") //
					.replace(Ansi.BLUE_BOLD.code, "") //
					.replace(Ansi.RESET.code, "");
		}

		@Override
		public String toString() {
			return marginBeg + beg + sep + end + marginEnd;
		}
	}

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
