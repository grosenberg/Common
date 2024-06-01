package net.certiv.common.id;

import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.stream.Stream;

import net.certiv.common.check.Assert;
import net.certiv.common.util.CompareUtil;
import net.certiv.common.util.Strings;

public class Seq implements UIdName {

	public final LinkedList<String> seq = new LinkedList<>();

	public Seq(String elem) {
		Assert.notNull(elem);
		seq.add(elem);
	}

	public Seq(Collection<String> elems) {
		Assert.notNull(elems);
		this.seq.addAll(elems);
	}

	@Override
	public String name() {
		return String.join(Strings.DOT, seq);
	}

	public String baseName() {
		int end = Math.max(0, seq.size() - 1);
		return String.join(Strings.DOT, seq.subList(0, end));
	}

	public String lastName() {
		return seq.peekLast();
	}

	public boolean contains(String o) {
		return seq.contains(o);
	}

	public boolean containsAll(Collection<String> c) {
		return seq.containsAll(c);
	}

	public List<String> elems() {
		return seq;
	}

	public String get(int index) {
		return seq.get(index);
	}

	public int indexOf(String o) {
		return seq.indexOf(o);
	}

	public int lastIndexOf(String o) {
		return seq.lastIndexOf(o);
	}

	public int size() {
		return seq.size();
	}

	public Stream<String> stream() {
		return seq.stream();
	}

	public void forEach(Consumer<? super String> action) {
		seq.forEach(action);
	}

	public Iterator<String> iterator() {
		return seq.iterator();
	}

	@Override
	public int compareTo(UIdName o) {
		if (o instanceof Seq s) {
			return CompareUtil.compare(seq, s.seq);
		}
		return name().compareTo(o.name());
	}

	@Override
	public int hashCode() {
		return Objects.hash(seq);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) return true;
		if (!(obj instanceof Seq)) return false;
		Seq other = (Seq) obj;
		return Objects.equals(seq, other.seq);
	}

	@Override
	public String toString() {
		return name();
	}
}
