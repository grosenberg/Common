package net.certiv.common.grid.sparse;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.File;
import java.util.LinkedList;
import java.util.List;
import java.util.Map.Entry;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import net.certiv.common.log.Log;
import net.certiv.common.stores.Result;
import net.certiv.common.util.FsUtil;

class SparseGridTest {

	SparseGrid<Region, Integer> grid = new SparseGrid<>();

	Region r0101_0101 = Region.of("1:1|1:1");
	Region r0102_0102 = Region.of("1:2|1:2");
	Region r0202_0202 = Region.of("2:2|2:2");

	Region r0406_0508 = Region.of("4:6|5:8");
	Region r1020_1020 = Region.of("10:20|10:20");
	Region r0020_0020 = Region.of("00:20|00:20");
	Region r2020_2020 = Region.of("20:20|20:20");

	@AfterEach
	void tearDown() {
		grid.clear();
	}

	@Test
	void testWithin() {
		Log.setTestMode(true);

		grid.put(r1020_1020, 0);

		List<Region> found = grid.getWithin(r1020_1020);
		assertEquals(List.of(), found);

		found = grid.getWithin(r0020_0020);
		assertEquals(List.of(), found);
	}

	@Test
	void testContaining() {
		Log.setTestMode(true);

		grid.put(r0102_0102, 0);
		grid.put(r0406_0508, 1);
		grid.put(r1020_1020, 2);

		List<Region> found = grid.getWithin(r0020_0020);
		assertEquals(List.of(r0102_0102, r0406_0508), found);

		found = grid.getWithin(r0101_0101);
		assertEquals(List.of(), found);

		found = grid.getContains(r0101_0101);
		assertEquals(List.of(r0102_0102), found);

		found = grid.getContains(r0102_0102);
		assertEquals(List.of(r0102_0102), found);

		found = grid.getContains(r0202_0202);
		assertEquals(List.of(r0102_0102), found);
	}

	@Test
	void testIntersecting() {
		grid.put(r0102_0102, 0);
		grid.put(r0406_0508, 1);
		grid.put(r1020_1020, 2);

		List<Region> found = grid.getIntersecting(r0101_0101);
		assertEquals(List.of(r0102_0102), found);

		found = grid.getIntersecting(r0202_0202);
		assertEquals(List.of(r0102_0102), found);

		found = grid.getIntersecting(r0020_0020);
		assertEquals(List.of(r0102_0102, r0406_0508, r1020_1020), found);
	}

	@Test
	void testPut() {
		LinkedList<Entry<Region, Integer>> p0 = grid.put(r0102_0102, 0);
		assertTrue(p0.isEmpty());

		LinkedList<Entry<Region, Integer>> p1 = grid.put(r0406_0508, 1);
		assertTrue(p1.isEmpty());

		LinkedList<Entry<Region, Integer>> p2 = grid.put(r1020_1020, 2);
		assertTrue(p2.isEmpty());

		// LinkedList<Entry<Region, Integer>> p3 = grid.put(r2020_2020, 3);
		// assertEquals(List.of(Map.entry(r1020_1020, 2)), p3);
		//
		// List<Region> k3 = grid.getKeys(r2020_2020);
		// assertEquals(List.of(r2020_2020), k3);

		List<Region> k2 = grid.getKeys(r2020_2020);
		assertTrue(k2.isEmpty());
	}

	@Test
	void testVisualize() {
		// Log.setTestMode(true);

		grid.put(r0102_0102, 0);
		grid.put(r0406_0508, 1);
		grid.put(r1020_1020, 2);

		Visualizer<Region, Integer> v = new Visualizer<>(grid);
		v.dim(800);

		File dir = new File(FsUtil.locateTest(getClass()));
		dir.mkdirs();
		File dest = dir.toPath().resolve("VisTest.png").toFile();
		dest.deleteOnExit();

		Result<Boolean> res = v.save(dest, Visualizer.PNG);
		assertEquals(Result.OK, res);
	}
}
