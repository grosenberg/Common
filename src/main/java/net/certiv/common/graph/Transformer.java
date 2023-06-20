package net.certiv.common.graph;

import java.util.Collection;
import java.util.LinkedList;
import java.util.function.Predicate;

import net.certiv.common.check.Assert;
import net.certiv.common.graph.Edge.Sense;
import net.certiv.common.graph.ex.GraphEx;
import net.certiv.common.graph.ops.ConsolidateOp;
import net.certiv.common.graph.ops.ITransformOp;
import net.certiv.common.graph.ops.MoveOp;
import net.certiv.common.graph.ops.ReduceOp;
import net.certiv.common.graph.ops.RemoveEdgeOp;
import net.certiv.common.graph.ops.RemoveNodeOp;
import net.certiv.common.graph.ops.ReplicateOp;
import net.certiv.common.stores.LinkedHashList;
import net.certiv.common.stores.UniqueList;

/**
 * Graph transformer. Create and accumulate a sequence of graph transform ops for deferred
 * application to a graph.
 *
 * <pre>
 * Transformer<DemoNode, DemoEdge> xf = new Transformer<>(graph);
 * xf.transfer(cd, b);
 * xf.removeNode(f);
 * xf.exec();
 * </pre>
 */
public class Transformer<N extends Node<N, E>, E extends Edge<N, E>> implements ITransform<N, E> {

	private final Graph<N, E> graph;
	private final LinkedList<ITransformOp<N, E>> ops = new LinkedList<>();

	public Transformer(Graph<N, E> graph) {
		this.graph = graph;
	}

	/**
	 * @return the accumulated transforms.
	 */
	public LinkedList<ITransformOp<N, E>> getTransformOps() {
		return ops;
	}

	/**
	 * Execute/apply the recorded transforms.
	 *
	 * @return {@code true} on success
	 */
	public boolean exec() {
		graph.lock();
		try {
			boolean ok = true;
			for (ITransformOp<N, E> op : ops) {
				ok &= op.exec(graph);
			}
			return ok;

		} finally {
			graph.unlock();
		}
	}

	// ---- Record operations ---------

	/**
	 * Records the given node for removal from the graph.
	 * <p>
	 * Implementation:
	 * <ul>
	 * <li>{@inheritDoc}
	 * </ul>
	 *
	 * @return {@code true} if the op is recorded
	 * @see Graph#removeNode(Node)
	 */
	@Override
	public boolean removeNode(N node) {
		Assert.notNull(node);
		if (!graph.contains(node)) return false;
		return ops.add(RemoveNodeOp.of(node));
	}

	/**
	 * Records the given edge for removal from the graph.
	 * <p>
	 * Implementation:
	 * <ul>
	 * <li>{@inheritDoc}
	 * </ul>
	 *
	 * @return {@code true} if the op is recorded
	 * @see Graph#removeEdge(Edge, boolean)
	 */
	@Override
	public boolean removeEdge(E edge, boolean clear) {
		Assert.notNull(edge);
		if (!graph.contains(edge)) return false;
		return ops.add(RemoveEdgeOp.of(edge, clear));
	}

	/**
	 * Records the given edges for removal from the graph.
	 * <p>
	 * Implementation:
	 * <ul>
	 * <li>{@inheritDoc}
	 * </ul>
	 *
	 * @return {@code true} if the op is recorded
	 * @see Graph#removeEdges(Collection, boolean)
	 */
	@Override
	public boolean removeEdges(Collection<? extends E> edges, boolean clear) {
		Assert.notNull(edges);
		return edges.stream().allMatch(e -> removeEdge(e, clear));
	}

	/**
	 * Records the given edges for removal from the graph if the edge satisfies the given
	 * filter predicate or if the filter is {@code null}.
	 * <p>
	 * Implementation:
	 * <ul>
	 * <li>{@inheritDoc}
	 * </ul>
	 *
	 * @return {@code true} if the op is recorded
	 * @see Graph#removeEdgeIf(Edge, boolean, Predicate)
	 */
	@Override
	public boolean removeEdgeIf(E edge, boolean clear, Predicate<? super E> filter) {
		Assert.notNull(edge);
		if (filter != null && !filter.test(edge)) return false;
		return removeEdge(edge, clear);
	}

	/**
	 * Records for removal all edges directly connecting from the given source node to the
	 * given destination node.
	 * <p>
	 * Implementation:
	 * <ul>
	 * <li>{@inheritDoc}
	 * </ul>
	 *
	 * @return {@code true} if the op is recorded
	 * @see Graph#removeEdges(Node, Node, boolean)
	 */
	@Override
	public boolean removeEdges(N src, N dst, boolean clear) {
		Assert.notNull(src, dst);
		UniqueList<E> edges = src.to(dst);
		if (edges.isEmpty()) return false;
		return edges.stream().allMatch(e -> removeEdge(e, clear));
	}

	/**
	 * Records for removal all edges directly connecting from the given source node to the
	 * given destination node, provided the edge satisfies the given filter predicate.
	 * <p>
	 * Implementation:
	 * <ul>
	 * <li>{@inheritDoc}
	 * </ul>
	 *
	 * @return {@code true} if the op is recorded
	 * @see Graph#removeEdgesIf(Node, Node, boolean, Predicate)
	 */
	@Override
	public boolean removeEdgesIf(N src, N dst, boolean clear, Predicate<? super E> filter) {
		Assert.notNull(src, dst);
		UniqueList<E> edges = src.to(dst);
		if (edges.isEmpty()) return false;
		if (filter == null) return edges.stream().allMatch(e -> removeEdge(e, clear));
		return edges.stream().filter(filter).allMatch(e -> removeEdge(e, clear));
	}

	/**
	 * Records the transfer of the subgraph represented by the given edge to depend from
	 * the given node.
	 * <p>
	 * Implementation:
	 * <ul>
	 * <li>{@inheritDoc}
	 * </ul>
	 *
	 * @return {@code true} if the op is recorded
	 * @see Graph#transfer(Edge, Node)
	 */
	@Override
	public boolean transfer(E edge, N beg) {
		Assert.notNull(edge, beg);
		if (edge.beg().equals(beg)) return false;
		return move(edge, beg, edge.end(), false);
	}

	/**
	 * Records the transfer of the subgraphs represented by the given edges to depend from
	 * the given node.
	 * <p>
	 * Implementation:
	 * <ul>
	 * <li>{@inheritDoc}
	 * </ul>
	 *
	 * @return {@code true} if the op is recorded
	 * @see Graph#transfer(Collection, Node)
	 */
	@Override
	public boolean transfer(Collection<? extends E> edges, N beg) {
		Assert.notNull(edges, beg);
		return edges.stream().allMatch(e -> transfer(e, beg));
	}

	/**
	 * Copies the given subgraph into the graph.
	 * <p>
	 * Implementation:
	 * <ul>
	 * <li>{@inheritDoc}
	 * </ul>
	 *
	 * @return {@code true} if the op is recorded
	 * @see Graph#copy(LinkedHashList, Node, boolean)
	 */
	@Override
	public boolean copy(LinkedHashList<N, E> sg, N dst, boolean remove) {
		return false;
	}

	/**
	 * Records the move of the given edge to connect between the given nodes.
	 * <p>
	 * Implementation:
	 * <ul>
	 * <li>{@inheritDoc}
	 * </ul>
	 *
	 * @return {@code true} if the op is recorded
	 * @see Graph#move(Edge, Node, Node)
	 */
	@Override
	public boolean move(E edge, N beg, N end) {
		return move(edge, beg, end, false);
	}

	/**
	 * Records the move of the given edge to connect between the given nodes.
	 * <p>
	 * Implementation:
	 * <ul>
	 * <li>{@inheritDoc}
	 * </ul>
	 *
	 * @return {@code true} if the op is recorded
	 * @see Graph#move(Edge, Node, Node, boolean)
	 */
	@Override
	public boolean move(E edge, N beg, N end, boolean cyclic) {
		Assert.notNull(edge, beg, end);

		if (edge.beg().equals(beg) && edge.end().equals(end)) return false;
		if (beg.equals(edge.end()) && !cyclic) { // impermissble self cycle
			ops.add(RemoveEdgeOp.of(edge, true));
			return false;
		}
		return ops.add(MoveOp.of(edge, beg, end, cyclic));
	}

	/**
	 * Records the move of the given edges to connect between the given nodes.
	 * <p>
	 * Implementation:
	 * <ul>
	 * <li>{@inheritDoc}
	 * </ul>
	 *
	 * @return {@code true} if the op is recorded
	 * @see Graph#move(Collection, Node, Node, boolean)
	 */
	@Override
	public boolean move(Collection<? extends E> edges, N beg, N end, boolean cyclic) {
		Assert.notNull(edges);
		return edges.stream().allMatch(e -> move(e, beg, end, cyclic));
	}

	/**
	 * Records the retermination of the given edge with the given end node.
	 * <p>
	 * Implementation:
	 * <ul>
	 * <li>{@inheritDoc}
	 * </ul>
	 *
	 * @return {@code true} if the op is recorded
	 * @see Graph#reterminate(Edge, Node)
	 */
	@Override
	public boolean reterminate(E edge, N end) {
		return reterminate(edge, end, false);
	}

	/**
	 * Records the retermination of the given edge with the given end node.
	 * <p>
	 * Implementation:
	 * <ul>
	 * <li>{@inheritDoc}
	 * </ul>
	 *
	 * @return {@code true} if the op is recorded
	 * @see Graph#reterminate(Edge, Node, boolean)
	 */
	@Override
	public boolean reterminate(E edge, N end, boolean cycles) {
		Assert.notNull(edge, end);
		if (edge.end().equals(end)) return false;
		return move(edge, edge.beg(), end, cycles);
	}

	/**
	 * Records the retermination of the given edges with the given end node.
	 * <p>
	 * Implementation:
	 * <ul>
	 * <li>{@inheritDoc}
	 * </ul>
	 *
	 * @return {@code true} if the op is recorded
	 * @see Graph#reterminate(Collection, Node)
	 */
	@Override
	public boolean reterminate(Collection<? extends E> edges, N end) {
		return reterminate(edges, end, false);
	}

	/**
	 * Records the retermination of the given edges with the given end node.
	 * <p>
	 * Implementation:
	 * <ul>
	 * <li>{@inheritDoc}
	 * </ul>
	 *
	 * @return {@code true} if the op is recorded
	 * @see Graph#reterminate(Collection, Node, boolean)
	 */
	@Override
	public boolean reterminate(Collection<? extends E> edges, N end, boolean cycles) {
		Assert.notNull(edges, end);
		return edges.stream().allMatch(e -> reterminate(e, end, cycles));
	}

	/**
	 * Records the consolidatation of the edges connecting to the source nodes to the
	 * target node.
	 * <p>
	 * Implementation:
	 * <ul>
	 * <li>{@inheritDoc}
	 * </ul>
	 *
	 * @return {@code true} if the op is recorded
	 * @see Graph#consolidateEdges(Collection, Node)
	 */
	@Override
	public boolean consolidateEdges(Collection<? extends N> sources, N target) {
		Assert.notNull(sources, target);
		return ops.add(ConsolidateOp.of(sources, target));
	}

	/**
	 * Records the replication of the existing edge connections with given source node to
	 * the given target nodes.
	 * <p>
	 * Implementation:
	 * <ul>
	 * <li>{@inheritDoc}
	 * </ul>
	 *
	 * @return {@code true} if the op is recorded
	 * @see Graph#replicateEdges(Node, Collection)
	 */
	@Override
	public boolean replicateEdges(N node, Collection<? extends N> targets) {
		return replicateEdges(node, targets, false);
	}

	/**
	 * Records the replication of the existing edge connections with given source node to
	 * the given target nodes.
	 * <p>
	 * Implementation:
	 * <ul>
	 * <li>{@inheritDoc}
	 * </ul>
	 *
	 * @return {@code true} if the op is recorded
	 * @see Graph#replicateEdges(Node, Collection, boolean)
	 */
	@Override
	public boolean replicateEdges(N node, Collection<? extends N> targets, boolean remove) {
		return ops.add(ReplicateOp.of(node, targets, remove));
	}

	/**
	 * Records a reduction of the graph by removing the given node while retaining the
	 * connectivity between the inbound and outbound nodes.
	 * <p>
	 * Implementation:
	 * <ul>
	 * <li>{@inheritDoc}
	 * </ul>
	 *
	 * @return {@code true} if the op is recorded
	 * @see Graph#reduce(Node)
	 */
	@Override
	public boolean reduce(N node) {
		UniqueList<E> srcs = node.edges(Sense.IN, false);	// A =>
		UniqueList<E> dsts = node.edges(Sense.OUT, false);	// => C

		boolean ok = true;
		for (E src : srcs) {
			for (E dst : dsts) {
				ok &= reduce(src, dst);
			}
		}
		return ok;
	}

	/**
	 * Records a reduction of the graph by reterminating the given source edge to the
	 * distal node of the given destination edge.
	 * <p>
	 * Implementation:
	 * <ul>
	 * <li>{@inheritDoc}
	 * </ul>
	 *
	 * @return {@code true} if the op is recorded
	 * @see Graph#reduce(Edge, Edge)
	 */
	@Override
	public boolean reduce(E src, E dst) {
		Assert.isTrue(GraphEx.of("Invalid Edge"), src.valid() && dst.valid());
		return ops.add(ReduceOp.of(src, dst));
	}
}
