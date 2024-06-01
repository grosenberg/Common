package net.certiv.common.graph;

import static net.certiv.common.graph.XfPermits.*;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import net.certiv.common.ex.Explainer;
import net.certiv.common.graph.Edge.Sense;
import net.certiv.common.graph.ex.GraphEx;
import net.certiv.common.graph.paths.GraphPath;
import net.certiv.common.graph.paths.SubGraph;
import net.certiv.common.id.IUId;
import net.certiv.common.stores.Result;
import net.certiv.common.stores.UniqueList;
import net.certiv.common.util.Strings;

/** In-place graph transformer. */
public class Transformer<I extends IUId, N extends Node<I, N, E>, E extends Edge<I, N, E>>
		implements ITransform<I, N, E> {

	public final Graph<I, N, E> graph;
	private XfPolicy policy;

	/**
	 * Constructs a {@link XfPolicy#REPORT} transformer for the given graph.
	 */
	public Transformer(Graph<I, N, E> graph) {
		this(graph, XfPolicy.REPORT);
	}

	/**
	 * Constructs a transformer for the given graph with the given {@link XfPolicy} value.
	 */
	public Transformer(Graph<I, N, E> graph, XfPolicy policy) {
		this.graph = graph;
		this.policy = policy;
	}

	public XfPolicy getPolicy() {
		return policy;
	}

	public void setPolicy(XfPolicy policy) {
		this.policy = policy;
	}

	@Override
	public Result<Boolean> removeNode(N node) {
		return removeNode(policy, node);
	}

	public Result<Boolean> removeNode(XfPolicy policy, N node) {
		graph.lock();
		Explainer xpr = new Explainer("Remove node");
		boolean ok = xpr.last();

		try {
			if (!graph.permits(REMOVE_NODE)) {
				if (policy.rptByRet()) return Result.of(xpr.reason(REMOVE_NODE.err()));
				throw GraphEx.of(REMOVE_NODE.err());
			}

			if (policy.qualify()) {
				ok &= chkNode(xpr, ok, node, !policy.repair());

				if (ok && policy.block()) return Result.OK;
				if (!ok && (policy.block() || policy.condStop())) {
					if (policy.rptByRet()) return Result.of(xpr);
					if (policy.rptByEx()) throw GraphEx.of("Remove node pre-condition fail: %s", node);
				}
			}

			if (!(policy.repair() && !graph.contains(node))) {
				graph.removeNode(node);
			}

			if (ok) return Result.OK;
			if (policy.rptByRet()) return Result.of(xpr);
			throw GraphEx.of("Remove node fail: %s", node);

		} catch (Exception | Error e) {
			xpr.addFirst(e);
			throw GraphEx.of(xpr);

		} finally {
			graph.unlock();
		}
	}

	@Override
	public Result<Boolean> removeNodes(Collection<? extends N> nodes) {
		return removeNodes(policy, nodes);
	}

	public Result<Boolean> removeNodes(XfPolicy policy, Collection<? extends N> nodes) {
		graph.lock();
		Explainer xpr = new Explainer("Remove nodes");
		boolean ok = xpr.last();

		try {
			if (!graph.permits(REMOVE_NODE)) {
				if (policy.rptByRet()) return Result.of(xpr.reason(REMOVE_NODE.err()));
				throw GraphEx.of(REMOVE_NODE.err());
			}

			if (policy.qualify()) {
				ok &= chkNodes(xpr, ok, nodes, !policy.repair());

				if (ok && policy.block()) return Result.OK;
				if (!ok && (policy.block() || policy.condStop())) {
					if (policy.rptByRet()) return Result.of(xpr);
					if (policy.rptByEx()) throw GraphEx.of("Remove nodes pre-condition fail: %s", nodes);
				}
			}

			if (policy.repair()) {
				nodes = nodes.stream().filter(n -> n != null && n.valid() && graph.contains(n)).toList();
				ok = true;
			}

			for (N node : nodes) {
				graph.removeNode(node);
			}

			if (ok) return Result.OK;
			if (policy.rptByRet()) return Result.of(xpr);
			throw GraphEx.of("Remove nodes fail: %s", nodes);

		} catch (Exception | Error e) {
			xpr.addFirst(e);
			throw GraphEx.of(xpr);

		} finally {
			graph.unlock();
		}
	}

	@Override
	public Result<Boolean> removeEdge(E edge, boolean clear) {
		return removeEdge(policy, edge, clear);
	}

	public Result<Boolean> removeEdge(XfPolicy policy, E edge, boolean clear) {
		graph.lock();
		Explainer xpr = new Explainer("Remove edge");
		boolean ok = xpr.last();

		try {
			if (!graph.permits(REMOVE_EDGE)) {
				if (policy.rptByRet()) return Result.of(xpr.reason(REMOVE_EDGE.err()));
				throw GraphEx.of(REMOVE_EDGE.err());
			}

			if (policy.qualify()) {
				ok &= chkEdge(xpr, ok, edge);

				if (ok && policy.block()) return Result.OK;
				if (!ok && (policy.block() || policy.condStop())) {
					if (policy.rptByRet()) return Result.of(xpr);
					if (policy.rptByEx()) throw GraphEx.of("Remove edge pre-condition fail: %s", edge);
				}
			}
			graph.removeEdge(edge, clear);

			if (ok) return Result.OK;
			if (policy.rptByRet()) return Result.of(xpr);
			throw GraphEx.of("Remove edge fail: %s", edge);

		} catch (Exception | Error e) {
			xpr.addFirst(e);
			throw GraphEx.of(xpr);

		} finally {
			graph.unlock();
		}
	}

	@Override
	public Result<Boolean> removeEdgeIf(E edge, boolean clear, Predicate<? super E> filter) {
		return removeEdgeIf(policy, edge, clear, filter);
	}

	public Result<Boolean> removeEdgeIf(XfPolicy policy, E edge, boolean clear, Predicate<? super E> filter) {
		graph.lock();
		Explainer xpr = new Explainer("Remove edge 'if'");
		boolean ok = xpr.last();

		try {
			if (!graph.permits(REMOVE_EDGE)) {
				if (policy.rptByRet()) return Result.of(xpr.reason(REMOVE_EDGE.err()));
				throw GraphEx.of(REMOVE_EDGE.err());
			}

			if (policy.qualify()) {
				ok &= chkEdge(xpr, ok, edge);

				if (ok && policy.block()) return Result.OK;
				if (!ok && (policy.block() || policy.condStop())) {
					if (policy.rptByRet()) return Result.of(xpr);
					if (policy.rptByEx()) throw GraphEx.of("Remove edge 'if' pre-condition fail: %s", edge);
				}
			}
			if (filter == null || filter.test(edge)) graph.removeEdge(edge, clear);

			if (ok) return Result.OK;
			if (policy.rptByRet()) return Result.of(xpr);
			throw GraphEx.of("Remove edge 'if' fail: %s", edge);

		} catch (Exception | Error e) {
			xpr.addFirst(e);
			throw GraphEx.of(xpr);

		} finally {
			graph.unlock();
		}
	}

	@Override
	public Result<Boolean> removeEdges(Collection<? extends E> edges, boolean clear) {
		return removeEdges(policy, edges, clear);
	}

	public Result<Boolean> removeEdges(XfPolicy policy, Collection<? extends E> edges, boolean clear) {
		graph.lock();
		Explainer xpr = new Explainer("Remove edges");
		boolean ok = xpr.last();

		try {
			if (!graph.permits(REMOVE_EDGE)) {
				if (policy.rptByRet()) return Result.of(xpr.reason(REMOVE_EDGE.err()));
				throw GraphEx.of(REMOVE_EDGE.err());
			}

			if (policy.qualify()) {
				ok &= chkEdges(xpr, ok, edges);

				if (ok && policy.block()) return Result.OK;
				if (!ok && (policy.block() || policy.condStop())) {
					if (policy.rptByRet()) return Result.of(xpr);
					if (policy.rptByEx()) throw GraphEx.of("Remove edges pre-condition fail: %s", edges);
				}
			}

			if (policy.repair()) {
				edges = edges.stream().filter(e -> e != null && e.valid() && graph.contains(e)).toList();
				ok = true;
			}

			for (E edge : edges) {
				graph.removeEdge(edge, clear);
			}

			if (ok) return Result.OK;
			if (policy.rptByRet()) return Result.of(xpr);
			throw GraphEx.of("Remove edges fail: %s", edges);

		} catch (Exception | Error e) {
			xpr.addFirst(e);
			throw GraphEx.of(xpr);

		} finally {
			graph.unlock();
		}
	}

	@Override
	public Result<Boolean> removeEdges(Sense dir, N src, N dst, boolean clear) {
		return removeEdges(policy, dir, src, dst, clear);
	}

	public Result<Boolean> removeEdges(XfPolicy policy, Sense dir, N src, N dst, boolean clear) {
		graph.lock();
		Explainer xpr = new Explainer("Remove N=>N");
		boolean ok = xpr.last();

		try {
			if (!graph.permits(REMOVE_EDGE)) {
				if (policy.rptByRet()) return Result.of(xpr.reason(REMOVE_EDGE.err()));
				throw GraphEx.of(REMOVE_EDGE.err());
			}

			if (policy.qualify()) {
				ok &= xpr.notNull(ok, dir, "Sense direction is null");
				ok &= chkNodes(xpr, ok, List.of(src, dst), true);

				if (ok && policy.block()) return Result.OK;
				if (!ok && (policy.block() || policy.condStop())) {
					if (policy.rptByRet()) return Result.of(xpr);
					if (policy.rptByEx())
						throw GraphEx.of("Remove N=>N pre-condition fail: %s -> %s", src, dst);
				}
			}

			List<E> edges = graph.getEdges(dir, src, dst);
			if (policy.repair()) {
				edges = edges.stream().filter(e -> e != null && e.valid() && graph.contains(e)).toList();
				ok = true;
			}

			for (E edge : edges) {
				graph.removeEdge(edge, clear);
			}

			if (ok) return Result.OK;
			if (policy.rptByRet()) return Result.of(xpr);
			throw GraphEx.of("Remove N=>N fail: %s", edges);

		} catch (Exception | Error e) {
			xpr.addFirst(e);
			throw GraphEx.of(xpr);

		} finally {
			graph.unlock();
		}
	}

	@Override
	public Result<Boolean> removeEdgesIf(Sense dir, N src, N dst, boolean clear,
			Predicate<? super E> filter) {
		return removeEdgesIf(policy, dir, src, dst, clear, filter);
	}

	public Result<Boolean> removeEdgesIf(XfPolicy policy, Sense dir, N src, N dst, boolean clear,
			Predicate<? super E> filter) {

		graph.lock();
		Explainer xpr = new Explainer("Remove N=>N 'if'");
		boolean ok = xpr.last();

		try {
			if (!graph.permits(REMOVE_EDGE)) {
				if (policy.rptByRet()) return Result.of(xpr.reason(REMOVE_EDGE.err()));
				throw GraphEx.of(REMOVE_EDGE.err());
			}

			if (policy.qualify()) {
				ok &= xpr.notNull(ok, dir, "Sense direction is null");
				ok &= chkNodes(xpr, ok, List.of(src, dst), true);

				if (ok && policy.block()) return Result.OK;
				if (!ok && (policy.block() || policy.condStop())) {
					if (policy.rptByRet()) return Result.of(xpr);
					if (policy.rptByEx())
						throw GraphEx.of("Remove N=>N 'if' pre-condition fail: %s -> %s", src, dst);
				}
			}

			List<E> edges = graph.getEdges(dir, src, dst);
			if (policy.repair()) {
				edges = edges.stream().filter(e -> e != null && e.valid() && graph.contains(e)).toList();
				ok = true;
			}

			for (E edge : edges) {
				if (filter == null || filter.test(edge)) {
					graph.removeEdge(edge, clear);
				}
			}

			if (ok) return Result.OK;
			if (policy.rptByRet()) return Result.of(xpr);
			throw GraphEx.of("Remove N=>N 'if' fail: %s", edges);

		} catch (Exception | Error e) {
			xpr.addFirst(e);
			throw GraphEx.of(xpr);

		} finally {
			graph.unlock();
		}
	}

	@Override
	public Result<Boolean> removeEdgesIf(Collection<? extends E> edges, boolean clear,
			Predicate<? super E> filter) {
		return removeEdgesIf(policy, edges, clear, filter);
	}

	public Result<Boolean> removeEdgesIf(XfPolicy check, Collection<? extends E> edges, boolean clear,
			Predicate<? super E> filter) {

		graph.lock();
		Explainer xpr = new Explainer("Remove edges 'if'");
		boolean ok = xpr.last();

		try {
			if (!graph.permits(REMOVE_EDGE)) {
				if (policy.rptByRet()) return Result.of(xpr.reason(REMOVE_EDGE.err()));
				throw GraphEx.of(REMOVE_EDGE.err());
			}

			if (policy.qualify()) {
				ok &= chkEdgesIf(xpr, ok, edges, filter);

				if (ok && policy.block()) return Result.OK;
				if (!ok && (policy.block() || policy.condStop())) {
					if (policy.rptByRet()) return Result.of(xpr);
					if (policy.rptByEx())
						throw GraphEx.of("Remove edges 'if' pre-condition fail: %s [%s]", edges, filter);
				}
			}

			if (policy.repair()) {
				edges = edges.stream().filter(e -> e != null && e.valid() && graph.contains(e)).toList();
				ok = true;
			}

			for (E edge : edges) {
				if (filter == null || filter.test(edge)) {
					graph.removeEdge(edge, clear);
				}
			}

			if (ok) return Result.OK;
			if (policy.rptByRet()) return Result.of(xpr);
			throw GraphEx.of("Remove N=>N 'if' fail: %s", edges);

		} catch (Exception | Error e) {
			xpr.addFirst(e);
			throw GraphEx.of(xpr);

		} finally {
			graph.unlock();
		}
	}

	@Override
	public Result<Boolean> remove(GraphPath<I, N, E> path, boolean clear) {
		return remove(policy, path, clear);
	}

	public Result<Boolean> remove(XfPolicy policy, GraphPath<I, N, E> path, boolean clear) {
		graph.lock();
		Explainer xpr = new Explainer("Remove GraphPath");
		boolean ok = xpr.last();

		try {
			if (!graph.permits(REMOVE_EDGE)) {
				if (policy.rptByRet()) return Result.of(xpr.reason(REMOVE_EDGE.err()));
				throw GraphEx.of(REMOVE_EDGE.err());
			}

			if (policy.qualify()) {
				ok &= xpr.notNull(ok, path, "GraphPath is null");
				ok &= chkEdges(xpr, ok, path.edges());

				if (ok && policy.block()) return Result.OK;
				if (!ok && (policy.block() || policy.condStop())) {
					if (policy.rptByRet()) return Result.of(xpr);
					if (policy.rptByEx()) throw GraphEx.of("Remove GraphPath pre-condition fail: %s", path);
				}
			}

			ok &= rmGraphPath(xpr, path, clear);

			if (ok) return Result.OK;
			if (policy.rptByRet()) return Result.of(xpr);
			throw GraphEx.of("Remove GraphPath fail: %s", path);

		} catch (Exception | Error e) {
			xpr.addFirst(e);
			throw GraphEx.of(xpr);

		} finally {
			graph.unlock();
		}
	}

	@Override
	public Result<Boolean> remove(SubGraph<I, N, E> subgraph, boolean clear) {
		return remove(policy, subgraph, clear);
	}

	public Result<Boolean> remove(XfPolicy policy, SubGraph<I, N, E> subgraph, boolean clear) {
		graph.lock();
		Explainer xpr = new Explainer("Remove SubGraph");
		boolean ok = xpr.last();

		try {
			if (!graph.permits(REMOVE_EDGE)) {
				if (policy.rptByRet()) return Result.of(xpr.reason(REMOVE_EDGE.err()));
				throw GraphEx.of(REMOVE_EDGE.err());
			}

			if (policy.qualify()) {
				ok &= xpr.notNull(ok, subgraph, "SubGraph is null");
				for (GraphPath<I, N, E> path : subgraph) {
					ok &= chkEdges(xpr, ok, path.edges());
				}

				if (ok && policy.block()) return Result.OK;
				if (!ok && (policy.block() || policy.condStop())) {
					if (policy.rptByRet()) return Result.of(xpr);
					if (policy.rptByEx())
						throw GraphEx.of("Remove SubGraph pre-condition fail: %s", subgraph);
				}
			}

			for (GraphPath<I, N, E> path : subgraph) {
				ok &= rmGraphPath(xpr, path, clear);
			}

			if (ok) return Result.OK;
			if (policy.rptByRet()) return Result.of(xpr);
			throw GraphEx.of("Remove SubGraph fail: %s", subgraph);

		} catch (Exception | Error e) {
			xpr.addFirst(e);
			throw GraphEx.of(xpr);

		} finally {
			graph.unlock();
		}
	}

	@Override
	public Result<Boolean> transfer(E edge, N beg) {
		return transfer(policy, edge, beg);
	}

	public Result<Boolean> transfer(XfPolicy policy, E edge, N beg) {
		graph.lock();
		Explainer xpr = new Explainer("Transfer edge");
		boolean ok = xpr.last();

		try {
			if (!graph.permits(MOVE)) {
				if (policy.rptByRet()) return Result.of(xpr.reason(MOVE.err()));
				throw GraphEx.of(MOVE.err());
			}

			if (policy.qualify()) {
				ok &= chkEdge(xpr, ok, edge);
				ok &= chkNode(xpr, ok, beg, false);

				if (ok && policy.block()) return Result.OK;
				if (!ok && (policy.block() || policy.condStop())) {
					if (policy.rptByRet()) return Result.of(xpr);
					if (policy.rptByEx()) throw GraphEx.of("Transfer edge pre-condition fail: %s::%s->%s",
							edge, beg, edge.end());
				}
			}

			graph.moveEdge(edge, beg, edge.end(), true);

			if (ok) return Result.OK;
			if (policy.rptByRet()) return Result.of(xpr);
			throw GraphEx.of("Transfer edge fail: %s::%s->%s", edge, beg, edge.end());

		} catch (Exception | Error e) {
			xpr.addFirst(e);
			throw GraphEx.of(xpr);

		} finally {
			graph.unlock();
		}
	}

	@Override
	public Result<Boolean> transfer(Collection<? extends E> edges, N beg) {
		return transfer(policy, edges, beg);
	}

	public Result<Boolean> transfer(XfPolicy policy, Collection<? extends E> edges, N beg) {
		graph.lock();
		Explainer xpr = new Explainer("Transfer edges");
		boolean ok = xpr.last();

		try {
			if (!graph.permits(MOVE)) {
				if (policy.rptByRet()) return Result.of(xpr.reason(MOVE.err()));
				throw GraphEx.of(MOVE.err());
			}

			if (policy.qualify()) {
				ok &= chkEdges(xpr, ok, edges);
				ok &= chkNode(xpr, ok, beg, false);

				if (ok && policy.block()) return Result.OK;
				if (!ok && (policy.block() || policy.condStop())) {
					if (policy.rptByRet()) return Result.of(xpr);
					if (policy.rptByEx())
						throw GraphEx.of("Transfer edges pre-condition fail: %s::%s->*", edges, beg);
				}
			}

			if (policy.repair()) {
				edges = edges.stream().filter(e -> e != null && e.valid() && graph.contains(e)).toList();
				ok = true;
			}

			for (E edge : edges) {
				graph.moveEdge(edge, beg, edge.end(), true);
			}

			if (ok) return Result.OK;
			if (policy.rptByRet()) return Result.of(xpr);
			throw GraphEx.of("Transfer edges fail: %s::%s->*", edges, beg);

		} catch (Exception | Error e) {
			xpr.addFirst(e);
			throw GraphEx.of(xpr);

		} finally {
			graph.unlock();
		}
	}

	@Override
	public Result<E> copy(E edge, N beg, N end) {
		Result<LinkedList<E>> res = copy(policy, List.of(edge), beg, end, false);
		if (res.err()) return Result.of(res.getErr());
		return Result.of(res.get().get(0));
	}

	public Result<E> copy(XfPolicy policy, E edge, N beg, N end) {
		Result<LinkedList<E>> res = copy(policy, List.of(edge), beg, end, false);
		if (res.err()) return Result.of(res.getErr());
		return Result.of(res.get().get(0));
	}

	@Override
	public Result<E> copy(E edge, N beg, N end, boolean cyclic) {
		Result<LinkedList<E>> res = copy(policy, List.of(edge), beg, end, cyclic);
		if (res.err()) return Result.of(res.getErr());
		return Result.of(res.get().get(0));
	}

	public Result<E> copy(XfPolicy policy, E edge, N beg, N end, boolean cyclic) {
		Result<LinkedList<E>> res = copy(policy, List.of(edge), beg, end, cyclic);
		if (res.err()) return Result.of(res.getErr());
		return Result.of(res.get().get(0));
	}

	@Override
	public Result<LinkedList<E>> copy(Collection<? extends E> edges, N beg, N end, boolean cyclic) {
		return copy(policy, edges, beg, end, cyclic);
	}

	public Result<LinkedList<E>> copy(XfPolicy policy, Collection<? extends E> edges, N beg, N end,
			boolean cyclic) {

		graph.lock();
		Explainer xpr = new Explainer("Copy Edges");
		boolean ok = xpr.last();

		try {
			if (!graph.permits(COPY)) {
				if (policy.rptByRet()) return Result.of(xpr.reason(COPY.err()));
				throw GraphEx.of(COPY.err());
			}

			if (policy.qualify()) {
				ok &= chkEdges(xpr, ok, edges);
				ok &= chkNode(xpr, ok, beg, false);
				ok &= chkNode(xpr, ok, end, false);

				if (ok && policy.block()) return Result.nil();
				if (!ok && (policy.block() || policy.condStop())) {
					if (policy.rptByRet()) return Result.of(xpr);
					if (policy.rptByEx()) throw GraphEx.of("Copy edges pre-condition fail: %s", edges);
				}
			}

			if (beg == end && !cyclic) return Result.nil();

			LinkedList<E> dups = new LinkedList<>();
			for (E edge : edges) {
				ok &= dupAndAddEdge(xpr, dups, edge, beg, end);
			}

			if (ok) return Result.of(dups);
			if (policy.rptByRet()) return Result.of(xpr);
			throw GraphEx.of("Copy edges fail: %s", edges);

		} catch (Exception | Error e) {
			xpr.addFirst(e);
			throw GraphEx.of(xpr);

		} finally {
			graph.unlock();
		}
	}

	@Override
	public Result<LinkedList<E>> copy(N src, N dst, boolean remove) {
		return copy(policy, List.of(src), dst, remove);
	}

	@Override
	public Result<LinkedList<E>> copy(Collection<? extends N> nodes, N dst, boolean remove) {
		return copy(policy, nodes, dst, remove);
	}

	public Result<LinkedList<E>> copy(XfPolicy policy, Collection<? extends N> nodes, N dst, boolean remove) {
		graph.lock();
		Explainer xpr = new Explainer("Copy Nodes");
		boolean ok = xpr.last();

		try {
			if (!graph.permits(COPY)) {
				if (policy.rptByRet()) return Result.of(xpr.reason(COPY.err()));
				throw GraphEx.of(COPY.err());
			}

			if (policy.qualify()) {
				ok &= chkNodes(xpr, ok, nodes, false);
				ok &= chkNode(xpr, ok, dst, true);

				if (ok && policy.block()) return Result.nil();
				if (!ok && (policy.block() || policy.condStop())) {
					if (policy.rptByRet()) return Result.of(xpr);
					if (policy.rptByEx()) throw GraphEx.of("Copy nodes pre-condition fail: %s", nodes);
				}
			}

			UniqueList<E> leads = dst.edges(Sense.IN);
			UniqueList<E> tails = dst.edges(Sense.OUT);

			LinkedList<E> dupEdges = new LinkedList<>();
			for (N node : nodes) {
				N dupNode = graph.copyNode(node);
				for (E lead : leads) {
					ok &= dupAndAddEdge(xpr, dupEdges, lead, lead.beg(), dupNode);
				}
				for (E tail : tails) {
					ok &= dupAndAddEdge(xpr, dupEdges, tail, dupNode, tail.end());
				}
			}

			if (remove) graph.removeNode(dst);

			if (ok) return Result.of(dupEdges);
			if (policy.rptByRet()) return Result.of(xpr);
			throw GraphEx.of("Copy nodes fail: %s", nodes);

		} catch (Exception | Error e) {
			xpr.addFirst(e);
			throw GraphEx.of(xpr);

		} finally {
			graph.unlock();
		}
	}

	@Override
	public Result<LinkedList<E>> copy(SubGraph<I, N, E> subgraph, N dst, boolean remove) {
		return copy(policy, subgraph, dst, remove);
	}

	public Result<LinkedList<E>> copy(XfPolicy policy, SubGraph<I, N, E> subgraph, N dst, boolean remove) {
		graph.lock();
		Explainer xpr = new Explainer("Copy SubGraph");
		boolean ok = xpr.last();

		try {
			if (!graph.permits(COPY)) {
				if (policy.rptByRet()) return Result.of(xpr.reason(COPY.err()));
				throw GraphEx.of(COPY.err());
			}

			if (policy.qualify()) {
				ok &= xpr.notNull(ok, subgraph, "SubGraph is null");
				for (GraphPath<I, N, E> path : subgraph) {
					ok &= chkEdges(xpr, ok, path.edges());
				}

				if (ok && policy.block()) return Result.nil();
				if (!ok && (policy.block() || policy.condStop())) {
					if (policy.rptByRet()) return Result.of(xpr);
					if (policy.rptByEx()) throw GraphEx.of("Copy SubGraph pre-condition fail: %s", subgraph);
				}
			}

			LinkedList<E> dups = new LinkedList<>();
			if (!subgraph.isEmpty()) {
				UniqueList<E> leads = dst.edges(Sense.IN);
				UniqueList<E> tails = dst.edges(Sense.OUT);

				for (N head : subgraph.heads()) {
					GraphPath<I, N, E> path = subgraph.getPath(head);
					for (E lead : leads) {
						ok &= insertPath(xpr, dups, lead, head, path, tails);
					}
				}

				if (remove) graph.removeNode(dst);
			}

			if (ok) return Result.of(dups);
			if (policy.rptByRet()) return Result.of(xpr);
			throw GraphEx.of("Copy SubGraph fail: %s", subgraph);

		} catch (Exception | Error e) {
			xpr.addFirst(e);
			throw GraphEx.of(xpr);

		} finally {
			graph.unlock();
		}
	}

	private final boolean insertPath(Explainer xpr, LinkedList<E> dups, E lead, N head,
			GraphPath<I, N, E> path, UniqueList<E> tails) {
		boolean ok = true;

		// if tails is empty, path terminals size is N/C
		// if tails is not empty, path terminals must be not empty
		boolean matches = tails.isEmpty() || !tails.isEmpty() && !path.terminals().isEmpty();

		ok &= xpr.is(ok, path.valid(), "Path head is null");
		ok &= xpr.is(ok, matches, "Path terminal(s) mismatch");

		if (ok) {
			// key=nameObj; value=copied node
			HashMap<Object, N> added = new HashMap<>();

			// start new path
			ok &= dupAndAddEdge(xpr, dups, lead, lead.beg(), find(added, head, true));

			// copy in the path edges
			for (E edge : path.edges()) {
				N beg = find(added, edge.beg(), true);
				N end = find(added, edge.end(), true);
				ok &= dupAndAddEdge(xpr, dups, edge, beg, end);
			}

			// connect path terminals to out
			for (N terminal : path.terminals()) {
				N end = find(added, terminal, false);
				for (E next : tails) {
					ok &= dupAndAddEdge(xpr, dups, next, end, next.end());
				}
			}
		}

		return ok;
	}

	private final N find(HashMap<Object, N> added, N node, boolean copy) {
		N n = added.get(node.id());
		if (n == null) {
			if (!copy) throw ERR_COPY_FIND.on(node);
			n = graph.copyNode(node);
			added.put(n.id(), n);
		}
		return n;
	}

	@Override
	public Result<Boolean> move(E edge, N beg, N end) {
		return move(policy, edge, beg, end, false);
	}

	public Result<Boolean> move(XfPolicy policy, E edge, N beg, N end) {
		return move(policy, edge, beg, end, false);
	}

	@Override
	public Result<Boolean> move(E edge, N beg, N end, boolean cyclic) {
		return move(policy, edge, beg, end, cyclic);
	}

	public Result<Boolean> move(XfPolicy policy, E edge, N beg, N end, boolean cyclic) {
		graph.lock();
		Explainer xpr = new Explainer("Move edge");
		boolean ok = xpr.last();

		try {
			if (!graph.permits(MOVE)) {
				if (policy.rptByRet()) return Result.of(xpr.reason(MOVE.err()));
				throw GraphEx.of(MOVE.err());
			}

			if (policy.qualify()) {
				ok &= chkEdge(xpr, ok, edge);
				ok &= chkNodes(xpr, ok, List.of(beg, end), false);

				if (ok && policy.block()) return Result.OK;
				if (!ok && (policy.block() || policy.condStop())) {
					if (policy.rptByRet()) return Result.of(xpr);
					if (policy.rptByEx())
						throw GraphEx.of("Move edge pre-condition fail: %s::%s->%s", edge, beg, end);
				}
			}

			graph.moveEdge(edge, beg, end, cyclic);

			if (ok) return Result.OK;
			if (policy.rptByRet()) return Result.of(xpr);
			throw GraphEx.of("Move edge fail: %s::%s->%s", edge, beg, end);

		} catch (Exception | Error e) {
			xpr.addFirst(e);
			throw GraphEx.of(xpr);

		} finally {
			graph.unlock();
		}
	}

	@Override
	public Result<Boolean> move(Collection<? extends E> edges, N beg, N end, boolean cyclic) {
		return move(policy, edges, beg, end, cyclic);
	}

	public Result<Boolean> move(XfPolicy policy, Collection<? extends E> edges, N beg, N end,
			boolean cyclic) {
		graph.lock();
		Explainer xpr = new Explainer("Move edges");
		boolean ok = xpr.last();

		try {
			if (!graph.permits(MOVE)) {
				if (policy.rptByRet()) return Result.of(xpr.reason(MOVE.err()));
				throw GraphEx.of(MOVE.err());
			}

			if (policy.qualify()) {
				ok &= chkEdges(xpr, ok, edges);
				ok &= chkNodes(xpr, ok, List.of(beg, end), false);

				if (ok && policy.block()) return Result.OK;
				if (!ok && (policy.block() || policy.condStop())) {
					if (policy.rptByRet()) return Result.of(xpr);
					if (policy.rptByEx())
						throw GraphEx.of("Move edges pre-condition fail: %s %s->%s", edges, beg, end);
				}
			}

			if (policy.repair()) {
				edges = edges.stream().filter(e -> e != null && e.valid() && graph.contains(e)).toList();
				ok = true;
			}

			for (E edge : edges) {
				graph.moveEdge(edge, beg, end, cyclic);
			}

			if (ok) return Result.OK;
			if (policy.rptByRet()) return Result.of(xpr);
			throw GraphEx.of("Move edges fail: %s %s->%s", edges, beg, end);

		} catch (Exception | Error e) {
			xpr.addFirst(e);
			throw GraphEx.of(xpr);

		} finally {
			graph.unlock();
		}
	}

	@Override
	public Result<Boolean> reterminate(E edge, N end) {
		return reterminate(policy, edge, end, false);
	}

	public Result<Boolean> reterminate(XfPolicy policy, E edge, N end) {
		return reterminate(policy, edge, end, false);
	}

	@Override
	public Result<Boolean> reterminate(E edge, N end, boolean cyclic) {
		return reterminate(policy, edge, end, cyclic);
	}

	public Result<Boolean> reterminate(XfPolicy policy, E edge, N end, boolean cyclic) {
		graph.lock();
		Explainer xpr = new Explainer("Reterminate edge");
		boolean ok = xpr.last();

		try {
			if (!graph.permits(MOVE)) {
				if (policy.rptByRet()) return Result.of(xpr.reason(MOVE.err()));
				throw GraphEx.of(MOVE.err());
			}

			if (policy.qualify()) {
				ok &= chkEdge(xpr, ok, edge);
				ok &= chkNode(xpr, ok, end, false);

				if (ok && policy.block()) return Result.OK;
				if (!ok && (policy.block() || policy.condStop())) {
					if (policy.rptByRet()) return Result.of(xpr);
					if (policy.rptByEx()) throw GraphEx.of("Reterminate edge pre-condition fail: %s::%s->%s",
							edge, edge.beg(), end);
				}
			}

			graph.moveEdge(edge, edge.beg(), end, cyclic);

			if (ok) return Result.OK;
			if (policy.rptByRet()) return Result.of(xpr);
			throw GraphEx.of("Reterminate edge fail: %s::%s->%s", edge, edge.beg(), end);

		} catch (Exception | Error e) {
			xpr.addFirst(e);
			throw GraphEx.of(xpr);

		} finally {
			graph.unlock();
		}
	}

	@Override
	public Result<Boolean> reterminate(Collection<? extends E> edges, N end) {
		return reterminate(policy, edges, end, false);
	}

	public Result<Boolean> reterminate(XfPolicy policy, Collection<? extends E> edges, N end) {
		return reterminate(policy, edges, end, false);
	}

	@Override
	public Result<Boolean> reterminate(Collection<? extends E> edges, N end, boolean cyclic) {
		return reterminate(policy, edges, end, cyclic);
	}

	public Result<Boolean> reterminate(XfPolicy policy, Collection<? extends E> edges, N end,
			boolean cyclic) {
		graph.lock();
		Explainer xpr = new Explainer("Reterminate edges");
		boolean ok = xpr.last();

		try {
			if (!graph.permits(MOVE)) {
				if (policy.rptByRet()) return Result.of(xpr.reason(MOVE.err()));
				throw GraphEx.of(MOVE.err());
			}

			if (policy.qualify()) {
				ok &= chkEdges(xpr, ok, edges);
				ok &= chkNode(xpr, ok, end, false);

				if (ok && policy.block()) return Result.OK;
				if (!ok && (policy.block() || policy.condStop())) {
					if (policy.rptByRet()) return Result.of(xpr);
					if (policy.rptByEx())
						throw GraphEx.of("Reterminate edges pre-condition fail: %s::*->%s", edges, end);
				}
			}

			if (policy.repair()) {
				edges = edges.stream().filter(e -> e != null && e.valid() && graph.contains(e)).toList();
				ok = true;
			}

			for (E edge : edges) {
				graph.moveEdge(edge, edge.beg(), end, cyclic);
			}

			if (ok) return Result.OK;
			if (policy.rptByRet()) return Result.of(xpr);
			throw GraphEx.of("Reterminate edges fail: %s::*->%s", edges, end);

		} catch (Exception | Error e) {
			xpr.addFirst(e);
			throw GraphEx.of(xpr);

		} finally {
			graph.unlock();
		}
	}

	@Override
	public Result<Boolean> consolidateEdges(Collection<? extends N> sources, N target) {
		return consolidateEdges(policy, sources, target);
	}

	public Result<Boolean> consolidateEdges(XfPolicy policy, Collection<? extends N> sources, N target) {
		graph.lock();
		Explainer xpr = new Explainer("Consolidate edges");
		boolean ok = xpr.last();

		try {
			if (!graph.permits(CONSOLIDATE)) {
				if (policy.rptByRet()) return Result.of(xpr.reason(CONSOLIDATE.err()));
				throw GraphEx.of(CONSOLIDATE.err());
			}

			if (policy.qualify()) {
				ok &= chkNodes(xpr, ok, sources, true);
				ok &= chkNode(xpr, ok, target, false);

				if (ok && policy.block()) return Result.OK;
				if (!ok && (policy.block() || policy.condStop())) {
					if (policy.rptByRet()) return Result.of(xpr);
					if (policy.rptByEx())
						throw GraphEx.of("Consolidate edges pre-condition fail: %s::%s", sources, target);
				}
			}

			if (policy.repair()) {
				sources = sources.stream().filter(e -> e != null && e.valid() && graph.contains(e)).toList();
				ok = true;
			}

			Set<N> nodes = new LinkedHashSet<>(sources);
			nodes.remove(target);

			UniqueList<E> leads = nodes.stream().map(n -> n.edges(Sense.IN)).flatMap(Collection::stream)
					.collect(Collectors.toCollection(UniqueList::new));
			UniqueList<E> tails = nodes.stream().map(n -> n.edges(Sense.OUT)).flatMap(Collection::stream)
					.collect(Collectors.toCollection(UniqueList::new));
			UniqueList<E> cycles = nodes.stream().map(n -> n.edges(Sense.OUT, e -> e.cyclic()))
					.flatMap(Collection::stream).collect(Collectors.toCollection(UniqueList::new));

			// lead edges [D,G] => [X,Y] to [D,G] => B
			for (E edge : leads) {
				graph.moveEdge(edge, edge.beg(), target, true);
			}
			// tail edges [X,Y] => [F,I] to B => [F,I]
			for (E edge : tails) {
				graph.moveEdge(edge, target, edge.end(), true);
			}
			// cyclic edges [X,Y] => [X,Y] to B => B
			for (E edge : cycles) {
				graph.moveEdge(edge, target, target, true);
			}

			if (ok) return Result.OK;
			if (policy.rptByRet()) return Result.of(xpr);
			throw GraphEx.of("Consolidate edges fail: %s::%s", sources, target);

		} catch (Exception | Error e) {
			xpr.addFirst(e);
			throw GraphEx.of(xpr);

		} finally {
			graph.unlock();
		}
	}

	@Override
	public Result<LinkedList<E>> replicateEdges(N node, Collection<? extends N> targets) {
		return replicateEdges(policy, node, targets, false);
	}

	public Result<LinkedList<E>> replicateEdges(XfPolicy policy, N node, Collection<? extends N> targets) {
		return replicateEdges(policy, node, targets, false);
	}

	@Override
	public Result<LinkedList<E>> replicateEdges(N node, Collection<? extends N> targets, boolean remove) {
		return replicateEdges(policy, node, targets, remove);
	}

	public Result<LinkedList<E>> replicateEdges(XfPolicy policy, N node, Collection<? extends N> targets,
			boolean remove) {
		graph.lock();
		Explainer xpr = new Explainer("Replicate edges");
		boolean ok = xpr.last();

		try {
			if (!graph.permits(REPLICATE)) {
				if (policy.rptByRet()) return Result.of(xpr.reason(REPLICATE.err()));
				throw GraphEx.of(REPLICATE.err());
			}

			if (policy.qualify()) {
				ok &= chkNode(xpr, ok, node, true);
				ok &= chkNodes(xpr, ok, targets, false);

				if (ok && policy.block()) return Result.nil();
				if (!ok && (policy.block() || policy.condStop())) {
					if (policy.rptByRet()) return Result.of(xpr);
					if (policy.rptByEx())
						throw GraphEx.of("Replicate edges pre-condition fail: %s %s", node, targets);
				}
			}

			if (policy.repair()) {
				targets = targets.stream().filter(n -> n != null && n.valid()).toList();
				ok = true;
			}

			Set<? extends N> tgts = new LinkedHashSet<>(targets);
			tgts.remove(node);

			UniqueList<E> leads = node.edges(Sense.IN);
			UniqueList<E> tails = node.edges(Sense.OUT);

			LinkedList<E> dups = new LinkedList<>();
			for (N tgt : tgts) {
				// for edges ? => node, create ? => targets
				for (E edge : leads) {
					ok &= dupAndAddEdge(xpr, dups, edge, edge.beg(), tgt);
				}

				// for edges node => ?, create targets => ?
				for (E edge : tails) {
					ok &= dupAndAddEdge(xpr, dups, edge, tgt, edge.end());
				}
			}
			if (remove) graph.removeNode(node);

			if (ok) return Result.of(dups);
			if (policy.rptByRet()) return Result.of(xpr);
			throw GraphEx.of("Replicate edges fail: %s %s", node, targets);

		} catch (Exception | Error e) {
			xpr.addFirst(e);
			throw GraphEx.of(xpr);

		} finally {
			graph.unlock();
		}
	}

	@Override
	public Result<Boolean> reduce(N node) {
		return reduce(policy, node);
	}

	public Result<Boolean> reduce(XfPolicy policy, N node) {
		graph.lock();
		Explainer xpr = new Explainer("Reduce node");
		boolean ok = xpr.last();

		try {
			if (!graph.permits(REDUCE)) {
				if (policy.rptByRet()) return Result.of(xpr.reason(REDUCE.err()));
				throw GraphEx.of(REDUCE.err());
			}

			if (policy.qualify()) {
				ok &= chkNode(xpr, ok, node, !policy.repair());

				if (ok && policy.block()) return Result.OK;
				if (!ok && (policy.block() || policy.condStop())) {
					if (policy.rptByRet()) return Result.of(xpr);
					if (policy.rptByEx()) throw GraphEx.of("Reduce node pre-condition fail: %s", node);
				}
			}

			if (!(policy.repair() && !graph.contains(node))) {
				UniqueList<E> leads = node.edges(Sense.IN, false);	// =>N
				UniqueList<E> tails = node.edges(Sense.OUT, false);	// N=>

				for (E lead : leads) {
					for (E tail : tails) {
						graph.joinEdges(lead, tail, true);
					}
				}
				graph.removeNode(node);
			}

			if (ok) return Result.OK;
			if (policy.rptByRet()) return Result.of(xpr);
			throw GraphEx.of("Reduce node fail: %s", node);

		} catch (Exception | Error e) {
			xpr.addFirst(e);
			throw GraphEx.of(xpr);

		} finally {
			graph.unlock();
		}
	}

	@Override
	public Result<Boolean> reduce(E src, E dst) {
		return reduce(policy, src, dst);
	}

	public Result<Boolean> reduce(XfPolicy policy, E src, E dst) {
		graph.lock();
		Explainer xpr = new Explainer("Reduce edges");
		boolean ok = xpr.last();

		try {
			if (!graph.permits(REDUCE)) {
				if (policy.rptByRet()) return Result.of(xpr.reason(REDUCE.err()));
				throw GraphEx.of(REDUCE.err());
			}

			if (policy.qualify()) {
				ok &= chkEdge(xpr, ok, src);
				ok &= chkEdge(xpr, ok, dst);

				if (ok && policy.block()) return Result.OK;
				if (!ok && (policy.block() || policy.condStop())) {
					if (policy.rptByRet()) return Result.of(xpr);
					if (policy.rptByEx())
						throw GraphEx.of("Reduce edges pre-condition fail: %s %s", src, dst);
				}
			}

			graph.joinEdges(src, dst, true);
			if (src.end().equals(dst.beg())) {
				graph.removeEdge(dst, false);
			}
			graph.removeEdge(src, false);

			if (ok) return Result.OK;
			if (policy.rptByRet()) return Result.of(xpr);
			throw GraphEx.of("Reduce edges fail: %s %s", src, dst);

		} catch (Exception | Error e) {
			xpr.addFirst(e);
			throw GraphEx.of(xpr);

		} finally {
			graph.unlock();
		}

	}

	// ---- Transform Helpers ---------

	/**
	 * Check a single edge for validity:
	 * <ol>
	 * <li>not {@code null}
	 * <li>begin node not {@code null} and in graph
	 * <li>end node not {@code null} and in graph
	 * </ol>
	 *
	 * @param xpr  {@link Explainer}
	 * @param ok   conditional check flag
	 * @param edge edge to check
	 * @return {@code true} on valid/success
	 */
	final boolean chkEdge(Explainer xpr, boolean ok, E edge) {
		return chkEdge(xpr, ok, Strings.EMPTY, edge);
	}

	/**
	 * Check an indexed edge for validity: edge is not {@code null}, and the begin and end
	 * nodes are not {@code null} and exist in the graph.
	 *
	 * @param xpr  {@link Explainer}
	 * @param ok   conditional check flag
	 * @param idx  collection index
	 * @param edge edge to check
	 * @return {@code true} on valid
	 */
	final boolean chkEdge(Explainer xpr, boolean ok, Object idx, E edge) {
		return chkEdgeIf(xpr, ok, idx, edge, null);
	}

	/**
	 * Check an indexed edge for validity: edge is not {@code null}, the predicate filter
	 * is {@code null} or succeeds, and the begin and end nodes are not {@code null} and
	 * exist in the graph.
	 *
	 * @param xpr    {@link Explainer}
	 * @param ok     conditional check flag
	 * @param idx    collection index
	 * @param edge   edge to check
	 * @param filter predicate filter
	 * @return {@code true} on valid
	 */
	final boolean chkEdgeIf(Explainer xpr, boolean ok, Object idx, E edge, Predicate<? super E> filter) {
		ok &= xpr.notNull(ok, edge, EDGE_NULL, idx);
		ok &= xpr.is(ok, filter == null || filter.test(edge), EDGE_FILTER_FAIL, idx, edge);
		ok &= xpr.notNull(ok, edge.beg(), EDGE_NODE_NULL, idx, BEGIN);
		ok &= xpr.notNull(ok, edge.end(), EDGE_NODE_NULL, idx, END);
		ok &= xpr.is(ok, graph.contains(edge.beg()), NO_GRAPH_EDGE_NODE, idx, BEGIN, edge.beg());
		ok &= xpr.is(ok, graph.contains(edge.end()), NO_GRAPH_EDGE_NODE, idx, END, edge.end());
		return ok;
	}

	final boolean chkEdges(Explainer xpr, boolean ok, Collection<? extends E> edges) {
		return chkEdgesIf(xpr, ok, edges, null);
	}

	/**
	 * Check each edge for validity: that an edge is not {@code null}, the predicate
	 * filter is {@code null} or succeeds, and the begin and end nodes are not
	 * {@code null} and exist in the graph.
	 *
	 * @param xpr    {@link Explainer}
	 * @param ok     conditional check flag
	 * @param edges  edges to check
	 * @param filter predicate filter
	 * @return {@code true} on valid
	 */
	final boolean chkEdgesIf(Explainer xpr, boolean ok, Collection<? extends E> edges,
			Predicate<? super E> filter) {

		ok &= xpr.notNull(ok, edges, EDGE_LIST_NULL);
		if (ok) {
			List<E> elems = new ArrayList<>(edges);
			for (int idx = 0; idx < elems.size(); idx++) {
				ok &= chkEdgeIf(xpr, true, idx, elems.get(idx), null);
			}
		}
		return ok;
	}

	/**
	 * Check a node for validity: that the node is not {@code null} and exists in the
	 * graph.
	 *
	 * @param xpr    {@link Explainer}
	 * @param ok     conditional check flag
	 * @param node   node to check
	 * @param exists {@code true} to test for existence in graph
	 * @return {@code true} on valid
	 */
	final boolean chkNode(Explainer xpr, boolean ok, N node, boolean exists) {
		ok &= xpr.notNull(ok, node, NODE_NULL);
		if (exists) ok &= xpr.is(ok, graph.contains(node), NO_GRAPH_NODE);
		return ok;
	}

	/**
	 * Check nodes for validity: that each node is not {@code null} and exists in the
	 * graph.
	 *
	 * @param xpr    {@link Explainer}
	 * @param ok     conditional check flag
	 * @param nodes  node to check
	 * @param exists {@code true} to test for existence in graph
	 * @return {@code true} on valid
	 */
	final boolean chkNodes(Explainer xpr, boolean ok, Collection<? extends N> nodes, boolean exists) {
		ok &= xpr.notNull(ok, nodes, NODE_LIST_NULL);
		ok &= xpr.any(ok, nodes, n -> n != null, NODE_NULL);
		if (exists) ok &= xpr.any(ok, nodes, n -> graph.contains(n), NO_GRAPH_NODE);
		return ok;
	}

	/**
	 * Internal: graph edge duplication and add function.
	 *
	 * @param xpr  explainer
	 * @param dups collector of duplicated edges
	 * @param edge edge to duplicate
	 * @param beg  duplicated edge begin node
	 * @param end  duplicated edge end node
	 * @return
	 */
	final boolean dupAndAddEdge(Explainer xpr, LinkedList<E> dups, E edge, N beg, N end) {
		try {
			E dup = graph.copyEdge(edge, beg, end, true);
			dups.add(dup);
			return true;

		} catch (Exception e) {
			xpr.reason("Edge duplication exception on: %s::%s->%s", edge, beg, end);
			xpr.add(e);
			return false;
		}
	}

	// final boolean mvEdge(Explainer xpr, E edge, N beg, N end, boolean cyclic) {
	// if (beg.equals(end) && !cyclic) return true; // skip cycle
	// if (edge.beg().equals(beg) && edge.end().equals(end)) return true; // skip no-op
	//
	// boolean ok = rmEdge(xpr, edge, false);
	// if (ok) edge.setBeg(beg);
	// if (ok) edge.setEnd(end);
	// if (ok) graph.addEdge(edge);
	// return ok;
	// }

	/** Internal: remove graph path */
	final boolean rmGraphPath(Explainer xpr, GraphPath<I, N, E> path, boolean clear) {
		UniqueList<E> edges = findSubGraphEdges(List.of(path), clear);
		for (E edge : edges) {
			graph.removeEdge(edge, clear);
		}
		return true;
	}

	/** Internal: find connections between the graph and given paths */
	final UniqueList<E> findSubGraphEdges(Collection<GraphPath<I, N, E>> paths, boolean clear) {
		UniqueList<E> edges = new UniqueList<>();
		if (paths != null) {
			// collect all edges
			for (GraphPath<I, N, E> path : paths) {
				for (N node : path.nodes()) {
					edges.addAll(node.edges(Sense.BOTH));
				}
			}

			if (!clear) {
				// remove internal edges
				for (GraphPath<I, N, E> path : paths) {
					edges.removeAll(path.edges());
				}
			}
		}
		return edges;
	}
}
