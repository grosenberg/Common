package net.certiv.common.stores.context;

import java.beans.ConstructorProperties;
import java.util.Collection;
import java.util.Objects;

import net.certiv.common.util.Strings;

/**
 * Generalized, nominally immutable data holder with optional unit qualifier. The
 * underlying type can be an instance object or a collection object (list or set) as
 * single (1-element) and range (2-element) {@code Value}s.
 * <p>
 * Unit-less if the unit is {@code blank}.
 *
 * @param <V> the data type
 * @see {@code Numeric}: provides specialized support for numeric types: {@code Hex},
 *      {@code Double}, {@code Long}, and {@code Integer}.
 */
public class Value<V> {

	protected static final String ERR_VAL = "'%s' is not compatible with Value: %s";

	protected final V beg;
	protected final V end;
	protected final String unit;
	protected final boolean range;

	public static <V> Value<V> of(V value) {
		return new Value<>(value, null, null, false);
	}

	public static <V> Value<V> of(V value, String unit) {
		return new Value<>(value, null, unit, false);
	}

	/** Range {@code Value} containing 2 elements. */
	public static <V> Value<V> of(V beg, V end) {
		return new Value<>(beg, end, null, true);
	}

	/** Range {@code Value} containing 2 elements. */
	public static <V> Value<V> of(V beg, V end, String unit) {
		return new Value<>(beg, end, unit, true);
	}

	// --------------------------------

	@ConstructorProperties({ "beg", "end", "unit", "range" })
	protected Value(V beg, V end, String unit, boolean range) {
		this.beg = beg;
		this.end = end;
		this.unit = unit != null ? unit : Strings.EMPTY;
		this.range = range;
	}

	/** Returns the underlying value; equivalent to {@code beg}. */
	public V value() {
		return beg;
	}

	public String unit() {
		return unit;
	}

	public V beg() {
		return beg;
	}

	public V end() {
		return end;
	}

	/**
	 * Returns {@code true} if this {@code Value} is
	 * <ol>
	 * <li>a single value that is {@code null}
	 * <li>a range where either value is {@code null}
	 * <li>a collection that is {@code empty}
	 * <li>a {@code CharSequence} that is {@code empty}
	 * </ol>
	 */
	public boolean nil() {
		boolean nil = nil(beg);
		if (range) nil = nil || nil(end);
		return nil;
	}

	private boolean nil(Object val) {
		if (val == null) return true;

		if (val instanceof Collection) {
			Collection<?> c = (Collection<?>) val;
			if (c.isEmpty()) return true;

		} else if (val instanceof CharSequence) {
			CharSequence seq = (CharSequence) val;
			if (seq.length() == 0) return true;
			if (seq.toString().trim().length() == 0) return true;
		}

		return false;
	}

	/** Returns {@code true} if this is a range-type value. */
	public boolean range() {
		return range;
	}

	/** Returns {@code true} if this is a collection-type value. */
	public boolean collection() {
		return beg instanceof Collection;
	}

	@Override
	public int hashCode() {
		return Objects.hash(end, range, unit, beg);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) return true;
		if (!(obj instanceof Value)) return false;
		Value<?> other = (Value<?>) obj;
		return Objects.deepEquals(beg, other.beg) //
				&& Objects.deepEquals(end, other.end) //
				&& range == other.range //
				&& Objects.equals(unit, other.unit);
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append(beg);
		if (range) sb.append(String.format(" .. %s", end));
		if (!unit.isBlank()) sb.append(String.format(" [%s]", unit));
		return sb.toString();
	}
}
