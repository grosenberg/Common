package net.certiv.common.graph;

import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import net.certiv.common.dot.Dictionary.ON;
import net.certiv.common.dot.DotAttr;
import net.certiv.common.dot.DotStyle;
import net.certiv.common.graph.Edge.Sense;
import net.certiv.common.util.Assert;

public abstract class Graph<N extends Node<N, E>, E extends Edge<N, E>> extends Props {

	/** All graph nodes. */
	private final Set<N> nodes = new LinkedHashSet<>();

	public Graph() {
		super();
	}

	public Graph(Map<Object, Object> props) {
		super();
		if (props != null) putProperties(props);
	}

	/**
	 * Descriptive name of this {@code Graph}. Defaults to the simple class name of
	 * the implementing class.
	 */
	public String name() {
		return getClass().getSimpleName();
	}

	/** Returns {@code true} if the graph contains the given node. */
	public boolean hasNode(N node) {
		return nodes.contains(node);
	}

	/** Returns a copy of the current graph node set. */
	public Set<N> getNodes() {
		return new LinkedHashSet<>(nodes);
	}

	/** Returns a copy of the current graph root node set. */
	public Set<N> getRoots() {
		return nodes.stream().filter(n -> n.isRoot()).collect(Collectors.toSet());
	}

	/** Returns {@code true} if any edge exists between the given nodes. */
	public boolean hasEdge(N src, N dst) {
		return !src.to(dst).isEmpty();
	}

	/**
	 * : given an {@code E} edge defined with respect to two {@code N} nodes, the
	 * call to {@link #addEdge()} connects the edge nodes to the edge and adds the
	 * given edge including terminal nodes to this graph, adjusting the root node
	 * set accordingly.
	 * <p>
	 * Primary graph tree constuction entry point: constucting a node or edge does
	 * not add it to the graph.
	 *
	 * @param edge a graph edge
	 * @returns {@code true} if either terminal node was not already present in the
	 *              graph
	 */
	public boolean addEdge(E edge) {
		Assert.notNull(edge);
		edge.beg().add(edge, Sense.OUT);
		edge.end().add(edge, Sense.IN);
		boolean ok = add(edge.beg());
		ok |= add(edge.end());
		return ok;
	}

	/**
	 * Internal use only. Adds the node to the graph node list.
	 *
	 * @param node the node to add
	 * @return {@code true} if not already present
	 * @see Graph#addEdge(Edge)
	 */
	private boolean add(N node) {
		return nodes.add(node);
	}

	/**
	 * Internal use only. Removes the node from the graph node list.
	 *
	 * @param node the node to remove
	 * @return {@code true} if removed
	 * @see Graph#addEdge(Edge)
	 */
	private boolean remove(N node) {
		return nodes.remove(node);
	}

	/**
	 * Removes the given node, including all connecting edges, from the graph.
	 *
	 * @param node the node to remove
	 * @return {@code true} if the node was present in the graph and is now removed
	 */
	public boolean removeNode(N node) {
		Assert.notNull(node);
		boolean rmvd = remove(node);
		node.edges().forEach(e -> removeEdge(e));
		return rmvd;
	}

	/**
	 * Removes the given edge from the graph. Removes either terminal node if the
	 * node has no remaining edge connections.
	 *
	 * @param edge a graph edge
	 * @return {@code true} if the edge was removed
	 */
	public boolean removeEdge(E edge) {
		Assert.notNull(edge);
		boolean rmvd = edge.remove(); // disconnect from end-points
		if (rmvd) {
			if (!edge.beg().hasEdges(false)) remove(edge.beg());
			if (!edge.end().hasEdges(false)) remove(edge.end());
		}
		return rmvd;
	}

	/**
	 * Removes the given edge if the edge satisfies the given filter predicate.
	 *
	 * @param edge a graph edge
	 * @param filter a predicate returning {@code true} to select for removal
	 * @return {@code true} if the edge was removed
	 */
	public boolean removeEdgeIf(E edge, Predicate<? super E> filter) {
		Assert.notNull(edge, filter);
		return filter.test(edge) ? removeEdge(edge) : false;
	}

	/**
	 * Removes all edges directly connecting the given nodes.
	 *
	 * @param src a source node
	 * @param dst a destination node
	 * @return {@code true} if an edge was removed
	 */
	public boolean removeEdges(N src, N dst) {
		Set<E> edges = src.to(dst);
		if (edges.isEmpty()) return false;
		edges.forEach(e -> removeEdge(e));
		return true;
	}

	/**
	 * Removes all edges directly connecting the given nodes and that satisfy the
	 * given filter predicate. Handles root node adjustment.
	 *
	 * @param src a source node
	 * @param dst a destination node
	 * @param filter a predicate returning {@code true} to select an edge for
	 *            removal from the graph
	 * @return {@code true} if any element was removed
	 */
	public boolean removeEdgesIf(N src, N dst, Predicate<? super E> filter) {
		Assert.notNull(src, dst, filter);

		boolean removed = false;
		for (Iterator<E> itr = src.to(dst).iterator(); itr.hasNext();) {
			removed |= removeEdgeIf(itr.next(), filter);
		}
		return removed;
	}

	/**
	 * Reterminate the given edge to connect between the given terminal nodes.
	 * Removes the edge if retermination would create an impermissble cycle. The
	 * prior edge terminal nodes are removed if retermination leaves them
	 * unconnected in the graph.
	 *
	 * @param edge a graph edge
	 * @param beg the new begin node
	 * @param end a new end node
	 * @param cycles {@code true} to permit creation of single edge cycles
	 * @return {@code true} if the edge was reterminated, or {@code false} if
	 *             removed
	 */
	public boolean reterminate(E edge, N beg, N end, boolean cycles) {
		Assert.notNull(edge, beg, end);

		if (beg.equals(edge.end()) && !cycles) { // impermissble cycle
			removeEdge(edge);
			return false;
		}

		removeEdge(edge);
		edge.beg(beg);
		edge.end(end);
		addEdge(edge);

		return true;
	}

	/**
	 * Reterminate the given edge with the given begin node. Removes the edge if
	 * retermination would create an impermissble cycle. The prior begin terminal
	 * node is removed if retermination leaves it unconnected in the graph.
	 *
	 * @param beg the new edge beg node
	 * @param edge a graph edge
	 * @param cycles {@code true} to permit creation of single edge cycles
	 * @return {@code true} if the edge was reterminated, or {@code false} if
	 *             removed
	 */
	public boolean reterminate(N beg, E edge, boolean cycles) {
		return reterminate(edge, beg, edge.end(), cycles);
	}

	/**
	 * Reterminate the given edge with the given end node. Removes the edge if
	 * retermination would create an impermissble cycle. The prior end terminal node
	 * is removed if retermination leaves it unconnected in the graph.
	 *
	 * @param edge a graph edge
	 * @param end a new edge end node
	 * @param cycles {@code true} to permit creation of single edge cycles
	 * @return {@code true} if the edge was reterminated, or {@code false} if
	 *             removed
	 */
	public boolean reterminate(E edge, N end, boolean cycles) {
		return reterminate(edge, edge.beg(), end, cycles);
	}

	/**
	 * Reterminate the given edges to share the given begin node. Removes edges if
	 * retermination would create an impermissble cycle. Prior begin terminal nodes
	 * are removed if retermination leaves them unconnected in the graph.
	 *
	 * @param beg the final shared edge beging node
	 * @param edges graph edges
	 * @param cycles {@code true} to permit creation of single edge cycles
	 */
	public void reterminate(N beg, Set<? extends E> edges, boolean cycles) {
		Assert.notNull(beg, edges);
		edges.forEach(e -> reterminate(beg, e, cycles));
	}

	/**
	 * Reterminate the given edges to share the given end node. Removes edges if
	 * retermination would create an impermissble cycle. Prior end terminal nodes
	 * are removed if retermination leaves them unconnected in the graph.
	 *
	 * @param edges graph edges
	 * @param end the final shared edge end node
	 * @param cycles {@code true} to permit creation of single edge cycles
	 */
	public void reterminate(Set<? extends E> edges, N end, boolean cycles) {
		Assert.notNull(edges, end);
		edges.forEach(e -> reterminate(e, end, cycles));
	}

	/**
	 * Returns the {@code DotStyle} store for the {@code Node} or {@code Edge}
	 * containing this properties store. Creates and adds an {@code ON#GRAPHS}
	 * default category {@code DotStyle} store, if a store does not exist.
	 *
	 * @return the dot style store
	 */
	public DotStyle getDotStyle() {
		return getDotStyle(ON.GRAPHS);
	}

	/**
	 * Defines a custom style for this graph. The default implementation provides
	 * some sane style values, but will not overwrite any existing styles.
	 */
	public DotStyle defineStyle() {
		DotStyle ds = getDotStyle();
		ds.putIfAbsent(DotAttr.LABEL, ON.GRAPHS, name());
		ds.putIfAbsent(DotAttr.FONTCOLOR, ON.GRAPHS, DotStyle.BLACK);
		ds.putIfAbsent(DotAttr.FONTNAME, ON.GRAPHS, DotStyle.FONTS);
		ds.putIfAbsent(DotAttr.FONTSIZE, ON.GRAPHS, 24);

		ds.putIfAbsent(DotAttr.FONTCOLOR, ON.CLUSTERS, DotStyle.BLACK);
		ds.putIfAbsent(DotAttr.FONTNAME, ON.CLUSTERS, DotStyle.FONTS);
		ds.putIfAbsent(DotAttr.FONTSIZE, ON.CLUSTERS, 18);

		ds.putIfAbsent(DotAttr.FONTCOLOR, ON.NODES, DotStyle.BLACK);
		ds.putIfAbsent(DotAttr.FONTNAME, ON.NODES, DotStyle.FONTS);
		ds.putIfAbsent(DotAttr.FONTSIZE, ON.NODES, 12);

		ds.putIfAbsent(DotAttr.FONTCOLOR, ON.EDGES, DotStyle.BLACK);
		ds.putIfAbsent(DotAttr.FONTNAME, ON.EDGES, DotStyle.FONTS);
		ds.putIfAbsent(DotAttr.FONTSIZE, ON.EDGES, 10);
		return ds;
	}

	@Override
	public void clear() {
		nodes.forEach(n -> n.edges().forEach(e -> e.remove()));
		nodes.clear();
		super.clear();
	}

	@Override
	public int hashCode() {
		return getClass().getName().hashCode();
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) return true;
		if (!(obj instanceof Graph)) return false;
		Graph<?, ?> other = (Graph<?, ?>) obj;
		return Objects.equals(getClass().getName(), other.getClass().getName());
	}

	@Override
	public String toString() {
		return name();
	}
}
