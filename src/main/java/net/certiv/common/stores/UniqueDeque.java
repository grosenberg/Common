package net.certiv.common.stores;

import java.util.ArrayDeque;
import java.util.Collection;
import java.util.function.Predicate;

/**
 * An ArrayDeque constrained to only allowing unique values. Also permits the data
 * structure to be made immutable.
 */
@Deprecated
public class UniqueDeque<E> extends ArrayDeque<E> {

	@SuppressWarnings("rawtypes")
	private static final UniqueDeque EMPTY = new UniqueDeque<>();

	private boolean nomod;

	@SuppressWarnings("unchecked")
	public static final <E> UniqueDeque<E> empty() {
		return (UniqueDeque<E>) EMPTY;
	}

	public UniqueDeque() {
		super();
	}

	public UniqueDeque(Collection<? extends E> c) {
		super(c);
	}

	public UniqueDeque(int num) {
		super(num);
	}

	/**
	 * Set the queue irreversibly to an unmodifiable condition.
	 *
	 * @return this
	 */
	public UniqueDeque<E> unmodifiable() {
		nomod = true;
		return this;
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
