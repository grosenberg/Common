package net.certiv.common.id;

import java.util.Collection;

/**
 * Provides a concrete class for a unique, structured, hierarchical {@code namespace::Seq}
 * identifier.
 * <p>
 * Namespace must be non-null and should be non-blank; use {@code #DEFAULT} to represent
 * the default namespace.
 * <p>
 * Default implementation uses {@code '::'} as the namespace/UIdName separator and
 * {@code '.'} as the sequential name element separator.
 */
public class Id extends SId<Seq> {

	public Id(String ns, String elem) {
		super(ns, new Seq(elem));
	}

	public Id(String ns, Collection<String> elems) {
		super(ns, new Seq(elems));
	}

	public Id(String ns, Seq seq) {
		super(ns, seq);
	}
}
