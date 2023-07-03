package net.certiv.common.graph.ops;

import java.util.Collection;
import java.util.List;
import java.util.Objects;

import net.certiv.common.graph.Edge;
import net.certiv.common.graph.Node;
import net.certiv.common.graph.Transformer;
import net.certiv.common.graph.XfPermits;
import net.certiv.common.graph.XfPolicy;
import net.certiv.common.stores.Result;
import net.certiv.common.util.Strings;

public class MoveOp<N extends Node<N, E>, E extends Edge<N, E>> implements ITransformOp<N, E> {

	public static <N extends Node<N, E>, E extends Edge<N, E>> MoveOp<N, E> of(E edge, N beg, N end,
			boolean cyclic) {
		return new MoveOp<>(List.of(edge), beg, end, cyclic);
	}

	public static <N extends Node<N, E>, E extends Edge<N, E>> MoveOp<N, E> of(Collection<? extends E> edges,
			N beg, N end, boolean cyclic) {
		return new MoveOp<>(edges, beg, end, cyclic);
	}

	// --------------------------------

	public final List<? extends E> edges;
	public final N beg;
	public final N end;
	public final boolean cyclic;

	private MoveOp(Collection<? extends E> edges, N beg, N end, boolean cyclic) {
		this.edges = List.copyOf(edges);
		this.beg = beg;
		this.end = end;
		this.cyclic = cyclic;
	}

	@Override
	public XfPermits type() {
		return XfPermits.MOVE;
	}

	@Override
	public Result<Boolean> canApply(Transformer<N, E> xf) {
		return xf.move(XfPolicy.CHECK, edges, beg, end, cyclic);
	}

	@Override
	public Result<Boolean> apply(Transformer<N, E> xf, XfPolicy policy) {
		return xf.move(policy, edges, beg, end, cyclic);
	}

	@Override
	public int hashCode() {
		return Objects.hash(beg, cyclic, edges, end);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) return true;
		if (obj == null) return false;
		if (getClass() != obj.getClass()) return false;
		MoveOp<?, ?> other = (MoveOp<?, ?>) obj;
		return Objects.equals(edges, other.edges) && Objects.equals(beg, other.beg)
				&& Objects.equals(end, other.end) && cyclic == other.cyclic;
	}

	@Override
	public String toString() {
		return String.format("[%s] %s %s=>%s (%s)", //
				type(), edges, beg, end, cyclic ? "cyclic" : Strings.EMPTY);
	}
}
