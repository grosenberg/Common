package net.certiv.common.stores.range;

import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.regex.Pattern;

import net.certiv.common.ex.IllegalArgsEx;

/**
 * An immutable range describing a span defined by minimum and maximum integer values,
 * inclusive, with a span of upto {@link Integer#MAX_VALUE}.
 */
public class PositionRange extends Range implements Iterable<Integer> {

	protected static final Pattern NUMS = Pattern.compile("\\h*\\:\\h*");

	public static final PositionRange ORIGIN = new PositionRange(0, 0);

	/** Coverage record. */
	private final PositionBits pos;

	/**
	 * Returns the range determined by parsing the given range specification. The
	 * specification is a <i>Colon Separated Value</i> representing zero to two integer
	 * values, nominally {@code MIN:MAX}.
	 * <p>
	 * If the specification is {@code null}, returns a {@link PositionRange}-typed
	 * {@code null}.
	 * <p>
	 * If the specification is {@code blank}, returns the {@code #ORIGIN} range.
	 * <p>
	 * If the specification is does not provide a {@code MAX} value, the maximum value is
	 * set to the minimum.
	 *
	 * @param spec range specification
	 * @return a {@code PositionRange}
	 */
	public static PositionRange of(String spec) {
		if (spec == null) return null;
		if (spec.isBlank()) return ORIGIN;

		String[] parts = NUMS.split(spec, -1);
		if (parts.length > 2) {
			throw IllegalArgsEx.of("Illegal parse argument specification '%s'.", spec);
		}

		int min = parseNum(parts[0], spec);
		int max = min;
		if (parts.length == 2) {
			max = parseNum(parts[1], spec);
		}
		return new PositionRange(min, max);
	}

	private static int parseNum(String subspec, String spec) {
		try {
			return Integer.parseInt(subspec.trim());
		} catch (Exception e) {
			throw IllegalArgsEx.of("Illegal parse argument value '%s' in '%s'.", subspec, spec);
		}
	}

	/**
	 * Returns range where the given value is set as both the range minimum and maximum.
	 *
	 * @param value range minimum and maximum value
	 */
	public PositionRange(int value) {
		this(value, value);
	}

	/**
	 * Constructs a new {@link PositionRange} with values between {@link Long#MIN_VALUE}
	 * and {@link Long#MAX_VALUE}.
	 *
	 * @param min minimum range value, inclusive
	 * @param max maximum range value, inclusive
	 */
	public PositionRange(int min, int max) {
		super(min, max);
		this.pos = new PositionBits(this);
	}

	@Override
	@SuppressWarnings("unchecked")
	protected <T extends IRange> T inst(T ori, int min, int max) {
		return (T) new PositionRange(min, max);
	}

	/**
	 * Accrue, to a bit set record representing this range, the intersection of this range
	 * with the given range. Two ranges intersect if they contain at least one value in
	 * common.
	 *
	 * @param other another range
	 * @return range representing the intersection of this range and the given range, or
	 *         {@code null} if there is no intersection
	 */
	public PositionRange intersection(PositionRange other, boolean accrue) {
		PositionRange xs = (PositionRange) intersection(other);
		if (accrue) pos.accrue(xs);
		return xs;
	}

	// /**
	// * @param begIdx begin index, inclusive (0..n)
	// * @param endIdx end index, exclusive (1..n+1)
	// */
	// protected void setBits(long begIdx, long endIdx) {
	// chkBits();
	// pos.accrue(begIdx, endIdx);
	// cardinality = -1; // dirty
	// }

	// /**
	// * Check and, if necessary, lazily instantiate a bit set.
	// */
	// protected void chkBits() {
	// if (pos == null) {
	// pos = new LongBitSet(span());
	// }
	// }

	/**
	 * Returns whether the accumulated intersections have covered this range.
	 *
	 * @return {@code true} if the entire range has been covered
	 */
	public boolean covered() {
		return pos.covered();
	}

	/**
	 * Returns number of intersection bits.
	 */
	public long cardinality() {
		return pos.cardinality();
	}

	/**
	 * Sets the intersection coverage to a fully uncovered state.
	 */
	public void clear() {
		pos.clear();
	}

	@Override
	public Iterator<Integer> iterator() {
		return new RangeIterator(this);
	}

	private class RangeIterator implements Iterator<Integer> {

		private final PositionRange range;
		private int cursor;
		private boolean hasNext;

		/**
		 * Constructor.
		 *
		 * @param range iteration range
		 */
		private RangeIterator(PositionRange range) {
			this.range = range;
			cursor = range.min;
			hasNext = true;
		}

		@Override
		public boolean hasNext() {
			return hasNext;
		}

		@Override
		public Integer next() {
			if (!hasNext) throw new NoSuchElementException();

			int cur = cursor;
			prepareNext();
			return cur;
		}

		/** Prepares next in the range. */
		private void prepareNext() {
			if (cursor < range.max()) {
				cursor++;
			} else {
				hasNext = false;
			}
		}
	}
}
