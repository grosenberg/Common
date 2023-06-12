package net.certiv.common.graph.ops;

import java.util.Collection;
import java.util.LinkedList;
import java.util.Objects;

import net.certiv.common.graph.Edge;
import net.certiv.common.graph.Graph;
import net.certiv.common.graph.Node;
import net.certiv.common.graph.ex.TransformException;
import net.certiv.common.stores.LinkedHashList;

public class ReplicateOp<N extends Node<N, E>, E extends Edge<N, E>> implements ITransformOp<N, E> {

	/**
	 * @param node    a source node
	 * @param targets the target nodes
	 * @param remove  {@code true} to remove the source node
	 */
	public static <N extends Node<N, E>, E extends Edge<N, E>> ReplicateOp<N, E> of(N node,
			Collection<? extends N> targets, boolean remove) {
		return new ReplicateOp<>(node, targets, remove);
	}

	// --------------------------------

	/** key=source node; value=list of args */
	private final LinkedHashList<N, ReplArgs> repls = new LinkedHashList<>();

	public class ReplArgs {
		public final Collection<? extends N> targets;
		public final boolean remove;

		private ReplArgs(Collection<? extends N> targets, boolean remove) {
			this.targets = targets;
			this.remove = remove;
		}
	}

	private ReplicateOp(N node, Collection<? extends N> targets, boolean remove) {
		ReplArgs replArgs = new ReplArgs(targets, remove);
		repls.put(node, replArgs);
	}

	public LinkedHashList<N, ReplArgs> replArgs() {
		return repls;
	}

	public boolean mergeRule(ITransformOp<N, E> rule) {
		RuleType type = type();
		if (rule == this) throw new TransformException(ERR_SELF_MERGE, type);
		if (rule.type() != type) {
			throw new TransformException(ERR_MERGE, rule.type(), type);
		}

		LinkedHashList<N, ReplArgs> in = ((ReplicateOp<N, E>) rule).repls;
		for (N src : in.keySet()) {
			if (!repls.containsKey(src)) {
				repls.put(src, in.get(src));
			} else {
				LinkedList<ReplArgs> replArgs = repls.get(src);
				replArgs.addAll(in.get(src));
			}
		}

		return true;
	}

	@Override
	public boolean exec(Graph<N, E> graph) {
		boolean ok = true;
		for (N src : repls.keySet()) {
			for (ReplArgs args : repls.get(src)) {
				ok &= graph.replicateEdges(src, args.targets, args.remove);
			}
		}
		return ok;
	}

	@Override
	public RuleType type() {
		return RuleType.REPLICATE;
	}

	@Override
	public int hashCode() {
		return Objects.hash(repls);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) return true;
		if (obj == null) return false;
		if (getClass() != obj.getClass()) return false;
		ReplicateOp<?, ?> other = (ReplicateOp<?, ?>) obj;
		return Objects.equals(repls, other.repls);
	}

	@Override
	public String toString() {
		return String.format("%s: %s", type(), repls);
	}
}
