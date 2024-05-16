package net.certiv.common.graph.ops;

import net.certiv.common.graph.Edge;
import net.certiv.common.graph.Node;
import net.certiv.common.graph.Transformer;
import net.certiv.common.graph.XfPermits;
import net.certiv.common.graph.XfPolicy;
import net.certiv.common.graph.ex.GraphEx;
import net.certiv.common.graph.ex.GraphException;
import net.certiv.common.graph.id.IUId;
import net.certiv.common.stores.Result;

/**
 * TransformOps describe elementary, deferrable graph manipulation operations. Each op is
 * executed by application to a {@code Graph}.
 */
public interface ITransformOp<I extends IUId, N extends Node<I, N, E>, E extends Edge<I, N, E>> {

	GraphException ERR_APPLY = GraphEx.of("Error applying graph transform op: %s.");

	/** @return the transform op type */
	XfPermits type();

	/**
	 * Determine if this rule can be applied to the given {@code Graph}. Evaluates
	 * pre-conditions; not a full simulation of the transform application.
	 *
	 * @param xf {@code Transformer} for the target graph
	 * @return {@code Result#OK} on success, or (conditional on policy) a
	 *         {@code Result#err} on failure
	 * @see XfPolicy#TEST
	 */
	Result<Boolean> canApply(Transformer<I, N, E> xf);

	/**
	 * Apply {@code this} transform on the given {@code Graph} subject to the given
	 * compliance policy.
	 *
	 * @param xf     {@code Transformer} for the target graph
	 * @param policy execution condition compliance
	 * @return {@code Result#OK} on success, or (conditional on policy) a
	 *         {@code Result#err} on failure
	 * @throws (conditional on policy) GraphException on failure
	 * @see XfPolicy#TEST
	 * @see XfPolicy#EXECUTE
	 */
	Result<Boolean> apply(Transformer<I, N, E> xf, XfPolicy policy);
}
