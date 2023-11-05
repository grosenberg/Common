package net.certiv.common.sheet;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.List;

import org.junit.jupiter.api.Test;

import net.certiv.common.diff.Differ;
import net.certiv.common.util.test.CommonTestBase;

class SheetTest extends CommonTestBase {

	@Test
	void testSheet() {
		Sheet s = Sheet.of("Test").define("One, Two").width(80);

		s.addRow("A", "B");
		s.putRow(0, "C", "D");
		s.build();

		String name = s.name();
		String sheet = s.print();
		// FsUtil.writeResource(getClass(), name + ".txt", sheet);

		String txt = loadResource(getClass(), name + ".txt");
		Differ.diff(s.name(), txt, sheet).sdiff(true, 120).out();

		assertEquals(txt, sheet);
	}

	@Test
	void testSheetAlign() {
		Sheet s = Sheet.of("TestAlign").define("L:Left:F:16, C:Center, R:Right").width(28);

		// ---------1234567890123456789012345678901234567890123456789012345678901234567890
		String a = "A word";
		String b = "B period";
		String c = "C sentence";

		s.addRow(a, b, c);
		s.build();

		String name = s.name();
		String sheet = s.print();
		// FsUtil.writeResource(getClass(), name + ".txt", sheet);

		String txt = loadResource(getClass(), name + ".txt");
		Differ.diff(s.name(), txt, sheet).sdiff(true, 120).out();

		assertEquals(txt, sheet);
	}

	@Test
	void testSheetFlow() {
		Sheet s = Sheet.of("TestFlow").define("L:Left:F:20, C:Center:F:30, R:Right:A").width(80);

		// ---------1234567890123456789012345678901234567890123456789012345678901234567890
		String a = "All good men have somewhere to be!";
		String b = "No matter where you go, there you are.";
		String c = "Insanity is repeating the same failure and expecting a different outcome";

		s.addRow(a, b, c);
		s.build();

		String name = s.name();
		String sheet = s.print();
		// FsUtil.writeResource(getClass(), name + ".txt", sheet);

		String txt = loadResource(getClass(), name + ".txt");
		Differ.diff(s.name(), txt, sheet).sdiff(true, 120).out();

		assertEquals(txt, sheet);
	}

	@Test
	void testSheetIndent() {
		Sheet s = Sheet.of("TestIndent").define("L:Left:F:20:4, C:Center, R:Right:20:4").width(50);

		// ---------1234567890123456789012345678901234567890123456789012345678901234567890
		String a = "A word";
		String b = "B period";
		String c = "C sentence";

		s.addRow(a, b, c);
		s.build();

		String name = s.name();
		String sheet = s.print();
		// FsUtil.writeResource(getClass(), name + ".txt", sheet);

		String txt = loadResource(getClass(), name + ".txt");
		Differ.diff(s.name(), txt, sheet).sdiff(true, 120).out();

		assertEquals(txt, sheet);
	}

	@Test
	void testSheetBuilder() {
		// ---------1234567890123456789012345678901234567890123456789012345678901234567890
		String a = "A word";
		String b = "B period";
		String c = "C sentence";

		Sheet s = Sheet.of("TestBuilder").width(50);
		Sheet.Builder builder = s.builder();

		builder.col().left().name("Left").fixed().width(20);
		builder.col().center().name("Center");
		builder.col().right().name("Right");

		List<String> data = List.of(a, b, c);
		s.addRow(data);
		s.build();

		String name = s.name();
		String sheet = s.print();
		// FsUtil.writeResource(getClass(), name + ".txt", sheet);

		String txt = loadResource(getClass(), name + ".txt");
		Differ.diff(s.name(), txt, sheet).sdiff(true, 120).out();

		assertEquals(txt, sheet);
	}

}
