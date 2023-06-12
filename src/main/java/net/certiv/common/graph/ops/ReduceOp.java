package net.certiv.common.graph.ops;

import java.util.LinkedHashMap;
import java.util.Map.Entry;
import java.util.Objects;

import net.certiv.common.graph.Edge;
import net.certiv.common.graph.Graph;
import net.certiv.common.graph.Node;
import net.certiv.common.graph.ex.TransformException;

public class ReduceOp<N extends Node<N, E>, E extends Edge<N, E>> implements ITransformOp<N, E> {

	/**
	 * @param src the source edge
	 * @param dst the destination edge
	 */
	public static <N extends Node<N, E>, E extends Edge<N, E>> ReduceOp<N, E> of(E src, E dst) {
		return new ReduceOp<>(src, dst);
	}

	// --------------------------------

	/** key=edge; value=args */
	private final LinkedHashMap<E, E> args = new LinkedHashMap<>();

	private ReduceOp(E src, E dst) {
		args.put(src, dst);
	}

	public LinkedHashMap<E, E> args() {
		return args;
	}

	public boolean mergeRule(ITransformOp<N, E> rule) {
		RuleType type = type();
		if (rule == this) throw new TransformException(ERR_SELF_MERGE, type);
		if (rule.type() != type) {
			throw new TransformException(ERR_MERGE, rule.type(), type);
		}

		boolean ok = true;
		LinkedHashMap<E, E> in = ((ReduceOp<N, E>) rule).args;
		for (Entry<E, E> entry : in.entrySet()) {
			ok &= args.putIfAbsent(entry.getKey(), entry.getValue()) == null;
		}

		return ok;
	}

	@Override
	public boolean exec(Graph<N, E> graph) {
		boolean ok = true;
		for (E src : args.keySet()) {
			E dst = args.get(src);
			if (graph.contains(src) && graph.contains(dst)) {
				ok &= graph.reduce(src, dst);
			}
		}
		return ok;
	}

	@Override
	public RuleType type() {
		return RuleType.REDUCE;
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
		ReduceOp<?, ?> other = (ReduceOp<?, ?>) obj;
		return Objects.equals(args, other.args);
	}

	@Override
	public String toString() {
		return String.format("%s: %s", type(), args);
	}
}
