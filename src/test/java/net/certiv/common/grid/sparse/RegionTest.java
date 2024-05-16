package net.certiv.common.grid.sparse;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvFileSource;

class RegionTest {

	@Test
	void testMinMax() {
		Region r0101_0101 = Region.of("1:1|1:1");
		Region r0102_0102 = Region.of("1:2|1:2");
		Region r0202_0202 = Region.of("2:2|2:2");

		Region max = r0102_0102.max();
		assertEquals(r0202_0202, max);

		Region min = r0102_0102.min();
		assertEquals(r0101_0101, min);
	}

	@ParameterizedTest
	@CsvFileSource(resources = "test_region_functions.csv", numLinesToSkip = 1)
	void testIntersection(int idx, //
			String region, String other, String intersection, //
			boolean intersects, boolean intersectsX, boolean intersectsY, //
			boolean before, boolean above, boolean leads, //
			boolean trails, boolean below, boolean after, //
			boolean contains, boolean within, int compareTo) {

		assertEquals(Region.of(intersection), Region.of(region).intersection(Region.of(other)));
	}

	@ParameterizedTest
	@CsvFileSource(resources = "test_region_functions.csv", numLinesToSkip = 1)
	void testIntersects(int idx, //
			String region, String other, String intersection, //
			boolean intersects, boolean intersectsX, boolean intersectsY, //
			boolean before, boolean above, boolean leads, //
			boolean trails, boolean below, boolean after, //
			boolean contains, boolean within, int compareTo) {

		assertEquals(intersects, Region.of(region).intersects(Region.of(other)));
	}

	@ParameterizedTest
	@CsvFileSource(resources = "test_region_functions.csv", numLinesToSkip = 1)
	void testIntersectsX(int idx, //
			String region, String other, String intersection, //
			boolean intersects, boolean intersectsX, boolean intersectsY, //
			boolean before, boolean above, boolean leads, //
			boolean trails, boolean below, boolean after, //
			boolean contains, boolean within, int compareTo) {

		assertEquals(intersectsX, Region.of(region).interceptsX(Region.of(other)));
	}

	@ParameterizedTest
	@CsvFileSource(resources = "test_region_functions.csv", numLinesToSkip = 1)
	void testIntersectsY(int idx, //
			String region, String other, String intersection, //
			boolean intersects, boolean intersectsX, boolean intersectsY, //
			boolean before, boolean above, boolean leads, //
			boolean trails, boolean below, boolean after, //
			boolean contains, boolean within, int compareTo) {

		assertEquals(intersectsY, Region.of(region).interceptsY(Region.of(other)));
	}

	@ParameterizedTest
	@CsvFileSource(resources = "test_region_functions.csv", numLinesToSkip = 1)
	void testRelativeTo(int idx, //
			String region, String other, String intersection, //
			boolean intersects, boolean intersectsX, boolean intersectsY, //
			boolean before, boolean above, boolean leads, //
			boolean trails, boolean below, boolean after, //
			boolean contains, boolean within, int compareTo) {

		assertEquals(compareTo, Region.of(region).relativeTo(Region.of(other)));
	}

	@ParameterizedTest
	@CsvFileSource(resources = "test_region_functions.csv", numLinesToSkip = 1)
	void testBefore(int idx, //
			String region, String other, String intersection, //
			boolean intersects, boolean intersectsX, boolean intersectsY, //
			boolean before, boolean above, boolean leads, //
			boolean trails, boolean below, boolean after, //
			boolean contains, boolean within, int compareTo) {

		assertEquals(before, Region.of(region).before(Region.of(other)));
	}

	@ParameterizedTest
	@CsvFileSource(resources = "test_region_functions.csv", numLinesToSkip = 1)
	void testAbove(int idx, //
			String region, String other, String intersection, //
			boolean intersects, boolean intersectsX, boolean intersectsY, //
			boolean before, boolean above, boolean leads, //
			boolean trails, boolean below, boolean after, //
			boolean contains, boolean within, int compareTo) {

		assertEquals(above, Region.of(region).above(Region.of(other)));
	}

	@ParameterizedTest
	@CsvFileSource(resources = "test_region_functions.csv", numLinesToSkip = 1)
	void testLeads(int idx, //
			String region, String other, String intersection, //
			boolean intersects, boolean intersectsX, boolean intersectsY, //
			boolean before, boolean above, boolean leads, //
			boolean trails, boolean below, boolean after, //
			boolean contains, boolean within, int compareTo) {

		assertEquals(leads, Region.of(region).leads(Region.of(other)));
	}

	@ParameterizedTest
	@CsvFileSource(resources = "test_region_functions.csv", numLinesToSkip = 1)
	void testBelow(int idx, //
			String region, String other, String intersection, //
			boolean intersects, boolean intersectsX, boolean intersectsY, //
			boolean before, boolean above, boolean leads, //
			boolean trails, boolean below, boolean after, //
			boolean contains, boolean within, int compareTo) {

		assertEquals(below, Region.of(region).below(Region.of(other)));
	}

	@ParameterizedTest
	@CsvFileSource(resources = "test_region_functions.csv", numLinesToSkip = 1)
	void testTrails(int idx, //
			String region, String other, String intersection, //
			boolean intersects, boolean intersectsX, boolean intersectsY, //
			boolean before, boolean above, boolean leads, //
			boolean trails, boolean below, boolean after, //
			boolean contains, boolean within, int compareTo) {

		assertEquals(trails, Region.of(region).trails(Region.of(other)));
	}

	@ParameterizedTest
	@CsvFileSource(resources = "test_region_functions.csv", numLinesToSkip = 1)
	void testAfter(int idx, //
			String region, String other, String intersection, //
			boolean intersects, boolean intersectsX, boolean intersectsY, //
			boolean before, boolean above, boolean leads, //
			boolean trails, boolean below, boolean after, //
			boolean contains, boolean within, int compareTo) {

		assertEquals(after, Region.of(region).after(Region.of(other)));
	}

	@ParameterizedTest
	@CsvFileSource(resources = "test_region_functions.csv", numLinesToSkip = 1)
	void testContains(int idx, //
			String region, String other, String intersection, //
			boolean intersects, boolean intersectsX, boolean intersectsY, //
			boolean before, boolean above, boolean leads, //
			boolean trails, boolean below, boolean after, //
			boolean contains, boolean within, int compareTo) {

		assertEquals(contains, Region.of(region).contains(Region.of(other)));
	}

	@ParameterizedTest
	@CsvFileSource(resources = "test_region_functions.csv", numLinesToSkip = 1)
	void testWithin(int idx, //
			String region, String other, String intersection, //
			boolean intersects, boolean intersectsX, boolean intersectsY, //
			boolean before, boolean above, boolean leads, //
			boolean trails, boolean below, boolean after, //
			boolean contains, boolean within, int compareTo) {

		assertEquals(within, Region.of(region).within(Region.of(other)));
	}
}
