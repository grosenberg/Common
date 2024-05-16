package net.certiv.common.stores;

import java.util.Objects;

/**
 * A container having a return value field and a {@code Throwable} error field.
 * <ul>
 * <li>{@link #valid()}: identifies whether the contained value is real, even if
 * {@code null}
 * <li>{@link #err()}: identifies whether an error is being reported
 * <li>{@link #get()}: the returned value; may be {@code null} if {@link #valid()}
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
	 * Typed {@code null}-valued result. The result is always {@code valid}.
	 *
	 * @param <T> result type
	 * @return constructed result
	 */
	public static <T> Result<T> nil() {
		return new Result<>((T) null, true, null);
	}

	/**
	 * Typed {@code null}-valued, invalid result.
	 *
	 * @param <T> result type
	 * @return constructed result
	 */
	public static <T> Result<T> nack() {
		return new Result<>((T) null, false, null);
	}

	/**
	 * Typed result containing the given value that is {@code valid} if the value is
	 * {@code non-null}.
	 *
	 * @param <T>   result type
	 * @param value result value
	 * @return constructed result
	 */
	public static <T> Result<T> of(T value) {
		return new Result<>(value, value != null, null);
	}

	/**
	 * Typed result containing the given value that is {@code valid} dependent on the
	 * given {@code validity}.
	 *
	 * @param <T>      result type
	 * @param value    result value
	 * @param validity value validity
	 * @return constructed result
	 */
	public static <T> Result<T> of(T value, boolean validity) {
		return new Result<>(value, validity, null);
	}

	/**
	 * Typed result containing the given value and error. The result is {@code valid} if
	 * the value is {@code non-null}.
	 *
	 * @param <T>   result type
	 * @param value result value
	 * @param err   captured throwable; may be {@code null}
	 * @return constructed result
	 */
	public static <T> Result<T> of(T value, Throwable err) {
		return new Result<>(value, value != null, err);
	}

	/**
	 * Typed result containing the given error. The result is always {@code not-valid}.
	 *
	 * @param <T> result type
	 * @param err captured throwable
	 * @return constructed result
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

	public boolean ok() {
		return valid && err == null && value.equals(Boolean.TRUE);
	}

	/**
	 * Returns {@code true} if the contained value represents a valid, potentially
	 * {@code null}, value.
	 */
	public boolean valid() {
		return valid;
	}

	/**
	 * Returns {@code true} if the contained value represents a {@code valid},
	 * {@code non-null} value.
	 */
	public boolean validNonNull() {
		return valid && value != null;
	}

	/**
	 * Returns {@code true} if the contained value represents a {@code valid} {@code null}
	 * value.
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
