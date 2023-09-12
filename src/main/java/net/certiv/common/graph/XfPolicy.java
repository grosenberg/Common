package net.certiv.common.graph;

import java.util.Arrays;
import java.util.EnumSet;
import java.util.Objects;

import net.certiv.common.graph.ex.GraphException;

public class XfPolicy {

	public enum Flg {
		/** Pre-condition testing of parameters. */
		Qualify,
		/** Repair/tolerate invalid parameters to extent possible. */
		Repair,
		/** Stop on pre-condition failure. */
		Stop,
		/** Block execution; test-only. */
		Block,
		/** Report success/failure by returning a {@code Result}. */
		Report,
		/** Report success/failure by throwing a {@link GraphException}. */
		Exception
	}

	/**
	 * Flags: {@link Flg#Qualify}, {@link Flg#Stop}, {@link Flg#Exception}
	 */
	public static final XfPolicy ERROR = XfPolicy.of(Flg.Qualify, Flg.Stop, Flg.Exception);

	/**
	 * Flags: {@link Flg#Qualify}, {@link Flg#Stop}, {@link Flg#Exception}
	 */
	public static final XfPolicy REPORT = XfPolicy.of(Flg.Qualify, Flg.Stop, Flg.Report);

	/**
	 * Flags: {@link Flg#Qualify}, {@link Flg#Block}, {@link Flg#Report}
	 */
	public static final XfPolicy TEST = XfPolicy.of(Flg.Qualify, Flg.Block, Flg.Report);

	/**
	 * Flags: {@link Flg#Exception}.
	 */
	public static final XfPolicy EXECUTE = XfPolicy.of(Flg.Exception);

	/**
	 * Flags: {@link Flg#Qualify}, {@link Flg#Repair}, {@link Flg#Report}
	 */
	public static final XfPolicy DEFAULT = XfPolicy.of(Flg.Qualify, Flg.Repair, Flg.Report);

	/**
	 * Scratch policy constructor from the supplied flags.
	 * <p>
	 * Internal validation ensures that one of {@link Flg#Exception}, {@link Flg#Report}
	 * is present in the policy as constructed. If both are provided, then only
	 * {@link Flg#Exception} is retained.
	 *
	 * @param flgs policy descriptor flags
	 * @return constructed policy
	 */
	public static XfPolicy of(Flg... flgs) {
		EnumSet<Flg> enums = EnumSet.copyOf(Arrays.asList(flgs));
		validate(enums);
		return new XfPolicy(enums);
	}

	/**
	 * Policy constructor derived from the given policy by the addition of the given
	 * flags.
	 * <p>
	 * Internal validation ensures that one of {@link Flg#Exception}, {@link Flg#Report}
	 * is present in the policy as constructed. If both are provided, then only
	 * {@link Flg#Exception} is retained.
	 *
	 * @param flgs policy descriptor flags
	 * @return constructed policy
	 */
	public static XfPolicy of(XfPolicy policy, Flg... flgs) {
		Objects.requireNonNull(policy);
		if (flgs == null) return policy;

		EnumSet<Flg> enums = EnumSet.copyOf(policy.flgs);
		enums.addAll(Arrays.asList(flgs));
		validate(enums);
		return new XfPolicy(enums);
	}

	// --------------------------------

	private static void validate(EnumSet<Flg> enums) {
		if (enums.contains(Flg.Exception) && enums.contains(Flg.Report)) {
			enums.remove(Flg.Report);
		}
		if (!enums.contains(Flg.Exception) && !enums.contains(Flg.Report)) {
			enums.add(Flg.Report);
		}
	}

	// --------------------------------

	private final EnumSet<Flg> flgs;

	private XfPolicy(EnumSet<Flg> flgs) {
		this.flgs = flgs;

	}

	/** Perform pre-qualification */
	public boolean qualify() {
		return flgs.contains(Flg.Qualify);
	}

	/** Stop on pre-qualification failure */
	public boolean condStop() {
		return flgs.contains(Flg.Stop);
	}

	/** Block execution */
	public boolean block() {
		return flgs.contains(Flg.Block);
	}

	/** Repair or tolerate invalid parameters to extent possible */
	public boolean repair() {
		return flgs.contains(Flg.Repair);
	}

	/** Report success/failure by return */
	public boolean rptByRet() {
		return flgs.contains(Flg.Report);
	}

	/** Report success by return; failures by exception */
	public boolean rptByEx() {
		return flgs.contains(Flg.Exception);
	}
}
