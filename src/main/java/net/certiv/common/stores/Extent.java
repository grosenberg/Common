package net.certiv.common.stores;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class Extent<V extends Number> implements IExtent<V> {

	public static final String TYPE = "type";
	public static final String DEFAULT = "default";

	public final V beg;
	public final V end;

	public final Map<String, Object> attributes = new HashMap<>();

	/**
	 * Create a new extent.
	 *
	 * @param beg the offset of the first element within the extent
	 * @param end the offset of the last element within the extent
	 */
	public Extent(V beg, V end) {
		this(beg, end, DEFAULT);
	}

	/**
	 * Create a new extent.
	 *
	 * @param beg the offset of the first element within the extent
	 * @param end the offset of the last element within the extent
	 */
	public Extent(V beg, V end, Object type) {
		this.beg = beg;
		this.end = end;
		attributes.put(TYPE, type);
	}

	@SuppressWarnings("unchecked")
	public <T> T getType() {
		return (T) attributes.get(TYPE);
	}

	@Override
	public boolean contains(V v) {
		if (v instanceof Float) {
			return beg.floatValue() <= v.floatValue() && v.floatValue() <= end.floatValue();
		} else if (v instanceof Double) {
			return beg.doubleValue() <= v.doubleValue() && v.doubleValue() <= end.doubleValue();
		}
		return beg.longValue() <= v.longValue() && v.longValue() <= end.longValue();
	}

	@Override
	public int hashCode() {
		return Objects.hash(beg, end);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) return true;
		if (obj == null) return false;
		if (!(obj instanceof Extent)) return false;
		Extent<?> other = (Extent<?>) obj;
		return Objects.equals(beg, other.beg) && Objects.equals(end, other.end);
	}

	@Override
	public String toString() {
		return String.format("%s .. %s %s", beg, end, attributes.toString());
	}
}
