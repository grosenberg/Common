package net.certiv.common.stores.range;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvFileSource;

class RangeTest {

	@ParameterizedTest
	@CsvFileSource(resources = "test_range_functions.csv", numLinesToSkip = 1)
	void testBefore(int idx, //
			boolean before, boolean after, //
			boolean intersects, String intersection, //
			boolean overlapsMin, boolean overlapsMax, //
			boolean contains, boolean within, //
			int min, int max, int oMin, int oMax) {

		assertEquals(before, new PositionRange(min, max).before(new PositionRange(oMin, oMax)));
	}

	@ParameterizedTest
	@CsvFileSource(resources = "test_range_functions.csv", numLinesToSkip = 1)
	void testAfter(int idx, //
			boolean before, boolean after, //
			boolean intersects, String intersection, //
			boolean overlapsMin, boolean overlapsMax, //
			boolean contains, boolean within, //
			int min, int max, int oMin, int oMax) {

		assertEquals(after, new PositionRange(min, max).after(new PositionRange(oMin, oMax)));
	}

	@ParameterizedTest
	@CsvFileSource(resources = "test_range_functions.csv", numLinesToSkip = 1)
	void testIntersects(int idx, //
			boolean before, boolean after, //
			boolean intersects, String intersection, //
			boolean overlapsMin, boolean overlapsMax, //
			boolean contains, boolean within, //
			int min, int max, int oMin, int oMax) {

		assertEquals(intersects, new PositionRange(min, max).intersects(new PositionRange(oMin, oMax)));
	}

	@ParameterizedTest
	@CsvFileSource(resources = "test_range_functions.csv", numLinesToSkip = 1)
	void testIntersection(int idx, //
			boolean before, boolean after, //
			boolean intersects, String intersection, //
			boolean overlapsMin, boolean overlapsMax, //
			boolean contains, boolean within, //
			int min, int max, int oMin, int oMax) {

		assertEquals(PositionRange.of(intersection),
				new PositionRange(min, max).intersection(new PositionRange(oMin, oMax)));
	}

	@ParameterizedTest
	@CsvFileSource(resources = "test_range_functions.csv", numLinesToSkip = 1)
	void testOverlapsMin(int idx, //
			boolean before, boolean after, //
			boolean intersects, String intersection, //
			boolean overlapsMin, boolean overlapsMax, //
			boolean contains, boolean within, //
			int min, int max, int oMin, int oMax) {

		assertEquals(overlapsMin, new PositionRange(min, max).overlapsMin(new PositionRange(oMin, oMax)));
	}

	@ParameterizedTest
	@CsvFileSource(resources = "test_range_functions.csv", numLinesToSkip = 1)
	void testOverlapsMax(int idx, //
			boolean before, boolean after, //
			boolean intersects, String intersection, //
			boolean overlapsMin, boolean overlapsMax, //
			boolean contains, boolean within, //
			int min, int max, int oMin, int oMax) {

		assertEquals(overlapsMax, new PositionRange(min, max).overlapsMax(new PositionRange(oMin, oMax)));
	}

	@ParameterizedTest
	@CsvFileSource(resources = "test_range_functions.csv", numLinesToSkip = 1)
	void testContains(int idx, //
			boolean before, boolean after, //
			boolean intersects, String intersection, //
			boolean overlapsMin, boolean overlapsMax, //
			boolean contains, boolean within, //
			int min, int max, int oMin, int oMax) {

		assertEquals(contains, new PositionRange(min, max).contains(new PositionRange(oMin, oMax)));
	}

	@ParameterizedTest
	@CsvFileSource(resources = "test_range_functions.csv", numLinesToSkip = 1)
	void testWithin(int idx, //
			boolean before, boolean after, //
			boolean intersects, String intersection, //
			boolean overlapsMin, boolean overlapsMax, //
			boolean contains, boolean within, //
			int min, int max, int oMin, int oMax) {

		assertEquals(within, new PositionRange(min, max).within(new PositionRange(oMin, oMax)));
	}

}
