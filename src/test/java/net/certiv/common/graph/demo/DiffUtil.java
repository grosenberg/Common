package net.certiv.common.graph.demo;

import java.util.Arrays;
import java.util.List;

import org.apache.commons.text.TextStringBuilder;

import com.github.difflib.DiffUtils;
import com.github.difflib.patch.AbstractDelta;
import com.github.difflib.patch.Patch;
import com.github.difflib.text.DiffRow;
import com.github.difflib.text.DiffRowGenerator;

import net.certiv.common.util.Chars;
import net.certiv.common.util.Strings;

public class DiffUtil {

	public static String diffParts(String src, String ref) {
		TextStringBuilder sb = new TextStringBuilder();
		Patch<String> patch = DiffUtils.diff(src, ref, null);
		for (AbstractDelta<String> delta : patch.getDeltas()) {
			sb.appendln(delta);
		}
		return sb.toString();
	}

	public static String diff(String src, String ref) {
		DiffRowGenerator gen = DiffRowGenerator //
				.create() //
				.ignoreWhiteSpaces(true) //
				// .columnWidth(100) //
				.showInlineDiffs(true) //
				// .mergeOriginalRevised(true) //
				.inlineDiffByWord(false) //
				.oldTag(f -> "~") //
				.newTag(f -> "**") //
				.build();

		List<DiffRow> rows = gen.generateDiffRows(split(src), split(ref));
		return print(rows);
	}

	private static List<String> split(String content) {
		return Arrays.asList(content.split(Strings.EOL));
	}

	private static String print(List<DiffRow> diffRows) {
		TextStringBuilder sb = new TextStringBuilder();
		for (DiffRow row : diffRows) {
			sb.append("| ");
			sb.appendFixedWidthPadRight(row.getOldLine().replace("&gt;", ">"), 60, Chars.SP);
			sb.appendFixedWidthPadRight(row.getNewLine().replace("&gt;", ">"), 60, Chars.SP);
			sb.appendln(" |");
		}
		return sb.toString();
	}

}
