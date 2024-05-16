package net.certiv.common.graph;

import java.util.function.Predicate;

import net.certiv.common.graph.id.IUId;
import net.certiv.common.stores.UniqueList;

/** Interface for a node edge set. */
public interface IEdgeSet<I extends IUId, N extends Node<I, N, E>, E extends Edge<I, N, E>> {

	N owner();

	void setOwner(N node);

	void add(E edge);

	/**
	 * Removes the given edge from this edge set.
	 *
	 * @param edge the edge to remove
	 * @return {@code true} if the edge was present and is now removed
	 */
	boolean remove(E edge);

	/**
	 * Checks whether any edge in this set contains the given node as its distal node.
	 *
	 * @param node a node
	 * @return {@code true} if any edge in this set contains the given distal node
	 */
	boolean isAdjacent(N node);

	/**
	 * Returns a unique list of all distal nodes that are connected by any edge in this
	 * edge set, including cyclic edges.
	 *
	 * @return all adjacent nodes
	 */
	UniqueList<N> adjacent();

	/**
	 * Returns a unique list of all distal nodes that are connected by any edge in this
	 * edge set, including cyclic edges, qualified by the given filter. A {@code null}
	 * filter is treated equivalent to no filter, <i>i.e.</i> include all.
	 */
	UniqueList<N> adjacent(Predicate<? super N> filter);

	boolean hasEdge(E edge);

	UniqueList<E> edges();

	UniqueList<E> edges(N node);

	/**
	 * Returns the set of edges constrained by the given filter on the set of edges, or
	 * all edges if the filter is {@code null}.
	 */
	UniqueList<E> edges(Predicate<? super E> filter);

	/** Returns the count of connected edges. */
	int size();

	void clear();

}
