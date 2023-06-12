package net.certiv.common.graph.ops;

import java.util.Collection;
import java.util.LinkedList;
import java.util.Objects;

import net.certiv.common.graph.Edge;
import net.certiv.common.graph.Graph;
import net.certiv.common.graph.Node;
import net.certiv.common.graph.ex.TransformException;
import net.certiv.common.stores.LinkedHashList;

public class ConsolidateOp<N extends Node<N, E>, E extends Edge<N, E>> implements ITransformOp<N, E> {

	public static <N extends Node<N, E>, E extends Edge<N, E>> ConsolidateOp<N, E> of(
			Collection<? extends N> sources, N target) {
		return new ConsolidateOp<>(sources, target);
	}

	// --------------------------------

	/** key=target node; value=edges */
	private final LinkedHashList<N, N> args = new LinkedHashList<>();

	public class Args {
		public final E edge;
		public final N beg;
		public final N end;
		public final boolean cyclic;

		private Args(E edge, N beg, N end, boolean cyclic) {
			this.edge = edge;
			this.beg = beg;
			this.end = end;
			this.cyclic = cyclic;
		}
	}

	private ConsolidateOp(Collection<? extends N> sources, N target) {
		args.put(target, sources);
	}

	public LinkedHashList<N, N> args() {
		return args;
	}

	public boolean mergeRule(ITransformOp<N, E> rule) {
		RuleType type = type();
		if (rule == this) throw new TransformException(ERR_SELF_MERGE, type);
		if (rule.type() != type) {
			throw new TransformException(ERR_MERGE, rule.type(), type);
		}

		LinkedHashList<N, N> in = ((ConsolidateOp<N, E>) rule).args;
		in.forEach((tgt, srcs) -> args.put(tgt, srcs));
		return true;
	}

	@Override
	public boolean exec(Graph<N, E> graph) {
		for (N target : args.keySet()) {
			LinkedList<N> sources = args.get(target);
			if (graph.contains(target)) {
				graph.consolidateEdges(sources, target);
			}
		}
		return true;
	}

	@Override
	public RuleType type() {
		return RuleType.CONSOLIDATE;
	}

	@Override
	public int hashCode() {
		return Objects.hash(args);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) return true;
		if (obj == null) return false;
		if (getClass() != obj.getClass()) return false;
		ConsolidateOp<?, ?> other = (ConsolidateOp<?, ?>) obj;
		return Objects.equals(args, other.args);
	}

	@Override
	public String toString() {
		return String.format("%s: %s", type(), args);
	}
}
