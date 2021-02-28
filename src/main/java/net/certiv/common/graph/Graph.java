package net.certiv.common.graph;

import java.util.LinkedHashSet;
import java.util.Set;

import net.certiv.common.graph.Edge.Sense;
import net.certiv.common.util.Assert;

public abstract class Graph<N extends Node<N, E>, E extends Edge<N, E>> {

	/** Individual roots. */
	protected final Set<N> roots = new LinkedHashSet<>();
	/** All nodes. */
	protected final Set<N> nodes = new LinkedHashSet<>();

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

	public Set<E> findEdges(N src, N dst) {
		return src.to(dst);
	}

	protected void clear() {
		nodes.forEach(n -> n.edges().forEach(e -> e.remove()));
		roots.clear();
		nodes.clear();
	}
}
