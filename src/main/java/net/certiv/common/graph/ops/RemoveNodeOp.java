package net.certiv.common.graph.ops;

import java.util.Collection;
import java.util.Objects;

import net.certiv.common.graph.Edge;
import net.certiv.common.graph.Node;
import net.certiv.common.graph.Transformer;
import net.certiv.common.graph.XfPermits;
import net.certiv.common.graph.XfPolicy;
import net.certiv.common.id.IUId;
import net.certiv.common.stores.Result;
import net.certiv.common.stores.UniqueList;

public class RemoveNodeOp<I extends IUId, N extends Node<I, N, E>, E extends Edge<I, N, E>>
		implements ITransformOp<I, N, E> {

	public static <I extends IUId, N extends Node<I, N, E>, E extends Edge<I, N, E>> RemoveNodeOp<I, N, E> of(
			N node) {
		RemoveNodeOp<I, N, E> op = new RemoveNodeOp<>();
		op.nodes.add(node);
		return op;
	}

	public static <I extends IUId, N extends Node<I, N, E>, E extends Edge<I, N, E>> RemoveNodeOp<I, N, E> of(
			Collection<? extends N> nodes) {
		RemoveNodeOp<I, N, E> op = new RemoveNodeOp<>();
		op.nodes.addAll(nodes);
		return op;
	}

	// --------------------------------

	public final UniqueList<N> nodes = new UniqueList<>();

	@Override
	public XfPermits type() {
		return XfPermits.REMOVE_NODE;
	}

	@Override
	public Result<Boolean> canApply(Transformer<I, N, E> xf) {
		return xf.removeNodes(nodes);
	}

	@Override
	public Result<Boolean> apply(Transformer<I, N, E> xf, XfPolicy policy) {
		return xf.removeNodes(policy, nodes);
	}

	@Override
	public int hashCode() {
		return Objects.hash(nodes);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) return true;
		if ((obj == null) || (getClass() != obj.getClass())) return false;
		RemoveNodeOp<?, ?, ?> other = (RemoveNodeOp<?, ?, ?>) obj;
		return Objects.equals(nodes, other.nodes);
	}

	@Override
	public String toString() {
		return String.format("[%s] %s", type(), nodes);
	}
}
