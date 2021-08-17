package net.certiv.common.graph;

import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Objects;
import java.util.Set;

import net.certiv.common.graph.Edge.Sense;
import net.certiv.common.util.Assert;

public abstract class Graph<N extends Node<N, E>, E extends Edge<N, E>> {

	/** Individual roots. */
	protected final Set<N> roots = new LinkedHashSet<>();
	/** All nodes. */
	protected final Set<N> nodes = new LinkedHashSet<>();

	private LinkedHashMap<Object, Object> props;

	/**
	 * Representational name of this {@code Graph}. Defaults to the simple class
	 * name.
	 */
	public String name() {
		return getClass().getSimpleName();
	}

	/**
	 * Add the given node; returns {@code true} if not already present. Handles root
	 * adjustment.
	 */
	protected boolean addNode(N node) {
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
	protected boolean removeNode(N node) {
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
	 * @returns {@code true} if not already present.
	 */
	protected boolean addEdge(E edge) {
		Assert.notNull(edge);
		edge.beg.add(edge, Sense.OUT);
		edge.end.add(edge, Sense.IN);
		boolean ok = addNode(edge.beg);
		ok |= addNode(edge.end);
		return ok;
	}

	/** Removes the edge between the given nodes. Handles root adjustment. */
	protected boolean removeEdge(E edge) {
		Assert.notNull(edge);
		boolean ok = edge.remove();
		if (edge.beg.isRoot()) roots.add(edge.beg);
		if (edge.end.isRoot()) roots.add(edge.end);
		return ok;
	}

	/** Returns the set of source to destination edges. */
	public Set<E> findEdges(N src, N dst) {
		return src.to(dst);
	}

	public Set<Node<?, ?>> getNodes() {
		return new LinkedHashSet<>(nodes);
	}

	protected void clear() {
		nodes.forEach(n -> n.edges().forEach(e -> e.remove()));
		roots.clear();
		nodes.clear();
	}

	/**
	 * Adds an arbitrary key/value "property" to this graph. If value is
	 * {@code null}, the property will be removed.
	 *
	 * @param key the property key
	 * @param value the new property value
	 * @return the previous property value associated with key, or {@code null} if
	 *             there was no mapping for the key
	 */
	public final Object putProperty(Object key, Object value) {
		if (props == null) {
			props = new LinkedHashMap<>();
		}
		if (value == null) return props.remove(key);
		return props.put(key, value);
	}

	/**
	 * Returns the value of the property with the specified key. Only properties
	 * added with putProperty will return a non-null value.
	 *
	 * @param key the property key
	 * @return the property value associated with key, or {@code null} if there was
	 *             no mapping for the key
	 */
	public final Object getProperty(Object key) {
		if (props == null) return null;
		return props.get(key);
	}

	/**
	 * Returns {@code true} if a property value is associated with the given key.
	 *
	 * @param key the property key
	 * @return {@code true} if a property value is associated with key
	 */
	public final boolean hasProperty(Object key) {
		if (props == null) return false;
		return props.containsKey(key);
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
