package net.certiv.common.graph.id;

import java.util.Objects;

import net.certiv.common.check.Assert;

/**
 * Provides an abstract base class for defining a unique, structured
 * {@code namespace::naming_object} identifier. Extend and admend to type and provide any
 * additional fields appropriate to establishing uniqueness.
 * <p>
 * Namespace must be non-null and should be non-blank; use {@code #DEFAULT} to represent
 * the default namespace.
 * <p>
 * Default implementation uses {@code '::'} as the namespace separator and {@code '.'} as
 * the name element separator.
 *
 * @param <U> element type
 */
public abstract class UId<U extends Comparable<? super U>> implements Comparable<UId<U>>, IUId {

	public final String ns;
	public final U no;

	public UId(String ns, U no) {
		Assert.notNull(ns, no);
		this.ns = ns;
		this.no = no;
	}

	@Override
	public String namespace() {
		return ns;
	}

	@Override
	public boolean isDefaultNamespace() {
		return UIdFactory.DEFAULT.equals(ns);
	}

	@Override
	public boolean sameNamespace(UId<?> id) {
		return ns.equals(id.ns);
	}

	public U get() {
		return no;
	}

	@Override
	public String name() {
		return no.toString();
	}

	@Override
	public String uname() {
		return String.format("%s::%s", ns, name());
	}

	@Override
	public int compareTo(UId<U> id) {
		if (this == id) return 0;
		if (id == null) return -1;
		int v = ns.compareTo(id.ns);
		if (v != 0) return v;
		return no.compareTo(id.no);
	}

	@Override
	public int hashCode() {
		return Objects.hash(ns, no);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) return true;
		if (!(obj instanceof UId)) return false;
		UId<?> other = (UId<?>) obj;
		return Objects.equals(ns, other.ns) && Objects.deepEquals(no, other.no);
	}

	@Override
	public String toString() {
		return uname();
	}
}
