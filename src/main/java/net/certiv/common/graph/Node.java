package net.certiv.common.graph;

import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

import net.certiv.common.dot.Dictionary.ON;
import net.certiv.common.dot.DotStyle;
import net.certiv.common.graph.Edge.Sense;
import net.certiv.common.graph.Walker.NodeVisitor;
import net.certiv.common.stores.Counter;
import net.certiv.common.stores.HashList;
import net.certiv.common.stores.HashMultiset;

public abstract class Node<N extends Node<N, E>, E extends Edge<N, E>> extends Props {

	private static final Counter Factor = new Counter();
	public final long _nid;

	/** Inbound Edge->Node map: key=in edge; value=distal node. */
	protected final LinkedHashMap<E, N> inEdges = new LinkedHashMap<>();
	/** Outbound Edge->Node map: key=out edge; value=distal node. */
	protected final LinkedHashMap<E, N> outEdges = new LinkedHashMap<>();

	/** Node:Edges index: key=distal node; value={@code in} connecting edge set. */
	protected final HashMultiset<N, E> inNodes = new HashMultiset<>();
	/** Node:Edges index: key=distal node; value={@code out} connecting edge set. */
	protected final HashMultiset<N, E> outNodes = new HashMultiset<>();

	public Node() {
		_nid = Factor.getAndIncrement();
	}

	public Node(Map<Object, Object> props) {
		this();
		putProperties(props);
	}

	public String name() {
		return String.valueOf(_nid);
	}

	/**
	 * Internal use only. Called from {@link Edge()} to add the given edge in the
	 * given direction.
	 *
	 * @return the distal node.
	 */
	N add(E edge, Sense dir) {
		@SuppressWarnings("unchecked")
		N distal = edge.other((N) this);

		if (dir == Sense.IN) {
			inEdges.put(edge, distal);
			inNodes.put(distal, edge);
		} else {
			outEdges.put(edge, distal);
			outNodes.put(distal, edge);
		}
		return distal;
	}

	/**
	 * Internal use only. Callthrough from {@link Edge#remove(boolean)}.
	 *
	 * @return {@code true} if the edge is removed from the node.
	 */
	boolean remove(E edge, Sense dir) {
		boolean rmvd = false;
		if (dir == Sense.IN) {
			N distal = inEdges.remove(edge);
			if (distal != null) {
				rmvd = inNodes.remove(distal, edge);
			}
		}
		if (dir == Sense.OUT) {
			N distal = outEdges.remove(edge);
			if (distal != null) {
				rmvd = outNodes.remove(distal, edge);
			}
		}
		return rmvd;
	}

	/** Return {@code true} if this node is adjacent the given node. */
	public boolean adjacent(N node) {
		return inNodes.containsKey(node) || outNodes.containsKey(node);
	}

	/** Returns the set of all immediately adjacent nodes. */
	public Set<N> adjacent() {
		return adjacent(null, null);
	}

	/**
	 * Returns the set of all immediately adjacent nodes connected with the given
	 * sense.
	 */
	public Set<N> adjacent(Sense dir) {
		return adjacent(null, dir);
	}

	/**
	 * Returns the immediately adjacent nodes, connected with the given sense, that
	 * have the same node Id. If {@code dir} is {@code null}, include both in and
	 * out directions. If {@code node} is {@code null}, include all vertices.
	 *
	 * @param node the constraining distal node or {@code null} for all distals
	 * @return set of adjacent nodes
	 */
	public Set<N> adjacent(N node, Sense dir) {
		Set<N> results = new LinkedHashSet<>();
		if (dir == null || dir == Sense.IN) {
			for (N parent : inEdges.values()) {
				if (node == null || parent.equals(node)) results.add(parent);
			}
		}
		if (dir == null || dir == Sense.OUT) {
			for (N child : outEdges.values()) {
				if (node == null || child.equals(node)) results.add(child);
			}
		}
		return results;
	}

	public Set<E> to(N end) {
		Set<E> results = new LinkedHashSet<>();
		Set<E> edges = outNodes.get(end);
		if (edges != null) results.addAll(edges);
		return results;
	}

	public Set<E> from(N beg) {
		Set<E> results = new LinkedHashSet<>();
		Set<E> edges = inNodes.get(beg);
		if (edges != null) results.addAll(edges);
		return results;
	}

	/** Returns the set of connected edges. */
	public Set<E> edges() {
		return edges(null, null);
	}

	/**
	 * Returns the set of connected edges that connect with the given node.
	 *
	 * @param node the constraining node or {@code null} for all
	 * @return set of adjacent node connecting edges
	 */
	public Set<E> edges(N node) {
		return edges(node, null);
	}

	/**
	 * Returns an ordered list of immediately connected edges having the given
	 * sense.
	 *
	 * @param dir the edge sense or {@code null} for both
	 * @return set of adjacent node connecting edges
	 */
	public Set<E> edges(Sense dir) {
		return edges(null, dir);
	}

	/**
	 * Returns an ordered list of immediately connected edges having the given
	 * sense.
	 *
	 * @param dir the edge sense or {@code null} for both
	 * @param selfs {@code true} to include self connected edges
	 * @return set of adjacent node connecting edges
	 */
	public Set<E> edges(Sense dir, boolean selfs) {
		Set<E> edges = new LinkedHashSet<>(edges(dir));
		if (!selfs) edges.removeIf(e -> e.selfLoop());
		return edges;
	}

	/**
	 * Returns the set of connected edges, having the given sense, that connect with
	 * the given node. If {@code dir} is {@code null}, include both in and out
	 * directions. If {@code node} is {@code null}, include all edges.
	 *
	 * @param node the constraining node or {@code null} for all
	 * @param dir the edge sense or {@code null} for both
	 * @return set of adjacent node connecting edges
	 */
	public Set<E> edges(N node, Sense dir) {
		Set<E> results = new LinkedHashSet<>();
		if (dir == null || dir == Sense.IN) {
			Set<E> edges = node != null ? inNodes.get(node) : inNodes.values();
			if (edges != null) results.addAll(edges);
		}
		if (dir == null || dir == Sense.OUT) {
			Set<E> edges = node != null ? outNodes.get(node) : outNodes.values();
			if (edges != null) results.addAll(edges);
		}
		return results;
	}

	/**
	 * Return the count of all immediately connected edges. Include self loop edges
	 * if {@code selfs} is {@code true}.
	 */
	public int edgeCount(boolean selfs) {
		return edgeCount(null, selfs);
	}

	/**
	 * Return the count of immediately connected edges of the given sense. If
	 * {@code dir} is {@code null}, include both in and out directions. Include self
	 * loop edges if {@code selfs} is {@code true}.
	 */
	public int edgeCount(Sense dir, boolean selfs) {
		return edges(dir, selfs).size();
	}

	public boolean isRoot() {
		return edgeCount(Sense.IN, false) == 0;
	}

	/** Walker callback. */
	public boolean enter(Sense dir, HashList<N, N> visited, NodeVisitor<N> listener, N node) {
		return true;
	}

	/** Walker callback. */
	public boolean exit(Sense dir, HashList<N, N> visited, NodeVisitor<N> listener, N node) {
		return true;
	}

	/**
	 * Return {@code true} if this node is an ancestor of the given node. Performs a
	 * depth first search.
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
	 * Returns the {@code DotStyle} store for the {@code Node} or {@code Edge}
	 * containing this properties store. Creates and adds an {@code ON#NODES}
	 * default category {@code DotStyle} store, if a store does not exist.
	 *
	 * @return the dot style store
	 */
	public DotStyle getDotStyle() {
		return getDotStyle(ON.NODES);
	}

	/**
	 * Defines a custom style for this node. The default implementation does
	 * nothing.
	 */
	public DotStyle defineStyle() {
		return getDotStyle();
	}

	@Override
	public void clear() {
		inEdges.clear();
		inNodes.clear();
		outEdges.clear();
		outNodes.clear();
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
		return String.format("%s[%s]", name(), isRoot() ? "root" : "child");
	}
}
