package net.certiv.common.stores;

import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * List of limited capacity. Minimum of 1. Default is 10. Maximum is
 * {@code Integer.MAX_VALUE}.
 *
 * @param <E>
 */
public class LimitList<E> implements Iterable<E> {

	private final LinkedList<E> list = new LinkedList<>();

	/** Maximum depth of the list. Default is 10. */
	private int limit = 10;

	public LimitList() {
		super();
	}

	public LimitList(int limit) {
		this();
		if (limit < 1) throw new IndexOutOfBoundsException(limit);
		this.limit = limit;
	}

	public LimitList(int limit, Collection<? extends E> c) {
		this(limit);
		addAll(c);
	}

	/**
	 * Valid insertion range.
	 * <p>
	 * For {@code limit=4}, the valid indicies are [0,1,2,3]
	 *
	 * @return {@code true} where an insertion index is within the list limit and in or at
	 *         the end of the existing list elements.
	 */
	private boolean valid(int idx) {
		return idx >= 0 && idx <= list.size() && idx < limit;
	}

	/**
	 * Valid removal range.
	 * <p>
	 * For {@code limit=4}, the valid indicies are [0,1,2,3]
	 *
	 * @return {@code true} where an insertion index is within the list limit and within
	 *         the existing list elements.
	 */
	private boolean extant(int idx) {
		return idx >= 0 && idx < list.size() && idx < limit;
	}

	/** Returns the current value of the list limit. */
	public int limit() {
		return limit;
	}

	/**
	 * Adjusts the list limit.
	 *
	 * @param limit the new depth of the list
	 * @return list of elements, if any, trimmed from the end of the list where the new
	 *         limit value is less than the pre-existing list size.
	 */
	public LinkedList<E> adjustLimit(int limit) {
		if (limit < 1) throw new IndexOutOfBoundsException(limit);

		LinkedList<E> ovr = new LinkedList<>();
		if (limit < list.size()) {
			while (list.size() > limit) {
				ovr.addFirst(list.removeLast());
			}
		}
		this.limit = limit;
		return ovr;
	}

	/**
	 * Adds the given element to the head of this list.
	 * <p>
	 * This method is equivalent to {@link #addFirst}.
	 *
	 * @param e the element to be added
	 * @return the element, if any, pushed off the end of the list
	 */
	public E add(E e) {
		return addFirst(e);
	}

	/**
	 * Inserts the specified element at the specified position in this list. Shifts the
	 * element currently at that position (if any) and any subsequent elements to the
	 * right.
	 *
	 * @param idx insertion index
	 * @param e   element to be inserted
	 * @return the element, if any, pushed off the end of the list
	 * @throws IndexOutOfBoundsException
	 */
	public E add(int idx, E e) {
		if (!valid(idx)) throw new IndexOutOfBoundsException(idx);
		E last = size() == limit ? removeLast() : null;
		list.add(idx, e);
		return last;
	}

	/**
	 * Inserts the specified element at the beginning of this list.
	 *
	 * @param e the element to add
	 * @return the element, if any, pushed off the end of the list
	 */
	public E addFirst(E e) {
		E last = size() == limit ? removeLast() : null;
		list.addFirst(e);
		return last;
	}

	/**
	 * Inserts the specified element at the end of this list.
	 *
	 * @param e the element to add
	 * @return the element, if any, replaced at the end of the list
	 */
	public E addLast(E e) {
		E last = size() == limit ? removeLast() : null;
		list.addLast(e);
		return last;
	}

	/**
	 * Inserts all of the elements in the specified collection to the head of this list,
	 * in the order that they are returned by the given collection's iterator.
	 *
	 * @param c collection to be added
	 * @return list of elements, if any, pushed off the end of the list
	 */
	public LinkedList<E> addAll(Collection<? extends E> c) {
		LinkedList<E> ovr = new LinkedList<>();
		if (c != null && !c.isEmpty()) {
			List<E> in = new LinkedList<>(c);
			for (int idx = in.size() - 1; idx >= 0; idx--) {
				E e = in.get(idx);
				E rem = addFirst(e);
				if (rem != null) ovr.addFirst(rem);
			}
		}
		return ovr;
	}

	/**
	 * Inserts all of the elements in the specified collection at the given index in this
	 * list, in the order that they are returned by the given collection's iterator.
	 *
	 * @param idx insertion index
	 * @param c   collection to be added
	 * @return list of elements, if any, pushed off the end of the list
	 */
	public LinkedList<E> addAll(int idx, Collection<? extends E> c) {
		if (!valid(idx)) throw new IndexOutOfBoundsException(idx);

		LinkedList<E> ovr = new LinkedList<>();
		if (c != null && !c.isEmpty()) {
			List<E> in = new LinkedList<>(c);
			for (int jdx = in.size() - 1; jdx >= 0; jdx--) {
				E last = add(idx, in.get(jdx));
				if (last != null) ovr.addFirst(last);
			}
		}
		return ovr;
	}

	/**
	 * Returns the element at the specified position in this list.
	 *
	 * @param idx index of the element to return
	 * @return the element at the specified position in this list
	 * @throws IndexOutOfBoundsException if the index is out of range
	 *                                   ({@code index < 0 || index >= size()})
	 */
	public E get(int idx) {
		if (!extant(idx)) throw new IndexOutOfBoundsException(idx);
		return list.get(idx);
	}

	/**
	 * Returns the first element in this list.
	 *
	 * @return the first element in this list
	 * @throws NoSuchElementException if this list is empty
	 */
	public E getFirst() {
		return list.getFirst();
	}

	/**
	 * Returns the last element in this list.
	 *
	 * @return the last element in this list
	 * @throws NoSuchElementException if this list is empty
	 */
	public E getLast() {
		return list.getLast();
	}

	/**
	 * Replaces the element at the given index with the given element.
	 *
	 * @param idx index of the element to replace
	 * @param e   the element to insert
	 * @return the element previously at the given index
	 * @throws IndexOutOfBoundsException
	 */
	public E set(int idx, E e) {
		if (!extant(idx)) throw new IndexOutOfBoundsException(idx);
		return list.set(idx, e);
	}

	/**
	 * Retrieves, but does not remove, the head (first element) of this list.
	 *
	 * @return the head of this list, or {@code null} if this list is empty
	 */
	public E peek() {
		return list.peek();
	}

	/**
	 * Retrieves, but does not remove, the head (first element) of this list.
	 *
	 * @return the head of this list, or {@code null} if this list is empty
	 */
	public E peekFirst() {
		return list.peekFirst();
	}

	/**
	 * Retrieves, but does not remove, the last element of this list, or returns
	 * {@code null} if this list is empty.
	 *
	 * @return the last element of this list, or {@code null} if this list is empty
	 */
	public E peekLast() {
		return list.peekLast();
	}

	/**
	 * Pops an element from the stack (list head).
	 * <p>
	 * Equivalent to {@link #removeFirst()}.
	 *
	 * @return the element at the top (head) of this list
	 * @throws NoSuchElementException if this list is empty
	 */
	public E pop() {
		return list.pop();
	}

	/**
	 * Pushes an element onto the stack (list head).
	 * <p>
	 * Equivalent to {@link #addFirst}.
	 *
	 * @param e the element to push
	 */
	public void push(E e) {
		addFirst(e);
	}

	/**
	 * Retrieves and removes the head (first element) of this list.
	 *
	 * @return the head of this list, or {@code null} if this list is empty
	 */
	public E remove() {
		return list.remove();
	}

	/**
	 * Removes the first occurrence of the given element from this list, if present.
	 * <p>
	 * Returns {@code true} if this list contained the given element.
	 *
	 * @param e element to be removed, if present
	 * @return {@code true} if this list contained the given element
	 */
	public boolean remove(E e) {
		return list.remove(e);
	}

	/**
	 * Removes the element at the given position in this list. Shifts any subsequent
	 * elements to the left. Returns the element removed from the list.
	 *
	 * @param idx the index of the element to be removed
	 * @return the element previously at the specified position
	 * @throws IndexOutOfBoundsException
	 */
	public E remove(int idx) {
		if (!extant(idx)) throw new IndexOutOfBoundsException(idx);
		return list.remove(idx);
	}

	/**
	 * Removes and returns the first element from this list.
	 *
	 * @return the first element from this list
	 * @throws NoSuchElementException if this list is empty
	 */
	public E removeFirst() {
		return list.removeFirst();
	}

	/**
	 * Removes and returns the last element from this list.
	 *
	 * @return the last element from this list
	 * @throws NoSuchElementException if this list is empty
	 */
	public E removeLast() {
		return list.removeLast();
	}

	/**
	 * Removes all of the elements of this collection that satisfy the given predicate.
	 * Errors or runtime exceptions thrown during iteration or by the predicate are
	 * relayed to the caller.
	 *
	 * @param filter a predicate that returns {@code true} for elements to be removed
	 * @return {@code true} if any elements were removed
	 * @throws NullPointerException          if the specified filter is null
	 * @throws UnsupportedOperationException if elements cannot be removed from this
	 *                                       collection.
	 */
	public boolean removeIf(Predicate<? super E> filter) {
		return list.removeIf(filter);
	}

	/**
	 * Removes all of this collection's elements that are also contained in the given
	 * collection. After this call returns, this collection will contain no elements in
	 * common with the specified collection.
	 *
	 * @param c collection containing elements to be removed from this collection
	 * @return {@code true} if this collection changed
	 * @throws ClassCastException   if the types of one or more elements in this
	 *                              collection are incompatible with the specified
	 *                              collection
	 * @throws NullPointerException if the given collection is null
	 */
	public boolean removeAll(Collection<?> c) {
		return list.removeAll(c);
	}

	/**
	 * Retains only the elements in this collection that are contained in the given
	 * collection. In other words, removes from this collection all of its elements that
	 * are not contained in the specified collection.
	 *
	 * @param c collection containing elements to be retained
	 * @return {@code true} if this collection changed
	 * @throws ClassCastException   if the types of one or more elements in this
	 *                              collection are incompatible with the specified
	 *                              collection
	 * @throws NullPointerException if the specified collection is null
	 */
	public boolean retainAll(Collection<?> c) {
		return list.retainAll(c);
	}

	/**
	 * Returns {@code true} if this list contains no elements.
	 *
	 * @return {@code true} if this list contains no elements
	 */
	public boolean isEmpty() {
		return list.isEmpty();
	}

	/**
	 * Returns the number of elements in this list, constrained to the list limit (maximum
	 * of {@code Integer.MAX_VALUE}).
	 *
	 * @return the number of elements in this list
	 */
	public int size() {
		return list.size();
	}

	/**
	 * Removes all of the elements from this list. The list will be empty after this call
	 * returns.
	 */
	public void clear() {
		list.clear();
	}

	/** Returns this limited list as a conventional {@code LinkedList}. */
	public LinkedList<E> toList() {
		return list.stream().collect(Collectors.toCollection(LinkedList::new));
	}

	/**
	 * Returns a view of the portion of this list between the specified {@code fromIndex},
	 * inclusive, and {@code toIndex}, exclusive. (If {@code fromIndex} and
	 * {@code toIndex} are equal, the returned list is empty.)
	 *
	 * @param from start index (inclusive)
	 * @param to   stop index (exclusive)
	 * @return a view of the specified range within this list
	 * @throws IndexOutOfBoundsException for an illegal endpoint index value
	 */
	public List<E> subList(int from, int to) {
		return list.subList(from, to);
	}

	/**
	 * Returns a sequential {@code Stream} for this list.
	 *
	 * @return a sequential {@code Stream} over the elements in this list
	 */
	public Stream<E> stream() {
		return list.stream();
	}

	@Override
	public Iterator<E> iterator() {
		return list.iterator();
	}

	@Override
	public boolean equals(Object o) {
		return list.equals(o);
	}

	@Override
	public int hashCode() {
		return list.hashCode();
	}

	@Override
	public String toString() {
		return list.toString();
	}
}
