package net.certiv.common.graph.ops;

import java.util.Collection;
import java.util.LinkedList;
import java.util.Objects;

import net.certiv.common.graph.Edge;
import net.certiv.common.graph.Node;
import net.certiv.common.graph.Transformer;
import net.certiv.common.graph.XfPermits;
import net.certiv.common.graph.XfPolicy;
import net.certiv.common.graph.id.Id;
import net.certiv.common.stores.Result;
import net.certiv.common.stores.UniqueList;
import net.certiv.common.util.Strings;

public class ReplicateOp<I extends Id, N extends Node<I, N, E>, E extends Edge<I, N, E>>
		implements ITransformOp<I, N, E> {

	/**
	 * @param src     a source node
	 * @param targets the target nodes
	 * @param remove  {@code true} to remove the source node
	 */
	public static <I extends Id, N extends Node<I, N, E>, E extends Edge<I, N, E>> ReplicateOp<I, N, E> of(
			N src, Collection<? extends N> targets, boolean remove) {
		return new ReplicateOp<>(src, targets, remove);
	}

	// --------------------------------

	public final N src;
	public final UniqueList<N> targets = new UniqueList<>();
	public final boolean remove;

	private ReplicateOp(N src, Collection<? extends N> targets, boolean remove) {
		this.src = src;
		this.targets.addAll(targets);
		this.remove = remove;
	}

	@Override
	public XfPermits type() {
		return XfPermits.REPLICATE;
	}

	@Override
	public Result<Boolean> canApply(Transformer<I, N, E> xf) {
		Result<LinkedList<E>> res = xf.replicateEdges(src, targets, remove);
		return res.valid() ? Result.OK : Result.FAIL;
	}

	@Override
	public Result<Boolean> apply(Transformer<I, N, E> xf, XfPolicy policy) {
		Result<LinkedList<E>> res = xf.replicateEdges(policy, src, targets, remove);
		return res.valid() ? Result.OK : Result.FAIL;
	}

	@Override
	public int hashCode() {
		return Objects.hash(src, remove, targets);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) return true;
		if (obj == null) return false;
		if (getClass() != obj.getClass()) return false;
		ReplicateOp<?, ?, ?> other = (ReplicateOp<?, ?, ?>) obj;
		return Objects.equals(src, other.src) && remove == other.remove
				&& Objects.equals(targets, other.targets);
	}

	@Override
	public String toString() {
		return String.format("[%s] %s -> %s %s", type(), src, targets, remove ? "rmv" : Strings.EMPTY);
	}
}
