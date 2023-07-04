package net.certiv.common.graph.ops;

import java.util.LinkedHashMap;
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

	public static <N extends Node<N, E>, E extends Edge<N, E>> CopyOp<N, E> of(Map<N, GraphPath<N, E>> sg,
			N dst, boolean remove) {

		return new CopyOp<>(sg, dst, remove);
	}

	// --------------------------------

	public final LinkedHashMap<N, GraphPath<N, E>> sg = new LinkedHashMap<>();
	public final N dst;
	public final boolean remove;

	private CopyOp(Map<N, GraphPath<N, E>> sg, N dst, boolean remove) {
		this.sg.putAll(sg);
		this.dst = dst;
		this.remove = remove;
	}

	@Override
	public XfPermits type() {
		return XfPermits.COPY;
	}

	@Override
	public Result<Boolean> canApply(Transformer<N, E> xf) {
		return xf.copy(sg, dst, remove);
	}

	@Override
	public Result<Boolean> apply(Transformer<N, E> xf, XfPolicy policy) {
		return xf.copy(policy, sg, dst, remove);
	}

	@Override
	public int hashCode() {
		return Objects.hash(sg, dst, remove);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) return true;
		if (obj == null) return false;
		if (getClass() != obj.getClass()) return false;
		CopyOp<?, ?> other = (CopyOp<?, ?>) obj;
		return Objects.equals(dst, other.dst) && remove == other.remove && Objects.equals(sg, other.sg);
	}

	@Override
	public String toString() {
		return String.format("[%s] %s -> %s %s", type(), sg, dst, remove ? "rmv" : Strings.EMPTY);
	}
}
