package net.certiv.common.stores;

import java.util.Objects;

/** {@code Table} support class. */
public class TableCell<R, C, V> {

	private final R row;
	private final C col;
	private final V val;

	public TableCell(R row, C col, V val) {
		this.row = row;
		this.col = col;
		this.val = val;
	}

	public R row() {
		return row;
	}

	public C col() {
		return col;
	}

	public V val() {
		return val;
	}

	@Override
	public int hashCode() {
		return Objects.hash(col, row, val);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) return true;
		if (!(obj instanceof TableCell)) return false;
		TableCell<?, ?, ?> other = (TableCell<?, ?, ?>) obj;
		return Objects.equals(col, other.col) && Objects.equals(row, other.row)
				&& Objects.equals(val, other.val);
	}

	@Override
	public String toString() {
		return String.format("(%s, %s)=%s", row, col, val);
	}
}
