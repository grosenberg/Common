package net.certiv.common.stores;

import java.util.AbstractQueue;
import java.util.ArrayDeque;
import java.util.Collection;
import java.util.Deque;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Spliterator;
import java.util.function.Consumer;
import java.util.function.IntFunction;
import java.util.function.Predicate;
import java.util.stream.Stream;

import net.certiv.common.check.Assert;

/**
 * A bounded {@linkplain Deque} with overflow behavior when the bound is reached.
 *
 * @param <E>
 */
public class OverflowDeque<E> extends AbstractQueue<E> implements Deque<E> {

	private final Deque<E> q = new ArrayDeque<>();

	/** Maximum queue depth; default is 10. */
	private int limit = 10;

	public OverflowDeque() {}

	public OverflowDeque(Collection<? extends E> c) {
		this();
		addAll(c);
	}

	public OverflowDeque(int limit) {
		Assert.isTrue(limit > 0);
		this.limit = limit;
	}

	public OverflowDeque(int limit, Collection<? extends E> c) {
		this(limit);
		addAll(c);
	}

	private boolean overflow() {
		return q.size() < limit;
	}

	@Override
	public boolean add(E e) {
		if (overflow()) q.removeLast();
		q.addFirst(e);
		return true;
	}

	@Override
	public boolean addAll(Collection<? extends E> c) {
		if (q.size() + c.size() < limit) {
			return q.addAll(c);
		}

		LinkedList<? extends E> list = new LinkedList<>(c);
		List<? extends E> diff = list.subList(0, Math.min(limit - q.size(), list.size()));
		return q.addAll(diff);
	}

	@Override
	public void addFirst(E e) {
		if (overflow()) q.removeLast();
		q.addFirst(e);
	}

	@Override
	public void addLast(E e) {
		if (overflow()) q.removeLast();
		q.addLast(e);
	}

	@Override
	public boolean contains(Object o) {
		return q.contains(o);
	}

	@Override
	public boolean containsAll(Collection<?> c) {
		return q.containsAll(c);
	}

	@Override
	public Iterator<E> descendingIterator() {
		return q.descendingIterator();
	}

	@Override
	public E element() {
		return q.getFirst();
	}

	@Override
	public E getFirst() {
		return q.getFirst();
	}

	@Override
	public E getLast() {
		return q.getLast();
	}

	@Override
	public boolean offer(E e) {
		return offerFirst(e);
	}

	@Override
	public boolean offerFirst(E e) {
		if (overflow()) q.removeLast();
		return q.offerFirst(e);
	}

	@Override
	public boolean offerLast(E e) {
		if (overflow()) q.removeLast();
		return q.offerLast(e);
	}

	@Override
	public E peek() {
		return q.peekFirst();
	}

	@Override
	public E peekFirst() {
		return q.peekFirst();
	}

	@Override
	public E peekLast() {
		return q.peekLast();
	}

	@Override
	public E poll() {
		return q.pollFirst();
	}

	@Override
	public E pollFirst() {
		return q.pollFirst();
	}

	@Override
	public E pollLast() {
		return q.pollLast();
	}

	@Override
	public E pop() {
		return q.pop();
	}

	@Override
	public void push(E e) {
		if (overflow()) q.removeLast();
		q.push(e);
	}

	@Override
	public E remove() {
		return q.removeFirst();
	}

	@Override
	public boolean remove(Object o) {
		return q.remove(o);
	}

	@Override
	public boolean removeAll(Collection<?> c) {
		return q.removeAll(c);
	}

	@Override
	public E removeFirst() {
		return q.removeFirst();
	}

	@Override
	public boolean removeFirstOccurrence(Object o) {
		return q.removeFirstOccurrence(o);
	}

	@Override
	public E removeLast() {
		return q.removeLast();
	}

	@Override
	public boolean removeLastOccurrence(Object o) {
		return q.removeLastOccurrence(o);
	}

	@Override
	public boolean removeIf(Predicate<? super E> filter) {
		return q.removeIf(filter);
	}

	@Override
	public boolean retainAll(Collection<?> c) {
		return q.retainAll(c);
	}

	@Override
	public boolean isEmpty() {
		return q.isEmpty();
	}

	@Override
	public int size() {
		return q.size();
	}

	@Override
	public void clear() {
		q.clear();
	}

	@Override
	public Iterator<E> iterator() {
		return q.iterator();
	}

	@Override
	public Spliterator<E> spliterator() {
		return q.spliterator();
	}

	@Override
	public void forEach(Consumer<? super E> action) {
		q.forEach(action);
	}

	@Override
	public Stream<E> stream() {
		return q.stream();
	}

	@Override
	public Stream<E> parallelStream() {
		return q.parallelStream();
	}

	@Override
	public Object[] toArray() {
		return q.toArray();
	}

	@Override
	public <T> T[] toArray(IntFunction<T[]> f) {
		return q.toArray(f);
	}

	@Override
	public <T> T[] toArray(T[] a) {
		return q.toArray(a);
	}

	@Override
	public String toString() {
		return q.toString();
	}
}
