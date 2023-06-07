package net.certiv.common.stores;

import java.util.Objects;

/**
 * A container object for potentially null objects. Includes a {@code Throwable} error
 * field. If a value is present ({@code non-null}) without an error being present,
 * {@code valid()} returns {@code true}.
 *
 * @apiNote This is a specialized alternative to {@code Optional}.
 * @param <T> the type of value
 */
public class Result<T> {

	public static final Result<Boolean> OK = new Result<>(true, null);
	public static final Result<Boolean> FAIL = new Result<>(false, null);

	/** The result value. */
	public final T value;
	/** The result error. */
	public final Throwable err;

	public static <T> Result<T> of(T result) {
		return new Result<>(result, null);
	}

	public static <T> Result<T> of(Throwable err) {
		return new Result<>(null, err);
	}

	// --------------------------------

	private Result(T value, Throwable err) {
		this.value = value;
		this.err = err;
	}

	/**
	 * Returns {@code true} if the value is {@code non-null} and the error is
	 * {@code null}.
	 */
	public boolean valid() {
		return value != null && err == null;
	}

	/**
	 * Returns {@code true} if the error is {@code non-null}.
	 */
	public boolean err() {
		return err != null;
	}

	/**
	 * Returns {@code true} if the value is {@code non-null}.
	 */
	public boolean present() {
		return value != null;
	}

	/**
	 * Returns {@code true} if the value is {@code null}.
	 */
	public boolean isNull() {
		return value == null;
	}

	@Override
	public int hashCode() {
		return Objects.hash(err, value);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) return true;
		if (!(obj instanceof Result)) return false;
		Result<?> other = (Result<?>) obj;
		return Objects.equals(err, other.err) && Objects.equals(value, other.value);
	}

	@Override
	public String toString() {
		if (err == null) return String.valueOf(value);
		return String.format("%s [%s]", String.valueOf(value), err.getMessage());
	}
}
