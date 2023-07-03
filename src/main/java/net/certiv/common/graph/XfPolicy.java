package net.certiv.common.graph;

import net.certiv.common.ex.Explainer;
import net.certiv.common.graph.ex.GraphException;

public enum XfPolicy {

	/**
	 * Error: exceptional reporting of pre-condition failures.
	 * <p>
	 * Executes only on pre-conditions success.
	 * <p>
	 * Throws a {@link GraphException} on any pre-condition failure, including a
	 * {@link Explainer} of impermissible pre-conditions as the cause. Throws a
	 * {@link GraphException} with an {@link Explainer} capturing any actual execution
	 * exceptions/errors.
	 */
	ERROR(true, true, false, false, false, true),

	/**
	 * Report: non-exceptional reporting of pre-condition failures.
	 * <p>
	 * Executes only on pre-conditions success. Returns {@link Explainer} of impermissible
	 * pre-conditions. Throws a {@link GraphException} with an {@link Explainer} capturing
	 * any actual execution exceptions/errors.
	 */
	REPORT(true, true, false, false, true, false),

	/**
	 * Permit: report with parameter repair.
	 * <p>
	 * Check pre-conditions and repairs parameters to extent possible prior to execution.
	 * Executes regardless of pre-condition success. Throws a {@link GraphException} with
	 * an {@link Explainer} capturing any actual execution exceptions/errors.
	 */
	PERMIT(true, false, false, true, true, false),

	/**
	 * Check: non-execution test with non-exceptional reporting of pre-condition failures.
	 * <p>
	 * Check pre-conditions, but always block execution. Return {@link Explainer} of
	 * impermissible pre-conditions.
	 */
	CHECK(true, true, true, false, true, false),

	/**
	 * Execute: no pre-qualification, but with parameter repair to extent possible.
	 * <p>
	 * Throws a {@link GraphException} with an {@link Explainer} capturing any actual
	 * execution exceptions/errors.
	 */
	EXEC(false, false, false, true, false, true),

	;

	// --------------------------------

	private final boolean qual;
	private final boolean stop;
	private final boolean block;
	private final boolean repair;
	private final boolean rptByRet;
	private final boolean rptByEx;

	XfPolicy(boolean qual, boolean stop, boolean block, boolean repair, boolean rptByRet, boolean rptByEx) {
		this.qual = qual;
		this.stop = stop;
		this.block = block;
		this.repair = repair;
		this.rptByRet = rptByRet;
		this.rptByEx = rptByEx;
	}

	/** Perform pre-qualification */
	public boolean qualify() {
		return qual;
	}

	/** Stop on pre-qualification failure */
	public boolean condStop() {
		return stop;
	}

	/** Block execution */
	public boolean block() {
		return block;
	}

	/** Repair or exclude invalid parameters to extent possible */
	public boolean repair() {
		return repair;
	}

	/** Report pre-qualification failures by return */
	public boolean rptByRet() {
		return rptByRet;
	}

	/** Report pre-qualification failures by exception */
	public boolean rptByEx() {
		return rptByEx;
	}
}
