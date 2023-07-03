package net.certiv.common.graph.ops;

import java.util.Objects;

import net.certiv.common.graph.Edge;
import net.certiv.common.graph.Node;
import net.certiv.common.graph.Transformer;
import net.certiv.common.graph.XfPermits;
import net.certiv.common.graph.XfPolicy;
import net.certiv.common.stores.Result;

public class ReduceOp<N extends Node<N, E>, E extends Edge<N, E>> implements ITransformOp<N, E> {

	public static <N extends Node<N, E>, E extends Edge<N, E>> ReduceOp<N, E> of(N node) {
		return new ReduceOp<>(node);
	}

	public static <N extends Node<N, E>, E extends Edge<N, E>> ReduceOp<N, E> of(E src, E dst) {
		return new ReduceOp<>(src, dst);
	}

	// --------------------------------

	public final N node;
	public final E src;
	public final E dst;

	private ReduceOp(N node) {
		this.node = node;
		this.src = null;
		this.dst = null;
	}

	private ReduceOp(E src, E dst) {
		this.node = null;
		this.src = src;
		this.dst = dst;
	}

	@Override
	public XfPermits type() {
		return XfPermits.REDUCE;
	}

	@Override
	public Result<Boolean> canApply(Transformer<N, E> xf) {
		return node != null ? xf.reduce(XfPolicy.CHECK, node) : xf.reduce(XfPolicy.CHECK, src, dst);
	}

	@Override
	public Result<Boolean> apply(Transformer<N, E> xf, XfPolicy policy) {
		return node != null ? xf.reduce(policy, node) : xf.reduce(policy, src, dst);
	}

	@Override
	public int hashCode() {
		return Objects.hash(node, dst, src);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) return true;
		if (obj == null) return false;
		if (getClass() != obj.getClass()) return false;
		ReduceOp<?, ?> other = (ReduceOp<?, ?>) obj;
		return Objects.equals(node, other.node) && Objects.equals(dst, other.dst)
				&& Objects.equals(src, other.src);
	}

	@Override
	public String toString() {
		if (node != null) return String.format("[%s] %s", type(), node);
		return String.format("[%s] %s::%s", type(), src, dst);
	}
}
