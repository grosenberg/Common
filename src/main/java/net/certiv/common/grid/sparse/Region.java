package net.certiv.common.grid.sparse;

import java.util.Comparator;
import java.util.Objects;
import java.util.function.Predicate;
import java.util.regex.Pattern;

import net.certiv.common.ex.IllegalArgsEx;
import net.certiv.common.stores.range.PositionBits;
import net.certiv.common.stores.range.PositionRange;

public class Region implements Comparable<Region> {

	private static final Comparator<Region> COMP = new Comparator<>() {

		public int compare(Region o1, Region o2) {
			if (o1.leads(o2) && !o1.below(o2) || o1.above(o2)) return -1;
			if (o1.trails(o2) && !o1.above(o2) || o1.below(o2)) return 1;
			return 0;
		}
	};

	static final Pattern RANGES = Pattern.compile("\\h*\\|\\h*");
	static final IllegalArgsEx ErrParse = IllegalArgsEx.of("Illegal parse argument value '%s' in '%s'.");

	/** X range (horizontal). */
	private PositionRange x;
	/** Y range (vertical). */
	private PositionRange y;

	/** Independent X intercept positions. */
	private PositionBits xIntercepts;
	/** Independent Y intercept positions. */
	private PositionBits yIntercepts;

	/**
	 * Returns the region determined by parsing the given dual range specification. Each
	 * range specification is a <i>Colon Separated Value</i> representing one to two
	 * integer values, nominally {@code MIN:MAX}. The range specifications are separated
	 * by a vertical bar, nominally {@code MinX:MaxX|MinY:MaxY}.
	 *
	 * @param spec range specification
	 * @return a {@code PositionRange}
	 */
	public static Region of(String spec) {
		if (spec == null || spec.isBlank()) return null;

		String[] parts = RANGES.split(spec, -1);
		if (parts.length != 2) {
			throw IllegalArgsEx.of("Illegal parse argument specification '%s'.", spec);
		}

		PositionRange x = parseRange(parts[0], spec);
		PositionRange y = parseRange(parts[1], spec);

		return new Region(x, y);
	}

	private static PositionRange parseRange(String subspec, String spec) {
		if (subspec == null || subspec.isBlank()) throw ErrParse.formatted(subspec, spec);
		try {
			return PositionRange.of(subspec);
		} catch (Exception e) {
			throw ErrParse.formatted(subspec, spec);
		}
	}

	/**
	 * Constructs an instance.
	 *
	 * @param x X region; horizontal span=1
	 * @param y Y region; vertical span=1
	 */
	public Region(int x, int y) {
		this(x, x, y, y);
	}

	/**
	 * Constructs an instance.
	 *
	 * @param x0 X region begin
	 * @param x1 X region end
	 * @param y0 Y region begin
	 * @param y1 Y region end
	 */
	public Region(int x0, int x1, int y0, int y1) {
		this(new PositionRange(x0, x1), new PositionRange(y0, y1));
	}

	public Region(PositionRange x, PositionRange y) {
		this.x = x;
		this.y = y;

		this.xIntercepts = new PositionBits(x);
		this.yIntercepts = new PositionBits(y);
	}

	/**
	 * Returns the X (horizontal/column indexes) range.
	 *
	 * @return the X range
	 */
	public PositionRange x() {
		return x;
	}

	/**
	 * Returns the minimum X (horizontal/column index) value.
	 *
	 * @return the minimum X value
	 */
	public int xMin() {
		return x.min;
	}

	/**
	 * Returns the maximum X (horizontal/column index) value.
	 *
	 * @return the maximum X value
	 */
	public int xMax() {
		return x.max;
	}

	/**
	 * Returns the Y (vertical/row indexes) range.
	 *
	 * @return the Y range
	 */
	public PositionRange y() {
		return y;
	}

	/**
	 * Returns the minimum Y (horizontal/column index) value.
	 *
	 * @return the minimum Y value
	 */
	public int yMin() {
		return y.min;
	}

	/**
	 * Returns the maximum Y (horizontal/column index) value.
	 *
	 * @return the maximum Y value
	 */
	public int yMax() {
		return y.max;
	}

	/**
	 * Returns a new {@link Region} constructed using the minimum X and Y values of this
	 * region.
	 *
	 * @return minimum values region
	 */
	public Region min() {
		return new Region(xMin(), yMin());
	}

	/**
	 * Returns a new {@link Region} constructed using the maximum X and Y values of this
	 * region.
	 *
	 * @return maximum values region
	 */
	public Region max() {
		return new Region(xMax(), yMax());
	}

	/**
	 * Returns the row indexes (Y/vertical) range.
	 *
	 * @return the row indexes range
	 */
	public PositionRange rows() {
		return y;
	}

	/**
	 * Returns the column indexes (X/horizontal) range.
	 *
	 * @return the column indexes range
	 */
	public PositionRange cols() {
		return x;
	}

	/**
	 * Determines whether this region contains the given point.
	 *
	 * @param region region to check; {@code null} returns false
	 * @return {@code true} if this region contains the given point
	 */
	public boolean contains(Region region) {
		if (region == null) return false;
		return y.contains(region.y()) && x.contains(region.x());
	}

	/**
	 * Determines whether this region contains the given region.
	 *
	 * @param px point x value
	 * @param py point y value
	 * @return {@code true} if this region contains the given region
	 */
	public boolean contains(int px, int py) {
		return x.contains(px) && y.contains(py);
	}

	/**
	 * Returns whether this region is fully within the given region.
	 *
	 * @param other another region
	 * @return {@code true} if this region is fully within the given region
	 */
	public boolean within(Region other) {
		return x.within(other.x()) && y.within(other.y());
	}

	// --------------------------------

	/**
	 * Determines whether this region insersects the given region. Two regions insersect
	 * if they contain at least one value in common.
	 *
	 * @param other range to check
	 * @return {@code true} if this range insersects the given region
	 */
	public boolean intersects(Region other) {
		return intersects(other, false);
	}

	/**
	 * Determines whether this region insersects the given region. Two regions insersect
	 * if they contain at least one value in common.
	 * <p>
	 * The intersection, if accrued, is accumulated into a pair of bit sets that together
	 * represent each position in this region.
	 * <p>
	 * The default {@link Region} implementation uses the bit sets built into the
	 * {@link #x()} and {@link #y()} ranges.
	 *
	 * @param other  another range
	 * @param accrue {@code true} to accrue intersection coverage
	 * @return {@code true} if this range insersects the given region
	 */
	public boolean intersects(Region other, boolean accrue) {
		if (other == null) return false;
		boolean ok = x.intersects(other.x()) && y.intersects(other.y());
		if (ok && accrue) {
			x.intersection(other.x(), accrue);
			y.intersection(other.y(), accrue);
		}
		return ok;
	}

	/**
	 * Return a region repesenting the intersection of this region and another. Returns
	 * {@code null} if there is no intersection.
	 *
	 * @param other another region
	 * @return range representing the intersection of this region and another, or
	 *         {@code null} if there is no intersection
	 */
	public Region intersection(Region other) {
		if (equals(other)) return this;
		PositionRange ix = (PositionRange) x.intersection(other.x());
		if (ix != null) {
			PositionRange iy = (PositionRange) y.intersection(other.y());
			if (iy != null) return new Region(ix, iy);
		}
		return null;
	}

	/**
	 * Returns whether the accumulated intersections have covered the region.
	 *
	 * @return {@code true} if the entire region has been covered
	 */
	public boolean covered() {
		return x.covered() && y.covered();
	}

	/**
	 * Returns whether the accumulated intersections have covered the region X range.
	 *
	 * @return {@code true} if the entire X range has been covered
	 */
	public boolean coveredX() {
		return x.covered();
	}

	/**
	 * Returns whether the accumulated intersections have covered the region Y range.
	 *
	 * @return {@code true} if the entire Y range has been covered
	 */
	public boolean coveredY() {
		return y.covered();
	}

	/**
	 * Sets the intersection coverage to a fully uncovered state.
	 */
	public void clearIntersections() {
		x.clear();
		y.clear();
	}

	// --------------------------------

	/**
	 * Determines whether the X range (column indexes) of this region insersects the X
	 * range of the given region. Returns {@code true} if there is any shared X value
	 * (column index).
	 *
	 * @param other range to check
	 * @return {@code true} if the X ranges insersect
	 */
	public boolean interceptsX(Region other) {
		return interceptsX(other, false, null);
	}

	/**
	 * Determines whether the X range (column indexes) of this region intercepts the X
	 * range of the given region. Returns {@code true} if there is any shared X value
	 * (column index).
	 * <p>
	 * Each intercept, if accrued, is accumulated into a bit set that represents each X
	 * position in this region. The X intercepts bit set is independent of the bit sets
	 * associated with {@link #intersection(IRegion)}.
	 * <p>
	 * The default {@link Region} implementation provides an X bit set distinct from that
	 * built into the {@link #x()} range.
	 *
	 * @param other  another range
	 * @param accrue {@code true} to accrue intercept coverage
	 * @return {@code true} if the X ranges intercept
	 */
	public boolean interceptsX(Region other, boolean accrue) {
		return interceptsX(other, accrue, null);
	}

	/**
	 * Determines whether the X range (column indexes) of this region intercepts the X
	 * range of the given region subject to the given predicate filter. A {@code null}
	 * filter predicate is equivalent to a 'match all' criteria. Returns {@code true} if
	 * there is any accepted X value (column index).
	 * <p>
	 * Each intercept, if accepted and accrued, is accumulated into a bit set that
	 * represents each X position in this region. The X intercepts bit set is independent
	 * of the bit sets associated with {@link #intersection(IRegion)}.
	 * <p>
	 * The default {@link Region} implementation provides an X bit set distinct from that
	 * built into the {@link #x()} range.
	 *
	 * @param other  another range
	 * @param accrue {@code true} to accrue accepted intercept coverage
	 * @param filter predicate defining an accept criteria; may be {@code null}
	 * @return {@code true} if the accepted X ranges intercept
	 */
	public boolean interceptsX(Region other, boolean accrue, Predicate<Region> filter) {
		if (other == null) return false;
		if (filter != null && !filter.test(other)) return false;
		boolean ok = x.intersects(other.x());
		if (ok && accrue) xIntercepts.accrue(x.intersection(other.x()));
		return ok;
	}

	/**
	 * Returns whether the accumulated X intercepts have covered the region.
	 *
	 * @return {@code true} if the X intercepts cover the entire region
	 */
	public boolean coveredXIntercepts() {
		return xIntercepts.covered();
	}

	/**
	 * Sets the X interception coverage to a fully uncovered state.
	 */
	public void clearXIntercepts() {
		xIntercepts.clear();
	}

	// --------------------------------

	/**
	 * Determines whether the Y range (row indexes) of this region insersects the Y range
	 * of the given region. Returns {@code true} if there is any shared Y value (row
	 * index).
	 *
	 * @param other range to check
	 * @return {@code true} if the Y ranges insersect
	 */
	public boolean interceptsY(Region other) {
		return interceptsY(other, false, null);
	}

	/**
	 * Determines whether the Y range (column indexes) of this region intercepts the Y
	 * range of the given region. Returns {@code true} if there is any shared Y value
	 * (column index).
	 * <p>
	 * Each intercept, if accrued, is accumulated into a bit set that represents each Y
	 * position in this region. The Y intercepts bit set is independent of the bit sets
	 * associated with {@link #intersection(IRegion)}.
	 * <p>
	 * The default {@link Region} implementation provides an Y bit set distinct from that
	 * built into the {@link #x()} range.
	 *
	 * @param other  another range
	 * @param accrue {@code true} to accrue intercept coverage
	 * @return {@code true} if the Y ranges intercept
	 */
	public boolean interceptsY(Region other, boolean accrue) {
		return interceptsY(other, accrue, null);
	}

	/**
	 * Determines whether the Y range (column indexes) of this region intercepts the Y
	 * range of the given region subject to the given predicate filter. A {@code null}
	 * filter predicate is equivalent to a 'match all' criteria. Returns {@code true} if
	 * there is any accepted Y value (column index).
	 * <p>
	 * Each intercept, if accepted and accrued, is accumulated into a bit set that
	 * represents each Y position in this region. The Y intercepts bit set is independent
	 * of the bit sets associated with {@link #intersection(IRegion)}.
	 * <p>
	 * The default {@link Region} implementation provides an Y bit set distinct from that
	 * built into the {@link #x()} range.
	 *
	 * @param other  another range
	 * @param accrue {@code true} to accrue accepted intercept coverage
	 * @param filter predicate defining an accept criteria; may be {@code null}
	 * @return {@code true} if the accepted Y ranges intercept
	 */
	public boolean interceptsY(Region other, boolean accrue, Predicate<Region> filter) {
		if (other == null) return false;
		if (filter != null && !filter.test(other)) return false;
		boolean ok = y.intersects(other.y());
		if (ok && accrue) yIntercepts.accrue(y.intersection(other.y()));
		return ok;
	}

	/**
	 * Returns whether the accumulated Y intercepts have covered the region.
	 *
	 * @return {@code true} if the Y intercepts cover the entire region
	 */
	public boolean coveredYIntercepts() {
		return yIntercepts.covered();
	}

	/**
	 * Sets the Y interception coverage to a fully uncovered state.
	 */
	public void clearYIntercepts() {
		yIntercepts.clear();
	}

	// --------------------------------

	public Comparator<Region> comparator() {
		return COMP;
	}

	/**
	 * Returns an integer in the range [-1..1] identifying the position of this region
	 * relative to the given region. Compares the X ranges for order. If the X ranges are
	 * the same, compares the Y ranges.
	 *
	 * <pre>{@code
	 * -1: before
	 *  0: equals
	 *  1: after
	 * }</pre>
	 *
	 * @param other the object to be compared.
	 * @return integer identifying the relative position of this region
	 */
	@Override
	public int compareTo(Region other) {
		if (other == null) throw IllegalArgsEx.of("CompareTo range argument cannot be 'null'.");

		int v = x().compareTo(other.x());
		if (v != 0) return v;
		return y().compareTo(other.y());
	}

	/**
	 * Extended {@link Comparable#compareTo}. Returns an integer in the range [-3..3]
	 * identifying the position of this region, as defined below, relative to the given
	 * region.
	 *
	 * <pre>{@code
	 * maxY  +--------+----------------------+
	 *       | before |        above         |
	 *       +--------+----------+-----------+
	 *       | leads  | (other)  |  trails   |
	 *       +--------+----------+-----------+
	 *       |       below       |  after    |
	 * minY  +-------------------+-----------+
	 *      minX                           maxX
	 *
	 *
	 * -3: before: above & leads
	 * -2: above:  y.min > other.y.max
	 * -1: leads:  x.max < other.x.min
	 *  0: intersects: == overlaps == colides
	 *  1: trails: x.min > other.x.max
	 *  2: below:  y.max < other.y.min
	 *  3: after:  below && trails
	 * }</pre>
	 *
	 * @param other the object to be compared.
	 * @return relative position of this region
	 */
	public int relativeTo(Region other) {
		if (before(other)) return -3;
		if (above(other)) return -2;
		if (leads(other)) return -1;
		if (after(other)) return 3;
		if (trails(other)) return 2;
		if (below(other)) return 1;

		// order intersected by min coordinate
		return compareTo(other);

		// if (x.min < other.x().min) return -1;
		// if (x.min > other.x().min) return 1;
		// if (y.min < other.y().min) return -1;
		// if (y.min > other.y().min) return 1;
		// return 0;
	}

	// Pos relativeTo(IRegion other);
	//
	// public enum Pos {
	// /** -3: before: above & leads */
	// BEFORE(-3),
	// /** -2: above: y.min > other.y.max */
	// ABOVE(-2),
	// /** -1: leads: x.max < other.x.min */
	// LEADS(-1),
	// /** 0: intersects: == overlaps == colides */
	// INTERSECTS(0),
	// /** 1: trails: x.min > other.x.max */
	// TRAILS(1),
	// /** 2: below: y.max < other.y.min */
	// BELOW(2),
	// /** 3: after: below && trails */
	// AFTER(3);
	//
	// public final int relative;
	//
	// Pos(int relative) {
	// this.relative = relative;
	// }
	// }

	/**
	 * Determine whether this region is ahead of the given region (before and leads).
	 *
	 * @param other another region
	 * @return {@code true} if this region is ahead
	 * @see #compareTo(IRegion)
	 */
	public boolean ahead(Region other) {
		return above(other) || leads(other);
	}

	/**
	 * Determine whether this region is before the given region.
	 *
	 * @param other another region
	 * @return {@code true} if this region is before
	 * @see #compareTo(IRegion)
	 */
	public boolean before(Region other) {
		return above(other) && leads(other);
	}

	/**
	 * Determine whether this region is above the given region.
	 *
	 * @param other another region
	 * @return {@code true} if this region is above
	 * @see #compareTo(IRegion)
	 */
	public boolean above(Region other) {
		return y.min > other.y().max;
	}

	/**
	 * Determine whether this region leads the given region.
	 *
	 * @param other another region
	 * @return {@code true} if this region leads
	 * @see #compareTo(IRegion)
	 */
	public boolean leads(Region other) {
		return x.max < other.x().min;
	}

	/**
	 * Determine whether this region trails the given region.
	 *
	 * @param other another region
	 * @return {@code true} if this region trails
	 * @see #compareTo(IRegion)
	 */
	public boolean trails(Region other) {
		return x.min > other.x().max;
	}

	/**
	 * Determine whether this region is below the given region.
	 *
	 * @param other another region
	 * @return {@code true} if this region is below
	 * @see #compareTo(IRegion)
	 */
	public boolean below(Region other) {
		return y.max < other.y().min;
	}

	/**
	 * Determine whether this region is after the given region (below and trails).
	 *
	 * @param other another region
	 * @return {@code true} if this region is after
	 * @see #compareTo(IRegion)
	 */
	public boolean after(Region other) {
		return below(other) && trails(other);
	}

	/**
	 * Determine whether this region is higher than the given region (below or trails).
	 *
	 * @param other another region
	 * @return {@code true} if this region is higher
	 * @see #compareTo(IRegion)
	 */
	public boolean behind(Region other) {
		return below(other) || trails(other);
	}

	// --------------------------------

	//
	// public Iterator<Integer> iterator() {
	// return null;
	// }

	@Override
	public int hashCode() {
		return Objects.hash(x, y);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) return true;
		if (!(obj instanceof Region)) return false;
		Region other = (Region) obj;
		return Objects.equals(x, other.x) && Objects.equals(y, other.y);
	}

	@Override
	public String toString() {
		return String.format("[%s|%s]", x, y);
	}
}
