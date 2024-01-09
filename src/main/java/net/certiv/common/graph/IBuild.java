package net.certiv.common.graph;

import net.certiv.common.graph.Edge.Sense;
import net.certiv.common.graph.ex.GraphEx;
import net.certiv.common.graph.ex.GraphException;
import net.certiv.common.graph.id.Id;
import net.certiv.common.stores.UniqueList;

public interface IBuild<I extends Id, N extends Node<I, N, E>, E extends Edge<I, N, E>> {

	GraphException ERR_EDGE = GraphEx.of("Invalid edge.");

	/**
	 * Returns {@code true} if any edge exists between the given nodes.
	 *
	 * @param src a source node
	 * @param dst a destination node
	 * @return {@code true} if any edge exists
	 */
	boolean hasEdge(N src, N dst);

	/**
	 * Returns the unique set of all non-cyclic edges.
	 *
	 * @return the unique set of existing edges
	 */
	UniqueList<E> getEdges();

	/**
	 * Returns the unique set of graph edges, conditionally including cyclic edges.
	 *
	 * @param cyclic {@code true} to include cyclic edges
	 * @return the unique set of existing edges
	 */
	UniqueList<E> getEdges(boolean cyclic);

	/**
	 * Returns the unique set of edges existing between the given nodes.
	 *
	 * @param src a source node
	 * @param dst a destination node
	 * @return the edges existing between the given nodes
	 */
	UniqueList<E> getEdges(N src, N dst);

	/**
	 * Returns the unique set of edges existing between the given nodes, constrained by
	 * the given direction.
	 *
	 * @param dir the connected edge direction criteria
	 * @param src a source node
	 * @param dst a destination node
	 * @return the edges existing between the given nodes
	 */
	UniqueList<E> getEdges(Sense dir, N src, N dst);

}
