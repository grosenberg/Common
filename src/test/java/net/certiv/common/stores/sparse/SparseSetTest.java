package net.certiv.common.stores.sparse;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.List;

import org.junit.jupiter.api.Test;

import net.certiv.common.stores.range.Range;

class SparseSetTest {

	static final Range R000_100 = new Range(0, 100);
	static final Range R000_009 = new Range(0, 9);
	static final Range R010_020 = new Range(10, 20);
	static final Range R020_060 = new Range(20, 60);
	static final Range R024_040 = new Range(24, 40);
	static final Range R024_029 = new Range(24, 29);
	static final Range R030_034 = new Range(30, 34);
	static final Range R035_060 = new Range(35, 60);

	@Test
	void testLower() {
		SparseSet<Range> set = new SparseSet<>();
		set.add(R035_060);
		set.add(R024_040);
		set.add(R030_034);
		set.add(R000_009);
		set.add(R024_029);
		set.add(R010_020);
		set.add(R020_060);
		set.add(R000_100);

		List<Range> parents = set.lower(R024_029, false, r -> r.contains(R024_029));
		assertEquals(List.of(R024_040, R020_060, R000_100), parents);

		parents = set.lower(R024_029, true, r -> r.contains(R024_029));
		assertEquals(List.of(R024_029, R024_040, R020_060, R000_100), parents);

		parents = set.lower(R024_029, false, r -> r.contains(R024_029), r -> r.contains(R024_029), false);
		assertEquals(List.of(R024_040), parents);
	}
}
