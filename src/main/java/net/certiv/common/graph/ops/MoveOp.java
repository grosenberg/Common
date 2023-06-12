package net.certiv.common.graph.ops;

import java.util.LinkedHashMap;
import java.util.Objects;

import net.certiv.common.graph.Edge;
import net.certiv.common.graph.Graph;
import net.certiv.common.graph.Node;
import net.certiv.common.graph.ex.TransformException;

public class MoveOp<N extends Node<N, E>, E extends Edge<N, E>> implements ITransformOp<N, E> {

	/**
	 * @param edge   an existing graph edge
	 * @param beg    the new begin node
	 * @param end    the new end node
	 * @param cyclic {@code true} to permit creation of single edge cycles
	 */
	public static <N extends Node<N, E>, E extends Edge<N, E>> MoveOp<N, E> of(E edge, N beg, N end,
			boolean cyclic) {

		return new MoveOp<>(edge, beg, end, cyclic);
	}

	// --------------------------------

	/** key=edge; value=args */
	private final LinkedHashMap<E, Args> moves = new LinkedHashMap<>();

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

	private MoveOp(E edge, N beg, N end, boolean cyclic) {
		Args args = new Args(edge, beg, end, cyclic);
		moves.put(edge, args);
	}

	public LinkedHashMap<E, Args> args() {
		return moves;
	}

	public boolean mergeRule(ITransformOp<N, E> rule) {
		RuleType type = type();
		if (rule == this) throw new TransformException(ERR_SELF_MERGE, type);
		if (rule.type() != type) {
			throw new TransformException(ERR_MERGE, rule.type(), type);
		}
		this.moves.putAll(((MoveOp<N, E>) rule).moves);
		return true;
	}

	@Override
	public boolean exec(Graph<N, E> graph) {
		boolean ok = true;
		for (E edge : moves.keySet()) {
			if (graph.contains(edge)) {
				Args args = moves.get(edge);
				ok &= graph.move(edge, args.beg, args.end, args.cyclic);
			}
		}
		return ok;
	}

	@Override
	public RuleType type() {
		return RuleType.MOVE;
	}

	@Override
	public int hashCode() {
		return Objects.hash(moves);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) return true;
		if (obj == null) return false;
		if (getClass() != obj.getClass()) return false;
		MoveOp<?, ?> other = (MoveOp<?, ?>) obj;
		return Objects.equals(moves, other.moves);
	}

	@Override
	public String toString() {
		return String.format("%s: %s", type(), moves);
	}
}
