package net.certiv.common.stores.range;

import java.util.LinkedList;
import java.util.List;
import java.util.Objects;

import net.certiv.common.ex.IllegalArgsEx;
import net.certiv.common.util.MsgBuilder;
import net.certiv.common.util.Strings;
import net.certiv.common.util.bits.LongBitSet;

public class PositionBits {

	private final PositionRange ref;

	private LongBitSet bits;
	private long cardinality;

	public PositionBits(PositionRange ref) {
		if (ref == null) throw IllegalArgsEx.of("Position reference range cannot be 'null'.");
		this.ref = ref;
	}

	public void accrue(IRange other) {
		if (other != null && ref.intersects(other)) {
			mkBits();
			IRange xs = ref.intersection(other);
			long beg = xs.min() + offset();
			bits.set(beg, beg + xs.span());
			cardinality = -1; // dirty
		}
	}

	/**
	 * Lazily instantiate a bit set.
	 */
	protected void mkBits() {
		if (bits == null) {
			bits = new LongBitSet(ref.span());
			cardinality = -1; // dirty
		}
	}

	/**
	 * Returns number of intersected positions.
	 */
	public long cardinality() {
		if (cardinality < 0) {
			cardinality = bits.cardinality();
		}
		return cardinality;
	}

	/**
	 * Returns whether the accumulated intersections have fully covered the reference
	 * range.
	 *
	 * @return {@code true} if the entire reference range has been covered
	 */
	public boolean covered() {
		if (cardinality < 0) {
			cardinality = bits.cardinality();
		}
		return span() == cardinality;
	}

	/**
	 * Return the intersected positions.
	 *
	 * @return intersected position list
	 */
	public List<Long> positions() {
		List<Long> pos = new LinkedList<>();
		if (bits != null) {
			long off = offset();
			long idx = bits.nextSetBit(0);
			while (idx > -1) {
				pos.add(idx - off);
				idx = bits.nextSetBit(idx + 1);
			}
		}

		return pos;
	}

	/**
	 * Determines an offset used to shift the reference PositionRange to fit within a bit
	 * set:
	 *
	 * <pre>{@code
	 * -n..0..n : range
	 * +0.....m ; bit set
	 * }</pre>
	 *
	 * where {@code n} is {@link Integer#MAX_VALUE} and {@code m} is
	 * {@link Long#MAX_VALUE}.
	 *
	 * <pre>{@code
	 *   ref    other  ->   ref    other
	 * +00:+10 +03:+05 -> +00:+10 +03:+05; off = +00
	 * +10:+20 +13:+15 -> +00:+10 +03:+05; off = -10
	 * -10:+10 +03:+05 -> +00:+20 +13:+15; off = +10
	 * -10:+10 -05:-03 -> +00:+20 +05:+07; off = +10
	 * }</pre>
	 */
	private int offset() {
		return -ref.min();
	}

	/**
	 * Clears all of the position bits, representing a fully uncovered state.
	 */
	public void clear() {
		if (bits != null) bits.clear();
	}

	/** Returns the total number of positions represented. */
	public long span() {
		return ref.span();
	}

	@Override
	public int hashCode() {
		return Objects.hash(bits, ref);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) return true;
		if (!(obj instanceof PositionBits)) return false;
		PositionBits other = (PositionBits) obj;
		return Objects.equals(bits, other.bits) && Objects.equals(ref, other.ref);
	}

	@Override
	public String toString() {
		MsgBuilder mb = new MsgBuilder();
		mb.append("[%s] {%s}", ref, Strings.join(positions()));
		return mb.toString();
	}
}
