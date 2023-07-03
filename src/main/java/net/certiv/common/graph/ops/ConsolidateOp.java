package net.certiv.common.graph.ops;

import java.util.Collection;
import java.util.Objects;

import net.certiv.common.graph.Edge;
import net.certiv.common.graph.Node;
import net.certiv.common.graph.Transformer;
import net.certiv.common.graph.XfPermits;
import net.certiv.common.graph.XfPolicy;
import net.certiv.common.stores.Result;
import net.certiv.common.stores.UniqueList;

public class ConsolidateOp<N extends Node<N, E>, E extends Edge<N, E>> implements ITransformOp<N, E> {

	public static <N extends Node<N, E>, E extends Edge<N, E>> ConsolidateOp<N, E> of(
			Collection<? extends N> sources, N target) {
		return new ConsolidateOp<>(sources, target);
	}

	// --------------------------------

	public final UniqueList<N> sources = new UniqueList<>();
	public final N target;

	private ConsolidateOp(Collection<? extends N> sources, N target) {
		this.sources.addAll(sources);
		this.target = target;
	}

	@Override
	public XfPermits type() {
		return XfPermits.CONSOLIDATE;
	}

	@Override
	public Result<Boolean> canApply(Transformer<N, E> xf) {
		return xf.consolidateEdges(XfPolicy.CHECK, sources, target);
	}

	@Override
	public Result<Boolean> apply(Transformer<N, E> xf, XfPolicy policy) {
		return xf.consolidateEdges(policy, sources, target);
	}

	@Override
	public int hashCode() {
		return Objects.hash(sources, target);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) return true;
		if (obj == null) return false;
		if (getClass() != obj.getClass()) return false;
		ConsolidateOp<?, ?> other = (ConsolidateOp<?, ?>) obj;
		return Objects.equals(sources, other.sources) && Objects.equals(target, other.target);
	}

	@Override
	public String toString() {
		return String.format("[%s] %s -> %s", type(), sources, target);
	}
}
