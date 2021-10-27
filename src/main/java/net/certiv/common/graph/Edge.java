package net.certiv.common.graph;

import java.util.Map;
import java.util.Objects;

import net.certiv.common.dot.Dictionary.ON;
import net.certiv.common.dot.DotStyle;
import net.certiv.common.stores.Counter;
import net.certiv.common.util.Assert;

/**
 * An edge connects two nodes and form a self-loop where both nodes are the
 * same. Edges are unique, permiting multiple distinct edges to connect the same
 * two nodes. An edge can only exist between two existing nodes: no dangling
 * edges permitted.
 */
public abstract class Edge<N extends Node<N, E>, E extends Edge<N, E>> extends Props {

	/** Sense of direction. */
	public enum Sense {
		IN, OUT;
	}

	private static final Counter Factor = new Counter();
	public final long _eid;

	protected final N beg;
	protected final N end;

	protected Edge(N beg, N end) {
		Assert.notNull(beg, end);
		this._eid = Factor.getAndIncrement();
		this.beg = beg;
		this.end = end;
	}

	protected Edge(N beg, N end, Map<Object, Object> props) {
		this(beg, end);
		putProperties(props);
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
	boolean remove() {
		boolean rmvd = beg.remove((E) this, Sense.OUT);
		rmvd &= end.remove((E) this, Sense.IN);
		return rmvd;
	}

	/**
	 * Returns the {@code DotStyle} store for the {@code Node} or {@code Edge}
	 * containing this properties store. Creates and adds an {@code ON#EDGES}
	 * default category {@code DotStyle} store, if a store does not exist.
	 *
	 * @return the dot style store
	 */
	public DotStyle getDotStyle() {
		return getDotStyle(ON.EDGES);
	}

	/**
	 * Defines a custom style for this edge. The default implementation does
	 * nothing.
	 */
	public DotStyle defineStyle() {
		return getDotStyle();
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
