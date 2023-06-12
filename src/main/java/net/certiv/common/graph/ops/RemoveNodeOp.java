package net.certiv.common.graph.ops;

import java.util.Collection;
import java.util.Objects;

import net.certiv.common.graph.Edge;
import net.certiv.common.graph.Graph;
import net.certiv.common.graph.Node;
import net.certiv.common.graph.ex.TransformException;
import net.certiv.common.stores.UniqueList;

public class RemoveNodeOp<N extends Node<N, E>, E extends Edge<N, E>> implements ITransformOp<N, E> {

	public static <N extends Node<N, E>, E extends Edge<N, E>> RemoveNodeOp<N, E> of(N node) {
		RemoveNodeOp<N, E> op = new RemoveNodeOp<>();
		op.nodes.add(node);
		return op;
	}

	public static <N extends Node<N, E>, E extends Edge<N, E>> RemoveNodeOp<N, E> of(
			Collection<? extends N> nodes) {
		RemoveNodeOp<N, E> op = new RemoveNodeOp<>();
		op.nodes.addAll(nodes);
		return op;
	}

	// --------------------------------

	private final UniqueList<N> nodes = new UniqueList<>();

	public UniqueList<N> getNodes() {
		return nodes;
	}

	public boolean mergeRule(ITransformOp<N, E> rule) {
		RuleType type = type();
		if (rule == this) throw new TransformException(ERR_SELF_MERGE, type);
		if (rule.type() != type) {
			throw new TransformException(ERR_MERGE, rule.type(), type);
		}
		return nodes.addAll(((RemoveNodeOp<N, E>) rule).nodes);
	}

	@Override
	public boolean exec(Graph<N, E> graph) {
		boolean ok = true;
		for (N node : nodes) {
			if (graph.contains(node)) {
				ok &= graph.removeNode(node);
			}
		}
		return ok;
	}

	@Override
	public RuleType type() {
		return RuleType.REMOVE_NODE;
	}

	@Override
	public int hashCode() {
		return Objects.hash(nodes);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) return true;
		if ((obj == null) || (getClass() != obj.getClass())) return false;
		RemoveNodeOp<?, ?> other = (RemoveNodeOp<?, ?>) obj;
		return Objects.equals(nodes, other.nodes);
	}

	@Override
	public String toString() {
		return String.format("%s: %s", type(), nodes);
	}
}
