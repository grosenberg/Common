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
import net.certiv.common.stores.Result;

public class RemoveEdgesIfOp<N extends Node<N, E>, E extends Edge<N, E>> implements ITransformOp<N, E> {

	public static <N extends Node<N, E>, E extends Edge<N, E>> RemoveEdgesIfOp<N, E> of(E edge, boolean clear,
			Predicate<? super E> filter) {
		return new RemoveEdgesIfOp<>(List.of(edge), clear, filter);
	}

	public static <N extends Node<N, E>, E extends Edge<N, E>> RemoveEdgesIfOp<N, E> of(
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
	public Result<Boolean> canApply(Transformer<N, E> xf) {
		return xf.removeEdgesIf(XfPolicy.CHECK, edges, clear, filter);
	}

	@Override
	public Result<Boolean> apply(Transformer<N, E> xf, XfPolicy policy) {
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
		RemoveEdgesIfOp<?, ?> other = (RemoveEdgesIfOp<?, ?>) obj;
		return clear == other.clear && Objects.equals(edges, other.edges)
				&& Objects.equals(filter, other.filter);
	}

	@Override
	public String toString() {
		return String.format("[%s] %s", type(), edges);
	}
}
