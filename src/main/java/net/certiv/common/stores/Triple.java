package net.certiv.common.stores;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

public class Triple<A, B, C> {

	private final List<Enum<?>> enums = new ArrayList<>();

	private A a;
	private B b;
	private C c;

	public static <A, B, C> Triple<A, B, C> of(Class<? extends Enum<?>> enums) {
		return new Triple<>(Arrays.asList(enums.getEnumConstants()));
	}

	protected Triple(List<Enum<?>> enums, A a, B b, C c) {
		this(enums);
		this.a = a;
		this.b = b;
		this.c = c;
	}

	protected Triple(List<Enum<?>> enums) {
		this.enums.addAll(enums);
	}

	public void set(A a, B b, C c) {
		this.a = a;
		this.b = b;
		this.c = c;
	}

	public A a() {
		return a;
	}

	public void a(A a) {
		this.a = a;
	}

	public B b() {
		return b;
	}

	public void b(B b) {
		this.b = b;
	}

	public C c() {
		return c;
	}

	public void c(C c) {
		this.c = c;
	}

	/**
	 * Returns the value corresponding to the given value index.
	 *
	 * @param <V>
	 * @param idx the value index
	 * @return the indexed value
	 * @throws IllegalArgumentException if the index is not recognized
	 * @throws ClassCastException if the receiver type is incorrect
	 */
	@SuppressWarnings("unchecked")
	public <V> V get(int idx) {
		switch (idx) {
			case 1:
				return (V) a;
			case 2:
				return (V) b;
			case 3:
				return (V) c;
			default:
				throw new IllegalArgumentException(String.format("Invald index '%s'", idx));
		}
	}

	/**
	 * Returns the value corresponding to the given {@code name}.
	 *
	 * @param <V>
	 * @param name
	 * @return the named value
	 * @throws IllegalArgumentException if the name is not recognized
	 * @throws ClassCastException if the receiver type is incorrect
	 */
	@SuppressWarnings("unchecked")
	public <V> V get(Enum<?> name) {
		switch (enums.indexOf(name)) {
			case 1:
				return (V) a;
			case 2:
				return (V) b;
			case 3:
				return (V) c;
			default:
				throw new IllegalArgumentException(String.format("Unknown name '%s'", name));
		}
	}

	@Override
	public int hashCode() {
		return Objects.hash(a, b, c);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) return true;
		if (!(obj instanceof Triple)) return false;
		Triple<?, ?, ?> other = (Triple<?, ?, ?>) obj;
		return Objects.equals(a, other.a) && Objects.equals(b, other.b) && Objects.equals(c, other.c);
	}
}
