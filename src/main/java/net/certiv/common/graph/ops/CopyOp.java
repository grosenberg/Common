package net.certiv.common.graph.ops;

import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import net.certiv.common.graph.Edge;
import net.certiv.common.graph.Node;
import net.certiv.common.graph.Transformer;
import net.certiv.common.graph.XfPermits;
import net.certiv.common.graph.XfPolicy;
import net.certiv.common.graph.algorithms.GraphPath;
import net.certiv.common.stores.Result;
import net.certiv.common.util.Strings;

public class CopyOp<N extends Node<N, E>, E extends Edge<N, E>> implements ITransformOp<N, E> {

	public static <N extends Node<N, E>, E extends Edge<N, E>> CopyOp<N, E> of(Collection<? extends E> edges,
			N beg, N end, boolean cyclic) {

		return new CopyOp<>(edges, beg, end, cyclic);
	}

	public static <N extends Node<N, E>, E extends Edge<N, E>> CopyOp<N, E> of(Map<N, GraphPath<N, E>> sg,
			N dst, boolean remove) {

		return new CopyOp<>(sg, dst, remove);
	}

	// --------------------------------

	private boolean paths = false;

	private List<E> edges;
	private N beg;
	private N end;
	private boolean cyclic;

	private LinkedHashMap<N, GraphPath<N, E>> sg = new LinkedHashMap<>();
	private N dst;
	private boolean remove;

	private CopyOp(Collection<? extends E> edges, N beg, N end, boolean cyclic) {
		this.edges = List.copyOf(edges);
		this.beg = beg;
		this.end = end;
		this.cyclic = cyclic;
	}

	private CopyOp(Map<N, GraphPath<N, E>> sg, N dst, boolean remove) {
		this.sg.putAll(sg);
		this.dst = dst;
		this.remove = remove;

		paths = true;
	}

	@Override
	public XfPermits type() {
		return XfPermits.COPY;
	}

	@Override
	public Result<Boolean> canApply(Transformer<N, E> xf) {
		Result<LinkedList<E>> res = paths //
				? xf.copy(sg, dst, remove) //
				: xf.copy(edges, beg, end, cyclic);
		return res.valid() ? Result.OK : Result.FAIL;
	}

	@Override
	public Result<Boolean> apply(Transformer<N, E> xf, XfPolicy policy) {
		Result<LinkedList<E>> res = paths //
				? xf.copy(policy, sg, dst, remove) //
				: xf.copy(policy, edges, beg, end, cyclic);
		return res.valid() ? Result.OK : Result.FAIL;
	}

	@Override
	public int hashCode() {
		return paths ? Objects.hash(sg, dst, remove) : Objects.hash(edges, beg, end, cyclic);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) return true;
		if (obj == null) return false;
		if (getClass() != obj.getClass()) return false;
		CopyOp<?, ?> other = (CopyOp<?, ?>) obj;
		return paths
				? Objects.equals(dst, other.dst) && remove == other.remove && Objects.equals(sg, other.sg)
				: Objects.equals(beg, other.beg) && Objects.equals(end, other.end) && cyclic == other.cyclic
						&& Objects.equals(edges, other.edges);
	}

	@Override
	public String toString() {
		return paths //
				? String.format("[%s] %s -> %s %s", type(), sg, dst, remove ? "rmv" : Strings.EMPTY)
				: String.format("[%s] %s %s->%s %s", type(), edges, beg, end, cyclic ? "cyc" : Strings.EMPTY);
	}
}
