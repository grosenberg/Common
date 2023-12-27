package net.certiv.common.graph.id;

import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Objects;
import java.util.function.Consumer;

import net.certiv.common.check.Assert;
import net.certiv.common.util.CompareUtil;
import net.certiv.common.util.Maths;
import net.certiv.common.util.Maths.RangeStyle;
import net.certiv.common.util.Strings;

/**
 * Provides a defining class for a unique, structured {@code namespace::name} identifier.
 * Extend and admend to provide any additional fields appropriate to establishing
 * uniqueness.
 * <p>
 * Default implementation uses {@code '::'} as the namespace separator and {@code '.'} as
 * the name element separator.
 */
public class Id implements Comparable<Id>, Iterable<String> {

	public final String ns;
	public final LinkedList<String> elems = new LinkedList<>();

	public Id(String ns, Collection<String> elems) {
		Assert.notNull(ns, elems);
		this.ns = ns;
		this.elems.addAll(elems);
	}

	/** The containing namespace. */
	public String namespace() {
		return ns;
	}

	/** Structured name - excludes the namespace; potentially not unique. */
	public String name() {
		return String.join(Strings.DOT, elements());
	}

	/** Structured unique name; includes the namespace and name. */
	public String uname() {
		return String.format("%s::%s", ns, name());
	}

	/** Structured package name - elements except last. */
	public String packageName() {
		int end = Math.max(0, elems.size() - 1);
		return String.join(Strings.DOT, elems.subList(0, end));
	}

	/** Structured last name element. */
	public String lastName() {
		return elems.peekLast();
	}

	/** Deconstructed name. */
	public LinkedList<String> elements() {
		return new LinkedList<>(elems);
	}

	/** Return indexed element of the ident name. */
	public String element(int index) {
		return elems.get(index);
	}

	/** Return the first index of the given element from the ident name. */
	public int indexOf(String element) {
		return elems.indexOf(element);
	}

	/** Return the last index of the given element from the ident name. */
	public int lastIndexOf(String element) {
		return elems.lastIndexOf(element);
	}

	public boolean contains(String element) {
		return elems.contains(element);
	}

	public boolean containsAll(Collection<String> names) {
		return elems.containsAll(names);
	}

	public int size() {
		return elems.size();
	}

	public boolean sameNamespace(Id id) {
		return ns.equals(id.ns);
	}

	public boolean sameNameRoot(Id id) {
		return Maths.inRange(order(id), -1, 1, RangeStyle.CLOSED);
	}

	/**
	 * Determines whether this Id is ordered between or equal to the given Ids.
	 *
	 * @param supra a superior Id
	 * @param infra an inferior Id
	 * @return {@code true} if this Id is inclusively between
	 */
	public boolean between(Id supra, Id infra) {
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
	public boolean superiorTo(Id other, boolean equal) {
		return Maths.inRange(order(other), -3, 0, equal ? RangeStyle.CLOSED : RangeStyle.RIGHT_OPEN);
	}

	/**
	 * Determines whether this Id is ordered after or conditionally equal to the given Id.
	 *
	 * @param other another Id
	 * @return {@code true} if this Id is inferior
	 */
	public boolean inferiorTo(Id other, boolean equal) {
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
	public int order(Id other) {
		int dot = ns.compareTo(other.ns);
		if (dot != 0) return dot * 3;
		return CompareUtil.within(elems, other.elems);
	}

	@Override
	public int compareTo(Id id) {
		if (this == id) return 0;
		if (id == null) return -1;
		int v = ns.compareTo(id.ns);
		if (v != 0) return v;
		return CompareUtil.compare(elems, id.elems);
	}

	@Override
	public Iterator<String> iterator() {
		return elems.iterator();
	}

	@Override
	public void forEach(Consumer<? super String> action) {
		elems.forEach(action);
	}

	@Override
	public int hashCode() {
		return Objects.hash(ns, elems);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) return true;
		if (obj == null) return false;
		if (getClass() != obj.getClass()) return false;
		Id id = (Id) obj;
		return Objects.equals(ns, id.ns) && Objects.equals(elems, id.elems);
	}

	@Override
	public String toString() {
		return uname();
	}
}
