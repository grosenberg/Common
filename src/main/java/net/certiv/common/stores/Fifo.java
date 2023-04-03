package net.certiv.common.stores;

import java.util.Iterator;
import java.util.LinkedList;

import net.certiv.common.check.Assert;

/**
 * Fixed capacity list-queue. All operations are relative to the newest added element.
 */
@Deprecated
public class Fifo<E> implements Iterable<E> {

	private final LinkedList<E> buf = new LinkedList<>();
	private int capacity;

	public Fifo(int capacity) {
		Assert.isTrue(capacity > 0);
		this.capacity = capacity;
	}

	/**
	 * Puts a new element into the queue. The oldest element exceeding capacity is
	 * evicted.
	 */
	public void push(E e) {
		if (buf.size() >= capacity) buf.removeFirst();
		buf.push(e); // addLast
	}

	/** Polls the newest added element. */
	public E poll() {
		return buf.pollLast();
	}

	/** Peeks at the newest added element. */
	public E peek() {
		return buf.peekLast();
	}

	/** Newest to oldest element ordering. */
	@Override
	public Iterator<E> iterator() {
		return buf.descendingIterator();
	}

	/** Returns the current size of the queue. */
	public int size() {
		return buf.size();
	}
}
