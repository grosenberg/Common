package net.certiv.common.sheet;

import java.util.LinkedList;
import java.util.Objects;

import net.certiv.common.check.Assert;
import net.certiv.common.ex.IllegalArgsEx;
import net.certiv.common.util.MsgBuilder;
import net.certiv.common.util.Strings;

public class Column {

	private static final IllegalArgsEx ERR_ROW_RANGE = IllegalArgsEx.of("Row index [%d] out of range");

	protected final LinkedList<Cell> cells = new LinkedList<>();

	/** Column index */
	private int idx = 0;
	/** Column name */
	private String name = Strings.EMPTY;
	/** Column layout data */
	private final ColData data;

	/**
	 * Builder Constructor
	 *
	 * @param idx column index
	 */
	public Column(int idx) {
		this(idx, Strings.EMPTY);
	}

	/**
	 * Constructor using title spec: (alignment:)?name(:flow)?(:width(:indent)?)?
	 *
	 * @param idx   column index
	 * @param title title spec
	 */
	public Column(int idx, String title) {
		this.idx = idx;
		this.data = new ColData(Align.LEFT, Flow.AUTO, 60, 0);
		parse(title);
	}

	public Column(int idx, Align align, String name, Flow flow, int width, int indent) {
		this.idx = idx;
		this.name = name;
		this.data = new ColData(align, flow, width, indent);
	}

	private void parse(String title) {
		if (title == null || title.isBlank()) return;

		String[] segs = title.split(Strings.COLON, -1);
		int cnt = segs.length;
		int off = 0;
		if (off >= cnt) return;

		String txt = segs[off].trim();
		if (Align.has(txt)) {
			data.align = Align.of(txt);
			off++;
			if (off >= cnt) return;
		}

		name = segs[off].trim();
		off++;
		if (off >= cnt) return;

		txt = segs[off].trim();
		if (Flow.has(txt)) {
			data.flow = Flow.of(txt);
			off++;
			if (off >= cnt) return;
		}

		txt = segs[off].trim();
		try {
			data.width = Integer.valueOf(txt);
			off++;
			if (off >= cnt) return;
		} catch (Exception e) {
			throw IllegalArgsEx.of("Bad column title spec (width spec error): [%s] %s", idx, title);
		}

		txt = segs[off].trim();
		try {
			data.indent = Integer.valueOf(txt);
		} catch (Exception e) {
			throw IllegalArgsEx.of("Bad column title spec (indent spec error): [%s] %s", idx, title);
		}
	}

	// --------------------------------
	// Builder ops

	public Column left() {
		setAlign(Align.LEFT);
		return this;
	}

	public Column center() {
		setAlign(Align.CENTER);
		return this;
	}

	public Column right() {
		setAlign(Align.RIGHT);
		return this;
	}

	public Column name(String name) {
		this.name = name;
		return this;
	}

	public Column auto() {
		setFlow(Flow.AUTO);
		return this;
	}

	public Column fixed() {
		setFlow(Flow.FIXED);
		return this;
	}

	public Column min() {
		setFlow(Flow.MIN);
		return this;
	}

	public Column width(int width) {
		setWidth(width);
		return this;
	}

	public Column indent(int indent) {
		setIndent(indent);
		return this;
	}

	// --------------------------------

	/** @return the column index */
	public int idx() {
		return idx;
	}

	public String name() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public ColData getData(boolean recalc) {
		if (recalc) calc();
		return new ColData(data);
	}

	private void calc() {
		if (Flow.FIXED == data.flow) {
			data.min = data.pref = data.computed = data.width + data.lpad + data.rpad;
			return;
		}

		data.min = data.pref = (Flow.MIN == data.flow ? data.width : 0) + data.lpad + data.rpad;
		for (Cell cell : cells) {
			data.pref = data.computed = Math.max(data.pref, cell.width() + data.lpad + data.rpad);
		}
	}

	public void apply(ColData data) {
		this.data.min = data.min;
		this.data.pref = data.pref;
		this.data.computed = data.computed;
	}

	public Align align() {
		return data.align;
	}

	public void setAlign(Align align) {
		data.align = align;
	}

	public Flow flow() {
		return data.flow;
	}

	public void setFlow(Flow flow) {
		data.flow = flow;
	}

	public int width() {
		return data.width;
	}

	public int minimumWidth() {
		return data.min;
	}

	public int preferredWidth() {
		return data.pref;
	}

	public int computed() {
		return data.computed;
	}

	public void setWidth(int width) {
		data.width = width;
	}

	public int indent() {
		return data.indent;
	}

	public void setIndent(int indent) {
		data.indent = indent;
		cells.forEach(c -> c.setIndent(indent));
	}

	public int lpad() {
		return data.lpad;
	}

	public int rpad() {
		return data.rpad;
	}

	/** Appends a new cell with the given content at the end of this column. */
	public void append(String content) {
		cells.add(new Cell(data.indent, content));
	}

	/**
	 * Inserts a new cell with the given content at the given row number in this column.
	 */
	public void insert(int num, String content) {
		Assert.isTrue(ERR_ROW_RANGE.on(num), num >= 0 && num <= size());
		cells.add(num, new Cell(data.indent, content));
	}

	/** Gets the content from the cell at the given row number. */
	public String get(int row) {
		return getCell(row).content();
	}

	/** Gets the cell at the given row number. */
	public Cell getCell(int row) {
		Assert.isTrue(ERR_ROW_RANGE.on(row), row >= 0 && row < size() && !cells.isEmpty());
		return cells.get(row);
	}

	/** Puts the given content into the cell at the given row number. */
	public boolean put(int row, String content) {
		Assert.isTrue(ERR_ROW_RANGE.on(idx), idx >= 0 && idx < size() && !cells.isEmpty());
		Cell cell = cells.get(row);
		return cell.setContent(content);
	}

	/** Removes the cell at the given row number. */
	public void remove(int row) {
		Assert.isTrue(ERR_ROW_RANGE.on(idx), idx >= 0 && idx < size() && !cells.isEmpty());
		cells.remove(row);
	}

	public int size() {
		return cells.size();
	}

	public void clear() {
		cells.clear();
	}

	@Override
	public int hashCode() {
		return Objects.hash(cells, idx);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) return true;
		if (obj == null) return false;
		if (getClass() != obj.getClass()) return false;
		Column other = (Column) obj;
		return Objects.equals(cells, other.cells) && idx == other.idx;
	}

	@Override
	public String toString() {
		MsgBuilder mb = new MsgBuilder();
		mb.append("[%s] ", idx);
		mb.append(data.align != Align.LEFT, "%s:", data.align);
		mb.append("%s", name);
		mb.append(data.flow != Flow.AUTO, ":%s", data.flow);
		mb.append(":%s", data.width);
		mb.append(data.indent > 0, ":%s", data.indent);
		mb.append(" (rows: %s)", cells.size());
		mb.append(!cells.isEmpty(), "\r\t%s", cells);
		return mb.toString();
	}

	// --------------------------------

	/** Column alignment policy */
	public enum Align {
		LEFT("L"),
		CENTER("C"),
		RIGHT("R");

		public final String mark;

		Align(String mark) {
			this.mark = mark;
		}

		public String mark() {
			return mark;
		}

		public static boolean has(String txt) {
			for (Align align : values()) {
				if (align.mark.equals(txt) || align.name().equals(txt)) return true;
			}
			return false;
		}

		public static Align of(String txt) {
			for (Align align : values()) {
				if (align.mark.equals(txt) || align.name().equals(txt)) return align;
			}
			return Align.LEFT;
		}
	}

	/** Column layout policy */
	public enum Flow {
		/** Required 'width' */
		FIXED("F"),
		/** Hint 'width' */
		AUTO("A"),
		/** Minimum 'width' */
		MIN("M");

		public final String mark;

		Flow(String mark) {
			this.mark = mark;
		}

		public static boolean has(String txt) {
			for (Flow flow : values()) {
				if (flow.mark.equals(txt) || flow.name().equals(txt)) return true;
			}
			return false;
		}

		public static Flow of(String txt) {
			for (Flow flow : values()) {
				if (flow.mark.equals(txt) || flow.name().equals(txt)) return flow;
			}
			return Flow.MIN;
		}
	}

	public class ColData {
		/** Column alignment; default LEFT */
		public Align align = Align.LEFT;
		/** Column layout flow behavior; default AUTO. */
		public Flow flow = Flow.AUTO;
		/** Column width in characters; default 60. */
		public int width = 60;
		/** Indent: internal, prefix-pad, alignment-dependent; default 0. */
		public int indent = 0;

		/** Left inner pad */
		public final int lpad = 1;
		/** Right inner pad */
		public final int rpad = 1;

		/** Minimum constraint width */
		public int min;
		/** Maximum actual width */
		public int pref;
		/** computed layout width */
		public int computed;

		public ColData() {}

		public ColData(Align align, Flow flow, int width, int indent) {
			this.align = align;
			this.flow = flow;
			this.width = width;
			this.indent = indent;
		}

		public ColData(ColData data) {
			this.align = data.align;
			this.flow = data.flow;
			this.width = data.width;
			this.indent = data.indent;

			this.min = data.min;
			this.pref = data.pref;
			this.computed = data.computed;
		}

		@Override
		public int hashCode() {
			return Objects.hash(align, computed, flow, indent, lpad, min, pref, rpad, width);
		}

		@Override
		public boolean equals(Object obj) {
			if (this == obj) return true;
			if (obj == null) return false;
			if (getClass() != obj.getClass()) return false;
			ColData other = (ColData) obj;
			return align == other.align && computed == other.computed && flow == other.flow
					&& indent == other.indent && lpad == other.lpad && min == other.min && pref == other.pref
					&& rpad == other.rpad && width == other.width;
		}

		@Override
		public String toString() {
			MsgBuilder mb = new MsgBuilder();
			mb.append(align != Align.LEFT, "%s:", align);
			mb.append("%s", name);
			mb.append(flow != Flow.AUTO, ":%s", flow);
			mb.append(":%s", width);
			mb.append(indent > 0, ":%s", indent);
			mb.append(" <%s:%s:%s>", min, pref, computed);
			return mb.toString();
		}
	}
}
