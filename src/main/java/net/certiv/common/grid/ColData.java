package net.certiv.common.grid;

import java.util.Objects;

import net.certiv.common.util.MsgBuilder;

public class ColData {

	/** Column alignment; default LEFT */
	public Align align = Align.LEFT;
	/** Column layout flow behavior; default AUTO. */
	public Flow flow = Flow.AUTO;

	/** Preferred column width in characters; default 60. */
	public int width = 60;
	/** Indent: internal, prefix-pad, alignment-dependent; default 0. */
	public int indent = 0;

	/** Lead inner pad */
	public int lpad;
	/** Trail inner pad */
	public int tpad;

	/** Minimum constraint width */
	public int min;
	/** Maximum actual width */
	public int pref;
	/** Computed layout width */
	public int computed;

	public ColData() {
		this(Align.LEFT, Flow.AUTO, 60, 0, 0, 0);
	}

	public ColData(Align align, Flow flow, int width, int indent, int lpad, int tpad) {
		this.align = align;
		this.flow = flow;
		this.width = width;
		this.indent = indent;
		this.lpad = lpad;
		this.tpad = tpad;
	}

	public ColData(ColData o) {
		this.align = o.align;
		this.flow = o.flow;
		this.width = o.width;
		this.indent = o.indent;
		this.lpad = o.lpad;
		this.tpad = o.tpad;

		this.min = o.min;
		this.pref = o.pref;
		this.computed = o.computed;
	}

	@Override
	public int hashCode() {
		return Objects.hash(align, computed, flow, indent, lpad, min, pref, tpad, width);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) return true;
		if (obj == null) return false;
		if (getClass() != obj.getClass()) return false;
		ColData o = (ColData) obj;
		return align == o.align && computed == o.computed && flow == o.flow && indent == o.indent
				&& lpad == o.lpad && min == o.min && pref == o.pref && tpad == o.tpad && width == o.width;
	}

	@Override
	public String toString() {
		MsgBuilder mb = new MsgBuilder();
		mb.append("%s", align.mark());
		mb.append(":%s", flow.mark());
		mb.append(":%s", width);
		mb.append(" [%d:%d:%d]", indent, lpad, tpad);
		mb.append(" <%s:%s:%s>", min, pref, computed);
		return mb.toString();
	}
}
