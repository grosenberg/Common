package net.certiv.common.id;

import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.function.Consumer;

import net.certiv.common.util.CompareUtil;
import net.certiv.common.util.Maths;
import net.certiv.common.util.Maths.RangeStyle;

/**
 * Provides a defining class for a unique, structured {@code namespace::UIdName}
 * identifier, where the {@link UIdName} is premised on a {@link Seq} of name elements to
 * implement an ontological/hierarchical qualified name. Extend {@link Seq} to type and
 * provide any additional fields appropriate to establishing uniqueness.
 * <p>
 * Namespace must be non-null and should be non-blank; use {@code #DEFAULT} to represent
 * the default namespace.
 * <p>
 * Default implementation uses {@code '::'} as the namespace/UIdName separator and
 * {@code '.'} as the sequential name element separator.
 *
 * @param <T> UIdName type
 */
public class SId<T extends Seq> extends UId<T> implements Iterable<String> {

	public SId(String ns, T nu) {
		super(ns, nu);
	}

	/** Structured package name - elements except last. */
	public String packageName() {
		return get().baseName();
	}

	/** Structured last name element. */
	public String lastName() {
		return get().lastName();
	}

	/** Deconstructed name. */
	public LinkedList<String> elem() {
		return new LinkedList<>(get().seq);
	}

	/** Return indexed element of the ident name. */
	public String elem(int index) {
		return get().get(index);
	}

	/** Return the first index of the given element from the ident name. */
	public int indexOf(String element) {
		return get().indexOf(element);
	}

	/** Return the last index of the given element from the ident name. */
	public int lastIndexOf(String element) {
		return get().lastIndexOf(element);
	}

	public boolean contains(String element) {
		return get().contains(element);
	}

	public boolean containsAll(Collection<String> names) {
		return get().containsAll(names);
	}

	public int size() {
		return get().size();
	}

	public boolean sameNamespace(SId<T> oId) {
		return ns.equals(oId.ns);
	}

	public boolean sameNameRoot(SId<T> oId) {
		return Maths.inRange(order(oId), -1, 1, RangeStyle.CLOSED);
	}

	/**
	 * Determines whether this Id is ordered between or equal to the given Ids.
	 *
	 * @param supra a superior Id
	 * @param infra an inferior Id
	 * @return {@code true} if this Id is inclusively between
	 */
	public boolean between(SId<T> supra, SId<T> infra) {
		return Maths.inRange(order(supra), 0, 1, RangeStyle.CLOSED)
				&& Maths.inRange(order(infra), -1, 0, RangeStyle.CLOSED);
	}

	/**
	 * Determines whether this Id is ordered before or conditionally equal to the given
	 * Id.
	 *
	 * @param other another Id
	 * @return {@code true} if this Id is superior
	 */
	public boolean superiorTo(SId<T> other, boolean equal) {
		return Maths.inRange(order(other), -3, 0, equal ? RangeStyle.CLOSED : RangeStyle.RIGHT_OPEN);
	}

	/**
	 * Determines whether this Id is ordered after or conditionally equal to the given Id.
	 *
	 * @param other another Id
	 * @return {@code true} if this Id is inferior
	 */
	public boolean inferiorTo(SId<T> other, boolean equal) {
		return Maths.inRange(order(other), 0, 3, equal ? RangeStyle.CLOSED : RangeStyle.LEFT_OPEN);
	}

	/**
	 * Extended comparison of this Id relative to the given Id.
	 * <p>
	 * Returns an ordering indicia of:
	 * <ul>
	 * <li>-3: this namespace before
	 * <li>-2: this elements before (no common root)
	 * <li>-1: this elements before (has common root)
	 * <li>+0: same namespace and elements
	 * <li>+1: this elements after (has common root)
	 * <li>+2: this elements after (no common root)
	 * <li>+3: this namespace after
	 * </ul>
	 *
	 * @param other another Id
	 * @return an extended compare-style relative ordering indicator
	 * @see CompareUtil#within
	 */
	public int order(SId<T> other) {
		int dot = ns.compareTo(other.ns);
		if (dot != 0) return dot * 3;
		return CompareUtil.within(nu.seq, other.nu.seq);
	}

	@Override
	public int compareTo(UId<T> id) {
		if (this == id) return 0;
		if (id == null) return -1;
		int v = ns.compareTo(id.ns);
		if (v != 0) return v;
		return nu.compareTo(id.nu);
	}

	@Override
	public Iterator<String> iterator() {
		return nu.iterator();
	}

	@Override
	public void forEach(Consumer<? super String> action) {
		nu.forEach(action);
	}
}
