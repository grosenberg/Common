package net.certiv.common.graph.ops;

import java.util.LinkedHashMap;
import java.util.Objects;

import net.certiv.common.graph.Edge;
import net.certiv.common.graph.Graph;
import net.certiv.common.graph.Node;
import net.certiv.common.graph.ex.TransformException;

public class RemoveEdgeOp<N extends Node<N, E>, E extends Edge<N, E>> implements ITransformOp<N, E> {

	public static <N extends Node<N, E>, E extends Edge<N, E>> RemoveEdgeOp<N, E> of(E edge, boolean clear) {
		RemoveEdgeOp<N, E> op = new RemoveEdgeOp<>();
		op.edges.put(edge, clear);
		return op;
	}

	// --------------------------------

	/** key=edge; value=clear */
	private final LinkedHashMap<E, Boolean> edges = new LinkedHashMap<>();

	public LinkedHashMap<E, Boolean> getEdges() {
		return edges;
	}

	public boolean mergeRule(ITransformOp<N, E> rule) {
		RuleType type = type();
		if (rule == this) throw new TransformException(ERR_SELF_MERGE, type);
		if (rule.type() != type) {
			throw new TransformException(ERR_MERGE, rule.type(), type);
		}
		this.edges.putAll(((RemoveEdgeOp<N, E>) rule).edges);
		return true;
	}

	@Override
	public boolean exec(Graph<N, E> graph) {
		boolean ok = true;
		for (E edge : edges.keySet()) {
			if (graph.contains(edge)) {
				ok &= graph.removeEdge(edge, edges.get(edge));
			}
		}
		return ok;
	}

	@Override
	public RuleType type() {
		return RuleType.REMOVE_EDGE;
	}

	@Override
	public int hashCode() {
		return Objects.hash(edges);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) return true;
		if (obj == null) return false;
		if (getClass() != obj.getClass()) return false;
		RemoveEdgeOp<?, ?> other = (RemoveEdgeOp<?, ?>) obj;
		return Objects.equals(edges, other.edges);
	}

	@Override
	public String toString() {
		return String.format("%s: %s", type(), edges);
	}
}
