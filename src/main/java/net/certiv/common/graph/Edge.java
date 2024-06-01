package net.certiv.common.graph;

import java.util.Map;

import net.certiv.common.check.Assert;
import net.certiv.common.dot.Dictionary.ON;
import net.certiv.common.dot.DotStyle;
import net.certiv.common.id.IUId;
import net.certiv.common.stores.Counter;
import net.certiv.common.stores.props.Props;

/**
 * An edge connects two nodes. Single edge cycles -- where both nodes are the same -- are
 * permitted. Edges are unique, permiting multiple distinct edges to connect the same two
 * nodes.
 * <p>
 * An edge can only exist between two existing nodes: no dangling edges permitted.
 */
public abstract class Edge<I extends IUId, N extends Node<I, N, E>, E extends Edge<I, N, E>> extends Props
		implements Comparable<E> {

	private static final String ERR_INVALID = "Invalid edge %s";

	/** Sense of direction. */
	public enum Sense {
		/** Edge in direction; in through begin node. */
		IN,
		/** Edge out direction; out through end node. */
		OUT,
		/** Bidirection or both. */
		BOTH;
	}

	static final Counter CTR = new Counter();

	/** Unique numerical edge identifier */
	public final long _eid;

	private N beg;
	private N end;

	protected Edge(N beg, N end) {
		Assert.notNull(beg, end);
		this._eid = CTR.getAndIncrement();
		this.beg = beg;
		this.end = end;
	}

	protected Edge(N beg, N end, Map<Object, Object> props) {
		this(beg, end);
		putAll(props);
	}

	public String name() {
		return String.valueOf(_eid);
	}

	public String displayName() {
		return name();
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
	public N setBeg(N beg) {
		Assert.notNull(beg);
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
	public N setEnd(N end) {
		Assert.notNull(end);
		N prior = this.end;
		this.end = end;
		return prior;
	}

	/**
	 * Checks whether the edge endpoint nodes are {@code null}. A valid edge may exist
	 * disconnected from the graph, <i>i.e.</i> pending reconnection.
	 *
	 * @return {@code true} if this edge is valid
	 */
	public boolean valid() {
		return beg != null && end != null;
	}

	/**
	 * Checks whether the edge is connected to the edge sets of its endpoint nodes.
	 *
	 * @return {@code true} if this edge is connected
	 */
	public boolean isConnected() {
		return beg().edges(Sense.OUT).contains(this) && end().edges(Sense.IN).contains(this);
	}

	/** Returns the node at the 'other' end of this edge. */
	public N other(N node) {
		Assert.isTrue(valid(), ERR_INVALID, this);
		return beg.equals(node) ? end : beg;
	}

	/** Returns the node of the given sense direction. */
	public N other(Sense dir) {
		Assert.isTrue(dir != Sense.BOTH);
		if (dir == Sense.IN) return beg;
		return end;
	}

	/** Returns {@code true} if this edge is directly connected to the given node. */
	public boolean connectsTo(N node) {
		if (node == null) return false;
		return node.equals(beg) || node.equals(end);
	}

	/**
	 * Returns {@code true} if this edge directly connects between the given nodes in
	 * either orientation.
	 */
	public boolean between(N src, N dst) {
		if (!valid()) return false;
		return (beg.equals(src) && end.equals(dst)) || (beg.equals(dst) && end.equals(src));
	}

	/**
	 * Returns {@code true} if this edge is a self-cyclic edge, defined where the begin
	 * and end nodes are the same.
	 */
	public boolean cyclic() {
		return valid() ? beg.equals(end) : false;
	}

	/**
	 * Returns the edge weight. Default weight is {@code 1.0}.
	 *
	 * @return the edge weight
	 */
	public double weight() {
		return 1.0;
	}

	/**
	 * Removes this edge from the graph by disconnecting from the internal edge sets of
	 * the edge begin and end node connections. Specify {@code delete=true} to clear the
	 * edge. Otherwise the edge remains intact and potentially available for reuse.
	 * <p>
	 * Internal use only. Callthrough from {@code Graph#remove(edge)}.
	 *
	 * <pre>
	 * before:
	 * 	A -> Beg -> End -> D
	 * after
	 * 	A -> Beg    End -> D
	 * </pre>
	 *
	 * @param clear {@code true} to clear the edge properties
	 * @return {@code true} if this edge is fully removed
	 */
	boolean remove(boolean clear) {
		Assert.isTrue(valid(), ERR_INVALID, this);

		@SuppressWarnings("unchecked")
		E edge = (E) this;
		boolean rmvd = beg.remove(edge, Sense.OUT);
		rmvd |= end.remove(edge, Sense.IN);
		if (clear) {
			beg = end = null;
			clear();
		}
		return rmvd;
	}

	/**
	 * Returns the existing {@code DotStyle}. Creates and adds an {@code ON#EDGES} default
	 * category {@code DotStyle} store, if a store does not exist.
	 *
	 * @return the dot style store
	 */
	public DotStyle getDotStyle() {
		return Dot.getStyles(this, ON.EDGES);
	}

	/**
	 * Defines a custom style for this edge. The default implementation does nothing.
	 * <p>
	 * Override to define a custom configured style.
	 */
	protected DotStyle defineStyle() {
		return getDotStyle();
	}

	@Override
	public int compareTo(E o) {
		if (beg()._nid < o.beg()._nid) return -1;
		if (beg()._nid > o.beg()._nid) return 1;
		if (end()._nid < o.end()._nid) return -1;
		if (end()._nid > o.end()._nid) return 1;
		if (_eid < o._eid) return -1;
		if (_eid > o._eid) return 1;
		return 0;
	}

	@Override
	public int hashCode() {
		return Long.hashCode(_eid);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) return true;
		if (!(obj instanceof Edge)) return false;
		Edge<?, ?, ?> other = (Edge<?, ?, ?>) obj;
		return _eid == other._eid;
	}

	@Override
	public String toString() {
		return String.format("%s -> %s", beg, end);
	}
}
