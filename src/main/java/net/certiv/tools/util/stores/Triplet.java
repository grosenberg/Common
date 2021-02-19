package net.certiv.tools.util.stores;

import java.util.Objects;

public class Triplet<A, B, C> {

	public final A a;
	public final B b;
	public final C c;

	public static <A, B, C> Triplet<A, B, C> of(A a, B b, C c) {
		return new Triplet<>(a, b, c);
	}

	public Triplet(A a, B b, C c) {
		this.a = a;
		this.b = b;
		this.c = c;
	}

	@Override
	public int hashCode() {
		return Objects.hash(a, b, c);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) return true;
		if (!(obj instanceof Triplet)) return false;
		Triplet<?, ?, ?> other = (Triplet<?, ?, ?>) obj;
		return Objects.equals(a, other.a) && Objects.equals(b, other.b) && Objects.equals(c, other.c);
	}
}
