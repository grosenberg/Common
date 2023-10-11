package net.certiv.common.stores;

import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.Set;
import java.util.function.Predicate;
import java.util.function.UnaryOperator;

import net.certiv.common.ex.NotImplementedException;

/**
 * An ArrayDeque constrained to only allowing unique values. Also permits the data
 * structure to be made immutable.
 */
public class UniqueList<E> extends LinkedList<E> {

	private static final UniqueList<?> EMPTY = new UniqueList<>().unmodifiable();

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

	public boolean isUnmodifiable() {
		return nomod;
	}

	/**
	 * Makes this list shallow unmodifiable. Irreversible. Use {@link #dup()} to create a
	 * new modifiable copy.
	 *
	 * @return this
	 */
	public UniqueList<E> unmodifiable() {
		nomod = true;
		return this;
	}

	/**
	 * Makes a new instance of this list. The returned list is modifiable even if this
	 * list is not.
	 *
	 * @return a new instance containing the elements of this list
	 */
	public UniqueList<E> dup() {
		return new UniqueList<>(this);
	}

	/**
	 * If unique, appends the given element to the end of this list. Otherwise, replaces
	 * the prior equivalent instance with the given element.
	 * <p>
	 * This method is equivalent to {@link #addLast}.
	 *
	 * @param e the element to be appended to this list
	 * @return {@code true} if unique appended
	 */
	@Override
	public boolean add(E e) {
		if (nomod) throw new UnsupportedOperationException();
		if (contains(e)) {
			super.set(indexOf(e), e);
			return false;
		}
		return super.add(e);
	}

	@Override
	public void add(int idx, E e) {
		if (nomod) throw new UnsupportedOperationException();
		if (!contains(e)) super.add(idx, e);
		if (idx == indexOf(e)) {
			super.set(idx, e);
		} else {
			remove(e);
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
		if (c == null) return false;
		Set<E> in = new LinkedHashSet<>(c);
		in.removeAll(this);
		return super.addAll(in);
	}

	@Override
	public boolean addAll(int idx, Collection<? extends E> c) {
		if (nomod) throw new UnsupportedOperationException();
		if (c == null) return false;
		Set<E> in = new LinkedHashSet<>(c);
		in.removeAll(this);
		return super.addAll(Math.min(idx, size()), in);
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
		E x = get(idx);
		remove(e);
		return super.set(indexOf(x), e);
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
