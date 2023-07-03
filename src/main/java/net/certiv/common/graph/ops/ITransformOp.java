package net.certiv.common.graph.ops;

import net.certiv.common.graph.Edge;
import net.certiv.common.graph.Node;
import net.certiv.common.graph.Transformer;
import net.certiv.common.graph.XfPermits;
import net.certiv.common.graph.XfPolicy;
import net.certiv.common.graph.ex.GraphEx;
import net.certiv.common.graph.ex.GraphException;
import net.certiv.common.stores.Result;

/**
 * TransformOps describe elementary, deferrable graph manipulation operations. Each op is
 * executed by application to a {@code Graph}.
 */
public interface ITransformOp<N extends Node<N, E>, E extends Edge<N, E>> {

	GraphException ERR_APPLY = GraphEx.of("Error applying graph transform op: %s.");

	/** @return the transform op type */
	XfPermits type();

	/**
	 * Determine if this rule can be applied to the given {@code Graph}. Evaluates
	 * pre-conditions; not a full simulation of the transform application.
	 *
	 * @param xf {@link Transformer} for the target graph
	 * @return {@link Result.OK} on success, or (conditional on policy) a
	 *         {@link Result#err} on failure
	 * @see XfPolicy#CHECK
	 */
	Result<Boolean> canApply(Transformer<N, E> xf);

	/**
	 * Apply {@code this} transform on the given {@code Graph} subject to the given
	 * compliance policy.
	 *
	 * @param xf     {@link Transformer} for the target graph
	 * @param policy execution condition compliance
	 * @return {@link Result.OK} on success, or (conditional on policy) a
	 *         {@link Result#err} on failure
	 * @throws (conditional on policy) GraphException on failure
	 * @see XfPolicy#PERMIT
	 * @see XfPolicy#EXEC
	 */
	Result<Boolean> apply(Transformer<N, E> xf, XfPolicy policy);
}
