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
		/** Edge in direction; in through begin node. */
		IN,
		/** Edge out direction; out through end node. */
		OUT;
	}

	private static final Counter Factor = new Counter();
	public final long _eid;

	private N beg;
	private N end;

	protected Edge(N beg, N end) {
		Assert.notNull(beg, end);
		this._eid = Factor.getAndIncrement();
		this.beg = beg;
		this.end = end;
	}

	protected Edge(N beg, N end, Map<Object, Object> props) {
		this(beg, end);
		if (props != null) putProperties(props);
	}

	public String name() {
		return String.valueOf(_eid);
	}

	/** Return the edge begin node. */
	public N beg() {
		return beg;
	}

	/** Return the edge end node. */
	public N end() {
		return end;
	}

	/**
	 * Set the edge begin node.
	 *
	 * @param beg the new edge begin node
	 * @return the prior edge begin node
	 */
	public N beg(N beg) {
		N prior = this.beg;
		this.beg = beg;
		return prior;
	}

	/**
	 * Set the edge end node.
	 *
	 * @param end the new edge end node
	 * @return the prior edge end node
	 */
	public N end(N end) {
		N prior = this.end;
		this.end = end;
		return prior;
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
	 * Internal use only. Removes this edge from the internal lists of begin and end
	 * node connections. Callthrough from {@link Graph#remove(edge)}.
	 *
	 * @return {@code true} if this edge is fully removed.
	 */
	@SuppressWarnings("unchecked")
	boolean remove() {
		boolean rmvd = beg.remove((E) this, Sense.OUT);
		rmvd |= end.remove((E) this, Sense.IN);
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
		return Objects.hash(beg, end);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) return true;
		if (!(obj instanceof Edge)) return false;
		Edge<?, ?> other = (Edge<?, ?>) obj;
		return Objects.equals(beg, other.beg) && Objects.equals(end, other.end);
	}

	@Override
	public String toString() {
		return String.format("%s -> %s", beg, end);
	}
}
