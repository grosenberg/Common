package net.certiv.common.stores;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;

/** A {@code HashSet} that generally implements the {@code List} contract. */
public class ListSet<E> {

	private final LinkedHashMap<E, E> store = new LinkedHashMap<>();

	/** A {@code HashSet} that generally implements the {@code List} contract. */
	public ListSet() {
		super();
	}

	public E get(E value) {
		return store.get(value);
	}

	public E get(int index) {
		return values().get(index);
	}

	public E add(E value) {
		return store.put(value, value);
	}

	public int indexOf(E o) {
		return values().indexOf(o);
	}

	public int lastIndexOf(E o) {
		return values().lastIndexOf(o);
	}

	public E remove(E value) {
		return store.remove(value);
	}

	public E remove(int index) {
		return remove(get(index));
	}

	public boolean removeAll(Collection<?> c) {
		return store.keySet().removeAll(c);
	}

	public boolean contains(E key) {
		return store.containsKey(key);
	}

	public boolean containsAll(Collection<?> c) {
		return store.keySet().containsAll(c);
	}

	public List<E> values() {
		return new ArrayList<>(store.values());
	}

	public Iterator<E> iterator() {
		return store.keySet().iterator();
	}

	public Object[] toArray() {
		return store.keySet().toArray();
	}

	public <T> T[] toArray(T[] a) {
		return store.keySet().toArray(a);
	}

	public int size() {
		return store.size();
	}

	public boolean isEmpty() {
		return store.isEmpty();
	}

	public void clear() {
		store.clear();
	}

	@Override
	public boolean equals(Object o) {
		return store.equals(o);
	}

	@Override
	public int hashCode() {
		return store.hashCode();
	}

	@Override
	public String toString() {
		return store.toString();
	}
}
