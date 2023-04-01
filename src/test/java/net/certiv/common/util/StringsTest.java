package net.certiv.common.util;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

class StringsTest {

	private static final String Str0 = "Abbreviates a String";

	@Test
	void testEllipsize() {
		assertEquals(Strings.ellipsize(null, 10), Strings.EMPTY);

		assertEquals(Strings.ellipsize(Str0, 0), Strings.EMPTY);
		assertEquals(Strings.ellipsize(Str0, 1), Strings.EMPTY);
		assertEquals(Strings.ellipsize(Str0, 3), "Ab" + Strings.ELLIPSIS_MARK);
		assertEquals(Strings.ellipsize(Str0, 4), "Abb" + Strings.ELLIPSIS_MARK);
		assertEquals(Strings.ellipsize(Str0, 12), "Abbreviates" + Strings.ELLIPSIS_MARK);
		assertEquals(Strings.ellipsize(Str0, 21), Str0);
		assertEquals(Strings.ellipsize(Str0, 30), Str0);

		assertEquals(Strings.ellipsize(Str0, -1), Strings.EMPTY);
		assertEquals(Strings.ellipsize(Str0, -3), Strings.ELLIPSIS_MARK + "ng");
		assertEquals(Strings.ellipsize(Str0, -4), Strings.ELLIPSIS_MARK + "ing");
		assertEquals(Strings.ellipsize(Str0, -12), Strings.ELLIPSIS_MARK + "es a String");
		assertEquals(Strings.ellipsize(Str0, -21), Str0);
		assertEquals(Strings.ellipsize(Str0, -30), Str0);
	}

}
