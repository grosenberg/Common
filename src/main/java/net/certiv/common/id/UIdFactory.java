package net.certiv.common.id;

import java.util.LinkedHashSet;
import java.util.Set;
import java.util.stream.Collectors;

public abstract class UIdFactory<T extends UId<U>, U extends UIdName> {

	public static final String ANON = "anon";
	public static final String DEFAULT = "default";
	public static final String NIL = "nil";
	public static final String UNKNOWN = "unknown";

	/** Cache: value=id */
	protected final Set<T> cache = new LinkedHashSet<>();

	/** The namespace specific to this factory instance. */
	public final String ns;

	/**
	 * Creates the factory with the given default namespace; {@code ANON} if {@code null}.
	 *
	 * @param ns the namespace for factored idents
	 */
	public UIdFactory(String ns) {
		this.ns = ns != null ? ns : DEFAULT;
	}

	// ---- Public API ----

	/**
	 * Returns the current id set for the default namespace.
	 *
	 * @return default namespace id set
	 */
	public Set<T> defined() {
		return defined(ns);
	}

	/**
	 * Returns the id defined in the given namespace.
	 *
	 * @param ns namespace
	 * @return namespace ids
	 */
	public Set<T> defined(String ns) {
		return cache.stream() //
				.filter(k -> k.ns.equals(ns)) //
				.collect(Collectors.toUnmodifiableSet());
	}

	/**
	 * Return an existing ident corresponding to the given naming elements in the given
	 * namespace.
	 *
	 * @param ns   namespace
	 * @param elem identifier element
	 * @return existing ident or {@code null}
	 */
	public T find(String ns, U elem) {
		return cache.stream() //
				.filter(t -> t.ns.equals(ns) && t.nu.equals(elem)) //
				.findFirst() //
				.orElse(null);
	}

	/**
	 * Makes a ident with the given name in the default namespace.
	 *
	 * @param elem an identifier
	 * @return existing or new ident
	 */
	public T make(U elem) {
		return make(ns, elem);
	}

	/**
	 * Makes an ident with the given name in the given namespace.
	 *
	 * @param ns   namespace
	 * @param elem an identifier
	 * @return existing or new ident
	 */
	protected T make(String ns, U elem) {
		T id = find(ns, elem);
		if (id != null) return (T) id;

		id = __make(ns, elem);
		cache.add(id);
		return (T) id;
	}

	// ---- Internal API --------------

	/**
	 * Makes a type-specific ident with the given name in the given namespace.
	 *
	 * @param ns   namespace
	 * @param elem an identifier
	 * @return new ident
	 */
	protected abstract T __make(String ns, U elem);

	// --------------------------------

	@Override
	public String toString() {
		return ns;
	}
}
