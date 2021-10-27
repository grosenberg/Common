package net.certiv.common.graph;

import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

import net.certiv.common.dot.Dictionary.ON;
import net.certiv.common.dot.DotAttr;
import net.certiv.common.dot.DotStyle;
import net.certiv.common.graph.Edge.Sense;
import net.certiv.common.util.Assert;

public abstract class Graph<N extends Node<N, E>, E extends Edge<N, E>> extends Props {

	/** Individual roots. */
	protected final Set<N> roots = new LinkedHashSet<>();
	/** All nodes. */
	protected final Set<N> nodes = new LinkedHashSet<>();

	public Graph() {
		super();
	}

	public Graph(Map<Object, Object> props) {
		super();
		putProperties(props);
	}

	/**
	 * Descriptive name of this {@code Graph}. Defaults to the simple class name of
	 * the implementing class.
	 */
	public String name() {
		return getClass().getSimpleName();
	}

	/**
	 * Add the given node; returns {@code true} if not already present. Handles root
	 * adjustment.
	 */
	public boolean addNode(N node) {
		Assert.notNull(node);
		if (node.isRoot()) {
			roots.add(node);
		} else {
			roots.remove(node);
		}
		return nodes.add(node);
	}

	/**
	 * Removes the given node including all connecting edges. Returns {@code true}
	 * if the node was present in the graph. Handles root adjustment.
	 */
	public boolean removeNode(N node) {
		Assert.notNull(node);

		boolean present = nodes.contains(node);
		if (present) {
			node.edges().forEach(e -> removeEdge(e));
			roots.remove(node);
			nodes.remove(node);
		}
		return present;
	}

	/**
	 * Primary tree constuction entry point: given a new {@code E} edge defined with
	 * respect to two {@code N} nodes, the call to {@link #addEdge()} connects the
	 * edge nodes to the edge and adds the given edge to this graph, adjusting the
	 * root set accordingly.
	 *
	 * @returns {@code true} if the node at either end was not already present.
	 */
	public boolean addEdge(E edge) {
		Assert.notNull(edge);
		edge.beg.add(edge, Sense.OUT);
		edge.end.add(edge, Sense.IN);
		boolean ok = addNode(edge.beg);
		ok |= addNode(edge.end);
		return ok;
	}

	/** Removes the given edge. Handles root adjustment. */
	public boolean removeEdge(E edge) {
		Assert.notNull(edge);
		boolean ok = edge.remove();
		if (edge.beg.isRoot()) roots.add(edge.beg);
		if (edge.end.isRoot()) roots.add(edge.end);
		return ok;
	}

	/** Removes the edges between the given nodes. Handles root adjustment. */
	public void removeEdges(N src, N dst) {
		src.to(dst).forEach(e -> removeEdge(e));
	}

	/** Returns {@code true} if any edge exists between the given nodes. */
	public boolean hasEdge(N src, N dst) {
		return !src.to(dst).isEmpty();
	}

	/** Returns the set of source to destination edges. */
	public Set<E> findEdges(N src, N dst) {
		return src.to(dst);
	}

	public Set<N> getNodes() {
		return new LinkedHashSet<>(nodes);
	}

	@Override
	public void clear() {
		nodes.forEach(n -> n.edges().forEach(e -> e.remove()));
		roots.clear();
		nodes.clear();
		super.clear();
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
	 * some sane defaults, but does not overwrite existing style values.
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
