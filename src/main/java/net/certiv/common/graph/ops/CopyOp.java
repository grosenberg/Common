package net.certiv.common.graph.ops;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;

import net.certiv.common.graph.Edge;
import net.certiv.common.graph.Node;
import net.certiv.common.graph.Transformer;
import net.certiv.common.graph.XfPermits;
import net.certiv.common.graph.XfPolicy;
import net.certiv.common.graph.paths.SubGraph;
import net.certiv.common.id.IUId;
import net.certiv.common.stores.Result;
import net.certiv.common.util.Strings;

public class CopyOp<I extends IUId, N extends Node<I, N, E>, E extends Edge<I, N, E>>
		implements ITransformOp<I, N, E> {

	public static <I extends IUId, N extends Node<I, N, E>, E extends Edge<I, N, E>> CopyOp<I, N, E> of(
			Collection<? extends N> nodes, N dst, boolean remove) {
		return new CopyOp<>(nodes, dst, remove);
	}

	public static <I extends IUId, N extends Node<I, N, E>, E extends Edge<I, N, E>> CopyOp<I, N, E> of(
			Collection<? extends E> edges, N beg, N end, boolean cyclic) {
		return new CopyOp<>(edges, beg, end, cyclic);
	}

	public static <I extends IUId, N extends Node<I, N, E>, E extends Edge<I, N, E>> CopyOp<I, N, E> of(
			SubGraph<I, N, E> sg, N dst, boolean remove) {
		return new CopyOp<>(sg, dst, remove);
	}

	// --------------------------------

	private enum C {
		NODES,
		EDGES,
		PATHS;
	}

	private List<N> nodes;
	private List<E> edges;
	private SubGraph<I, N, E> sg;

	private N beg;
	private N end;
	private N dst;
	private boolean cyclic;
	private boolean remove;

	private C kind;

	private CopyOp(Collection<? extends N> nodes, N dst, boolean remove) {
		this.nodes = List.copyOf(nodes);
		this.dst = dst;
		this.remove = remove;
		this.kind = C.NODES;
	}

	private CopyOp(Collection<? extends E> edges, N beg, N end, boolean cyclic) {
		this.edges = List.copyOf(edges);
		this.beg = beg;
		this.end = end;
		this.cyclic = cyclic;
		this.kind = C.EDGES;
	}

	private CopyOp(SubGraph<I, N, E> sg, N dst, boolean remove) {
		this.sg = sg.dup();
		this.dst = dst;
		this.remove = remove;
		this.kind = C.PATHS;
	}

	@Override
	public XfPermits type() {
		return XfPermits.COPY;
	}

	@Override
	public Result<Boolean> canApply(Transformer<I, N, E> xf) {
		Result<LinkedList<E>> res;
		switch (kind) {
			case NODES:
				res = xf.copy(nodes, dst, remove);
				break;
			case EDGES:
				res = xf.copy(edges, beg, end, cyclic);
				break;
			case PATHS:
			default:
				res = xf.copy(sg, dst, remove);
				break;
		}
		return res.valid() ? Result.OK : Result.FAIL;
	}

	@Override
	public Result<Boolean> apply(Transformer<I, N, E> xf, XfPolicy policy) {
		Result<LinkedList<E>> res;
		switch (kind) {
			case NODES:
				res = xf.copy(policy, nodes, dst, remove);
				break;
			case EDGES:
				res = xf.copy(policy, edges, beg, end, cyclic);
				break;
			case PATHS:
			default:
				res = xf.copy(policy, sg, dst, remove);
				break;
		}
		return res.valid() ? Result.OK : Result.FAIL;
	}

	@Override
	public int hashCode() {
		return Objects.hash(beg, cyclic, dst, edges, end, kind, nodes, remove, sg);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) return true;
		if (obj == null) return false;
		if (getClass() != obj.getClass()) return false;
		CopyOp<?, ?, ?> other = (CopyOp<?, ?, ?>) obj;
		return Objects.equals(beg, other.beg) && cyclic == other.cyclic && Objects.equals(dst, other.dst)
				&& Objects.equals(edges, other.edges) && Objects.equals(end, other.end) && kind == other.kind
				&& Objects.equals(nodes, other.nodes) && remove == other.remove
				&& Objects.equals(sg, other.sg);
	}

	@Override
	public String toString() {
		switch (kind) {
			case NODES:
				return String.format("[%s] %s -> %s %s", type(), nodes, dst, remove ? "rmv" : Strings.EMPTY);
			case EDGES:
				return String.format("[%s] %s %s->%s %s", type(), edges, beg, end,
						cyclic ? "cyc" : Strings.EMPTY);
			case PATHS:
			default:
				return String.format("[%s] %s -> %s %s", type(), sg, dst, remove ? "rmv" : Strings.EMPTY);
		}

	}
}
