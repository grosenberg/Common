package net.certiv.common.stores.range;

import java.util.Objects;
import java.util.function.Predicate;

import net.certiv.common.ex.BoundsEx;
import net.certiv.common.ex.IllegalArgsEx;

/**
 * An immutable range describing a span defined by minimum and maximum integer values,
 * inclusive, with a span of upto {@link Integer#MAX_VALUE}.
 */
public class Range implements IRange {

	protected static final int LIMIT = Integer.MAX_VALUE;

	protected static final BoundsEx ERR_INVALID = BoundsEx.of("Range invalid: [%d:%d].");
	protected static final BoundsEx ERR_SPAN = BoundsEx.of("Range [%d:%d] exceeds span limit.");

	/** Minimum range value, inclusive. */
	public final int min;
	/** Maximum range value, inclusive. */
	public final int max;

	/**
	 * Returns range where the given value is set as both the range minimum and maximum.
	 *
	 * @param value range minimum and maximum value
	 */
	public Range(int value) {
		this(value, value);
	}

	/**
	 * Constructs a new {@link Range} with values between {@link Long#MIN_VALUE} and
	 * {@link Long#MAX_VALUE}.
	 *
	 * @param min minimum range value, inclusive
	 * @param max maximum range value, inclusive
	 */
	public Range(int min, int max) {
		chkRange(min, max);
		this.min = min;
		this.max = max;
	}

	/**
	 * Validate range parameters.
	 *
	 * @param min minimum range value, inclusive
	 * @param max maximum range value, inclusive
	 * @throws IllegalArgumentException if the range parameters are not permitted
	 */
	protected void chkRange(int min, int max) {
		if (min > max) {
			throw ERR_INVALID.formatted(min, max);
		}
		if ((long) max + (long) min >= LIMIT) throw ERR_SPAN.formatted(min, max);
	}

	/**
	 * Override to create a subclass specific instance.
	 *
	 * @param ori original instance
	 * @param min revised minimum value, inclusive
	 * @param max revised maximum value, inclusive
	 * @return new subclass specifice instance
	 */
	@SuppressWarnings("unchecked")
	protected <T extends IRange> T inst(T ori, int min, int max) {
		return (T) new Range(min, max);
	}

	@Override
	public int min() {
		return min;
	}

	@Override
	public int max() {
		return max;
	}

	@Override
	public Range reviseMin(int value) {
		return inst(this, value, Math.max(value, max));
	}

	@Override
	public Range reviseMax(int value) {
		return inst(this, Math.min(min, value), value);
	}

	@Override
	public Range extendMin(int value) {
		return inst(this, min + value, max);
	}

	@Override
	public Range extendMax(int value) {
		return inst(this, min, max + value);
	}

	@Override
	public long span() {
		return 1 + max - min;
	}

	@Override
	public boolean adjacent(IRange other) {
		if (other == null) return false;
		return other.min() == max + 1 || other.max() + 1 == min;
	}

	@Override
	public IRange larger(IRange other) {
		if (other == null) return this;
		return span() > other.span() ? this : other;
	}

	@Override
	public IRange smaller(IRange other) {
		if (other == null) return this;
		return span() < other.span() ? this : other;
	}

	@Override
	public boolean contains(IRange other) {
		if (other == null) return false;
		return min <= other.min() && max >= other.max();
	}

	@Override
	public boolean contains(int value) {
		return min <= value && max >= value;
	}

	@Override
	public boolean within(IRange other) {
		return within(other, false);
	}

	@Override
	public boolean within(IRange other, boolean include) {
		boolean a = include ? min >= other.min() : min > other.min();
		boolean b = include ? max <= other.max() : max < other.max();
		return a && b;
	}

	@Override
	public boolean matches(IRange other) {
		return min == other.min() && max == other.max();
	}

	@Override
	public boolean intersects(IRange other) {
		return intersects(other, null);
	}

	@Override
	public boolean intersects(IRange other, Predicate<? super IRange> filter) {
		if (other == null) return false;
		if (filter != null && !filter.test(other)) return false;
		return min <= other.max() && max >= other.min();
	}

	@Override
	public Range intersection(IRange other) {
		if (!intersects(other)) return null;
		if (equals(other)) return this;
		return inst(this, Math.max(min, other.min()), Math.min(max, other.max()));
	}

	@Override
	public Range union(IRange other) {
		if (!intersects(other)) return null;
		if (equals(other)) return this;
		return inst(this, Math.min(min, other.min()), Math.max(max, other.max()));
	}

	public boolean overlaps(Range range) {
		if (max > range.min && max < range.max) return true;
		if (min > range.min && min < range.max) return true;
		return false;
	}

	@Override
	public boolean overlapsMin(IRange other) {
		return min <= other.min() && max >= other.min() && max < other.max();
	}

	@Override
	public boolean overlapsMax(IRange other) {
		return min > other.min() && min <= other.max() && max > other.max();
	}

	@Override
	public boolean before(IRange other) {
		if (other == null) return false;
		return max < other.min();
	}

	@Override
	public boolean before(int value) {
		return max < value;
	}

	@Override
	public boolean after(IRange other) {
		if (other == null) return false;
		return min > other.max();
	}

	@Override
	public boolean after(int value) {
		return min > value;
	}

	@Override
	public int relativeTo(IRange other) {
		if (other == null) throw IllegalArgsEx.of("RelativeTo range argument cannot be 'null'.");

		if (after(other)) return 3;
		if (overlapsMax(other)) return 2;
		if (within(other)) return 1;
		if (equals(other)) return 0;
		if (contains(other)) return -1;
		if (overlapsMin(other)) return -2;
		if (before(other)) return -3;
		throw IllegalArgsEx.of("'compareTo': should never occur '%s' -> '%s'.", this, other);
	}

	@Override
	public int compareTo(IRange other) {
		if (other == null) throw IllegalArgsEx.of("CompareTo range argument cannot be 'null'.");

		if (min < other.min()) return -1;
		if (min > other.min()) return 1;
		if (max > other.max()) return -1;
		if (max < other.max()) return 1;
		return 0;
	}

	@Override
	public int hashCode() {
		return Objects.hash(max, min);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) return true;
		if (!(obj instanceof Range)) return false;
		Range r = (Range) obj;
		return max == r.max && min == r.min;
	}

	@Override
	public String toString() {
		return min == max //
				? String.format("%d", min) //
				: String.format("%d:%d", min, max);
	}
}
