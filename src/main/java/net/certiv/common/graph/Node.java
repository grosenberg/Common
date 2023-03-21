package net.certiv.common.graph;

import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;
import java.util.function.Predicate;

import net.certiv.common.dot.Dictionary.ON;
import net.certiv.common.dot.DotStyle;
import net.certiv.common.graph.Edge.Sense;
import net.certiv.common.graph.Walker.NodeVisitor;
import net.certiv.common.stores.Counter;
import net.certiv.common.stores.HashList;
import net.certiv.common.util.Assert;
import net.certiv.common.util.Strings;

public abstract class Node<N extends Node<N, E>, E extends Edge<N, E>> extends Props {

	private static final Counter CTR = new Counter();

	public static final String NODE_NAME = "NodeName";

	/** Unique numerical node identifier */
	public final long _nid;

	/** Set of inbound edges */
	private final EdgeSet<N, E> in = new EdgeSet<>(Sense.IN);
	/** Set of outbound edges */
	private final EdgeSet<N, E> out = new EdgeSet<>(Sense.OUT);

	// /** Inbound Edge->Node map: key=in edge; value=distal node. */
	// private final LinkedHashMap<E, N> inEdges = new LinkedHashMap<>();
	// /** Outbound Edge->Node map: key=out edge; value=distal node. */
	// private final LinkedHashMap<E, N> outEdges = new LinkedHashMap<>();
	//
	// /** Node:Edges index: key=distal node; value={@code in} connecting edge set. */
	// private final HashMultiset<N, E> inNodes = new HashMultiset<>();
	// /** Node:Edges index: key=distal node; value={@code out} connecting edge set. */
	// private final HashMultiset<N, E> outNodes = new HashMultiset<>();

	protected Node() {
		super();
		_nid = CTR.getAndIncrement();
		putProperty(NODE_NAME, String.valueOf(_nid));
	}

	protected Node(Map<Object, Object> props) {
		this();
		putProperties(props);
	}

	public String name() {
		return (String) getProperty(NODE_NAME, String.valueOf(_nid));
	}

	/**
	 * Internal use only. Called from {@link Edge()} to add the given edge in the given
	 * direction.
	 *
	 * @return the distal node.
	 */
	N add(E edge, Sense dir) {
		Assert.isLegal(dir != Sense.BOTH);
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
	 * Internal use only. Callthrough from {@link Edge#remove(boolean)}.
	 *
	 * @return {@code true} if the edge is removed from the node.
	 */
	boolean remove(E edge, Sense dir) {
		Assert.isLegal(dir != Sense.BOTH);
		if (dir == Sense.IN) return in.remove(edge);
		return out.remove(edge);
	}

	/** Return {@code true} if this node is adjacent the given node. */
	public boolean adjacent(N node) {
		return in.isAdjacent(node) || out.isAdjacent(node);
	}

	/** Returns the set of all immediately adjacent nodes. */
	public Set<N> adjacent() {
		return adjacent(Sense.BOTH, null);
	}

	/**
	 * Returns the set of all immediately adjacent nodes connected with the given sense.
	 */
	public Set<N> adjacent(Sense dir) {
		return adjacent(dir, null);
	}

	/**
	 * Returns the immediately adjacent nodes, connected with the given sense, that meet
	 * the filter criteria. If {@code filter} is {@code null}, include all adjacent nodes.
	 *
	 * @param dir    the adjacency direction criteria
	 * @param filter the distal node qualification criteria
	 * @return set of adjacent nodes meeting the given criteria
	 */
	public Set<N> adjacent(Sense dir, Predicate<? super N> filter) {
		switch (dir) {
			case IN:
				return in.adjacent(filter);
			case OUT:
				return out.adjacent(filter);
			case BOTH:
			default:
				Set<N> results = new LinkedHashSet<>();
				results.addAll(in.adjacent(filter));
				results.addAll(out.adjacent(filter));
				return Collections.unmodifiableSet(results);
		}
	}

	/**
	 * Returns the set of outbound edges that connect with the given node.
	 *
	 * @param node a distal node
	 * @return set of connecting edges
	 */
	public Set<E> to(N end) {
		return edges(Sense.OUT, e -> e.end()._nid == end._nid);
	}

	/**
	 * Returns the set of inbound edges that connect with the given node.
	 *
	 * @param node a distal node
	 * @return set of connecting edges
	 */
	public Set<E> from(N beg) {
		return edges(Sense.IN, e -> e.beg()._nid == beg._nid);
	}

	/**
	 * Returns {@code true} if the edge count, conditionally including self-cyclic edges,
	 * is non-zero.
	 *
	 * @param cycles {@code true} to include self-cyclic edges
	 * @return {@code true} on a non-zero edge count
	 */
	public boolean hasEdges(Sense dir, boolean cycles) {
		if (cycles) return size(dir, null) != 0;
		return size(dir, e -> !e.selfCyclic()) != 0;
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
	 * Returns the edge count, including self-cyclic edges (each counted once).
	 *
	 * @return the total count of unique connected edges
	 */
	public int size() {
		return size(null);
	}

	/**
	 * Return the count of all immediately connected edges, subject to the filter
	 * criteria.
	 *
	 * @param filter the connected edge qualification criteria
	 */
	public int size(Predicate<? super E> filter) {
		return size(Sense.BOTH, filter);
	}

	/**
	 * Return the count of immediately connected edges of the given sense. If {@code dir}
	 * is {@code null}, include both in and out directions. Include self loop edges if
	 * {@code selfs} is {@code true}.
	 */
	public int size(Sense dir, Predicate<? super E> filter) {
		return edges(dir, filter).size();
	}

	/** Returns the set of connected edges. */
	public Set<E> edges() {
		return edges(Sense.BOTH, null);
	}

	/** Returns the set of connected edges, constrained by the given direction. */
	public Set<E> edges(Sense dir) {
		return edges(dir, null);
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
	public Set<E> edges(Sense dir, Predicate<? super E> filter) {
		switch (dir) {
			case IN:
				return in.edges(filter);
			case OUT:
				return out.edges(filter);
			case BOTH:
			default:
				Set<E> results = new LinkedHashSet<>();
				results.addAll(in.edges(filter));
				results.addAll(out.edges(filter));
				return Collections.unmodifiableSet(results);
		}
	}

	// /**
	// * Returns the set of connected edges that connect with the given node.
	// *
	// * @param node the constraining node or {@code null} for all
	// * @return set of adjacent node connecting edges
	// */
	// public Set<E> edges(N node) {
	// return edges(node, null);
	// }
	//
	// /**
	// * Returns an ordered list of immediately connected edges having the given sense.
	// *
	// * @param dir the edge sense or {@code null} for both
	// * @return set of adjacent node connecting edges
	// */
	// public Set<E> edges(Sense dir) {
	// return edges(null, dir);
	// }
	//
	// /**
	// * Returns an ordered list of immediately connected edges having the given sense.
	// *
	// * @param dir the edge sense or {@code null} for both
	// * @param selfs {@code true} to include self connected edges
	// * @return set of adjacent node connecting edges
	// */
	// public Set<E> edges(Sense dir, boolean selfs) {
	// Set<E> edges = new LinkedHashSet<>(edges(dir));
	// if (!selfs) edges.removeIf(e -> e.selfLoop());
	// return edges;
	// }
	//
	// /**
	// * Returns the set of connected edges, having the given sense, that connect with the
	// * given node. If {@code dir} is {@code null}, include both in and out directions.
	// If
	// * {@code node} is {@code null}, include all edges.
	// *
	// * @param node the constraining node or {@code null} for all
	// * @param dir the edge sense or {@code null} for both
	// * @return set of adjacent node connecting edges
	// */
	// public Set<E> edges(N node, Sense dir) {
	// Set<E> results = new LinkedHashSet<>();
	// if (dir == null || dir == Sense.IN) {
	// Set<E> edges = node != null ? inNodes.get(node) : inNodes.values();
	// if (edges != null) results.addAll(edges);
	// }
	// if (dir == null || dir == Sense.OUT) {
	// Set<E> edges = node != null ? outNodes.get(node) : outNodes.values();
	// if (edges != null) results.addAll(edges);
	// }
	// return results;
	// }

	// /**
	// * Return the count of all immediately connected edges. Include self loop edges if
	// * {@code selfs} is {@code true}.
	// */
	// public int edgeCount(boolean selfs) {
	// return edgeCount(null, selfs);
	// }
	//
	// /**
	// * Return the count of immediately connected edges of the given sense. If {@code
	// dir}
	// * is {@code null}, include both in and out directions. Include self loop edges if
	// * {@code selfs} is {@code true}.
	// */
	// public int edgeCount(Sense dir, boolean selfs) {
	// return edges(dir, selfs).size();
	// }

	/** Walker callback. */
	public boolean enter(Sense dir, HashList<N, N> visited, NodeVisitor<N> listener, N node) {
		return true;
	}

	/** Walker callback. */
	public boolean exit(Sense dir, HashList<N, N> visited, NodeVisitor<N> listener, N node) {
		return true;
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
	 * Returns the {@code DotStyle} store for the {@code Node} or {@code Edge} containing
	 * this properties store. Creates and adds an {@code ON#NODES} default category
	 * {@code DotStyle} store, if a store does not exist.
	 *
	 * @return the dot style store
	 */
	public DotStyle getDotStyle() {
		return getDotStyle(ON.NODES);
	}

	// /**
	// * Returns the {@code DotStyle} store for the {@code Node} or {@code Edge}
	// containing
	// * this properties store. Creates and adds an {@code ON#NODES} default category
	// * {@code DotStyle} store, if a store does not exist.
	// *
	// * @return the dot style store
	// */
	// public DotStyle getDotStyle() {
	// return DotUtil.getDotStyle(this, ON.NODES);
	// }

	/**
	 * Defines a custom style for this node. The default implementation does nothing.
	 */
	public DotStyle defineStyle() {
		return getDotStyle();
	}

	@Override
	public void clear() {
		in.clear();
		out.clear();
		super.clear();
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
		return String.format("%s%s", name(), isRoot() ? "<root>" : Strings.EMPTY);
	}
}
