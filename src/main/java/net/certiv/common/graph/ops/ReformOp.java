package net.certiv.common.graph.ops;

import java.util.Collection;
import java.util.List;
import java.util.Objects;

import net.certiv.common.check.Assert;
import net.certiv.common.graph.Edge;
import net.certiv.common.graph.Node;
import net.certiv.common.graph.Transformer;
import net.certiv.common.graph.XfPermits;
import net.certiv.common.graph.XfPolicy;
import net.certiv.common.graph.id.Id;
import net.certiv.common.stores.Result;
import net.certiv.common.util.Strings;

public class ReformOp<I extends Id, N extends Node<I, N, E>, E extends Edge<I, N, E>>
		implements ITransformOp<I, N, E> {

	public static <I extends Id, N extends Node<I, N, E>, E extends Edge<I, N, E>> ReformOp<I, N, E> transfer(
			E edge, N beg) {
		return new ReformOp<>(XfPermits.TRANSFER, List.of(edge), beg, false);
	}

	public static <I extends Id, N extends Node<I, N, E>, E extends Edge<I, N, E>> ReformOp<I, N, E> transfer(
			Collection<? extends E> edges, N beg) {
		return new ReformOp<>(XfPermits.TRANSFER, edges, beg, false);
	}

	public static <I extends Id, N extends Node<I, N, E>, E extends Edge<I, N, E>> ReformOp<I, N, E> reterminate(
			E edge, N end, boolean cyclic) {
		return new ReformOp<>(XfPermits.RETERMINATE, List.of(edge), end, cyclic);
	}

	public static <I extends Id, N extends Node<I, N, E>, E extends Edge<I, N, E>> ReformOp<I, N, E> reterminate(
			Collection<? extends E> edges, N end, boolean cyclic) {
		return new ReformOp<>(XfPermits.RETERMINATE, edges, end, cyclic);
	}

	// --------------------------------

	public final XfPermits type;
	public final List<? extends E> edges;
	public final N target;
	public final boolean cyclic;

	private ReformOp(XfPermits type, Collection<? extends E> edges, N target, boolean cyclic) {
		Assert.isTrue(XfPermits.TRANSFER == type || XfPermits.RETERMINATE == type);
		this.type = type;
		this.edges = List.copyOf(edges);
		this.target = target;
		this.cyclic = cyclic;
	}

	@Override
	public XfPermits type() {
		return type;
	}

	@Override
	public Result<Boolean> canApply(Transformer<I, N, E> xf) {
		if (XfPermits.TRANSFER == type) return xf.transfer(edges, target);
		return xf.reterminate(edges, target, cyclic);
	}

	@Override
	public Result<Boolean> apply(Transformer<I, N, E> xf, XfPolicy policy) {
		if (XfPermits.TRANSFER == type) return xf.transfer(policy, edges, target);
		return xf.reterminate(policy, edges, target, cyclic);
	}

	@Override
	public int hashCode() {
		return Objects.hash(cyclic, edges, target, type);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) return true;
		if (obj == null) return false;
		if (getClass() != obj.getClass()) return false;
		ReformOp<?, ?, ?> other = (ReformOp<?, ?, ?>) obj;
		return cyclic == other.cyclic && Objects.equals(edges, other.edges)
				&& Objects.equals(target, other.target) && type == other.type;
	}

	@Override
	public String toString() {
		return String.format("[%s] %s::%s (%s)", //
				type, edges, target, cyclic ? "allow cycles" : Strings.EMPTY);
	}
}
