package net.certiv.common.stores;

import java.util.Collection;

/** An instance holder for a value of type {@code T}. */
public class Holder<T> {

	/** The {@code T} value. */
	public T value;

	/** Constructs a new empty {@code Holder} object. */
	public static <T> Holder<T> of() {
		return new Holder<>();
	}

	/**
	 * Constructs a new {@code Holder} object initialized to the given value.
	 *
	 * @param value the initial {@code T} value
	 */
	public static <T> Holder<T> of(T value) {
		return new Holder<>(value);
	}

	/** Constructs a new {@code Holder} object. */
	public Holder() {}

	/**
	 * Constructs a new {@code Holder} object initialized to the given value.
	 *
	 * @param value the initial {@code T} value
	 */
	public Holder(T value) {
		this.value = value;
	}

	public T get() {
		return value;
	}

	public void set(T value) {
		this.value = value;
	}

	/**
	 * Returns {@code true} if the contained {@code value} is {@code null} or, if an array
	 * or standard collection, is empty.
	 *
	 * @return
	 */
	public boolean nullOrEmpty() {
		if (value == null) return true;
		if (value.getClass().isArray()) return ((Object[]) value).length == 0;
		if (value instanceof Collection) return ((Collection<?>) value).isEmpty();
		return false;
	}

	@Override
	public String toString() {
		return value.toString();
	}
}
