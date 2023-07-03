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

public class RemoveEdgeOp<N extends Node<N, E>, E extends Edge<N, E>> implements ITransformOp<N, E> {

	public static <N extends Node<N, E>, E extends Edge<N, E>> RemoveEdgeOp<N, E> of(E edge, boolean clear) {
		return new RemoveEdgeOp<>(List.of(edge), clear);
	}

	public static <N extends Node<N, E>, E extends Edge<N, E>> RemoveEdgeOp<N, E> of(
			Collection<? extends E> edges, boolean clear) {
		return new RemoveEdgeOp<>(edges, clear);
	}

	// --------------------------------

	public final List<? extends E> edge;
	public final boolean clear;

	private RemoveEdgeOp(Collection<? extends E> edges, boolean clear) {
		this.edge = List.copyOf(edges);
		this.clear = clear;
	}

	@Override
	public XfPermits type() {
		return XfPermits.REMOVE_EDGE;
	}

	@Override
	public Result<Boolean> canApply(Transformer<N, E> xf) {
		return xf.removeEdges(XfPolicy.CHECK, edge, clear);
	}

	@Override
	public Result<Boolean> apply(Transformer<N, E> xf, XfPolicy policy) {
		return xf.removeEdges(policy, edge, clear);
	}

	@Override
	public int hashCode() {
		return Objects.hash(clear, edge);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) return true;
		if (obj == null) return false;
		if (getClass() != obj.getClass()) return false;
		RemoveEdgeOp<?, ?> other = (RemoveEdgeOp<?, ?>) obj;
		return clear == other.clear && Objects.equals(edge, other.edge);
	}

	@Override
	public String toString() {
		return String.format("[%s] %s", type(), edge);
	}
}
