package net.certiv.common.graph;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.function.Predicate;

import net.certiv.common.annotations.VisibleForTesting;
import net.certiv.common.check.Assert;
import net.certiv.common.dot.Dictionary.ON;
import net.certiv.common.dot.DotStyle;
import net.certiv.common.graph.Edge.Sense;
import net.certiv.common.graph.Walker.NodeVisitor;
import net.certiv.common.stores.Counter;
import net.certiv.common.stores.LinkedHashList;
import net.certiv.common.stores.UniqueList;
import net.certiv.common.stores.props.Props;

/**
 * Abstract base class for a directed multigraph node. Edge connections are managed
 * through separate input and output edge sets.
 */
public abstract class Node<N extends Node<N, E>, E extends Edge<N, E>> extends Props
		implements Comparable<N> {

	public static final String NODE_ID = "NodeId";

	@VisibleForTesting
	static final Counter CTR = new Counter();

	/** Set of inbound edges */
	protected final IEdgeSet<N, E> in;
	/** Set of outbound edges */
	protected final IEdgeSet<N, E> out;

	/** Unique numerical node identifier */
	public final long _nid;

	protected Node(IEdgeSet<N, E> in, IEdgeSet<N, E> out) {
		Assert.notNull(in, out);
		this.in = in;
		this.out = out;
		_nid = CTR.getAndIncrement();
		put(NODE_ID, String.valueOf(_nid)); // default id
	}

	protected Node(IEdgeSet<N, E> in, IEdgeSet<N, E> out, Map<Object, Object> props) {
		this(in, out);
		putAll(props);
	}

	/** Return the node instance identifying object. */
	public Object nodeId() {
		return get(NODE_ID);
	}

	/** Set the object used to provide the name of this node instance. */
	public <V> V setNodeId(V id) {
		Assert.notNull(id);
		return put(NODE_ID, id);
	}

	/** Return a simple display name for this node instance. */
	public String name() {
		Object id = nodeId();
		if (id == null || id.toString().isBlank()) return String.valueOf(_nid);
		return id.toString();
	}

	/** Return a unique display name for this node instance. */
	public String uname() {
		String name = name();
		String nid = String.valueOf(_nid);
		if (name.equals(nid)) return nid;
		return String.format("%s(%s)", name, nid);
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

	/**
	 * Returns the full set of inbound and outbound adjacent nodes. Excludes {@code this}
	 * node.
	 */
	public UniqueList<N> adjacent() {
		return adjacent(Sense.BOTH);
	}

	/**
	 * Returns the set of all immediately adjacent nodes connected with the given sense.
	 * Excludes {@code this} node.
	 *
	 * @param dir the adjacency direction criteria
	 * @return set of adjacent nodes in the given direction, excluding {@code this} node
	 */
	public UniqueList<N> adjacent(Sense dir) {
		return adjacent(dir, false);
	}

	public UniqueList<N> adjacent(Sense dir, boolean cyclic) {
		if (cyclic) return adjacent(dir, null);
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
	public UniqueList<N> adjacent(Sense dir, Predicate<? super N> filter) {
		switch (dir) {
			case IN:
				return in.adjacent(filter);
			case OUT:
				return out.adjacent(filter);
			case BOTH:
			default:
				UniqueList<N> results = new UniqueList<>();
				results.addAll(in.adjacent(filter));
				results.addAll(out.adjacent(filter));
				return results.unmodifiable();
		}
	}

	/**
	 * Return {@code true} if this node is an ancestor of the given node. Performs a depth
	 * first search starting at this node.
	 *
	 * @param node the search target descendent node
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
	 * @param end a distal node
	 * @return set of connecting edges
	 */
	public UniqueList<E> to(N end) {
		return edges(Sense.OUT, e -> e.end()._nid == end._nid);
	}

	/**
	 * Returns the set of inbound edges that connect with the given node.
	 *
	 * @param beg a distal node
	 * @return set of connecting edges
	 */
	public UniqueList<E> from(N beg) {
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
	 * Checks whether the node is valid. A valid node may exist disconnected from the
	 * graph, <i>i.e.</i> pending reconnection. Nominally, any non-null node is valid.
	 *
	 * @return {@code true} if this node is valid
	 */
	public boolean valid() {
		return true;
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

	/**
	 * Returns the set of edges connected to this node. Excludes single-edge cycles.
	 */
	public UniqueList<E> edges() {
		return edges(Sense.BOTH, false);
	}

	/**
	 * Returns the set of connected edges, constrained by the given direction. Excludes
	 * single-edge cycles.
	 *
	 * @param dir the connected edge direction criteria
	 * @return set of connected edges of the given direction and excluding single-edge
	 *         cycles
	 */
	public UniqueList<E> edges(Sense dir) {
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
	public UniqueList<E> edges(Sense dir, boolean cyclic) {
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
	public UniqueList<E> edges(Sense dir, Predicate<? super E> filter) {
		switch (dir) {
			case IN:
				return in.edges(filter);
			case OUT:
				return out.edges(filter);
			case BOTH:
			default:
				UniqueList<E> results = new UniqueList<>();
				results.addAll(in.edges(filter));
				results.addAll(out.edges(filter));
				return results;
		}
	}

	/**
	 * Walker enter callback. Default implementation does nothing, while permitting the
	 * walk to proceed. Typically, implement in a {@code Node} subclass as
	 *
	 * <pre>
	 * return listener.enter(dir, visited, parent, (N) this);
	 * </pre>
	 *
	 * @param dir      the walk traversal direction
	 * @param visited  collection of previously visited parent/node combinations
	 * @param listener the defining visitor/listener
	 * @param parent   the parent node
	 * @return {@code true} to walk the children of the current node
	 */
	protected boolean enter(Sense dir, LinkedHashList<N, N> visited, NodeVisitor<N> listener, N parent) {
		return true;
	}

	/**
	 * Walker exit callback. Default implementation does nothing, while permitting the
	 * walk to proceed. Typically, implement in a {@code Node} subclass as
	 *
	 * <pre>
	 * return listener.exit(dir, visited, parent, (N) this);
	 * </pre>
	 *
	 * @param dir      the walk traversal direction
	 * @param visited  collection of previously visited parent/node combinations
	 * @param listener the defining visitor/listener
	 * @param parent   the parent node
	 * @return {@code true} on success
	 */
	protected boolean exit(Sense dir, LinkedHashList<N, N> visited, NodeVisitor<N> listener, N parent) {
		return true;
	}

	/**
	 * Returns the {@code DotStyle} store for the {@code Node} or {@code Edge} containing
	 * this properties store. Creates and adds an {@code ON#NODES} default category
	 * {@code DotStyle} store, if a store does not exist.
	 * <p>
	 * Override {@code #defineStyle()} to define a custom configured style.
	 *
	 * @return the dot style store
	 */
	public DotStyle getDotStyle() {
		return Dot.getStyles(this, ON.NODES);
	}

	/**
	 * Defines a custom style for this node. The default implementation does nothing.
	 * <p>
	 * Override to define a custom configured style.
	 */
	public DotStyle defineStyle() {
		return getDotStyle();
	}

	/**
	 * Clears the edge sets and property store. Retains the node name property.
	 * <p>
	 * Internal use only.
	 */
	@Override
	public void clear() {
		in.clear();
		out.clear();
		Object name = nodeId();
		super.clear();
		setNodeId(name);
	}

	@Override
	public int compareTo(N o) {
		if (_nid < o._nid) return -1;
		if (_nid > o._nid) return 1;
		return 0;
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
		return uname();
	}
}
