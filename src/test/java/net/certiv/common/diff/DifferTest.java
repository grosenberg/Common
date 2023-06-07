package net.certiv.common.diff;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import net.certiv.common.util.Strings;

class DifferTest {

	static final String ELPS = Strings.ELLIPSIS_MARK;
	static final String BLUE = Ansi.BLUE_BOLD.code;
	static final String RED = Ansi.BLUE_BOLD.code;
	static final String RST = Ansi.RESET.code;

	String txt = "An " + BLUE + "insert" + RST + " string. A " + RED + "removed" + RST + " string.";
	// ------------1234567890123456789012345678901234567
	String plain = "An insert string. A removed string.";

	@Test
	void testFilter() {
		String filtered = Differ.filter(txt);

		assertEquals(filtered, plain);
	}

	@Test
	void testTrim() {
		String str = Differ.trim(txt, 4);
		assertEquals(str, "An " + ELPS);

		str = Differ.trim(txt, 6);
		assertEquals(str, "An " + BLUE + "in" + RST + ELPS);

		str = Differ.trim(txt, 10);
		assertEquals(str, "An " + BLUE + "insert" + RST + ELPS);

		str = Differ.trim(txt, 11);
		assertEquals(str, "An " + BLUE + "insert" + RST + " " + ELPS);

		str = Differ.trim(txt, 22);
		assertEquals(str, "An " + BLUE + "insert" + RST + " string. A " + RED + "r" + RST + ELPS);

		str = Differ.trim(txt, 28);
		assertEquals(str, "An " + BLUE + "insert" + RST + " string. A " + RED + "removed" + RST + ELPS);

		str = Differ.trim(txt, 29);
		assertEquals(str, "An " + BLUE + "insert" + RST + " string. A " + RED + "removed" + RST + " " + ELPS);

		str = Differ.trim(txt, 36);
		assertEquals(str,
				"An " + BLUE + "insert" + RST + " string. A " + RED + "removed" + RST + " string." + ELPS);
	}
}
