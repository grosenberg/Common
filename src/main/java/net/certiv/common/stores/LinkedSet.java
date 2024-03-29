/*******************************************************************************
 * Copyright (c) 2017, 2018 Certiv Analytics. All rights reserved.
 * Use of this file is governed by the Eclipse Public License v1.0
 * that can be found in the LICENSE.txt file in the project root,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package net.certiv.common.stores;

import java.util.AbstractList;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.SortedSet;
import java.util.Spliterator;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.stream.Stream;

/**
 * LinkedSet is a List implementation that satisfies the Set interface
 * uniqueness requirements while also implementing the SortedSet interface for
 * access convenience. The backing store is an ArrayList and instances are
 * stored in accordance with the List interface. Adding a duplicate instance
 * does not affect the existing order.
 * <p>
 * If a comparator is provided, instances added to this collection must be
 * comparable and, when added, are sorted in the order specified by the
 * comparator.
 */
public class LinkedSet<E> extends AbstractList<E> implements SortedSet<E>, Comparable<E> {

	private final List<E> elements = new ArrayList<>();	// backing collection
	private Comparator<? super E> comp;

	/** Create a new LinkedSet. */
	public LinkedSet() {
	}

	/** Create a new LinkedSet with the mappings from the given Set. */
	public LinkedSet(Collection<? extends E> values) {
		elements.addAll(values);
	}

	public LinkedSet(Comparator<? super E> comp) {
		this.comp = comp;
	}

	/**
	 * Adds the specified object to this set. The set is not modified if it already
	 * contains the object. Objects are added to the Set in insertion order unless
	 * the Set was created with a comparator.
	 *
	 * @param e the object to add.
	 * @return {@code true}if this set is modified, {@code false} otherwise.
	 * @throws ClassCastException when the class of the object is inappropriate for
	 *             this set.
	 */
	@Override
	public boolean add(E e) {
		if (indexOf(e) != -1) return false;
		boolean result = elements.add(e);
		if (result && comp != null) elements.sort(comp);
		return result;
	}

	/**
	 * Adds the specified Collection to this Set. The set is not modified if it
	 * already contains the object. Objects are added to the Set in insertion order
	 * unless the Set was created with a comparator.
	 *
	 * @param c the Collection to add.
	 * @return {@code true}if this set is modified, {@code false} otherwise.
	 * @throws ClassCastException when the class of the object is inappropriate for
	 *             this set.
	 */
	@Override
	public boolean addAll(Collection<? extends E> c) {
		boolean result = false;
		for (E o : c) {
			result = add(o) || result;
		}
		if (result && comp != null) elements.sort(comp);
		return result;
	}

	@Override
	public void add(int index, E e) {
		if (comp != null) throw new UnsupportedOperationException();
		if (indexOf(e) != -1) return;
		elements.add(index, e);
	}

	@Override
	public boolean addAll(int index, Collection<? extends E> c) {
		if (comp != null) throw new UnsupportedOperationException();
		List<? extends E> x = new ArrayList<>(c);
		boolean modified = false;
		for (int idx = c.size() - 1; idx >= 0; idx--) {
			E e = x.get(idx);
			if (indexOf(e) == -1) {
				elements.add(index, e);
				modified = true;
			}
		}
		return modified;
	}

	@Override
	public E first() {
		return elements.get(0);
	}

	@Override
	public E last() {
		return get(elements.size() - 1);
	}

	@Override
	public E get(int index) {
		return elements.get(index);
	}

	@Override
	public int indexOf(Object o) {
		return elements.indexOf(o);
	}

	@Override
	public int lastIndexOf(Object o) {
		return elements.lastIndexOf(o);
	}

	@Override
	public boolean contains(Object o) {
		return elements.contains(o);
	}

	@Override
	public boolean containsAll(Collection<?> c) {
		return elements.containsAll(c);
	}

	@Override
	public boolean retainAll(Collection<?> c) {
		return elements.retainAll(c);
	}

	@Override
	public E remove(int index) {
		return elements.remove(index);
	}

	@Override
	public boolean remove(Object o) {
		return elements.remove(o);
	}

	@Override
	public boolean removeAll(Collection<?> c) {
		return elements.removeAll(c);
	}

	@Override
	public boolean isEmpty() {
		return elements.isEmpty();
	}

	@Override
	public int size() {
		return elements.size();
	}

	@Override
	public void clear() {
		elements.clear();
	}

	@Override
	public Iterator<E> iterator() {
		return elements.iterator();
	}

	@Override
	public ListIterator<E> listIterator() {
		return elements.listIterator();
	}

	@Override
	public ListIterator<E> listIterator(int index) {
		return elements.listIterator(index);
	}

	@Override
	public Comparator<? super E> comparator() {
		return comp;
	}

	@Override
	public int compareTo(E o) {
		// TODO: implement
		return 0;
	}

	@Override
	public Object[] toArray() {
		return elements.toArray();
	}

	@Override
	public <T> T[] toArray(T[] a) {
		return elements.toArray(a);
	}

	public LinkedSet<E> headSet() {
		List<E> subset = elements.subList(0, elements.size() - 1);
		return new LinkedSet<>(subset);
	}

	@Override
	public SortedSet<E> headSet(E toElement) {
		List<E> subset = elements.subList(0, indexOf(toElement));
		return new LinkedSet<>(subset);
	}

	@Override
	public SortedSet<E> tailSet(E fromElement) {
		List<E> subset = elements.subList(indexOf(fromElement), elements.size());
		return new LinkedSet<>(subset);
	}

	@Override
	public SortedSet<E> subSet(E fromElement, E toElement) {
		List<E> subset = subList(indexOf(fromElement), indexOf(toElement));
		return new LinkedSet<>(subset);
	}

	@Override
	public List<E> subList(int fromIndex, int toIndex) {
		return elements.subList(fromIndex, toIndex);
	}

	// Override default methods in Collection
	@Override
	public void forEach(Consumer<? super E> consumer) {
		elements.forEach(consumer);
	}

	@Override
	public boolean removeIf(Predicate<? super E> filter) {
		return elements.removeIf(filter);
	}

	@Override
	public Spliterator<E> spliterator() {
		return elements.spliterator();
	}

	@Override
	public Stream<E> stream() {
		return elements.stream();
	}

	@Override
	public Stream<E> parallelStream() {
		return elements.parallelStream();
	}

	@Override
	@Deprecated
	public E set(int index, E element) {
		throw new UnsupportedOperationException();
	}

	@Override
	public int hashCode() {
		return elements.hashCode();
	}

	@Override
	public boolean equals(Object o) {
		return elements.equals(o);
	}

	@Override
	public String toString() {
		return elements.toString();
	}
}
