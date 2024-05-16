package net.certiv.common.stores;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.junit.jupiter.api.Test;

class CounterTest {

	@Test
	void testCounter() {
		Counter ctr = new Counter();
		assertNotNull(ctr);
		assertEquals(0, ctr.get());
		assertEquals(0, ctr.dec());
		assertEquals(1, ctr.inc());

		assertDoesNotThrow(() -> ctr.set(-1));
		assertEquals(-1, ctr.get());

		assertEquals(0, ctr.inc());
		assertEquals(1, ctr.inc());

		assertEquals(0, ctr.dec());
		assertEquals(0, ctr.dec());
		assertEquals(0, ctr.dec());
	}

	@Test
	void testCounterNeg() {
		Counter ctr = new Counter(-1);
		assertNotNull(ctr);
		assertEquals(-1, ctr.get());

		assertDoesNotThrow(() -> ctr.set(-10));
		assertEquals(-10, ctr.get());
		assertEquals(0, ctr.inc());

		assertDoesNotThrow(() -> ctr.set(-10));
		assertEquals(0, ctr.dec());
	}
}
