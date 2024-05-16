package net.certiv.common.stores.sparse;

import java.util.AbstractSet;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map.Entry;
import java.util.NavigableSet;
import java.util.Objects;
import java.util.SortedSet;
import java.util.concurrent.ConcurrentSkipListMap;
import java.util.concurrent.ConcurrentSkipListSet;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.stream.Stream;

import net.certiv.common.ex.IllegalArgsEx;
import net.certiv.common.stores.UniqueList;

/**
 * Sparse, scalable, concurrent, ordered single-valued data store.
 * <p>
 * Implemented on a {@link ConcurrentSkipListSet}. The store is sorted according to the
 * {@linkplain Comparable natural ordering} of its elements, or by a {@link Comparator}
 * provided at map creation time, depending on the constructor used for store creation.
 * <p>
 * The store does not allow {@code null} elements; elements must implement
 * {@link Comparable} if a separate {@link Comparator} is not provided.
 * <p>
 * Intended to rationalize the {@link ConcurrentSkipListMap} methods that return a
 * {@code null} value indistinguishably indicating either a valid store entry containing a
 * {@code null} value or the absence of an entry. Rather, this class will always return an
 * {@link Entry} if the corresponding element exists in the store, leaving a {@code null}
 * return to exclusively indicate that no entry element was present.
 *
 * @param <E> element type
 */
public class SparseSet<E> extends AbstractSet<E> implements NavigableSet<E> {

	protected static final String ERR_ARG = "%s cannot be 'null'.";
	protected static final String ELEM = "Element";

	protected final ConcurrentSkipListSet<E> set;

	/**
	 * Constructs a new, empty set that orders its elements according to the
	 * {@linkplain Comparable natural ordering} of the set elements.
	 */
	public SparseSet() {
		this.set = new ConcurrentSkipListSet<>();
	}

	/**
	 * Constructs a new, empty set that orders its elements according to the specified
	 * comparator. If the given comparator is {@code null}, the {@linkplain Comparable
	 * natural ordering} of the set elements will be used.
	 *
	 * @param comp comparator used to order this set
	 */
	public SparseSet(Comparator<? super E> comp) {
		this.set = new ConcurrentSkipListSet<>(comp);
	}

	/**
	 * Constructs a new set containing the elements in the specified collection, that
	 * orders its elements according to their {@linkplain Comparable natural ordering}.
	 *
	 * @param elems elements that will comprise the new set
	 * @throws ClassCastException   if the elements in {@code c} are not
	 *                              {@link Comparable}, or are not mutually comparable
	 * @throws NullPointerException if the specified collection or any of its elements are
	 *                              null
	 */
	public SparseSet(Collection<? extends E> elems) {
		this.set = new ConcurrentSkipListSet<>(elems);
	}

	/**
	 * Constructs a new set containing the same elements and using the same ordering as
	 * the specified sorted set.
	 *
	 * @param elems sorted set whose elements will comprise the new set
	 * @throws NullPointerException if the specified sorted set or any of its elements are
	 *                              null
	 */
	public SparseSet(SortedSet<E> elems) {
		this.set = new ConcurrentSkipListSet<>(elems);
	}

	public SparseSet(ConcurrentSkipListSet<E> set) {
		this.set = set;
	}

	@Override
	public boolean add(E e) {
		return set.add(e);
	}

	@Override
	public boolean addAll(Collection<? extends E> c) {
		return set.addAll(c);
	}

	@Override
	public boolean remove(Object o) {
		return set.remove(o);
	}

	@Override
	public boolean contains(Object o) {
		return set.contains(o);
	}

	@Override
	public boolean containsAll(Collection<?> c) {
		return set.containsAll(c);
	}

	@Override
	public boolean retainAll(Collection<?> c) {
		return set.retainAll(c);
	}

	@Override
	public boolean removeAll(Collection<?> c) {
		return set.removeAll(c);
	}

	@Override
	public boolean removeIf(Predicate<? super E> filter) {
		return set.removeIf(filter);
	}

	@Override
	public E first() {
		return set.first();
	}

	@Override
	public E lower(E e) {
		return set.lower(e);
	}

	@Override
	public E floor(E e) {
		return set.floor(e);
	}

	@Override
	public E ceiling(E e) {
		return set.ceiling(e);
	}

	@Override
	public E higher(E e) {
		return set.higher(e);
	}

	@Override
	public E last() {
		return set.last();
	}

	@Override
	public E pollFirst() {
		return set.pollFirst();
	}

	@Override
	public E pollLast() {
		return set.pollLast();
	}

	/**
	 * Returns the lowest (first) element in this store.
	 *
	 * @return lowest element
	 */
	public E lowest() {
		return set.first();
	}

	/**
	 * Searches, starting with the given elem, for lower elements, relative to the given
	 * elem, located in the store that meet the criteria given by the given include
	 * predicate. If inclusive, the search starts, relative to the given elem, with the
	 * floor element; otherwise with the lower element.
	 * <p>
	 * {@code Lower}: the greatest element less than another, <em>i.e.</em>, immediately
	 * before.<br>
	 * {@code Floor}: the greatest element less than or equal to another, <em>i.e.</em>,
	 * existing at or immediatly before.
	 *
	 * @param elem      starting element
	 * @param inclusive {@code true} to include the starting element in the search
	 * @return select head elements
	 * @throws IllegalArgumentException if the given elem is {@code null}
	 */
	public List<E> lower(E elem, boolean inclusive) {
		return lower(elem, inclusive, null, null);
	}

	/**
	 * Searches, starting with the given elem, for lower elements, relative to the given
	 * elem, located in the store that meet the criteria given by the given include
	 * predicate. If inclusive, the search starts, relative to the given elem, with the
	 * floor element; otherwise with the lower element.
	 * <p>
	 * A {@code null} include predicate is equivalent to a 'match all' criteria. A
	 * {@code null} stop predicate disables early termination consideration.
	 * <p>
	 * {@code Lower}: the greatest element less than another, <em>i.e.</em>, immediately
	 * before.<br>
	 * {@code Floor}: the greatest element less than or equal to another, <em>i.e.</em>,
	 * existing at or immediatly before.
	 *
	 * @param elem      starting element
	 * @param inclusive {@code true} to include the starting element in the search
	 * @param include   inclusion filter criteria; may be {@code null}
	 * @return select head elements
	 * @throws IllegalArgumentException if the given elem is {@code null}
	 */
	public List<E> lower(E elem, boolean inclusive, Predicate<? super E> include) {
		return lower(elem, inclusive, include, null);
	}

	/**
	 * Searches, starting with the given elem, for lower elements, relative to the given
	 * elem, located in the store that meet the criteria given by the given include
	 * predicate. If inclusive, the search starts, relative to the given elem, with the
	 * floor element; otherwise with the lower element.
	 * <p>
	 * The search terminates early on the first element that meets the given stop
	 * criteria. An element that meets both the include and stop criteria is <em>not</em>
	 * included in the search results.
	 * <p>
	 * A {@code null} include predicate is equivalent to a 'match all' criteria. A
	 * {@code null} stop predicate disables early termination consideration.
	 * <p>
	 * {@code Lower}: the greatest element less than another, <em>i.e.</em>, immediately
	 * before.<br>
	 * {@code Floor}: the greatest element less than or equal to another, <em>i.e.</em>,
	 * existing at or immediatly before.
	 *
	 * @param elem      starting element
	 * @param inclusive {@code true} to include the starting element in the search
	 * @param include   inclusion filter criteria; may be {@code null}
	 * @param stop      search termination criteria; may be {@code null}
	 * @return select head elements
	 * @throws IllegalArgumentException if the given elem is {@code null}
	 */
	public List<E> lower(E elem, boolean inclusive, Predicate<? super E> include, Predicate<? super E> stop) {
		return lower(elem, inclusive, include, stop, true);
	}

	/**
	 * Searches, starting with the given elem, for lower elements, relative to the given
	 * elem, located in the store that meet the criteria given by the given include
	 * predicate. If inclusive, the search starts, relative to the given elem, with the
	 * floor element; otherwise with the lower element.
	 * <p>
	 * The search terminates early on the first element that meets the given stop
	 * criteria. An element that meets both the include and stop criteria is excluded from
	 * the search results conditioned on the given exclude value.
	 * <p>
	 * A {@code null} include predicate is equivalent to a 'match all' criteria. A
	 * {@code null} stop predicate disables early termination consideration.
	 * <p>
	 * {@code Lower}: the greatest element less than another, <em>i.e.</em>, immediately
	 * before.<br>
	 * {@code Floor}: the greatest element less than or equal to another, <em>i.e.</em>,
	 * existing at or immediatly before.
	 *
	 * @param elem      starting element
	 * @param inclusive {@code true} to include the starting element in the search
	 * @param include   inclusion filter criteria; may be {@code null}
	 * @param stop      search termination criteria; may be {@code null}
	 * @param exclude   {@code true} to exclude the stop element even if it matches the
	 *                  inclusion filter
	 * @return selected head elements
	 * @throws IllegalArgumentException if the given elem is {@code null}
	 */
	public List<E> lower(E elem, boolean inclusive, Predicate<? super E> include, Predicate<? super E> stop,
			boolean exclude) {
		chkArg(elem, ELEM);
		List<E> results = new UniqueList<>();
		for (E next = inclusive ? set.floor(elem) : set.lower(elem); //
				next != null; //
				next = set.lower(next)) {
			if (stop != null && exclude && stop.test(next)) break;
			if (include == null || include.test(next)) results.add(next);
			if (stop != null && !exclude && stop.test(next)) break;
		}
		return results;
	}

	/**
	 * Searches, starting with the given elem, for higher elements, relative to the given
	 * elem, located in the store. If inclusive, the search starts, relative to the given
	 * elem, with the ceiling element; otherwise with the higher element.
	 * <p>
	 * {@code Higher}: least element greater than another, <em>i.e.</em>, existing
	 * immediatly after<br>
	 * {@code Ceiling}: least element greater than or equal to another, <em>i.e.</em>,
	 * existing at or immedidatly after
	 *
	 * @param elem      starting element
	 * @param inclusive {@code true} to include the starting element in the search
	 * @return select tail elements
	 * @throws IllegalArgumentException if the given elem is {@code null}
	 */
	public List<E> higher(E elem, boolean inclusive) {
		return higher(elem, inclusive, null, null);
	}

	/**
	 * Searches, starting with the given elem, for higher elements, relative to the given
	 * elem, located in the store that meet the criteria given by the given include
	 * predicate. If inclusive, the search starts, relative to the given elem, with the
	 * ceiling element; otherwise with the higher element.
	 * <p>
	 * A {@code null} include predicate is equivalent to a 'match all' criteria. A
	 * {@code null} stop predicate disables early termination consideration.
	 * <p>
	 * {@code Higher}: least element greater than another, <em>i.e.</em>, existing
	 * immediatly after<br>
	 * {@code Ceiling}: least element greater than or equal to another, <em>i.e.</em>,
	 * existing at or immedidatly after
	 *
	 * @param elem      starting element
	 * @param inclusive {@code true} to include the starting element in the search
	 * @param include   inclusion filter criteria; may be {@code null}
	 * @return select tail elements
	 * @throws IllegalArgumentException if the given elem is {@code null}
	 */
	public List<E> higher(E elem, boolean inclusive, Predicate<? super E> include) {
		return higher(elem, inclusive, include, null);
	}

	/**
	 * Searches, starting with the given elem, for higher elements, relative to the given
	 * elem, located in the store that meet the criteria given by the given include
	 * predicate. If inclusive, the search starts, relative to the given elem, with the
	 * ceiling element; otherwise with the higher element.
	 * <p>
	 * The search terminates early on the first element that meets the given stop
	 * criteria. An element that meets both the include and stop criteria is <em>not</em>
	 * included in the search results.
	 * <p>
	 * A {@code null} include predicate is equivalent to a 'match all' criteria. A
	 * {@code null} stop predicate disables early termination consideration.
	 * <p>
	 * {@code Higher}: least element greater than another, <em>i.e.</em>, existing
	 * immediatly after<br>
	 * {@code Ceiling}: least element greater than or equal to another, <em>i.e.</em>,
	 * existing at or immedidatly after
	 *
	 * @param elem      starting element
	 * @param inclusive {@code true} to include the starting element in the search
	 * @param include   inclusion filter criteria; may be {@code null}
	 * @param stop      search termination criteria; may be {@code null}
	 * @return select tail elements
	 * @throws IllegalArgumentException if the given elem is {@code null}
	 */
	public List<E> higher(E elem, boolean inclusive, Predicate<? super E> include,
			Predicate<? super E> stop) {
		return higher(elem, inclusive, include, stop, true);
	}

	/**
	 * Searches, starting with the given elem, for higher elements, relative to the given
	 * elem, located in the store that meet the criteria given by the given include
	 * predicate. If inclusive, the search starts, relative to the given elem, with the
	 * ceiling element; otherwise with the higher element.
	 * <p>
	 * The search terminates early on the first element that meets the given stop
	 * criteria. An element that meets both the include and stop criteria is excluded from
	 * the search results dependent on the given exclude value.
	 * <p>
	 * A {@code null} include predicate is equivalent to a 'match all' criteria. A
	 * {@code null} stop predicate disables early termination consideration.
	 * <p>
	 * {@code Higher}: least element greater than another, <em>i.e.</em>, existing
	 * immediatly after<br>
	 * {@code Ceiling}: least element greater than or equal to another, <em>i.e.</em>,
	 * existing at or immedidatly after
	 *
	 * @param elem      starting element
	 * @param inclusive {@code true} to include the starting element in the search
	 * @param include   inclusion filter criteria; may be {@code null}
	 * @param stop      search termination criteria; may be {@code null}
	 * @param exclude   {@code true} to exclude the stop element even if it matches the
	 *                  inclusion filter
	 * @return selected tail elements
	 * @throws IllegalArgumentException if the given elem is {@code null}
	 */
	public List<E> higher(E elem, boolean inclusive, Predicate<? super E> include, Predicate<? super E> stop,
			boolean exclude) {
		chkArg(elem, ELEM);
		List<E> results = new UniqueList<>();
		for (E next = inclusive ? set.ceiling(elem) : set.higher(elem); //
				next != null; //
				next = set.higher(next)) {
			if (stop != null && exclude && stop.test(next)) break;
			if (include == null || include.test(next)) results.add(next);
			if (stop != null && !exclude && stop.test(next)) break;
		}
		return results;
	}

	/**
	 * Returns the highest (last) element in this store.
	 *
	 * @return highest element
	 */
	public E highest() {
		return set.last();
	}

	// /**
	// * Returns a {@link UniqueList} of selected store entries.
	// * <p>
	// * The resulting list is not backed by the store, so changes to the store are not
	// * reflected in the list, and vice-versa.
	// * <p>
	// * The order of the list will nominally reflect the order of the given elements. *
	// * <p>
	// * This is an exact element match operation.
	// *
	// * @param elements entry selection elements
	// * @return list of selected store entries
	// */
	// public UniqueList<Entry<K, V>> entries(Collection<? extends K> elements) {
	// chkArg(elements, KEYS);
	// return elements.stream() //
	// .filter(k -> k != null) //
	// .map(k -> Map.entry((K) k, store.get(k))) //
	// .collect(Collectors.toCollection(UniqueList::new));
	// }

	/**
	 * Sanity check for {@code null}. Throws an {@link IllegalArgumentException} on
	 * failure using the {@link #ERR_ARG} message and message qualifier.
	 *
	 * @param arg  argument to check
	 * @param qual failure message qualifier
	 */
	protected void chkArg(Object arg, String qual) {
		if (arg == null) throw IllegalArgsEx.of(ERR_ARG).formatted(qual);
	}

	/**
	 * Returns a {@link LinkedList} of the store values.
	 * <p>
	 * The resulting list is not backed by the store, so changes to the store are not
	 * reflected in the list, and vice-versa.
	 * <p>
	 * The order of the list will nominally reflect the order of entries in the store.
	 *
	 * @return list of store values
	 */
	public LinkedList<E> values() {
		return new LinkedList<>(set);
	}

	/**
	 * Returns an unmodifiable {@link NavigableSet} view of the elements contained in this
	 * store.
	 * <p>
	 * The set's iterator returns the elements in ascending order.
	 *
	 * @return a navigable set view of the elements in this store
	 */
	public NavigableSet<E> navigableKeySet() {
		return Collections.unmodifiableNavigableSet(set);
	}

	@Override
	public void forEach(Consumer<? super E> action) {
		set.forEach(action);
	}

	/**
	 * Returns a sequential {@code Stream} of the elements of this map as its source.
	 *
	 * @return a sequential {@code Stream} over the elements of this map
	 */
	@Override
	public Stream<E> stream() {
		return set.stream();
	}

	@Override
	public Stream<E> parallelStream() {
		return set.parallelStream();
	}

	@Override
	public SortedSet<E> subSet(E fromElement, E toElement) {
		return set.subSet(fromElement, toElement);
	}

	@Override
	public NavigableSet<E> subSet(E fromElement, boolean fromInclusive, E toElement, boolean toInclusive) {
		return set.subSet(fromElement, fromInclusive, toElement, toInclusive);
	}

	@Override
	public SortedSet<E> headSet(E toElement) {
		return set.headSet(toElement);
	}

	/**
	 * Returns a navigable head map view of the entries in this store relative to the
	 * given elem.
	 *
	 * @param elem      starting element
	 * @param inclusive {@code true} to include the starting element in the map
	 * @return navigable head map view
	 * @throws IllegalArgumentException if the given elem is {@code null}
	 */
	@Override
	public NavigableSet<E> headSet(E elem, boolean inclusive) {
		chkArg(elem, ELEM);
		return set.headSet(elem, inclusive);
	}

	@Override
	public SortedSet<E> tailSet(E fromElement) {
		return set.tailSet(fromElement);
	}

	/**
	 * Returns a navigable tail map view of the entries in this store relative to the
	 * given elem.
	 *
	 * @param elem      starting element
	 * @param inclusive {@code true} to include the starting element in the map
	 * @return navigable tail map view
	 * @throws IllegalArgumentException if the given elem is {@code null}
	 */
	@Override
	public NavigableSet<E> tailSet(E elem, boolean inclusive) {
		chkArg(elem, ELEM);
		return set.tailSet(elem, inclusive);
	}

	// /**
	// * Returns a {@link UniqueList} of selected store entries.
	// * <p>
	// * The resulting list is not backed by the store, so changes to the store are not
	// * reflected in the list, and vice-versa.
	// * <p>
	// * The order of the list will nominally reflect the order of the given elements. *
	// * <p>
	// * This is an exact element match operation.
	// *
	// * @param elements entry selection elements
	// * @return list of selected store entries
	// */
	// public UniqueList<Entry<K, V>> entries(Collection<? extends K> elements) {
	// chkArg(elements, KEYS);
	// return elements.stream() //
	// .filter(k -> k != null) //
	// .map(k -> Map.entry((K) k, store.get(k))) //
	// .collect(Collectors.toCollection(UniqueList::new));
	// }

	/**
	 * Returns the number of elements in this store.
	 *
	 * @return number of elements
	 */
	@Override
	public int size() {
		return set.size();
	}

	/**
	 * Returns {@code true} if this store contains no elements.
	 *
	 * @return {@code true} if this store contains no elements
	 */
	@Override
	public boolean isEmpty() {
		return set.isEmpty();
	}

	/**
	 * Removes all mappings.
	 */
	@Override
	public void clear() {
		set.clear();
	}

	@Override
	public Iterator<E> iterator() {
		return set.iterator();
	}

	@Override
	public NavigableSet<E> descendingSet() {
		return set.descendingSet();
	}

	@Override
	public Iterator<E> descendingIterator() {
		return set.descendingIterator();
	}

	/**
	 * Return the comparator used to maintain order in this store, or {@code null} if the
	 * store is using the natural ordering the elements.
	 *
	 * @return the store comparator or {@code null}
	 */
	@Override
	public Comparator<? super E> comparator() {
		return set.comparator();
	}

	@Override
	public int hashCode() {
		return Objects.hash(set);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) return true;
		if (!(obj instanceof SparseSet)) return false;
		SparseSet<?> other = (SparseSet<?>) obj;
		return Objects.equals(set, other.set);
	}

	@Override
	public String toString() {
		return set.toString();
	}
}
