package net.certiv.common.stores;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Spliterator;
import java.util.stream.Stream;

/**
 * This class is a thread safe list that is designed for storing lists of unique
 * values.
 */
public class UniquesList<E> implements Iterable<E> {

	/** Mode constant indicating that values are compared using {@code #equals}. */
	public static final int EQUALITY = -10;

	/** Mode constant indicating that values are compared using {@code ==}. */
	public static final int IDENTITY = -11;

	/** Indicates the comparison mode used */
	private final boolean identMode;

	/** List of unique, non-null values. */
	private final List<E> uniques;

	/** Creates a default list where values are compared using equality. */
	public UniquesList() {
		this(EQUALITY);
	}

	/**
	 * Creates a list where values are compared using the provided comparison mode.
	 */
	public UniquesList(int mode) {
		this(mode, 0);
	}

	/**
	 * Creates a list where values are compared using the provided comparison mode.
	 */
	public UniquesList(int mode, int initalCapacity) {
		if (mode != EQUALITY && mode != IDENTITY) throw new IllegalArgumentException();
		this.identMode = mode == IDENTITY;
		if (initalCapacity < 0) initalCapacity = 0;
		uniques = new ArrayList<>(initalCapacity);
	}

	/**
	 * Adds a unique value to this list. Synchronized to protect against multiple
	 * threads adding or removing values concurrently. Does not block concurrent
	 * readers.
	 */
	public synchronized boolean add(E value) {
		if (value == null) throw new IllegalArgumentException();

		if (contains(value)) return false;
		return uniques.add(value);
	}

	/**
	 * Removes a value from this list. Synchronized to protect against multiple
	 * threads adding or removing values concurrently. Does not block concurrent
	 * readers.
	 */
	public synchronized E remove(E value) {
		if (value == null) throw new IllegalArgumentException();

		int dot = uniques.indexOf(value);
		if (dot == -1) return null;
		return uniques.remove(dot);
	}

	public synchronized List<E> get() {
		return new ArrayList<>(uniques);
	}

	public synchronized E get(E value) {
		int dot = uniques.indexOf(value);
		if (dot == -1) return null;
		return uniques.get(dot);
	}

	public synchronized E get(int idx) {
		return uniques.get(idx);
	}

	/** Removes all values from this list. */
	public synchronized void clear() {
		uniques.clear();
	}

	public boolean contains(E value) {
		for (E unique : uniques) {
			if (identMode ? value == unique : value.equals(unique)) return true;
		}
		return false;
	}

	public int indexOf(E value) {
		for (int idx = 0, len = uniques.size(); idx < len; idx++) {
			E unique = uniques.get(idx);
			if (identMode ? value == unique : value.equals(unique)) return idx;
		}
		return -1;
	}

	/** Returns whether this list is empty. */
	public boolean isEmpty() {
		return uniques.isEmpty();
	}

	/** Returns the number of unique values. */
	public int size() {
		return uniques.size();
	}

	/** Returns an iterator over all the unique values. */
	@Override
	public Iterator<E> iterator() {
		return uniques.iterator();
	}

	/** Returns a Spliterator covering the unique values. */
	@Override
	public Spliterator<E> spliterator() {
		return uniques.spliterator();
	}

	/** Returns a sequential {@code Stream} over the unique values. */
	public Stream<E> stream() {
		return uniques.stream();
	}

	/** Returns a parallel {@code Stream} over the unique values. */
	public Stream<E> parallelStream() {
		return uniques.parallelStream();
	}

	@Override
	public String toString() {
		return uniques.toString();
	}
}
