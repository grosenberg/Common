package net.certiv.common.grid;

import java.util.Objects;

/**
 * Container for a generic value.
 * <p>
 * Extend this class to specialize dependent on the value type.
 *
 * @param <T> API value type
 */
public abstract class Cell<T> {

	/** Cell content value. */
	private T value;

	/**
	 * Constructs a new Cell with the given value.
	 *
	 * @param value cell value
	 */
	public Cell(T value) {
		this.value = value;
	}

	/**
	 * Construct a new NIL instance.
	 *
	 * @return new NIL instance
	 */
	protected abstract Cell<T> nil();

	/**
	 * Construct a new instance containing the given value.
	 *
	 * @param value contained value
	 * @return new {@link Cell} instance
	 */
	protected abstract Cell<T> make(T value);

	/**
	 * Returns the contained value.
	 *
	 * @return cell value
	 */
	public T value() {
		return value;
	}

	/**
	 * Sets the contained value.
	 *
	 * @param value new cell value
	 * @return the prior value
	 */
	public T setValue(T value) {
		T prior = this.value;
		this.value = value;
		return prior;
	}

	/** Returns the effective internal width of this cell in characters. */
	public abstract int width();

	/** Returns the effective height of the contained value in lines. */
	public abstract int height();

	@Override
	public int hashCode() {
		return Objects.hash(value);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) return true;
		if (obj == null) return false;
		if (getClass() != obj.getClass()) return false;
		Cell<?> other = (Cell<?>) obj;
		return Objects.equals(value, other.value);
	}

	@Override
	public String toString() {
		return value.toString();
	}
}
