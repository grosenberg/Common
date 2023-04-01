package net.certiv.common.graph;

import java.util.Map;
import java.util.Objects;

import net.certiv.common.dot.Dictionary.ON;
import net.certiv.common.dot.DotStyle;
import net.certiv.common.stores.Counter;
import net.certiv.common.util.Assert;

/**
 * An edge connects two nodes. Single edge cycles -- where both nodes are the same -- are
 * permitted. Edges are unique, permiting multiple distinct edges to connect the same two
 * nodes.
 * <p>
 * An edge can only exist between two existing nodes: no dangling edges permitted.
 */
public abstract class Edge<N extends Node<N, E>, E extends Edge<N, E>> extends Props {

	// private static final GraphException ERR_REMOVE = GraphException.of(Test.NOT_NULL,
	// "Redundant remove");

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

	/** Returns the node at the 'other' end of this edge. */
	public N other(N node) {
		return beg.equals(node) ? end : beg;
	}

	/** Returns the node of the given sense direction. */
	public N other(Sense dir) {
		Assert.isTrue(dir != Sense.BOTH);
		if (dir == Sense.IN) return beg;
		return end;
	}

	/** Returns {@code true} if this edge directly connects to the given node. */
	public boolean connectsTo(N node) {
		return node.equals(beg) || node.equals(end);
	}

	/**
	 * Returns {@code true} if this edge is a self-cyclic edge, defined where the begin
	 * and end nodes are the same.
	 */
	public boolean cyclic() {
		return beg.equals(end);
	}

	/**
	 * Removes this edge from the graph by disconnecting from the internal edge sets of
	 * the edge begin and end node connections. Specify {@code delete==true} to clear the
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
	 * @param delete {@code true} to clear the edge
	 * @return {@code true} if this edge is fully removed.
	 */
	@SuppressWarnings("unchecked")
	boolean remove(boolean delete) {
		boolean rmvd = beg != null && beg.remove((E) this, Sense.OUT);
		rmvd |= end != null && end.remove((E) this, Sense.IN);
		if (delete) {
			beg = end = null;
			clear();
		}
		return rmvd;
	}

	/**
	 * Returns the {@code DotStyle} store for the {@code Node} or {@code Edge} containing
	 * this properties store. Creates and adds an {@code ON#EDGES} default category
	 * {@code DotStyle} store, if a store does not exist.
	 *
	 * @return the dot style store
	 */
	public DotStyle getDotStyle() {
		return getDotStyle(ON.EDGES);
	}

	/**
	 * Defines a custom style for this edge. The default implementation does nothing.
	 * <p>
	 * Override to define a custom configured style.
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
		return String.format("%s -{%s}-> %s", beg, _eid, end);
	}
}
