package net.certiv.common.graph;

import java.util.Collection;
import java.util.LinkedList;
import java.util.function.Predicate;

import net.certiv.common.ex.Explainer;
import net.certiv.common.graph.Edge.Sense;
import net.certiv.common.graph.ex.GraphEx;
import net.certiv.common.graph.ex.GraphException;
import net.certiv.common.graph.paths.GraphPath;
import net.certiv.common.graph.paths.SubGraph;
import net.certiv.common.stores.Result;

public interface ITransform<N extends Node<N, E>, E extends Edge<N, E>> {

	GraphException ERR_SENSE = GraphEx.of("Sense is null");
	GraphException ERR_NO_PATH = GraphEx.of("GraphPath is null");
	GraphException ERR_EDGE = GraphEx.of("Invalid edge.");
	GraphException ERR_COPY_FIND = GraphEx.of("Copy: added node not found: %s");

	String NODE_NULL = "Node %s is null";
	String NODE_LIST_NULL = "Node list is null";

	String EDGE_NULL = "Edge %s is null";
	String EDGE_LIST_NULL = "Edge list is null";
	String EDGE_NODE_NULL = "Edge %s %s node is null";
	String EDGE_FILTER_FAIL = "Edge %s %s filter failed";

	String NO_GRAPH_NODE = "Graph does not contain node %s %s";
	String NO_GRAPH_EDGE_NODE = "Graph does not contain edge %s %s node %s";

	String BEGIN = "begin";
	String END = "end";

	// ---- Operations ---------

	/**
	 * Removes the given node from the graph. All connecting edges are removed and
	 * cleared.
	 *
	 * @param node the node to remove
	 * @return {@link Result#OK} on success, or {@link Result#err()} explaining the
	 *         failure
	 */
	Result<Boolean> removeNode(N node);

	/**
	 * Removes the given nodes from the graph. All connecting edges are removed and
	 * cleared.
	 *
	 * @param nodes the nodes to remove
	 * @return {@link Result#OK} on success, or {@link Result#err()} explaining the
	 *         failure
	 */
	Result<Boolean> removeNodes(Collection<? extends N> nodes);

	/**
	 * Removes the given edge from the graph. Removes either terminal node if the node has
	 * no remaining edge connections.
	 *
	 * @param edge  a graph edge
	 * @param clear {@code true} to clear the edge terminal nodes and properties
	 * @return {@link Result#OK} on success, or {@link Result#err()} explaining the
	 *         failure
	 */
	Result<Boolean> removeEdge(E edge, boolean clear);

	/**
	 * Removes the given edges from the graph. Removes either terminal node of an edge if
	 * that node has no remaining edge connections.
	 *
	 * @param edges a list of graph edges
	 * @param clear {@code true} to clear the edge terminal nodes and properties
	 * @return {@link Result#OK} on success, or {@link Result#err()} explaining the
	 *         failure
	 */
	Result<Boolean> removeEdges(Collection<? extends E> edges, boolean clear);

	/**
	 * Removes the given edge if the edge satisfies the given filter predicate or if the
	 * filter is {@code null}. Non-removal of an edge because the edge does not satisfiy
	 * the filter predicate is not a failure.
	 *
	 * @param edge   a graph edge
	 * @param clear  {@code true} to clear the edge terminal nodes and properties
	 * @param filter a predicate returning {@code true} to select for removal
	 * @return {@link Result#OK} on success, or {@link Result#err()} explaining the
	 *         failure
	 */
	Result<Boolean> removeEdgeIf(E edge, boolean clear, Predicate<? super E> filter);

	/**
	 * Removes all edges, constrained to the given direction sense, directly connecting
	 * the given source and destination nodes.
	 *
	 * @param dir   edge direction {@link Sense}
	 * @param src   a source node
	 * @param dst   a destination node
	 * @param clear {@code true} to clear the removed edge terminal nodes and properties
	 * @return {@link Result#OK} on success, or {@link Result#err()} explaining the
	 *         failure
	 */
	Result<Boolean> removeEdges(Sense dir, N src, N dst, boolean clear);

	/**
	 * Removes all edges, constrained to the given direction sense, directly connecting
	 * the given source and destination nodes that satisfy the given filter predicate. A
	 * {@code null} filter functionally tests as {@code true}. Non-removal of an edge
	 * because the edge does not satisfiy the filter predicate is not a failure.
	 *
	 * @param dir    edge direction {@link Sense}
	 * @param src    a source node
	 * @param dst    a destination node
	 * @param clear  {@code true} to clear the edge terminal nodes and properties
	 * @param filter a predicate returning {@code true} to select for removal
	 * @return {@link Result#OK} on success, or {@link Result#err()} explaining the
	 *         failure
	 */
	Result<Boolean> removeEdgesIf(Sense dir, N src, N dst, boolean clear, Predicate<? super E> filter);

	/**
	 * Removes the given edges that satisfy the given filter predicate. A {@code null}
	 * filter functionally tests as {@code true}. Non-removal of an edge because the edge
	 * does not satisfiy the filter predicate is not a failure.
	 *
	 * @param edges  edges to conditionally remove
	 * @param clear  {@code true} to clear the edge terminal nodes and properties
	 * @param filter a predicate returning {@code true} to select for removal
	 * @return {@link Result#OK} on success, or {@link Result#err()} explaining the
	 *         failure
	 */
	Result<Boolean> removeEdgesIf(Collection<? extends E> edges, boolean clear, Predicate<? super E> filter);

	/**
	 * Removes all edges connecting the given path to the graph and conditionally removes
	 * the nodes contained within the path from the graph.
	 *
	 * @param path  a graph path
	 * @param clear {@code true} to remove and clear the properties of the nodes and edges
	 *              contained within this path
	 * @return {@link Result#OK} on success, or {@link Result#err()} explaining the
	 *         failure
	 */
	Result<Boolean> remove(GraphPath<N, E> path, boolean clear);

	/**
	 * Removes all edges connecting the given subgraph to the graph and conditionally
	 * removes the nodes contained within the subgraph from the graph.
	 *
	 * @param sg    subgraph set of GraphPaths
	 * @param clear {@code true} to remove and clear the properties of the nodes and edges
	 *              contained within this subgraph
	 * @return {@link Result#OK} on success, or {@link Result#err()} explaining the
	 *         failure
	 */
	Result<Boolean> remove(SubGraph<N, E> sg, boolean clear);

	/**
	 * Transfers the subgraph represented by the given edge to depend from the given node.
	 * <p>
	 * Does nothing if the given edge begin node is the same as the given node.
	 * <p>
	 * If the transfer would create a new root node cycle -- the given edge end node is
	 * the same as the given node -- the edge is removed from the graph.
	 * <p>
	 * If the given edge begin node becomes unconnected in the graph, except by
	 * self-cyclic edges, that terminal node is removed from the graph.
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
	 * @return {@link Result#OK} on success, or {@link Result#err()} explaining the
	 *         failure
	 */
	Result<Boolean> transfer(E edge, N beg);

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
	 * @return {@link Result#OK} on success, or {@link Result#err()} explaining the
	 *         failure
	 */
	Result<Boolean> transfer(Collection<? extends E> edges, N beg);

	/**
	 * Copies the given edge to connect between the given nodes, provided the copy would
	 * not create a single edge cycle.
	 *
	 * @param edge an existing graph edge
	 * @param beg  the new begin node
	 * @param end  the new end node
	 * @return {@link Result} containing the new edge, or {@link Result#err()} explaining
	 *         the failure
	 */
	Result<E> copy(E edge, N beg, N end);

	/**
	 * Copies the given edge to connect between the given nodes.
	 * <p>
	 * If {@code cyclic} is {@code false}, no edge is created if the copy would create a
	 * single edge cycle.
	 *
	 * @param edge   an existing graph edge
	 * @param beg    the new begin node
	 * @param end    the new end node
	 * @param cyclic {@code true} to permit creation of single edge cycles
	 * @return {@link Result} containing the new edge, or {@link Result#err()} explaining
	 *         the failure
	 */
	Result<E> copy(E edge, N beg, N end, boolean cyclic);

	/**
	 * Copies the given edges to connect between the given nodes.
	 * <p>
	 * If {@code cyclic} is {@code false}, edges will not be created if the copy would
	 * create a single edge cycle.
	 *
	 * @param edges  the existing graph edges
	 * @param beg    the new begin node
	 * @param end    the new end node
	 * @param cyclic {@code true} to permit creation of single edge cycles
	 * @return {@link Result} containing the new edges, or {@link Result#err()} explaining
	 *         the failure
	 */
	Result<LinkedList<E>> copy(Collection<? extends E> edges, N beg, N end, boolean cyclic);

	/**
	 * Copies the given subgraph into the graph. Both nodes and edges of the subgraph are
	 * replicated. The destination node defines the inbound and outbound edges to the
	 * copied subgraph. The destination node is effectively replaced by the subgraph when
	 * {@code remove} is {@code true}.
	 *
	 * @param sg     subgraph set of GraphPaths
	 * @param dst    destination node
	 * @param remove {@code true} to remove the destination node
	 * @return {@link Result} containing the new edges, or {@link Result#err()} explaining
	 *         the failure
	 */
	Result<LinkedList<E>> copy(SubGraph<N, E> sg, N dst, boolean remove);

	/**
	 * Copies the given individual source node into the graph. The source node is
	 * replicated. The destination node defines the inbound and outbound edges that are
	 * copied and connected to the replicated node. Removes the destination node from the
	 * graph if {@code remove} is {@code true}.
	 *
	 * <pre>
	 * Given:
	 * A => B => C
	 * X => D => Y
	 * </pre>
	 *
	 * <pre>
	 * copy(b, d, false);
	 * A => B => C
	 * X => [B',D] => Y
	 * </pre>
	 *
	 * <pre>
	 * copy(b, d, true);
	 * A => B => C
	 * X => B' => Y
	 * </pre>
	 *
	 * @param src    source node
	 * @param dst    destination node
	 * @param remove {@code true} to remove the destination node
	 * @return {@link Result} containing the new edges, or {@link Result#err()} explaining
	 *         the failure
	 */
	Result<LinkedList<E>> copy(N src, N dst, boolean remove);

	/**
	 * Copies the given individual source nodes into the graph. Each source node is
	 * replicated. The destination node defines the inbound and outbound edges that are
	 * copied and connected to the replicated nodes. Removes the destination node from the
	 * graph if {@code remove} is {@code true}.
	 *
	 * <pre>
	 * Given:
	 * A => B => C
	 * X => D => Y
	 * </pre>
	 *
	 * <pre>
	 * copy(b, d, false);
	 * A => B => C
	 * X => [B',D] => Y
	 * </pre>
	 *
	 * <pre>
	 * copy(b, d, true);
	 * A => B => C
	 * X => B' => Y
	 * </pre>
	 *
	 * @param src    source nodes
	 * @param dst    destination node
	 * @param remove {@code true} to remove the destination node
	 * @return {@link Result} containing the new edges, or {@link Result#err()} explaining
	 *         the failure
	 */
	Result<LinkedList<E>> copy(Collection<? extends N> src, N dst, boolean remove);

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
	 * @return {@link Result#OK} on success, or {@link Result#err()} explaining the
	 *         failure
	 */
	Result<Boolean> move(E edge, N beg, N end);

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
	 * @return {@link Result#OK} on success, or {@link Result#err()} explaining the
	 *         failure
	 */
	Result<Boolean> move(E edge, N beg, N end, boolean cyclic);

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
	 * @return {@link Result#OK} on success, or {@link Explainer} describing failure
	 */
	Result<Boolean> move(Collection<? extends E> edges, N beg, N end, boolean cyclic);

	/**
	 * Reterminate the given edge with the given end node.
	 * <p>
	 * Removes the edge from the graph if retermination would create a single edge cycle.
	 * <p>
	 * If either of the initial terminal nodes of the given edge become unconnected in the
	 * graph, except by self-cyclic edges, that initial terminal node is removed from the
	 * graph.
	 *
	 * @param edge a graph edge
	 * @param end  a new edge end node
	 * @return {@link Result#OK} on success, or {@link Result#err()} explaining the
	 *         failure
	 */
	Result<Boolean> reterminate(E edge, N end);

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
	 * @param cyclic {@code true} to permit creation of single edge cycles
	 * @return {@link Result#OK} on success, or {@link Result#err()} explaining the
	 *         failure
	 */
	Result<Boolean> reterminate(E edge, N end, boolean cyclic);

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
	 * @return {@link Result#OK} on success, or {@link Result#err()} explaining the
	 *         failure
	 */
	Result<Boolean> reterminate(Collection<? extends E> edges, N end);

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
	 * @param cyclic {@code true} to permit creation of single edge cycles
	 * @return {@link Result#OK} on success, or {@link Result#err()} explaining the
	 *         failure
	 */
	Result<Boolean> reterminate(Collection<? extends E> edges, N end, boolean cyclic);

	/**
	 * Consolidate the edges connecting to the source nodes to the target node. Excludes
	 * the target node from the source nodes. Removes the finally unconnected source nodes
	 * from the graph.
	 * <p>
	 * Existing cycles are moved. May create new cycles.
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
	 * @return {@link Result#OK} on success, or {@link Result#err()} explaining the
	 *         failure
	 */
	Result<Boolean> consolidateEdges(Collection<? extends N> sources, N target);

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
	 * @return {@link Result#OK} on success, or {@link Result#err()} explaining the
	 *         failure
	 */
	Result<LinkedList<E>> replicateEdges(N node, Collection<? extends N> targets);

	/**
	 * Replicates the existing edge connections with given source node to the given target
	 * nodes. Creates new edge connections to each target node equivalent to the source
	 * node edge connections. Conditionally removes the source node.
	 * <p>
	 * All replica edges are added to the graph.
	 *
	 * <pre>
	 * builder.createAndAddEdges("A => B => C");
	 * builder.findOrCreateNodes("[B,X,Y,Z]");
	 * replicateEdges(b, [b,x,y,z], false);
	 * a => [b,x,y,z] => c
	 * </pre>
	 *
	 * <pre>
	 * replicateEdges(B, [B,X,Y,Z], true); // removes B
	 * a => [x,y,z] => c
	 * </pre>
	 *
	 * @param node    a source node
	 * @param targets the target nodes
	 * @param remove  {@code true} to remove the source node
	 * @return {@link Result#OK} on success, or {@link Result#err()} explaining the
	 *         failure
	 */
	Result<LinkedList<E>> replicateEdges(N node, Collection<? extends N> targets, boolean remove);

	/**
	 * Reduce the graph relative to the given node by joining each of the inbound edges to
	 * each of the outbound edges and then removing the given node.
	 * <p>
	 * {@code [X] => N => [Y]} becomes {@code [X] => [Y]}
	 *
	 * @param node the node to be reduced
	 * @return {@link Result#OK} on success, or {@link Result#err()} explaining the
	 *         failure
	 */
	Result<Boolean> reduce(N node);

	/**
	 * Reduce the graph by reterminating the given source edge to the distal node of the
	 * given destination edge. The retermination retains, and appropriately adjusts the
	 * connectivity between the resultant distal inbound and outbound nodes. The given
	 * destination edge is removed, potentially resulting in the shared node being
	 * removed.
	 * <p>
	 * {@code reduce(A->B, B->C)} yields {@code A -> C}
	 * <p>
	 * {@code reduce(A->B, C->D)} yields {@code A -> C -> D}
	 *
	 * @param src the source edge
	 * @param dst the destination edge
	 * @return {@link Result#OK} on success, or {@link Result#err()} explaining the
	 *         failure
	 */
	Result<Boolean> reduce(E src, E dst);
}
