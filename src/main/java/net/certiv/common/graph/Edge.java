package net.certiv.common.graph;

import java.util.LinkedHashMap;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicLong;

import net.certiv.common.util.Assert;

/**
 * An edge connects two nodes and form a self-loop where both nodes are the
 * same. Edges are unique, permiting multiple distinct edges to connect the same
 * two nodes. An edge can only exist between two existing nodes: no dangling
 * edges permitted.
 */
public abstract class Edge<N extends Node<N, E>, E extends Edge<N, E>> {

	/** Sense of direction. */
	public enum Sense {
		IN, OUT;
	}

	private static final AtomicLong Factor = new AtomicLong();
	public final long _eid;

	protected final N beg;
	protected final N end;

	private LinkedHashMap<Object, Object> props;

	protected Edge(N beg, N end) {
		Assert.notNull(beg, end);
		this._eid = Factor.getAndIncrement();
		this.beg = beg;
		this.end = end;
	}

	public String name() {
		return String.valueOf(_eid);
	}

	public N beg() {
		return beg;
	}

	public N end() {
		return end;
	}

	/** Returns the node at the 'other' end of this edge. */
	public N other(N node) {
		return beg.equals(node) ? end : beg;
	}

	/** Returns {@code true} if this edge connects to the given node. */
	public boolean connectsTo(N node) {
		return node.equals(beg) || node.equals(end);
	}

	public boolean selfLoop() {
		return beg.equals(end);
	}

	/**
	 * Internal use only. Callthrough from {@link Graph#remove(edge)}.
	 *
	 * @return {@code true} if this edge is fully removed.
	 */
	@SuppressWarnings("unchecked")
	public boolean remove() {
		boolean rmvd = beg.remove((E) this, Sense.OUT);
		rmvd &= end.remove((E) this, Sense.IN);
		return rmvd;
	}

	protected void dispose() {
		remove();
	}

	/**
	 * Adds an arbitrary key/value "property" to this edge. If value is
	 * {@code null}, the property will be removed.
	 *
	 * @param key the property key
	 * @param value the new property value
	 * @return the previous property value associated with key, or {@code null} if
	 *             there was no mapping for the key
	 */
	public final Object putProperty(Object key, Object value) {
		if (props == null) {
			props = new LinkedHashMap<>();
		}
		if (value == null) return props.remove(key);
		return props.put(key, value);
	}

	/**
	 * Returns the value of the property with the specified key. Only properties
	 * added with putProperty will return a non-null value.
	 *
	 * @param key the property key
	 * @return the property value associated with key, or {@code null} if there was
	 *             no mapping for the key
	 */
	public final Object getProperty(Object key) {
		if (props == null) return null;
		return props.get(key);
	}

	/**
	 * Returns {@code true} if a property value is associated with the given key.
	 *
	 * @param key the property key
	 * @return {@code true} if a property value is associated with key
	 */
	public final boolean hasProperty(Object key) {
		if (props == null) return false;
		return props.containsKey(key);
	}

	@Override
	public int hashCode() {
		return Long.hashCode(_eid);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) return true;
		if (!(obj instanceof Edge)) return false;
		Edge<?, ?> other = (Edge<?, ?>) obj;
		return Objects.equals(_eid, other._eid);
	}

	@Override
	public String toString() {
		return String.format("%s -> %s", beg, end);
	}
}
