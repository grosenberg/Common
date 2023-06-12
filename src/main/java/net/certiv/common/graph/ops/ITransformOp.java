package net.certiv.common.graph.ops;

import net.certiv.common.graph.Edge;
import net.certiv.common.graph.Graph;
import net.certiv.common.graph.Node;

/**
 * A transform describes an elementary graph manipulation operation. Transforms are
 * executed by applying them to a {@code Graph}.
 */
public interface ITransformOp<N extends Node<N, E>, E extends Edge<N, E>> {

	String ERR_SELF_MERGE = "Cannot merge %s rule into self.";
	String ERR_MERGE = "Cannot merge %s into %s.";

	public enum RuleType {
		CONSOLIDATE,
		MOVE,
		REDUCE,
		REMOVE_NODE,
		REMOVE_EDGE,
		REPLICATE,
	}

	/**
	 * Execute {@code this} transform on the given {@code Graph}.
	 *
	 * @param graph the graph to transform
	 * @return {@code true} on success
	 */
	boolean exec(Graph<N, E> graph);

	/** @return the transform type */
	RuleType type();
}
