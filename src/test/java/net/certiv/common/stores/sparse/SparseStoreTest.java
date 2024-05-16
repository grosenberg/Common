package net.certiv.common.stores.sparse;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

class SparseStoreTest {

	SparseStore<Coord, Integer> store = new SparseStore<>();

	Coord p00_00 = Coord.of();

	Coord p01_01 = Coord.of(1, 1);
	Coord p01_02 = Coord.of(1, 2);
	Coord p05_02 = Coord.of(5, 2);
	Coord p10_20 = Coord.of(10, 20);
	Coord p20_20 = Coord.of(20, 20);

	Coord n01_01 = Coord.of(-1, -1);
	Coord n01_02 = Coord.of(-1, -2);
	Coord n05_02 = Coord.of(-5, -2);
	Coord n10_20 = Coord.of(-10, -20);
	Coord n20_20 = Coord.of(-20, -20);

	@AfterEach
	void tearDown() {
		store.clear();
	}

	@Test
	void testPutGet() {
		Entry<Coord, Integer> p0 = store.put(p01_02, 0);
		assertEquals(null, p0);

		Entry<Coord, Integer> p1 = store.put(p05_02, 1);
		assertEquals(null, p1);

		Entry<Coord, Integer> p2 = store.put(p10_20, 2);
		assertEquals(null, p2);

		Entry<Coord, Integer> p3 = store.put(p10_20, 3);
		assertEquals(Map.entry(p10_20, 2), p3);

		Entry<Coord, Integer> g3 = store.get(p10_20);
		assertEquals(Map.entry(p10_20, 3), g3);

		Entry<Coord, Integer> g4 = store.get(p20_20);
		assertEquals(null, g4);
	}

	@Test
	void testLower() {
		load();

		List<Coord> l1 = store.lower(n01_01, false);
		assertEquals(4, l1.size());

		List<Coord> l2 = store.lower(n01_01, true);
		assertEquals(5, l2.size());
	}

	@Test
	void testHigher() {
		load();

		List<Coord> h1 = store.higher(n01_01, false);
		assertEquals(6, h1.size());

		List<Coord> h2 = store.higher(n01_01, true);
		assertEquals(7, h2.size());
	}

	@Test
	void testLowerPredicates() {
		load();

		List<Coord> l1 = store.lower(n01_01, true, c -> store.get(c).getValue() > -1050);
		assertEquals(4, l1.size());

		List<Coord> l2 = store.lower(n01_01, true, c -> store.get(c).getValue() > -1050,
				c -> store.get(c).getValue() < -1000);
		assertEquals(3, l2.size());

		List<Coord> l6 = store.lower(p05_02, true, c -> store.get(c).getValue() > -1050);
		assertEquals(8, l6.size());

		List<Coord> l7 = store.lower(p05_02, true, c -> store.get(c).getValue() > -1050,
				c -> store.get(c).getValue() < -1000);
		assertEquals(7, l7.size());

		List<Coord> l9 = store.lower(p05_02, true, c -> store.get(c).getValue() % 2 == 0,
				c -> store.get(c).getValue() < -1000);
		assertEquals(5, l9.size());
	}

	@Test
	void testHigherPredicates() {
		load();

		List<Coord> h1 = store.higher(n01_01, true, c -> store.get(c).getValue() < 1050);
		assertEquals(6, h1.size());

		List<Coord> h2 = store.higher(n01_01, true, c -> store.get(c).getValue() < 1050,
				c -> store.get(c).getValue() > 1000);
		assertEquals(5, h2.size());

		List<Coord> h3 = store.higher(n01_01, true, c -> store.get(c).getValue() < 1050,
				c -> store.get(c).getValue() > -200);
		assertEquals(0, h3.size());

		List<Coord> h6 = store.higher(p05_02, true, c -> store.get(c).getValue() < 1050);
		assertEquals(2, h6.size());

		List<Coord> h7 = store.higher(p05_02, true, c -> store.get(c).getValue() < 1050,
				c -> store.get(c).getValue() > 1000);
		assertEquals(1, h7.size());

		List<Coord> h9 = store.higher(p05_02, true, c -> store.get(c).getValue() % 2 == 0,
				c -> store.get(c).getValue() > 1000);
		assertEquals(1, h9.size());
	}

	private void load() {
		store.put(p20_20, 2020);
		store.put(p10_20, 1020);
		store.put(p05_02, 502);	// <--
		store.put(p01_02, 102);
		store.put(p01_01, 101);

		store.put(p00_00, 0);

		store.put(n01_01, -101);
		store.put(n01_02, -102);
		store.put(n05_02, -502);
		store.put(n10_20, -1020);
		store.put(n20_20, -2020);

	}
}
