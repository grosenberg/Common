package net.certiv.common.graph;

import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.function.Predicate;

import net.certiv.common.ex.Explainer;
import net.certiv.common.graph.Edge.Sense;
import net.certiv.common.graph.XfPolicy.Flg;
import net.certiv.common.graph.ex.GraphEx;
import net.certiv.common.graph.ops.ConsolidateOp;
import net.certiv.common.graph.ops.CopyOp;
import net.certiv.common.graph.ops.ITransformOp;
import net.certiv.common.graph.ops.MoveOp;
import net.certiv.common.graph.ops.ReduceOp;
import net.certiv.common.graph.ops.ReformOp;
import net.certiv.common.graph.ops.RemoveEdgeOp;
import net.certiv.common.graph.ops.RemoveEdgesIfOp;
import net.certiv.common.graph.ops.RemoveNodeOp;
import net.certiv.common.graph.ops.ReplicateOp;
import net.certiv.common.graph.paths.GraphPath;
import net.certiv.common.graph.paths.SubGraph;
import net.certiv.common.log.Log;
import net.certiv.common.stores.Result;
import net.certiv.common.stores.UniqueList;

/**
 * Deferred graph transformer. Accumulates graph transform ops for deferred, sequential
 * application to a graph.
 *
 * <pre>{@code
 * Transfuture<DemoNode, DemoEdge> xf = new Transfuture<>(graph);
 * xf.reterminate(cd, b);
 * xf.removeNode(f);
 * xf.apply();
 * }</pre>
 */
public class Transfuture<N extends Node<N, E>, E extends Edge<N, E>> implements ITransform<N, E> {

	private static final XfPolicy CHECK = XfPolicy.of(XfPolicy.TEST, Flg.Repair);

	private final LinkedList<ITransformOp<N, E>> ops = new LinkedList<>();
	private final Transformer<N, E> xf;
	private final XfPolicy policy;

	/**
	 * Constructor. Enables qualified recording of transform ops for subsequent
	 * application to the given graph, subject to the given execution transform policy.
	 * <p>
	 * Default execution policy: {@link XfPolicy#DEFAULT}.
	 * <p>
	 * Permissive recordation acceptance policy: {@link Flg#Qualify}, {@link Flg#Repair},
	 * {@link Flg#Block}, {@link Flg#Report}.
	 *
	 * @param graph target graph
	 */
	public Transfuture(Graph<N, E> graph) {
		this(graph, XfPolicy.DEFAULT, CHECK);
	}

	/**
	 * Constructor. Enables qualified recording of transform ops for subsequent
	 * application to the given graph, subject to the given execution transform policy.
	 * <p>
	 * Permissive recordation acceptance policy: {@link Flg#Qualify}, {@link Flg#Repair},
	 * {@link Flg#Block}, {@link Flg#Report}.
	 *
	 * @param graph target graph
	 * @param exec  execution policy
	 */
	public Transfuture(Graph<N, E> graph, XfPolicy exec) {
		this(graph, exec, CHECK);
	}

	/**
	 * Constructor. Enables qualified recording of transform ops for subsequent
	 * application to the given graph, subject to the given execution transform policy.
	 *
	 * @param graph target graph
	 * @param exec  execution policy
	 * @param check recordation acceptance policy; {@link Flg#Block} is enforced
	 */
	public Transfuture(Graph<N, E> graph, XfPolicy exec, XfPolicy check) {
		this(new Transformer<>(graph), exec, check);
	}

	/**
	 * Constructor. Enables qualified recording of transform ops for subsequent
	 * application using the given transformer, subject to the given execution transform
	 * policy.
	 * <p>
	 * Default execution policy: {@link XfPolicy#DEFAULT}.
	 * <p>
	 * Permissive recordation acceptance policy: {@link Flg#Qualify}, {@link Flg#Repair},
	 * {@link Flg#Block}, {@link Flg#Report}.
	 *
	 * @param xf reference transformer
	 */
	public Transfuture(Transformer<N, E> xf) {
		this(xf, XfPolicy.DEFAULT, CHECK);
	}

	/**
	 * Constructor. Enables qualified recording of transform ops for subsequent
	 * application using the given transformer, subject to the given execution transform
	 * policy.
	 * <p>
	 * Permissive recordation acceptance policy: {@link Flg#Qualify}, {@link Flg#Repair},
	 * {@link Flg#Block}, {@link Flg#Report}.
	 *
	 * @param xf   reference transformer
	 * @param exec execution policy
	 */
	public Transfuture(Transformer<N, E> xf, XfPolicy exec) {
		this(xf, exec, CHECK);
	}

	/**
	 * Constructor. Enables qualified recording of transform ops for subsequent
	 * application using the given transformer, subject to the given execution transform
	 * policy.
	 *
	 * @param xf    reference transformer
	 * @param exec  execution policy
	 * @param check recordation acceptance policy; {@link Flg#Block} is enforced
	 */
	public Transfuture(Transformer<N, E> xf, XfPolicy exec, XfPolicy check) {
		this.xf = xf;
		this.policy = exec;
		this.xf.setPolicy(XfPolicy.of(check, Flg.Block)); // must block
	}

	/** @return the accumulated transform ops (modifiable) */
	public LinkedList<ITransformOp<N, E>> transforms() {
		return ops;
	}

	/** Reverse the order of the accumulated transform ops. */
	public void reverse() {
		Collections.reverse(ops);
	}

	/** @return {@code true} if there are no accumulated transform ops */
	public boolean isEmpty() {
		return ops.isEmpty();
	}

	/** Clear the accumulated transform ops. */
	public void clear() {
		ops.clear();
	}

	/**
	 * Sequentially apply the recorded transforms.
	 *
	 * @return {@link Result#OK} on success, or (conditional on policy) a
	 *         {@link Result#err} on failure
	 * @throws (conditional on policy) GraphException on failure
	 */
	public Result<Boolean> apply() {
		return apply(false);
	}

	/**
	 * Sequentially apply the recorded transforms.
	 *
	 * @param verbose {@code true} to log each transform immediately prior to execution
	 * @return {@link Result#OK} on success, or (conditional on policy) a
	 *         {@link Result#err} on failure
	 * @throws (conditional on policy) GraphException on failure
	 */
	public Result<Boolean> apply(boolean verbose) {
		xf.graph.lock();
		Explainer xpr = new Explainer("Transfuture");
		try {
			for (ITransformOp<N, E> op : ops) {
				if (verbose) Log.info("XF %s", op);
				try {
					xpr.add(op.apply(xf, policy));
				} catch (Exception | Error e) {
					xpr.add(e);
					if (policy.rptByEx()) throw GraphEx.of(xpr);
				}
			}
			return xpr.isEmpty() ? Result.OK : Result.of(xpr);

		} finally {
			xf.graph.unlock();
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
	 * @return {@code Result#OK} if the op is recorded, otherwise {@code Result.err}
	 *         describing the pre-condition failure
	 */
	@Override
	public Result<Boolean> removeNode(N node) {
		if (!policy.qualify()) {
			ops.add(RemoveNodeOp.of(node));
			return Result.OK;
		}

		Result<Boolean> res = xf.removeNode(node);
		if (res.valid()) ops.add(RemoveNodeOp.of(node));
		return report(res);
	}

	/**
	 * Records the given nodes for removal from the graph.
	 * <p>
	 * Implementation:
	 * <ul>
	 * <li>{@inheritDoc}
	 * </ul>
	 *
	 * @return {@code Result#OK} if the op is recorded, otherwise {@code Result.err} with
	 *         a pre-condition failure explaination
	 */
	@Override
	public Result<Boolean> removeNodes(Collection<? extends N> nodes) {
		if (!policy.qualify()) {
			ops.add(RemoveNodeOp.of(nodes));
			return Result.OK;
		}

		Result<Boolean> res = xf.removeNodes(nodes);
		if (res.valid()) ops.add(RemoveNodeOp.of(nodes));
		return report(res);
	}

	/**
	 * Records the given edge for removal from the graph.
	 * <p>
	 * Implementation:
	 * <ul>
	 * <li>{@inheritDoc}
	 * </ul>
	 *
	 * @return {@code Result#OK} if the op is recorded, otherwise {@code Result.err} with
	 *         a pre-condition failure explaination
	 */
	@Override
	public Result<Boolean> removeEdge(E edge, boolean clear) {
		if (!policy.qualify()) {
			ops.add(RemoveEdgeOp.of(edge, clear));
			return Result.OK;
		}

		Result<Boolean> res = xf.removeEdge(edge, clear);
		if (res.valid()) ops.add(RemoveEdgeOp.of(edge, clear));
		return report(res);
	}

	/**
	 * Records the given edges for removal from the graph.
	 * <p>
	 * Implementation:
	 * <ul>
	 * <li>{@inheritDoc}
	 * </ul>
	 *
	 * @return {@code Result#OK} if the op is recorded, otherwise {@code Result.err} with
	 *         a pre-condition failure explaination
	 */
	@Override
	public Result<Boolean> removeEdges(Collection<? extends E> edges, boolean clear) {
		if (!policy.qualify()) {
			ops.add(RemoveEdgeOp.of(edges, clear));
			return Result.OK;
		}

		Result<Boolean> res = xf.removeEdges(edges, clear);
		if (res.valid()) ops.add(RemoveEdgeOp.of(edges, clear));
		return report(res);
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
	 * @return {@code Result#OK} if the op is recorded, otherwise {@code Result.err} with
	 *         a pre-condition failure explaination
	 */
	@Override
	public Result<Boolean> removeEdgeIf(E edge, boolean clear, Predicate<? super E> filter) {
		if (!policy.qualify()) {
			ops.add(RemoveEdgesIfOp.of(edge, clear, filter));
			return Result.OK;
		}

		Result<Boolean> res = xf.removeEdgeIf(edge, clear, filter);
		if (res.valid()) ops.add(RemoveEdgesIfOp.of(edge, clear, filter));
		return report(res);
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
	 * @return {@code Result#OK} if the op is recorded, otherwise {@code Result.err} with
	 *         a pre-condition failure explaination
	 */
	@Override
	public Result<Boolean> removeEdges(Sense dir, N src, N dst, boolean clear) {
		UniqueList<E> edges = xf.graph.getEdges(dir, src, dst);
		if (!policy.qualify()) {
			ops.add(RemoveEdgeOp.of(edges, clear));
			return Result.OK;
		}

		Result<Boolean> res = xf.removeEdges(edges, clear);
		if (res.valid()) ops.add(RemoveEdgeOp.of(edges, clear));
		return report(res);
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
	 * @return {@code Result#OK} if the op is recorded, otherwise {@code Result.err} with
	 *         a pre-condition failure explaination
	 */
	@Override
	public Result<Boolean> removeEdgesIf(Sense dir, N src, N dst, boolean clear,
			Predicate<? super E> filter) {

		UniqueList<E> edges = xf.graph.getEdges(dir, src, dst);
		if (!policy.qualify()) {
			ops.add(RemoveEdgesIfOp.of(edges, clear, filter));
			return Result.OK;
		}

		Result<Boolean> res = xf.removeEdgesIf(dir, src, dst, clear, filter);
		if (res.valid()) ops.add(RemoveEdgesIfOp.of(edges, clear, filter));
		return report(res);
	}

	/**
	 * Records for removal the given edges, provided the edge satisfies the given filter
	 * predicate.
	 * <p>
	 * Implementation:
	 * <ul>
	 * <li>{@inheritDoc}
	 * </ul>
	 *
	 * @return {@code Result#OK} if the op is recorded, otherwise {@code Result.err} with
	 *         a pre-condition failure explaination
	 */
	@Override
	public Result<Boolean> removeEdgesIf(Collection<? extends E> edges, boolean clear,
			Predicate<? super E> filter) {

		if (!policy.qualify()) {
			ops.add(RemoveEdgesIfOp.of(edges, clear, filter));
			return Result.OK;
		}

		Result<Boolean> res = xf.removeEdgesIf(edges, clear, filter);
		if (res.valid()) ops.add(RemoveEdgesIfOp.of(edges, clear, filter));
		return report(res);
	}

	/**
	 * Records for removal all edges directly connecting from the given path to the graph
	 * and conditionally removes the nodes within the given path.
	 * <p>
	 * Implementation:
	 * <ul>
	 * <li>{@inheritDoc}
	 * </ul>
	 *
	 * @return {@code Result#OK} if the op is recorded, otherwise {@code Result.err} with
	 *         a pre-condition failure explaination
	 */
	@Override
	public Result<Boolean> remove(GraphPath<N, E> path, boolean clear) {
		UniqueList<E> edges = xf.findSubGraphEdges(List.of(path), clear);
		if (!policy.qualify()) {
			ops.add(RemoveEdgeOp.of(edges, clear));
			return Result.OK;
		}

		Result<Boolean> res = xf.removeEdges(edges, clear);
		if (res.valid()) ops.add(RemoveEdgeOp.of(edges, clear));
		return report(res);
	}

	/**
	 * Records for removal all edges directly connecting from the given subgraph to the
	 * graph and conditionally removes the nodes within the given subgraph.
	 * <p>
	 * Implementation:
	 * <ul>
	 * <li>{@inheritDoc}
	 * </ul>
	 *
	 * @return {@code Result#OK} if the op is recorded, otherwise {@code Result.err} with
	 *         a pre-condition failure explaination
	 */
	@Override
	public Result<Boolean> remove(SubGraph<N, E> subgraph, boolean clear) {
		if (!policy.qualify()) {
			UniqueList<E> edges = xf.findSubGraphEdges(subgraph.paths(), clear);
			ops.add(RemoveEdgeOp.of(edges, clear));
			return Result.OK;
		}

		Result<Boolean> res = xf.remove(subgraph, clear);
		if (res.valid()) {
			UniqueList<E> edges = xf.findSubGraphEdges(subgraph.paths(), clear);
			ops.add(RemoveEdgeOp.of(edges, clear));
		}
		return report(res);
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
	 * @return {@code Result#OK} if the op is recorded, otherwise {@code Result.err} with
	 *         a pre-condition failure explaination
	 */
	@Override
	public Result<Boolean> transfer(E edge, N beg) {
		if (!policy.qualify()) {
			ops.add(ReformOp.transfer(edge, beg));
			return Result.OK;
		}

		Result<Boolean> res = xf.transfer(edge, beg);
		if (res.valid()) ops.add(ReformOp.transfer(edge, beg));
		return report(res);
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
	 * @return {@code Result#OK} if the op is recorded, otherwise {@code Result.err} with
	 *         a pre-condition failure explaination
	 */
	@Override
	public Result<Boolean> transfer(Collection<? extends E> edges, N beg) {
		if (!policy.qualify()) {
			ops.add(ReformOp.transfer(edges, beg));
			return Result.OK;
		}

		Result<Boolean> res = xf.transfer(edges, beg);
		if (res.valid()) ops.add(ReformOp.transfer(edges, beg));
		return report(res);
	}

	/**
	 * Records the copying of the given edge into the graph between the given nodes.
	 * <p>
	 * Implementation:
	 * <ul>
	 * <li>{@inheritDoc}
	 * </ul>
	 *
	 * @return {@code Result#OK} if the op is recorded, otherwise {@code Result.err} with
	 *         a pre-condition failure explaination
	 */
	@Override
	public Result<E> copy(E edge, N beg, N end) {
		Result<LinkedList<E>> res = copy(List.of(edge), beg, end, false);
		if (res.err()) return Result.of(res.err);
		return Result.of(res.value.get(0));
	}

	/**
	 * Records the copying of the given edge into the graph between the given nodes.
	 * <p>
	 * Implementation:
	 * <ul>
	 * <li>{@inheritDoc}
	 * </ul>
	 *
	 * @return {@code Result#OK} if the op is recorded, otherwise {@code Result.err} with
	 *         a pre-condition failure explaination
	 */
	@Override
	public Result<E> copy(E edge, N beg, N end, boolean cyclic) {
		Result<LinkedList<E>> res = copy(List.of(edge), beg, end, cyclic);
		if (res.err()) return Result.of(res.err);
		return Result.of(res.value.get(0));
	}

	/**
	 * Records the copying of the given edges into the graph between the given nodes.
	 * <p>
	 * Implementation:
	 * <ul>
	 * <li>{@inheritDoc}
	 * </ul>
	 *
	 * @return {@code Result#OK} if the op is recorded, otherwise {@code Result.err} with
	 *         a pre-condition failure explaination
	 */
	@Override
	public Result<LinkedList<E>> copy(Collection<? extends E> edges, N beg, N end, boolean cyclic) {
		if (!policy.qualify()) {
			ops.add(CopyOp.of(edges, beg, end, cyclic));
			return Result.nil();
		}

		Result<LinkedList<E>> res = xf.copy(edges, beg, end, cyclic);
		if (res.valid()) ops.add(CopyOp.of(edges, beg, end, cyclic));
		return report(res);
	}

	/**
	 * Records the copying the given node into the graph.
	 * <p>
	 * Implementation:
	 * <ul>
	 * <li>{@inheritDoc}
	 * </ul>
	 *
	 * @return {@code Result#OK} if the op is recorded, otherwise {@code Result.err} with
	 *         a pre-condition failure explaination
	 */
	@Override
	public Result<LinkedList<E>> copy(N src, N dst, boolean remove) {
		return copy(List.of(src), dst, remove);
	}

	/**
	 * Records the copying the given nodes into the graph.
	 * <p>
	 * Implementation:
	 * <ul>
	 * <li>{@inheritDoc}
	 * </ul>
	 *
	 * @return {@code Result#OK} if the op is recorded, otherwise {@code Result.err} with
	 *         a pre-condition failure explaination
	 */
	@Override
	public Result<LinkedList<E>> copy(Collection<? extends N> nodes, N dst, boolean remove) {
		if (!policy.qualify()) {
			ops.add(CopyOp.of(nodes, dst, remove));
			return Result.nil();
		}

		Result<LinkedList<E>> res = xf.copy(nodes, dst, remove);
		if (res.valid()) ops.add(CopyOp.of(nodes, dst, remove));
		return report(res);
	}

	/**
	 * Records the copying the given subgraph into the graph.
	 * <p>
	 * Implementation:
	 * <ul>
	 * <li>{@inheritDoc}
	 * </ul>
	 *
	 * @return {@code Result#OK} if the op is recorded, otherwise {@code Result.err} with
	 *         a pre-condition failure explaination
	 */
	@Override
	public Result<LinkedList<E>> copy(SubGraph<N, E> sg, N dst, boolean remove) {
		if (!policy.qualify()) {
			ops.add(CopyOp.of(sg, dst, remove));
			return Result.nil();
		}

		Result<LinkedList<E>> res = xf.copy(sg, dst, remove);
		if (res.valid()) ops.add(CopyOp.of(sg, dst, remove));
		return report(res);
	}

	/**
	 * Records the move of the given edge to connect between the given nodes.
	 * <p>
	 * Implementation:
	 * <ul>
	 * <li>{@inheritDoc}
	 * </ul>
	 *
	 * @return {@code Result#OK} if the op is recorded, otherwise {@code Result.err} with
	 *         a pre-condition failure explaination
	 */
	@Override
	public Result<Boolean> move(E edge, N beg, N end) {
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
	 * @return {@code Result#OK} if the op is recorded, otherwise {@code Result.err} with
	 *         a pre-condition failure explaination
	 */
	@Override
	public Result<Boolean> move(E edge, N beg, N end, boolean cyclic) {
		if (!policy.qualify()) {
			ops.add(MoveOp.of(edge, beg, end, cyclic));
			return Result.OK;
		}

		Result<Boolean> res = xf.move(edge, beg, end, cyclic);
		if (res.valid()) ops.add(MoveOp.of(edge, beg, end, cyclic));
		return report(res);
	}

	/**
	 * Records the move of the given edges to connect between the given nodes.
	 * <p>
	 * Implementation:
	 * <ul>
	 * <li>{@inheritDoc}
	 * </ul>
	 *
	 * @return {@code Result#OK} if the op is recorded, otherwise {@code Result.err} with
	 *         a pre-condition failure explaination
	 */
	@Override
	public Result<Boolean> move(Collection<? extends E> edges, N beg, N end, boolean cyclic) {
		if (!policy.qualify()) {
			ops.add(MoveOp.of(edges, beg, end, cyclic));
			return Result.OK;
		}

		Result<Boolean> res = xf.move(edges, beg, end, cyclic);
		if (res.valid()) ops.add(MoveOp.of(edges, beg, end, cyclic));
		return report(res);
	}

	/**
	 * Records the retermination of the given edge with the given end node.
	 * <p>
	 * Implementation:
	 * <ul>
	 * <li>{@inheritDoc}
	 * </ul>
	 *
	 * @return {@code Result#OK} if the op is recorded, otherwise {@code Result.err} with
	 *         a pre-condition failure explaination
	 */
	@Override
	public Result<Boolean> reterminate(E edge, N end) {
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
	 * @return {@code Result#OK} if the op is recorded, otherwise {@code Result.err} with
	 *         a pre-condition failure explaination
	 */
	@Override
	public Result<Boolean> reterminate(E edge, N end, boolean cyclic) {
		if (!policy.qualify()) {
			ops.add(ReformOp.reterminate(edge, end, cyclic));
			return Result.OK;
		}

		Result<Boolean> res = xf.reterminate(edge, end, cyclic);
		if (res.valid()) ops.add(ReformOp.reterminate(edge, end, cyclic));
		return report(res);
	}

	/**
	 * Records the retermination of the given edges with the given end node.
	 * <p>
	 * Implementation:
	 * <ul>
	 * <li>{@inheritDoc}
	 * </ul>
	 *
	 * @return {@code Result#OK} if the op is recorded, otherwise {@code Result.err} with
	 *         a pre-condition failure explaination
	 */
	@Override
	public Result<Boolean> reterminate(Collection<? extends E> edges, N end) {
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
	 * @return {@code Result#OK} if the op is recorded, otherwise {@code Result.err} with
	 *         a pre-condition failure explaination
	 */
	@Override
	public Result<Boolean> reterminate(Collection<? extends E> edges, N end, boolean cyclic) {
		if (!policy.qualify()) {
			ops.add(ReformOp.reterminate(edges, end, cyclic));
			return Result.OK;
		}

		Result<Boolean> res = xf.reterminate(edges, end, cyclic);
		if (res.valid()) ops.add(ReformOp.reterminate(edges, end, cyclic));
		return report(res);
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
	 * @return {@code Result#OK} if the op is recorded, otherwise {@code Result.err} with
	 *         a pre-condition failure explaination
	 */
	@Override
	public Result<Boolean> consolidateEdges(Collection<? extends N> sources, N target) {
		if (!policy.qualify()) {
			ops.add(ConsolidateOp.of(sources, target));
			return Result.OK;
		}

		Result<Boolean> res = xf.consolidateEdges(sources, target);
		if (res.valid()) ops.add(ConsolidateOp.of(sources, target));
		return report(res);
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
	 * @return {@code Result#OK} if the op is recorded, otherwise {@code Result.err} with
	 *         a pre-condition failure explaination
	 */
	@Override
	public Result<LinkedList<E>> replicateEdges(N node, Collection<? extends N> targets) {
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
	 * @return {@code Result#OK} if the op is recorded, otherwise {@code Result.err} with
	 *         a pre-condition failure explaination
	 */
	@Override
	public Result<LinkedList<E>> replicateEdges(N node, Collection<? extends N> targets, boolean remove) {
		if (!policy.qualify()) {
			ops.add(ReplicateOp.of(node, targets, remove));
			return Result.nil();
		}

		Result<LinkedList<E>> res = xf.replicateEdges(node, targets, remove);
		if (res.valid()) ops.add(ReplicateOp.of(node, targets, remove));
		return report(res);
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
	 * @return {@code Result#OK} if the op is recorded, otherwise {@code Result.err} with
	 *         a pre-condition failure explaination
	 */
	@Override
	public Result<Boolean> reduce(N node) {
		if (!policy.qualify()) {
			ops.add(ReduceOp.of(node));
			return Result.OK;
		}

		Result<Boolean> res = xf.reduce(node);
		if (res.valid()) ops.add(ReduceOp.of(node));
		return report(res);
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
	 * @return {@code Result#OK} if the op is recorded, otherwise {@code Result.err} with
	 *         a pre-condition failure explaination
	 */
	@Override
	public Result<Boolean> reduce(E src, E dst) {
		if (!policy.qualify()) {
			ops.add(ReduceOp.of(src, dst));
			return Result.OK;
		}

		Result<Boolean> res = xf.reduce(src, dst);
		if (res.valid()) ops.add(ReduceOp.of(src, dst));
		return report(res);
	}

	private <T> Result<T> report(Result<T> res) {
		if (res.err()) Log.debug(res.err.getMessage());
		return res;
	}

	@Override
	public String toString() {
		return ops.toString();
	}
}
