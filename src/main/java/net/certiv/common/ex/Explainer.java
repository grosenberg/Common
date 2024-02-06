package net.certiv.common.ex;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.function.Predicate;

import org.apache.commons.text.TextStringBuilder;

import net.certiv.common.check.Assert;
import net.certiv.common.graph.ex.GraphEx;
import net.certiv.common.graph.ex.GraphException;
import net.certiv.common.stores.Result;
import net.certiv.common.util.Maths;
import net.certiv.common.util.Strings;

public class Explainer extends RuntimeException {

	private static final String EXPLAINER = "Explainer";
	private static final String TAT = "\tat ";

	private static final String ERR_NULL = "Collection is 'null'";

	/** Caption for reasons list */
	private static final String MSG_CAPTION = "%s - Reasons: ";
	/** Caption for 'location' stack trace */
	private static final String TRACE_CAPTION = "Stack trace: ";
	/** Caption for 'cause' stack trace */
	private static final String CAUSE_CAPTION = "Caused by: ";

	private final LinkedList<String> reasons = new LinkedList<>();

	/** Last 'last' state */
	private boolean last = true;

	public Explainer() {
		this(EXPLAINER);
	}

	public Explainer(String title) {
		super(title);
	}

	public LinkedList<String> reasons() {
		return reasons;
	}

	public Explainer add(Result<Boolean> res) {
		if (res.err()) add(res.getErr());
		return this;
	}

	public Explainer add(Throwable t) {
		if (t instanceof Explainer) {
			add(((Explainer) t).reasons());
		} else if (t != null) {
			reasons.add(getTrace(t));
			last = false;
		}
		return this;
	}

	public Explainer add(List<String> reasons) {
		this.reasons.addAll(reasons);
		this.last = this.reasons.isEmpty();
		return this;
	}

	public Explainer addFirst(Result<Boolean> res) {
		if (res.err()) addFirst(res.getErr());
		return this;
	}

	public Explainer addFirst(Throwable t) {
		if (t instanceof Explainer) {
			addFirst(((Explainer) t).reasons());
		} else if (t != null) {
			reasons.addFirst(getTrace(t));
			last = false;
		}
		return this;
	}

	public Explainer addFirst(List<String> reasons) {
		this.reasons.addAll(0, reasons);
		this.last = this.reasons.isEmpty();
		return this;
	}

	public Explainer reason(String msg) {
		reasons.add(msg);
		return this;
	}

	public Explainer reason(String fmt, Object... args) {
		reasons.add(String.format(fmt, args));
		return this;
	}

	public Explainer reason(boolean ok, String msg) {
		if (ok) {
			reasons.add(msg);
			this.last = ok;
		}
		return this;
	}

	public boolean reason(boolean ok, String fmt, Object... args) {
		if (ok) {
			reasons.add(String.format(fmt, args));
			this.last = ok;
		}
		return ok;
	}

	/**
	 * Tests the given condition. Pass on {@code true}. Otherwise, (1) adds the given
	 * message as a failure reason and (2) sets the result flag to {@code false}.
	 *
	 * @param ok   test enable flag
	 * @param cond test condition
	 * @param msg  reason template
	 * @return result flag
	 */
	public boolean is(boolean ok, boolean cond, String msg) {
		if (ok && !cond) {
			reasons.add(msg);
			this.last = ok = false;
		}
		return ok;
	}

	/**
	 * Tests the given condition. Pass on {@code true}. Otherwise, (1) adds the given
	 * message as a failure reason and (2) sets the result flag to {@code false}.
	 *
	 * @param ok   test enable flag
	 * @param cond test condition
	 * @param fmt  reason template
	 * @param args reason parameters
	 * @return result flag
	 */
	public boolean is(boolean ok, boolean cond, String fmt, Object... args) {
		if (ok && !cond) {
			reasons.add(String.format(fmt, args));
			this.last = ok = false;
		}
		return ok;
	}

	/**
	 * Tests the given element for {@code != null}. Pass on {@code true}. Otherwise, (1)
	 * adds the given message as a failure reason and (2) sets the result flag to
	 * {@code false}.
	 *
	 * @param ok   test enable flag
	 * @param elem element to test
	 * @param msg  reason template
	 * @return result flag
	 */
	public boolean notNull(boolean ok, Object elem, String msg) {
		if (ok && elem == null) {
			reasons.add(msg);
			this.last = ok = false;
		}
		return ok;
	}

	/**
	 * Tests the given element for {@code != null}. Pass on {@code true}. Otherwise, (1)
	 * adds the given message as a failure reason and (2) sets the result flag to
	 * {@code false}.
	 *
	 * @param ok   test enable flag
	 * @param elem element to test
	 * @param fmt  reason template
	 * @param args reason parameters
	 * @return result flag
	 */
	public boolean notNull(boolean ok, Object elem, String fmt, Object... args) {
		if (ok && elem == null) {
			reasons.add(String.format(fmt, args));
			this.last = ok = false;
		}
		return ok;
	}

	/**
	 * Tests each element of the given collection against the given predicate condition.
	 * For each element of the given collection: pass on {@code true}; otherwise, adds a
	 * 'reason' for failure.
	 * <p>
	 * The given message template must include two parameter variables: (1) collection
	 * element index and (2) collection element.
	 *
	 * @param <E>   collection value type
	 * @param ok    test enable flag
	 * @param elems collection of elements to test
	 * @param pred  test predicate
	 * @param msg   reason template (w/two parameter variables)
	 * @return accumulated test result flag
	 * @throws GraphException if the collection is {@code null}
	 */
	public <E> boolean any(boolean ok, Collection<? extends E> elems, Predicate<E> pred, String msg) {
		if (ok) {
			Assert.notNull(GraphEx.of(ERR_NULL), elems);
			boolean flg = true;
			List<E> tmp = new ArrayList<>(elems);
			for (int idx = 0; idx < tmp.size(); idx++) {
				E elem = tmp.get(idx);
				if (!pred.test(elem)) {
					reasons.add(String.format(msg, idx, elem));
					flg &= false;
				}
			}
			this.last = ok = flg;
		}
		return ok;
	}

	/** Last 'last' state */
	public boolean last() {
		return last;
	}

	/** @return if any reasons are present */
	public boolean isEmpty() {
		return reasons.isEmpty();
	}

	/** @return the number of reasons present */
	public int size() {
		return reasons.size();
	}

	/** Clears all present reasons. */
	public void clear() {
		reasons.clear();
	}

	@Override
	public String getMessage() {
		TextStringBuilder sb = new TextStringBuilder(super.getMessage());
		sb.appendln(MSG_CAPTION, super.getMessage());
		for (String reason : reasons) {
			sb.appendln(Strings.TAB + reason);
		}
		return sb.toString();
	}

	/** @return a summary stack trace */
	public String getTrace() {
		return getTrace(this);
	}

	/** @return a summary stack trace of the given throwable */
	private String getTrace(Throwable t) {
		TextStringBuilder sb = new TextStringBuilder(t.getMessage());
		StackTraceElement[] trace = t.getStackTrace();
		int len = Maths.constrain(trace.length, 0, 8);
		if (len > 0) {
			sb.appendln(TRACE_CAPTION);
			for (int idx = 0; idx < len; idx++) {
				sb.appendln(TAT + trace[idx]);
			}
		}

		Throwable c = t.getCause();
		if (c != null) {
			StackTraceElement[] causes = c.getStackTrace();
			int clen = Maths.constrain(causes.length, 0, 8);
			if (clen > 0) {
				sb.appendln(CAUSE_CAPTION);
				for (int idx = 0; idx < clen; idx++) {
					sb.appendln(TAT + causes[idx]);
				}
			}
		}
		return sb.toString();
	}

	/** @deprecated {@code cause} is blocked/not implemented */
	@Deprecated
	@Override
	public synchronized Throwable initCause(Throwable cause) {
		throw new NotImplementedException();
	}

	@Override
	public String toString() {
		return super.toString();
	}
}
