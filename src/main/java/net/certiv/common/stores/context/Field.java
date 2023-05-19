package net.certiv.common.stores.context;

import java.util.Objects;

import net.certiv.common.check.Assert;

/**
 * Generalized, immutable typed-key/value holder. The underlying type is enforced to both
 * the key and value.
 *
 * @param <V> the field type
 * @see {@code Key}
 * @see {@code Value}
 */
public class Field<V> {

	public final Key<V> key;
	public final Value<V> value;

	public static <V> Field<V> of(Key<V> key) {
		return of(key, Value.of(null));
	}

	public static <V> Field<V> of(Key<V> key, Value<V> value) {
		Assert.notNull(key);
		return new Field<>(key, value);
	}

	// --------------------------------

	protected Field(Key<V> key, Value<V> value) {
		this.key = key;
		this.value = value;
	}

	/** Returns the value of this field. */
	public Value<V> value() {
		return value;
	}

	@Override
	public int hashCode() {
		return Objects.hash(key, value);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) return true;
		if (!(obj instanceof Field)) return false;
		Field<?> other = (Field<?>) obj;
		return Objects.equals(key, other.key) && Objects.equals(value, other.value);
	}

	@Override
	public String toString() {
		return String.format("%s = %s", key, value);
	}
}
