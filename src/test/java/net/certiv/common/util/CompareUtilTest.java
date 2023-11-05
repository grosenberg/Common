package net.certiv.common.util;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.List;

import org.junit.jupiter.api.Test;

class CompareUtilTest {

	/**
	 * <pre>
	 *    , a   => -1
	 * a  , a.b => -1
	 * a.d, c.b => -1
	 * a.b, a.c => -1
	 *    ,     => 0
	 * a  , a   => 0
	 * a.b, a.b => 0
	 * a  ,     => +1
	 * b  , a.b => +1
	 * c  , a.b => +1
	 * a.b, a   => +1
	 * a.c, a.b => +1
	 * </pre>
	 */

	static final String a = "a";
	static final String b = "b";
	static final String c = "c";
	static final List<String> A = List.of(a);
	static final List<String> B = List.of(b);
	static final List<String> C = List.of(c);
	static final List<String> AB = List.of(a, b);
	static final List<String> AC = List.of(a, c);
	static final List<String> BC = List.of(b, c);

	static final List<String> ZIP = List.of();
	static final List<String> EMP = List.of("");

	@Test
	void testCompare() {
		assertEquals(0, CompareUtil.compare(ZIP, ZIP));
		assertEquals(-1, CompareUtil.compare(ZIP, EMP));
		assertEquals(-1, CompareUtil.compare(ZIP, A));

		assertEquals(1, CompareUtil.compare(EMP, ZIP));
		assertEquals(0, CompareUtil.compare(EMP, EMP));
		assertEquals(-1, CompareUtil.compare(EMP, A));

		assertEquals(0, CompareUtil.compare(A, A));
		assertEquals(-1, CompareUtil.compare(A, B));
		assertEquals(-1, CompareUtil.compare(A, AB));

		assertEquals(1, CompareUtil.compare(B, A));
		assertEquals(0, CompareUtil.compare(B, B));
		assertEquals(1, CompareUtil.compare(B, AB));

	}

	@Test
	void testWithin() {
		assertEquals(0, CompareUtil.within(ZIP, ZIP));
		assertEquals(-2, CompareUtil.within(ZIP, EMP));
		assertEquals(-2, CompareUtil.within(ZIP, A));

		assertEquals(2, CompareUtil.within(EMP, ZIP));
		assertEquals(0, CompareUtil.within(EMP, EMP));
		assertEquals(-2, CompareUtil.within(EMP, A));

		assertEquals(0, CompareUtil.within(A, A));
		assertEquals(-2, CompareUtil.within(A, B));
		assertEquals(-1, CompareUtil.within(A, AB));
		assertEquals(1, CompareUtil.within(AB, A));
		assertEquals(-2, CompareUtil.within(AB, B));

		assertEquals(2, CompareUtil.within(B, A));
		assertEquals(0, CompareUtil.within(B, B));
		assertEquals(2, CompareUtil.within(B, AB));
		assertEquals(-1, CompareUtil.within(B, BC));
		assertEquals(1, CompareUtil.within(BC, B));

	}
}
