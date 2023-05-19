package net.certiv.common.stores.context;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;

import net.certiv.common.check.Assert;
import net.certiv.common.stores.context.ex.TypeException;
import net.certiv.common.stores.context.ex.ValidationException;
import net.certiv.common.util.Strings;

public class Key<T> {

	private static final String ERR_KEY = "Key '%s' exists with different type.";
	private static final String ERR_NAME = "Key name must not be empty.";

	private static final Map<String, Key<?>> Registry = new LinkedHashMap<>();

	public final String name;

	/**
	 * Returns the key for the given name. Throws unchecked, runtime exceptions if the
	 * given name is {@code empty}, or if the named key pre-exists with a different key
	 * type.
	 *
	 * @param <T>  the intended key type
	 * @param name the name of the key to find
	 * @return the corresponding key
	 * @throws AssertionException if the key is empty
	 * @throws TypeException      if the key does not match the intended key type
	 */
	public static <T> Key<T> of(String name) {
		Assert.notEmpty(name, ERR_NAME);

		try {
			@SuppressWarnings("unchecked")
			Key<T> key = (Key<T>) Registry.get(name);
			if (key == null) {
				key = new Key<>(name);
				Registry.put(name, key);
			}
			return key;

		} catch (Exception e) {
			throw new TypeException(e, ERR_KEY, name);
		}
	}

	/** Returns {@code true} if the given key name is known. */
	public static boolean defined(String name) {
		if (Strings.blank(name)) return false;
		return Registry.get(name) != null;
	}

	/**
	 * Returns the existing key for the given name, or {@code null} if the key does not
	 * exist.
	 * <p>
	 * Throws a checked {@code TypeException} if the named key pre-exists with a different
	 * key type.
	 *
	 * @param <T>  the intended key type
	 * @param name the name of the key to find
	 * @return the key, if found, or {@code null}
	 * @throws TypeException if the key does not match the intended key type
	 */
	public static <T> Key<T> find(String name) throws TypeException {
		return find(name, false);
	}

	/**
	 * Returns the existing key for the given name. If the key does not exist and
	 * {@code force}, the key is created. Otherwise, returns {@code null} if the key does
	 * not exist.
	 * <p>
	 * Throws a checked {@code TypeException} if the named key pre-exists with a different
	 * key type.
	 *
	 * @param <T>   the intended key type
	 * @param name  the name of the key to find
	 * @param force if {@code true}, create a non-existant key
	 * @return the key, if found, or {@code null}
	 * @throws TypeException if the key does not match the intended key type
	 */
	public static <T> Key<T> find(String name, boolean force) throws TypeException {
		if (Strings.blank(name)) throw new TypeException(ERR_NAME);

		try {
			@SuppressWarnings("unchecked")
			Key<T> key = (Key<T>) Registry.get(name);
			if (key == null && force) {
				key = new Key<>(name);
				Registry.put(name, key);
			}
			return key;

		} catch (Exception e) {
			throw new TypeException(e, ERR_KEY, name);
		}
	}

	// --------------------------------

	private Key(String name) {
		this.name = name;
	}

	/**
	 * Returns {@code true} if the given object can be validly cast to the {@code Key}
	 * type. If the object is a {@code Value}, tests the underlying value.
	 *
	 * @param value to cast
	 * @return {@code true} if a the cast would succeed
	 */
	public boolean valid(Object value) {
		Object val = value instanceof Value ? ((Value<?>) value).beg : value;
		try {
			@SuppressWarnings({ "unchecked", "unused" })
			T tmp = (T) val;
			return true;

		} catch (Exception e) {
			return false;
		}
	}

	/**
	 * Returns the given object cast to the {@code Key} type. If the object is a
	 * {@code Value}, casts the underlying {@code beg} field of the value.
	 * <p>
	 * Throws a {@code ValidationException} if the object is not assignable to the key
	 * type. Use {@code #valid} to test for assignability.
	 *
	 * @param value to cast
	 * @return the cast value
	 * @throws ValidationException on cast failure
	 */
	@SuppressWarnings("unchecked")
	public T cast(Object value) throws ValidationException {
		if (!valid(value)) throw new ValidationException(name);
		return value instanceof Value ? (T) ((Value<?>) value).beg : (T) value;
	}

	/**
	 * Returns the given object cast to the {@code Key} type. If the object is a
	 * {@code Value}, casts the underlying {@code beg} field of the value.
	 * <p>
	 * Returns {@code null} if the object is not assignable to the key type.
	 *
	 * @param value to cast
	 * @return the cast value
	 */
	public T ucast(Object value) {
		try {
			return cast(value);
		} catch (ValidationException e) {
			return null;
		}
	}

	@Override
	public int hashCode() {
		return Objects.hash(name);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) return true;
		if (!(obj instanceof Key)) return false;
		Key<?> other = (Key<?>) obj;
		return Objects.equals(name, other.name);
	}

	@Override
	public String toString() {
		return name;
	}
}
