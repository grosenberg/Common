package net.certiv.common.stores.range;

import java.util.function.Predicate;

public interface IRange extends Comparable<IRange> {

	/**
	 * @return minimum range value, inclusive
	 */
	int min();

	/**
	 * @return maximum range value, inclusive
	 */
	int max();

	/**
	 * Returns a new range with the existing minimum replaced by the given value. The
	 * range is guaranteed valid: the maximum is automatically revised to be not less than
	 * the given value.
	 *
	 * @param value revised minimum
	 * @return range with minimum, and potentially maximum, revised
	 */
	IRange reviseMin(int value);

	/**
	 * Returns a new range with the existing maximum replaced by the given value. The
	 * range is guaranteed valid: the minimum is automatically revised to be not greater
	 * than the given value.
	 *
	 * @param value revised maximum
	 * @return range with maximum, and potentially minimum, revised
	 */
	IRange reviseMax(int value);

	/**
	 * Returns a new range with the existing range minimum extended by the given value.
	 * May result in an invalid range if the new range minimum is greater than the
	 * existing range maximum.
	 *
	 * @param value value to add to the existing range minimum
	 * @return range with maximum extended
	 */
	IRange extendMin(int value);

	/**
	 * Returns a new range with the existing range maximum extended by the given value.
	 * May result in an invalid range if the new range maximum is less than the existing
	 * range minimum.
	 *
	 * @param value value to add to the existing range maximum
	 * @return range with maximum extended
	 */
	IRange extendMax(int value);

	/**
	 * Returns the span of this range; defined as the count of positions.
	 *
	 * <pre>{@code
	 *  max  min  span
	 *  ---  ---  ----
	 *   10   02   09
	 *   10  -02   13
	 *  -09  -12   04
	 * }</pre>
	 *
	 * @return range span
	 */
	long span();

	/**
	 * Returns whether this range is adjacent the given range. An adjacent range is
	 * non-overlapping and contiguous.
	 *
	 * @param other another range
	 * @return {@code true} if the given range is adjacent
	 */
	boolean adjacent(IRange other);

	/**
	 * Returns the larger of this range and the given range, based on span.
	 *
	 * @param other another range
	 * @return larger range
	 */
	IRange larger(IRange other);

	/**
	 * Returns the smaller of this range and the given range, based on span.
	 *
	 * @param other another range
	 * @return smaller range
	 */
	IRange smaller(IRange other);

	/**
	 * Returns whether this range contains the given range. A range contains another if
	 * the range is coextensive with or includes the other.
	 *
	 * @param other another range
	 * @return {@code true} if the given range is contained
	 */
	boolean contains(IRange other);

	/**
	 * Returns whether this range includes the given value.
	 *
	 * @param value value to check
	 * @return {@code true} if the given value is within this range, inclusive
	 */
	boolean contains(int value);

	/**
	 * Returns whether this range is fully within the given range.
	 *
	 * @param other another range
	 * @return {@code true} if this range is fully within the given range
	 */
	boolean within(IRange other);

	/**
	 * Returns whether this range is within the given range, conditionally including the
	 * endpoints of the given range.
	 *
	 * @param other   another range
	 * @param include {@code true} to accept equal range endpoints as being within
	 * @return {@code true} if within
	 */
	boolean within(IRange other, boolean include);

	/**
	 * Returns whether the this range matches the given range.
	 *
	 * @param other another range
	 * @return {@code true} if the ranges match
	 */
	boolean matches(IRange other);

	/**
	 * Checks whether this range intersets the given range. Two ranges intersect if they
	 * contain at least one value in common.
	 *
	 * @param other another range
	 * @return {@code true} if this range intersets the given range
	 */
	boolean intersects(IRange other);

	/**
	 * Checks whether this range intersets the given range subject to the given predicate
	 * filter. Two ranges intersect if they contain at least one value in common. A
	 * {@code null} filter predicate is equivalent to a 'match all' criteria.
	 *
	 * @param other  another range
	 * @param filter predicate defining an accept criteria; may be {@code null}
	 * @return {@code true} if this range intersets the given range
	 */
	boolean intersects(IRange other, Predicate<? super IRange> filter);

	/**
	 * Return a range repesenting the intersection of this range and the given range.
	 * Returns {@code null} if there is no intersection.
	 *
	 * @param other another range
	 * @return range representing the intersection of this range and the given range, or
	 *         {@code null} if there is no intersection
	 */
	IRange intersection(IRange other);

	/**
	 * Return a range repesenting the union of this range and the given range. Returns
	 * {@code null} if there is no intersection.
	 *
	 * @param other another range
	 * @return range representing the union of this range and the given range, or
	 *         {@code null} if there is no intersection
	 */
	IRange union(IRange other);

	/**
	 * Returns whether this range is overlaps only the start of the given range.
	 *
	 * @param other another range
	 * @return {@code true} if this range overlaps only the start of the given range
	 */
	boolean overlapsMin(IRange other);

	/**
	 * Returns whether this range is overlaps only the end of the given range.
	 *
	 * @param other another range
	 * @return {@code true} if this range overlaps only the end of the given range
	 */
	boolean overlapsMax(IRange other);

	/**
	 * Checks whether this range is entirely before the given range.
	 *
	 * @param other another range
	 * @return {@code true} if this range is before the given range
	 */
	boolean before(IRange other);

	/**
	 * Checks whether this range is entirely before the given value.
	 *
	 * @param value range value to check
	 * @return {@code true} if this range is before the given value
	 */
	boolean before(int value);

	/**
	 * Checks whether this range is entirely after the given range.
	 *
	 * @param other another range
	 * @return {@code true} if this range is after the given range
	 */
	boolean after(IRange other);

	/**
	 * Checks whether this range is entirely after the given value.
	 *
	 * @param value range value to check
	 * @return {@code true} if this range is after the given value
	 */
	boolean after(int value);

	/**
	 * Compares this range with the given range for order.
	 * <p>
	 * If {@code x.compareTo(y) == 0}, then {@code x.equals(y)} is {@code true}.
	 *
	 * <pre>{@code
	 *  3: this range is entirely after the given range
	 *  2: this range overlaps the given range maximum, but not minimum
	 *  1: this range is within the given range
	 *  0: this range is equal to the given range
	 * -1: this range is contains the given range
	 * -2: this range overlaps the given range minimum, but not maximum
	 * -3: this range is entirely before this range
	 *
	 * other:                    |------|
	 *  3: after:                          |----|
	 *  2: overlapsMax:               |-----|
	 *  1: within:                |--|
	 *  0: equals:               |------|
	 * -1: contains:           |---------|
	 * -2: overlapsMin:      |----|
	 * -3: before:       |----|
	 * }</pre>
	 *
	 * @param other another range
	 * @return integer, as defineed above
	 * @throws IllegalArgumentException if the given range is {@code null}
	 */
	int relativeTo(IRange other);

	/**
	 * Returns an integer in the range [-1..1] identifying the position of this range
	 * relative to the given range. Compares the starting (min) value of this range with
	 * that of the given range to define an ascending order. Where the starting values are
	 * the same, compares the ending (max) values to define an inclusively descending
	 * order.
	 *
	 * <pre>{@code
	 * -1: this before other
	 *  0: this same as other
	 *  1: this after other
	 * }</pre>
	 *
	 * @param other another range
	 * @return integer, as defined above
	 * @throws IllegalArgumentException if the given range is {@code null}
	 */
	@Override
	int compareTo(IRange other);
}
