package net.certiv.common.stores;

import java.util.Collection;
import java.util.LinkedList;
import java.util.function.Predicate;
import java.util.function.UnaryOperator;

import net.certiv.common.ex.NotImplementedException;
import net.certiv.common.util.Maths;

/**
 * An ArrayDeque constrained to only allowing unique values. Also permits the data
 * structure to be made immutable.
 */
public class UniqueList<E> extends LinkedList<E> {

	private static final UniqueList<?> EMPTY = new UniqueList<>();

	private boolean nomod;

	@SuppressWarnings("unchecked")
	public static final <E> UniqueList<E> empty() {
		return (UniqueList<E>) EMPTY;
	}

	public UniqueList() {
		super();
	}

	public UniqueList(Collection<? extends E> c) {
		super(c);
	}

	/**
	 * Set the queue irreversibly to an unmodifiable condition.
	 *
	 * @return this
	 */
	public UniqueList<E> unmodifiable() {
		nomod = true;
		return this;
	}

	@Override
	public boolean add(E e) {
		if (nomod) throw new UnsupportedOperationException();
		if (contains(e)) super.set(indexOf(e), e);
		return super.add(e);
	}

	@Override
	public void add(int idx, E e) {
		if (nomod) throw new UnsupportedOperationException();
		if (!contains(e)) super.add(idx, e);

		int dot = indexOf(e);
		switch (Maths.retrict(dot - idx)) {
			case -1: // before
				remove(e);
				super.add(idx - 1, e);
				break;
			case 1: // after
				remove(e);
				super.add(idx, e);
				break;
			case 0:
			default:
				super.add(idx, e);
		}
	}

	@Override
	public void addFirst(E e) {
		if (nomod) throw new UnsupportedOperationException();
		if (contains(e)) remove(e);
		super.addFirst(e);
	}

	@Override
	public void addLast(E e) {
		if (nomod) throw new UnsupportedOperationException();
		if (contains(e)) remove(e);
		super.addLast(e);
	}

	@Override
	public boolean addAll(Collection<? extends E> c) {
		if (nomod) throw new UnsupportedOperationException();
		removeAll(c);
		return super.addAll(c);
	}

	@Override
	public boolean addAll(int idx, Collection<? extends E> c) {
		if (nomod) throw new UnsupportedOperationException();
		int cnt = (int) stream().filter(e -> contains(e) && indexOf(e) < idx).count();
		removeAll(c);
		return super.addAll(idx - cnt, c);
	}

	@Override
	public boolean offer(E e) {
		if (nomod) throw new UnsupportedOperationException();
		if (contains(e)) remove(e);
		return super.offer(e);
	}

	@Override
	public E set(int idx, E e) {
		if (nomod) throw new UnsupportedOperationException();
		if (!contains(e)) return super.set(idx, e);

		int dot = indexOf(e);
		switch (Maths.retrict(dot - idx)) {
			case -1: // before
				remove(e);
				return super.set(idx - 1, e);
			case 1: // after
				remove(e);
				return super.set(idx, e);
			case 0:
			default:
				return super.set(idx, e);
		}
	}

	@Override
	public boolean remove(Object o) {
		if (nomod) throw new UnsupportedOperationException();
		return super.remove(o);
	}

	@Override
	public E remove(int index) {
		if (nomod) throw new UnsupportedOperationException();
		return super.remove(index);
	}

	@Override
	public E removeFirst() {
		if (nomod) throw new UnsupportedOperationException();
		return super.removeFirst();
	}

	@Override
	public E removeLast() {
		if (nomod) throw new UnsupportedOperationException();
		return super.removeLast();
	}

	@Override
	public boolean removeFirstOccurrence(Object o) {
		if (nomod) throw new UnsupportedOperationException();
		return super.removeFirstOccurrence(o);
	}

	@Override
	public boolean removeLastOccurrence(Object o) {
		if (nomod) throw new UnsupportedOperationException();
		return super.removeLastOccurrence(o);
	}

	@Override
	public boolean removeIf(Predicate<? super E> filter) {
		if (nomod) throw new UnsupportedOperationException();
		return super.removeIf(filter);
	}

	@Override
	public boolean removeAll(Collection<?> c) {
		if (nomod) throw new UnsupportedOperationException();
		return super.removeAll(c);
	}

	@Override
	public void replaceAll(UnaryOperator<E> operator) {
		throw new NotImplementedException();
	}

	@Override
	public boolean retainAll(Collection<?> c) {
		if (nomod) throw new UnsupportedOperationException();
		return super.retainAll(c);
	}

	@Override
	public void clear() {
		if (nomod) throw new UnsupportedOperationException();
		super.clear();
	}
}
