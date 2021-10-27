package net.certiv.common.stores;

import java.util.Objects;

public class Pair<L, R> {

	public final L left;
	public final R right;

	public static <L, R> Pair<L, R> of(final L open, final R close) {
		return new Pair<>(open, close);
	}

	protected Pair(final L left, final R right) {
		this.left = left;
		this.right = right;
	}

	public L getLeft() {
		return left;
	}

	public R getRight() {
		return right;
	}

	public L key() {
		return left;
	}

	public R value() {
		return right;
	}

	@Override
	public int hashCode() {
		return Objects.hash(left, right);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) return true;
		if (obj == null) return false;
		if (!(obj instanceof Pair)) return false;
		Pair<?, ?> other = (Pair<?, ?>) obj;
		return Objects.equals(left, other.left) && Objects.equals(right, other.right);
	}

	@Override
	public String toString() {
		return String.format("[%s, %s]", left, right);
	}

	public String toString(final String fmt) {
		return String.format(fmt, left, right);
	}
}
