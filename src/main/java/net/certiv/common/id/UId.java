package net.certiv.common.id;

import java.util.Objects;

import net.certiv.common.check.Assert;

/**
 * Provides an abstract base class for defining a unique, structured
 * {@code namespace::UIdName} identifier. Implement {@link UIdName} to type and provide
 * any additional fields appropriate to establishing uniqueness.
 * <p>
 * Namespace must be non-null and should be non-blank; use {@code #DEFAULT} to represent
 * the default namespace.
 * <p>
 * Default implementation uses {@code '::'} as the namespace/UIdName separator.
 *
 * @param <U> UIdName type
 */
public abstract class UId<U extends UIdName> implements Comparable<UId<U>>, IUId {

	protected final String ns;
	protected final U nu;

	public UId(String ns, U nu) {
		Assert.notNull(ns, nu);
		this.ns = ns;
		this.nu = nu;
	}

	/** The containing namespace. */
	@Override
	public String namespace() {
		return ns;
	}

	/**
	 * Determines if the namespace is the {@code default} namespace.
	 *
	 * @return {@code true} if the namespace is the {@code default} namespace
	 */
	@Override
	public boolean isDefaultNamespace() {
		return UIdFactory.DEFAULT.equals(ns);
	}

	@Override
	public boolean sameNamespace(UId<?> id) {
		return ns.equals(id.ns);
	}

	public U get() {
		return nu;
	}

	/** Simple name - excludes the namespace; potentially not unique. */
	@Override
	public String name() {
		return nu.toString();
	}

	/** Unique, structured name; includes the namespace and name. */
	@Override
	public String uname() {
		return String.format("%s::%s", ns, name());
	}

	@Override
	public int compareTo(UId<U> o) {
		if (this == o) return 0;
		if (o == null) return -1;
		int v = ns.compareTo(o.ns);
		if (v != 0) return v;
		return nu.compareTo(o.nu);
	}

	@Override
	public int hashCode() {
		return Objects.hash(ns, nu);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) return true;
		if (!(obj instanceof UId)) return false;
		UId<?> other = (UId<?>) obj;
		return Objects.equals(ns, other.ns) && Objects.equals(nu, other.nu);
	}

	@Override
	public String toString() {
		return uname();
	}
}
