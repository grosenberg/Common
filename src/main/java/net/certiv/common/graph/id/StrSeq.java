package net.certiv.common.graph.id;

import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Objects;
import java.util.stream.Stream;

import net.certiv.common.check.Assert;
import net.certiv.common.util.CompareUtil;
import net.certiv.common.util.Strings;

public class StrSeq implements Comparable<StrSeq>, Iterable<String> {

	public final LinkedList<String> elems = new LinkedList<>();

	public StrSeq(String elem) {
		Assert.notNull(elem);
		elems.add(elem);
	}

	public StrSeq(Collection<String> elems) {
		Assert.notNull(elems);
		this.elems.addAll(elems);
	}

	public String baseName() {
		int end = Math.max(0, elems.size() - 1);
		return String.join(Strings.DOT, elems.subList(0, end));
	}

	public String lastName() {
		return elems.peekLast();
	}

	public boolean contains(Object o) {
		return elems.contains(o);
	}

	public boolean containsAll(Collection<?> c) {
		return elems.containsAll(c);
	}

	public String get(int index) {
		return elems.get(index);
	}

	public int indexOf(Object o) {
		return elems.indexOf(o);
	}

	public int lastIndexOf(Object o) {
		return elems.lastIndexOf(o);
	}

	public int size() {
		return elems.size();
	}

	public Stream<String> stream() {
		return elems.stream();
	}

	@Override
	public int compareTo(StrSeq o) {
		return CompareUtil.compare(elems, o.elems);
	}

	@Override
	public Iterator<String> iterator() {
		return elems.iterator();
	}

	@Override
	public int hashCode() {
		return Objects.hash(elems);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) return true;
		if (!(obj instanceof StrSeq)) return false;
		StrSeq other = (StrSeq) obj;
		return Objects.equals(elems, other.elems);
	}

	@Override
	public String toString() {
		return String.join(Strings.DOT, elems);
	}
}
