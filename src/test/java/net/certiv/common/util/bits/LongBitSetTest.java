package net.certiv.common.util.bits;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

import java.util.List;

import org.junit.jupiter.api.Test;

class LongBitSetTest {

	@Test
	void testPositions() {
		LongBitSet bits = new LongBitSet(10);
		bits.set(3);

		List<Long> pos = bits.positions();
		assertFalse(pos.isEmpty());
		assertEquals(1, pos.size());
		assertEquals("{3}", bits.toString());
	}
}
