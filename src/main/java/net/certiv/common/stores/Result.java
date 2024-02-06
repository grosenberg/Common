package net.certiv.common.stores;

import java.util.Objects;

/**
 * A container object for potentially null objects. Includes a {@code Throwable} error
 * field. If a contained value is present ({@code non-null}) without an error being
 * present, {@code valid()} returns {@code true}.
 * <p>
 * Primary API:
 * <ul>
 * <li>{@link #valid()}: identifies whether the contained value is valid (error free),
 * even if {@code null}
 * <li>{@link #err()}: identifies whether an error is being reported
 * <li>{@link #get()}: the returned value; may be {@code null} if {@link #valid()}; always
 * {@code null} when reporting an error
 * <li>{@link #getErr()}: the returned error; otherwise {@code null}
 * </ul>
 *
 * @param <T> the type of contained value
 */
public class Result<T> {

	/** Valid {@code true} result. */
	public static final Result<Boolean> OK = new Result<>(true, true, null);
	/** Valid {@code false} result. */
	public static final Result<Boolean> FAIL = new Result<>(false, true, null);

	/** The result contained value. */
	private final T value;
	/** The result validity. */
	private final boolean valid;
	/** The result error. */
	private final Throwable err;

	/**
	 * Typed, valid {@code null}-valued constructed result.
	 *
	 * @param <T> the result type
	 * @return the constructed result
	 */
	public static <T> Result<T> nil() {
		return new Result<>((T) null, true, null);
	}

	/**
	 * Construct a result containing the given result value. The result is valid if the
	 * contained value is {@code non-null}.
	 *
	 * @param <T>   the result type
	 * @param value the result value
	 * @return the constructed result
	 */
	public static <T> Result<T> of(T value) {
		return new Result<>(value, value != null, null);
	}

	/**
	 * Construct a result containing the given result value having the given validity.
	 *
	 * @param <T>   the result type
	 * @param value the result value
	 * @param valid the result validity
	 * @return the constructed result
	 */
	public static <T> Result<T> of(T value, boolean valid) {
		return new Result<>(value, valid, null);
	}

	/**
	 * Typed, valid {@code null}-valued constructed result containing the given error.
	 *
	 * @param <T> the result type
	 * @param err a Throwable error
	 * @return the constructed result
	 */
	public static <T> Result<T> of(Throwable err) {
		return new Result<>(null, false, err);
	}

	// --------------------------------

	private Result(T value, boolean valid, Throwable err) {
		this.value = value;
		this.valid = valid;
		this.err = err;
	}

	/**
	 * Returns {@code true} if the contained value represents a valid, potentially
	 * {@code null}, value.
	 */
	public boolean valid() {
		return valid;
	}

	/**
	 * Returns {@code true} if the contained value represents a {@code non-null} value.
	 */
	public boolean validNonNull() {
		return valid && value != null;
	}

	/**
	 * Returns {@code true} if the contained value represents a valid, {@code null} value.
	 */
	public boolean validNull() {
		return valid && value == null;
	}

	/**
	 * Returns {@code true} if the error is {@code non-null} or the contained value is not
	 * valid.
	 */
	public boolean err() {
		return err != null || !valid;
	}

	/** Returns the contained value. */
	public T get() {
		return value;
	}

	public Throwable getErr() {
		return err;
	}

	/** Returns the error message, if any. */
	public String getErrMsg() {
		return err != null ? err.getMessage() : "<No error>";
	}

	/** Throws the contained {@link Throwable}. */
	public void rethrow() throws Throwable {
		if (err()) throw err;
	}

	@Override
	public int hashCode() {
		return Objects.hash(err, value, valid);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) return true;
		if (!(obj instanceof Result)) return false;
		Result<?> other = (Result<?>) obj;
		return Objects.equals(err, other.err) && Objects.equals(value, other.value) && valid == other.valid;
	}

	@Override
	public String toString() {
		if (err()) return err.getMessage();
		if (validNull()) return "<null>";
		return value.toString();
	}
}
