package net.certiv.common.graph.ops;

import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.function.Predicate;

import net.certiv.common.graph.Edge;
import net.certiv.common.graph.Node;
import net.certiv.common.graph.Transformer;
import net.certiv.common.graph.XfPermits;
import net.certiv.common.graph.XfPolicy;
import net.certiv.common.graph.id.IUId;
import net.certiv.common.stores.Result;

public class RemoveEdgesIfOp<I extends IUId, N extends Node<I, N, E>, E extends Edge<I, N, E>>
		implements ITransformOp<I, N, E> {

	public static <I extends IUId, N extends Node<I, N, E>, E extends Edge<I, N, E>> RemoveEdgesIfOp<I, N, E> of(
			E edge, boolean clear, Predicate<? super E> filter) {
		return new RemoveEdgesIfOp<>(List.of(edge), clear, filter);
	}

	public static <I extends IUId, N extends Node<I, N, E>, E extends Edge<I, N, E>> RemoveEdgesIfOp<I, N, E> of(
			Collection<? extends E> edges, boolean clear, Predicate<? super E> filter) {
		return new RemoveEdgesIfOp<>(edges, clear, filter);
	}

	// --------------------------------

	public final List<? extends E> edges;
	public final boolean clear;
	public final Predicate<? super E> filter;

	private RemoveEdgesIfOp(Collection<? extends E> edges, boolean clear, Predicate<? super E> filter) {
		this.edges = List.copyOf(edges);
		this.clear = clear;
		this.filter = filter;
	}

	@Override
	public XfPermits type() {
		return XfPermits.REMOVE_EDGE;
	}

	@Override
	public Result<Boolean> canApply(Transformer<I, N, E> xf) {
		return xf.removeEdgesIf(edges, clear, filter);
	}

	@Override
	public Result<Boolean> apply(Transformer<I, N, E> xf, XfPolicy policy) {
		return xf.removeEdgesIf(policy, edges, clear, filter);
	}

	@Override
	public int hashCode() {
		return Objects.hash(clear, edges, filter);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) return true;
		if (obj == null) return false;
		if (getClass() != obj.getClass()) return false;
		RemoveEdgesIfOp<?, ?, ?> other = (RemoveEdgesIfOp<?, ?, ?>) obj;
		return clear == other.clear && Objects.equals(edges, other.edges)
				&& Objects.equals(filter, other.filter);
	}

	@Override
	public String toString() {
		return String.format("[%s] %s", type(), edges);
	}
}
