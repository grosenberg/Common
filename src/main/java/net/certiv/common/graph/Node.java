package net.certiv.common.graph;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.function.Predicate;

import net.certiv.common.dot.Dictionary.ON;
import net.certiv.common.dot.DotStyle;
import net.certiv.common.graph.Edge.Sense;
import net.certiv.common.graph.Walker.NodeVisitor;
import net.certiv.common.stores.Counter;
import net.certiv.common.stores.LinkedHashList;
import net.certiv.common.stores.UniqueDeque;
import net.certiv.common.util.Assert;
import net.certiv.common.util.Strings;

public abstract class Node<N extends Node<N, E>, E extends Edge<N, E>> extends Props {

	public static final String NODE_NAME = "NodeName";

	static final Counter CTR = new Counter();

	/** Unique numerical node identifier */
	public final long _nid;

	/** Set of inbound edges */
	private final EdgeSet<N, E> in = new EdgeSet<>(Sense.IN);
	/** Set of outbound edges */
	private final EdgeSet<N, E> out = new EdgeSet<>(Sense.OUT);

	protected Node() {
		super();
		_nid = CTR.getAndIncrement();
		put(NODE_NAME, String.valueOf(_nid));
	}

	protected Node(Map<Object, Object> props) {
		this();
		putAll(props);
	}

	public String name() {
		return (String) get(NODE_NAME, String.valueOf(_nid));
	}

	public String uniqueName() {
		String nid = String.valueOf(_nid);
		if (name().equals(nid)) return nid;
		return String.format("%s (%s)", name(), nid);
	}

	/**
	 * Internal use only. Called from {@link Edge()} to add the given edge in the given
	 * direction.
	 *
	 * @return the distal node.
	 */
	N add(E edge, Sense dir) {
		Assert.isTrue(dir != Sense.BOTH);
		@SuppressWarnings("unchecked")
		N distal = edge.other((N) this);

		if (dir == Sense.IN) {
			in.add(edge);
		} else {
			out.add(edge);
		}
		return distal;
	}

	/**
	 * Removes the given edge connected to this node in the given direction. The edge
	 * remains otherwise intact.
	 * <p>
	 * Internal use only. Callthrough from {@link Edge#remove(boolean)}.
	 *
	 * @return {@code true} if the edge is removed from the node.
	 */
	boolean remove(E edge, Sense dir) {
		Assert.isTrue(dir != Sense.BOTH);

		boolean ok;
		if (dir == Sense.IN) {
			ok = in.remove(edge);
		} else {
			ok = out.remove(edge);
		}
		return ok;
	}

	/**
	 * Clears the edge sets and property store.
	 * <p>
	 * Internal use only.
	 */
	@Override
	void clear() {
		in.clear();
		out.clear();
		super.clear();
	}

	/**
	 * Return {@code true} if this node is adjacent the given node.
	 * <p>
	 * Adjacency between nodes is defined as being directly connected by a single
	 * non-cyclic edge.
	 *
	 * @param node a target distal node
	 * @return {@code true} if the given node is directly connected by an edge
	 */
	public boolean isAdjacent(N node) {
		if (this.equals(node)) return false;
		return in.isAdjacent(node) || out.isAdjacent(node);
	}

	/** Returns the full set of inbound and outbound adjacent nodes. */
	public UniqueDeque<N> adjacent() {
		return adjacent(Sense.BOTH);
	}

	/**
	 * Returns the set of all immediately adjacent nodes connected with the given sense.
	 * Excludes {@code this} node.
	 *
	 * @param dir the adjacency direction criteria
	 * @return set of adjacent nodes in the given direction, excluding {@code this} node
	 */
	public UniqueDeque<N> adjacent(Sense dir) {
		return adjacent(dir, n -> n._nid != _nid);
	}

	/**
	 * Returns the immediately adjacent nodes, connected with the given sense, that meet
	 * the filter criteria. If {@code filter} is {@code null}, include all adjacent nodes.
	 *
	 * @param dir    the adjacency direction criteria
	 * @param filter the distal node qualification criteria
	 * @return set of adjacent nodes meeting the given criteria
	 */
	public UniqueDeque<N> adjacent(Sense dir, Predicate<? super N> filter) {
		switch (dir) {
			case IN:
				return in.adjacent(filter);
			case OUT:
				return out.adjacent(filter);
			case BOTH:
			default:
				UniqueDeque<N> results = new UniqueDeque<>();
				results.addAll(in.adjacent(filter));
				results.addAll(out.adjacent(filter));
				return results.unmodifiable();
		}
	}

	/**
	 * Return {@code true} if this node is an ancestor of the given node. Performs a depth
	 * first search.
	 */
	@SuppressWarnings("unchecked")
	public boolean ancestorOf(N node) {
		if (this.equals(node)) return true;
		Set<N> visited = new HashSet<>();
		visited.add((N) this);
		return ancestorOf(visited, node);
	}

	/** Internal recursion use only. */
	boolean ancestorOf(Set<N> visited, N node) {
		for (N child : adjacent(Sense.OUT)) {
			if (!visited.contains(child)) {
				if (child.equals(node)) return true;
				visited.add(child);
				if (child.ancestorOf(visited, node)) return true;
			}
		}
		return false;
	}

	/**
	 * Returns the set of outbound edges that connect with the given node.
	 *
	 * @param node a distal node
	 * @return set of connecting edges
	 */
	public UniqueDeque<E> to(N end) {
		return edges(Sense.OUT, e -> e.end()._nid == end._nid);
	}

	/**
	 * Returns the set of inbound edges that connect with the given node.
	 *
	 * @param node a distal node
	 * @return set of connecting edges
	 */
	public UniqueDeque<E> from(N beg) {
		return edges(Sense.IN, e -> e.beg()._nid == beg._nid);
	}

	/**
	 * Returns {@code true} if the inbound edge count, excluding self-cyclic edges, is
	 * zero.
	 *
	 * @return {@code true} if this node is a root node
	 */
	public boolean isRoot() {
		return !hasEdges(Sense.IN, false);
	}

	/**
	 * Returns {@code true} if the edge count, conditionally including self-cyclic edges,
	 * is non-zero.
	 *
	 * @param cycles {@code true} to include self-cyclic edges
	 * @return {@code true} on a non-zero edge count
	 */
	public boolean hasEdges(Sense dir, boolean cycles) {
		return size(dir, cycles) != 0;
	}

	/**
	 * Returns the edge count, including self-cyclic edges (each counted once).
	 *
	 * @return the total count of unique connected edges
	 */
	public int size() {
		return size(Sense.BOTH, true);
	}

	/**
	 * Return the count of all immediately connected edges, constrained by the given
	 * direction and whether to include single-edge cycles.
	 *
	 * @param dir    the connected edge direction criteria
	 * @param cyclic {@code true} to include single-edge cycles, otherwise exclude
	 * @return count of the connected edges, constrained by the given criteria
	 */
	public int size(Sense dir, boolean cyclic) {
		return size(dir, !cyclic ? e -> !e.cyclic() : null);
	}

	// /**
	// * Return the count of all immediately connected edges, subject to the filter
	// * criteria.
	// *
	// * @param filter the connected edge qualification criteria
	// */
	// public int size(Predicate<? super E> filter) {
	// return size(Sense.BOTH, filter);
	// }

	/**
	 * Return the count of immediately connected edges of the given sense. If {@code dir}
	 * is {@code null}, include both in and out directions. Include self loop edges if
	 * {@code selfs} is {@code true}.
	 */
	public int size(Sense dir, Predicate<? super E> filter) {
		return edges(dir, filter).size();
	}

	/** Returns the set of connected edges. */
	public UniqueDeque<E> edges() {
		return edges(Sense.BOTH, null);
	}

	/**
	 * Returns the set of connected edges, constrained by the given direction. Excludes
	 * single-edge cycles.
	 *
	 * @param dir the connected edge direction criteria
	 * @return set of connected edges of the given direction and excluding single-edge
	 *         cycles
	 */
	public UniqueDeque<E> edges(Sense dir) {
		return edges(dir, false);
	}

	/**
	 * Returns the set of connected edges, constrained by the given direction and whether
	 * to include single-edge cycles.
	 *
	 * @param dir    the connected edge direction criteria
	 * @param cyclic {@code true} to include single-edge cycles, otherwise exclude
	 * @return set of connected edges meeting the given criteria
	 */
	public UniqueDeque<E> edges(Sense dir, boolean cyclic) {
		return edges(dir, !cyclic ? e -> !e.cyclic() : null);
	}

	/**
	 * Returns the connected edges, constrained by the given direction, that meet the
	 * given filter criteria. If {@code filter} is {@code null}, include all adjacent
	 * edges of the given direction.
	 *
	 * @param dir    the connected edge direction criteria
	 * @param filter the connected edge qualification criteria
	 * @return set of connected edges meeting the given criteria
	 */
	public UniqueDeque<E> edges(Sense dir, Predicate<? super E> filter) {
		switch (dir) {
			case IN:
				return in.edges(filter);
			case OUT:
				return out.edges(filter);
			case BOTH:
			default:
				UniqueDeque<E> results = new UniqueDeque<>();
				results.addAll(in.edges(filter));
				results.addAll(out.edges(filter));
				return results;
		}
	}

	/** Walker callback. */
	protected boolean enter(Sense dir, LinkedHashList<N, N> visited, NodeVisitor<N> listener, N node) {
		return true;
	}

	/** Walker callback. */
	protected boolean exit(Sense dir, LinkedHashList<N, N> visited, NodeVisitor<N> listener, N node) {
		return true;
	}

	/**
	 * Returns the {@code DotStyle} store for the {@code Node} or {@code Edge} containing
	 * this properties store. Creates and adds an {@code ON#NODES} default category
	 * {@code DotStyle} store, if a store does not exist.
	 *
	 * @return the dot style store
	 */
	public DotStyle getDotStyle() {
		return getDotStyle(ON.NODES);
	}

	/**
	 * Defines a custom style for this node. The default implementation does nothing.
	 * <p>
	 * Override to define a custom configured style.
	 */
	public DotStyle defineStyle() {
		return getDotStyle();
	}

	@Override
	public int hashCode() {
		return Long.hashCode(_nid);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) return true;
		if (!(obj instanceof Node)) return false;
		Node<?, ?> other = (Node<?, ?>) obj;
		return _nid == other._nid;
	}

	@Override
	public String toString() {
		String root = isRoot() ? "root:" : Strings.EMPTY;
		return String.format("%s<%s%s>", name(), root, _nid);
	}
}
