package net.certiv.common.graph;

import java.util.Collection;
import java.util.function.Predicate;

public interface ITransform<N extends Node<N, E>, E extends Edge<N, E>> {

	/**
	 * Removes the given node from the graph. All connecting edges are removed (and
	 * cleared).
	 *
	 * @param node the node to remove
	 * @return {@code true} if the node was present in the graph and is now removed
	 */
	boolean removeNode(N node);

	/**
	 * Removes the given edge from the graph. Removes either terminal node if the node has
	 * no remaining edge connections.
	 *
	 * @param edge  a graph edge
	 * @param clear {@code true} to clear the edge terminal nodes and properties
	 * @return {@code true} if the edge was removed
	 */
	boolean removeEdge(E edge, boolean clear);

	/**
	 * Removes the given edges from the graph. Removes either terminal node of an edge if
	 * that node has no remaining edge connections.
	 *
	 * @param edges a list of graph edges
	 * @param clear {@code true} to clear the edge terminal nodes and properties
	 * @return {@code true} if all of the edges were removed
	 */
	boolean removeEdges(Collection<? extends E> edges, boolean clear);

	/**
	 * Removes the given edge if the edge satisfies the given filter predicate or if the
	 * filter is {@code null}.
	 *
	 * @param edge   a graph edge
	 * @param clear  {@code true} to clear the edge terminal nodes and properties
	 * @param filter a predicate returning {@code true} to select for removal
	 * @return {@code true} if the edge was removed
	 */
	boolean removeEdgeIf(E edge, boolean clear, Predicate<? super E> filter);

	/**
	 * Removes all edges directly connecting from the given source node to the given
	 * destination node.
	 *
	 * @param src   a source node
	 * @param dst   a destination node
	 * @param clear {@code true} to clear the removed edge terminal nodes and properties
	 * @return {@code true} if the selected edges were removed
	 */
	boolean removeEdges(N src, N dst, boolean clear);

	/**
	 * Removes all edges directly connecting from the given source node to the given
	 * destination node and that satisfy the given filter predicate. All selected edges
	 * are removed if the filter is {@code null}.
	 *
	 * @param src    a source node
	 * @param dst    a destination node
	 * @param clear  {@code true} to clear the edge terminal nodes and properties
	 * @param filter a predicate returning {@code true} to select for removal
	 * @return {@code true} if the selected edges were removed
	 */
	boolean removeEdgesIf(N src, N dst, boolean clear, Predicate<? super E> filter);

	/**
	 * Transfers the subgraph represented by the given edge to depend from the given node.
	 * <p>
	 * Does nothing if the given edge begin node is the same as the given node.
	 * <p>
	 * If the transfer would create a new root node cycle -- the given edge end node is
	 * the same as the given node -- the edge is removed from the graph.
	 * <p>
	 * If the given edge begin node becomes unconnected in the graph, except by
	 * self-cyclic edges, that terminal node is removed from the graph. *
	 *
	 * <pre>
	 * A -> B -> C -> D -> E
	 * C -> F -> G
	 * transfer(CF, B);	// CF becomes BF
	 * A -> B -> C -> D -> E
	 * B -> F -> G
	 * </pre>
	 *
	 * @param edge an edge defining a subgraph
	 * @param beg  the target beg node for the subgraph
	 * @return {@code true} if the edge was transferred
	 */
	boolean transfer(E edge, N beg);

	/**
	 * Transfers the subgraphs represented by the given edges to depend from the given
	 * node.
	 * <p>
	 * Does nothing for a subgraph if its given edge begin node is the same as the given
	 * node.
	 * <p>
	 * If any transfer would create a new root node cycle -- that given edge end node is
	 * the same as the given node -- that edge is removed from the graph.
	 * <p>
	 * If any given edge begin node becomes unconnected in the graph, except by
	 * self-cyclic edges, that terminal node is removed from the graph.
	 *
	 * @param edges the edges defining subgraphs
	 * @param beg   the target beg node for the subgraphs
	 * @return {@code true} if all edges were transferred
	 */
	boolean transfer(Collection<? extends E> edges, N beg);

	/**
	 * Moves the given edge to connect between the given nodes.
	 * <p>
	 * Removes the edge from the graph if the value of the move would create a single edge
	 * cycle.
	 * <p>
	 * If either of the initial terminal nodes of the given edge become unconnected in the
	 * graph, except by self-cyclic edges, that initial terminal node is removed from the
	 * graph.
	 *
	 * @param edge an existing graph edge
	 * @param beg  the new begin node
	 * @param end  the new end node
	 * @return {@code true} if the edge was moved
	 */
	boolean move(E edge, N beg, N end);

	/**
	 * Moves the given edge to connect between the given nodes.
	 * <p>
	 * If {@code cyclic} is {@code false}, removes the edge from the graph if the move
	 * would create a single edge cycle.
	 * <p>
	 * If either of the initial terminal nodes of the given edge become unconnected in the
	 * graph, except by self-cyclic edges, that initial terminal node is removed from the
	 * graph.
	 *
	 * @param edge   an existing graph edge
	 * @param beg    the new begin node
	 * @param end    the new end node
	 * @param cyclic {@code true} to permit creation of single edge cycles
	 * @return {@code true} if the edge was moved
	 */
	boolean move(E edge, N beg, N end, boolean cyclic);

	/**
	 * Moves the given edges to connect between the given nodes.
	 * <p>
	 * If {@code cyclic} is {@code false}, removes the edge from the graph if the move
	 * would create a single edge cycle.
	 * <p>
	 * If either of the initial terminal nodes of any given edge become unconnected in the
	 * graph, except by self-cyclic edges, that initial terminal node is removed from the
	 * graph.
	 *
	 * @param edges  the existing graph edges
	 * @param beg    the new begin node
	 * @param end    the new end node
	 * @param cyclic {@code true} to permit creation of single edge cycles
	 * @return {@code true} if the edges were moved
	 */
	boolean move(Collection<? extends E> edges, N beg, N end, boolean cyclic);

	/**
	 * Reterminate the given edge with the given end node.
	 * <p>
	 * Removes the edge from the graph if retermination would create a single edge cycle.
	 * <p>
	 * If either of the initial terminal nodes of the given edge become unconnected in the
	 * graph, except by self-cyclic edges, that initial terminal node is removed from the
	 * graph.
	 *
	 * @param edge   a graph edge
	 * @param end    a new edge end node
	 * @param cycles {@code true} to permit creation of single edge cycles
	 * @return {@code true} if the edge was reterminated
	 */
	boolean reterminate(E edge, N end);

	/**
	 * Reterminate the given edge with the given end node.
	 * <p>
	 * If {@code cyclic} is {@code false}, removes the edge from the graph if
	 * retermination would create a single edge cycle.
	 * <p>
	 * If either of the initial terminal nodes of the given edge become unconnected in the
	 * graph, except by self-cyclic edges, that initial terminal node is removed from the
	 * graph.
	 *
	 * @param edge   a graph edge
	 * @param end    a new edge end node
	 * @param cycles {@code true} to permit creation of single edge cycles
	 * @return {@code true} if the edge was reterminated
	 */
	boolean reterminate(E edge, N end, boolean cycles);

	/**
	 * Reterminate the given edges with the given end node.
	 * <p>
	 * Removes any edge from the graph if retermination would create a single edge cycle.
	 * <p>
	 * If either of the initial terminal nodes of any given edge become unconnected in the
	 * graph, except by self-cyclic edges, that initial terminal node is removed from the
	 * graph.
	 *
	 * @param edges existing graph edges
	 * @param end   a new edge end node
	 * @return {@code true} if the edges were reterminated
	 */
	boolean reterminate(Collection<? extends E> edges, N end);

	/**
	 * Reterminate the given edges with the given end node.
	 * <p>
	 * If {@code cyclic} is {@code false}, removes any edge from the graph if
	 * retermination would create a single edge cycle.
	 * <p>
	 * If either of the initial terminal nodes of any given edge become unconnected in the
	 * graph, except by self-cyclic edges, that initial terminal node is removed from the
	 * graph.
	 *
	 * @param edges  existing graph edges
	 * @param end    a new edge end node
	 * @param cycles {@code true} to permit creation of single edge cycles
	 * @return {@code true} if the edges were reterminated
	 */
	boolean reterminate(Collection<? extends E> edges, N end, boolean cycles);

	/**
	 * Consolidate the edges connecting to the source nodes to the target node. Excludes
	 * the target node from the source nodes. Removes the finally unconnected source nodes
	 * from the graph.
	 *
	 * <pre>
	 * A => B => C
	 * D => E => F
	 * G => H => I
	 * consolidateEdges([B,E,H], B);	// implicitly removes [E,H]
	 * consolidateEdges([E,H], B);		// equivalent
	 * [A,D,G] => B => [C,F,I]
	 * </pre>
	 *
	 * @param sources collection of edge begin nodes
	 * @param target  a target node
	 * @return
	 */
	boolean consolidateEdges(Collection<? extends N> sources, N target);

	/**
	 * Replicates the existing edge connections with given source node to the given target
	 * nodes. Creates new edge connections to each target node equivalent to the source
	 * node edge connections.
	 * <p>
	 * All replica edges are added to the graph.
	 *
	 * <pre>
	 * A => B => C
	 * replicateEdges(B, [B,X,Y,Z]);
	 * A => [B,X,Y,Z] => C
	 * </pre>
	 *
	 * @param node    a source node
	 * @param targets the target nodes
	 * @return {@code true} on replication success
	 */
	boolean replicateEdges(N node, Collection<? extends N> targets);

	/**
	 * Replicates the existing edge connections with given source node to the given target
	 * nodes. Creates new edge connections to each target node equivalent to the source
	 * node edge connections. Conditionally removes the source node.
	 * <p>
	 * All replica edges are added to the graph.
	 *
	 * <pre>
	 * A => B => C
	 * replicateEdges(B, [B,X,Y,Z], false);
	 * A => [B,X,Y,Z] => C
	 * </pre>
	 *
	 * <pre>
	 * A => B => C
	 * replicateEdges(B, [B,X,Y,Z], true); // removes B
	 * A => [X,Y,Z] => C
	 * </pre>
	 *
	 * @param node    a source node
	 * @param targets the target nodes
	 * @param remove  {@code true} to remove the source node
	 * @return {@code true} on replication success
	 */
	boolean replicateEdges(N node, Collection<? extends N> targets, boolean remove);

	/**
	 * Reduce the graph by removing the given node while retaining the connectivity
	 * between the inbound and outbound nodes.
	 *
	 * <pre>
	 * reduce(B)
	 * A => B => C
	 * becomes
	 * A => C
	 * </pre>
	 *
	 * @param node the term to remove from the graph
	 * @return {@code true} on reduction success
	 */
	boolean reduce(N node);

	/**
	 * Reduce the graph by reterminating the given source edge to the distal node of the
	 * given destination edge. The retermination retains, and appropriately adjusts the
	 * connectivity between the resultant distal inbound and outbound nodes. The given
	 * destination edge is removed, potentially resulting in the shared node being
	 * removed.
	 *
	 * <pre>
	 * reduce(AB,BC)
	 * A -> B -> C
	 * becomes
	 * A -> C
	 * </pre>
	 *
	 * <pre>
	 * reduce(AB,CD)
	 * A -> B
	 * C -> D
	 * becomes
	 * A -> C -> D
	 * </pre>
	 *
	 * @param src the source edge
	 * @param dst the destination edge
	 * @return {@code true} on reduction success
	 */
	boolean reduce(E src, E dst);
}
