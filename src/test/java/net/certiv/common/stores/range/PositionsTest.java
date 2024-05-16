package net.certiv.common.stores.range;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;

import org.junit.jupiter.api.Test;

class PositionsTest {

	PositionRange r0101 = PositionRange.of("1:1");
	PositionRange r0102 = PositionRange.of("1:2");
	PositionRange r0202 = PositionRange.of("2:2");

	PositionRange r0205 = PositionRange.of("2:5");
	PositionRange r0306 = PositionRange.of("3:6");
	PositionRange r0020 = PositionRange.of("00:20");
	PositionRange r1020 = PositionRange.of("10:20");
	PositionRange r2020 = PositionRange.of("20:20");

	@Test
	void testPositions() {
		PositionBits bits = new PositionBits(r0020);
		bits.accrue(r0101);
		bits.accrue(r0306);
		bits.accrue(r2020);

		List<Long> pos = bits.positions();
		assertFalse(pos.isEmpty());
		assertEquals(6, pos.size());
	}

	@Test
	void testPositionsNeg() {
		PositionBits bits = new PositionBits(new PositionRange(-20, 10));
		bits.accrue(new PositionRange(-20));
		bits.accrue(new PositionRange(-15, -12));
		bits.accrue(new PositionRange(-3, 2));
		bits.accrue(new PositionRange(10));

		List<Long> pos = bits.positions();
		assertFalse(pos.isEmpty());
		assertEquals(12, pos.size());

		assertEquals(31, bits.span());
		assertEquals(12, bits.cardinality());
		assertFalse(bits.covered());

		assertEquals("[-20:10] {-20, -15, -14, -13, -12, -3, -2, -1, 0, 1, 2, 10}", bits.toString());
	}

	@Test
	void testIntersectRange() {
		PositionBits bits = new PositionBits(r0020);
		bits.accrue(r0101);
		bits.accrue(r0306);
		bits.accrue(r2020);

		assertEquals(21, bits.span());
		assertEquals(6, bits.cardinality());
		assertFalse(bits.covered());

		bits.accrue(r0020);
		assertEquals(21, bits.cardinality());
		assertTrue(bits.covered());

		// Positions bits = new Positions(r0102);
		// assertEquals(2, bits.length());
		// assertEquals(0, bits.cardinality());
		//
		// bits.accrue(r0101);
		// assertEquals(1, bits.cardinality());
		// assertFalse(bits.covered());
		//
		// System.out.println(bits.toString());
		//
		// bits.accrue(r0205);
		// assertEquals(2, bits.cardinality());
		// assertTrue(bits.covered());
	}
}
