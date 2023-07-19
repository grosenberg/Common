package net.certiv.common.sheet;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.regex.Pattern;

import org.apache.commons.text.TextStringBuilder;

import net.certiv.common.check.Assert;
import net.certiv.common.ex.IllegalArgsEx;
import net.certiv.common.log.Log;
import net.certiv.common.sheet.Column.Align;
import net.certiv.common.sheet.Column.ColData;
import net.certiv.common.sheet.Column.Flow;
import net.certiv.common.util.Maths;
import net.certiv.common.util.MsgBuilder;
import net.certiv.common.util.Strings;

public class Sheet {

	private static final Pattern SEPR = Pattern.compile("\\h*,\\h*");
	private static final IllegalArgsEx ERR_COL_RANGE = IllegalArgsEx.of("Column index [%d] out of range");
	private static final IllegalArgsEx ERR_WIDTH = IllegalArgsEx.of("Sheet width [%d] invalid.");
	private static final IllegalArgsEx ERR_COMPUTED = IllegalArgsEx.of("Illegal computed value: %s < %s");

	private final LinkedList<Column> columns = new LinkedList<>();

	/** Sheet index */
	private int idx = 0;
	/** Sheet name */
	private String name = Strings.EMPTY;
	/** Sheet width hint */
	private int width = 120;

	/** Construct an initial Sheet with the given name. */
	public static Sheet of(String name) {
		return new Sheet(0, name);
	}

	/** Construct a Sheet with the given index value and name. */
	public static Sheet of(int idx, String name) {
		return new Sheet(idx, name);
	}

	private Sheet(int idx, String name) {
		this.idx = idx;
		this.name = name;
	}

	/** Define the Sheet column properties. */
	public Sheet define(String titles) {
		columns.addAll(createCols(titles));
		return this;
	}

	/** Hint overall Sheet width. */
	public Sheet width(int width) {
		setWidth(width);
		return this;
	}

	private LinkedList<Column> createCols(final String titles) {
		LinkedList<Column> cols = new LinkedList<>();

		String spec = Strings.deQuote(titles.trim());
		String[] segs = SEPR.split(spec.trim());
		for (int idx = 0; idx < segs.length; idx++) {
			String title = segs[idx].trim();
			Column col = new Column(idx, title);
			cols.add(col);
		}
		return cols;
	}

	public void setIdx(int idx) {
		this.idx = idx;
	}

	public String name() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public void setWidth(int width) {
		Assert.isTrue(ERR_WIDTH.on(width), width > 0);
		this.width = width;
	}

	public boolean addRow(String... values) {
		return insertRow(rowSize(), Arrays.asList(values));
	}

	public boolean addRow(List<String> values) {
		return insertRow(rowSize(), values);
	}

	public boolean putRow(int num, String... values) {
		return insertRow(num, Arrays.asList(values));
	}

	public boolean putRow(int num, List<String> values) {
		return insertRow(num, values);
	}

	private boolean insertRow(int num, List<String> values) {
		int len = Math.min(values.size(), columns.size());
		for (int idx = 0; idx < len; idx++) {
			Column col = columns.get(idx);
			col.insert(num, values.get(idx));
		}
		for (int idx = len; idx < columns.size(); idx++) {
			Column col = columns.get(idx);
			col.insert(num, Strings.EMPTY);
		}
		return true;
	}

	/**
	 * Returns the column at the given index position in this sheet.
	 *
	 * @param idx column index
	 * @return the indexed column
	 * @throws IllegalArgumentException on index error (negative or >= columns size)
	 */
	public Column getColumn(int idx) {
		Assert.isTrue(ERR_COL_RANGE.on(idx), idx >= 0 && idx < columns.size());
		return columns.get(idx);
	}

	/** Gets the content from the cell at the given row and column numbers. */
	public String get(int row, int col) {
		Column c = getColumn(col);
		return c.get(row);
	}

	/** Gets the cell at the given row and column numbers. */
	private Cell getCell(int row, int col) {
		Column c = getColumn(col);
		return c.getCell(row);
	}

	/** Puts the given content into the cell at the given row and column numbers. */
	public boolean put(int row, int col, String content) {
		Column c = getColumn(col);
		return c.put(row, content);
	}

	/** Removes the row at the given row number from this sheet. */
	public void remove(int row) {
		columns.forEach(c -> c.remove(row));
	}

	/** Returns the number of rows in this Sheet. */
	public int rowSize() {
		if (columns.isEmpty()) return 0;
		return columns.get(0).size();
	}

	public int colSize() {
		return columns.size();
	}

	public boolean isEmpty() {
		return columns.isEmpty();
	}

	public void clear() {
		columns.forEach(c -> c.clear());
		columns.clear();
	}

	/** Build: layout cells. */
	public Sheet build() {
		layoutColWidths();
		return this;
	}

	private void layoutColWidths() {
		LinkedList<ColData> datas = colDatas(true);

		int totalPrefs = datas.stream().mapToInt(d -> d.pref).sum();
		int delta = width - totalPrefs;
		if (delta != 0) {
			delta = spread(delta, datas, Flow.AUTO, Flow.MIN);
			if (delta != 0) {
				spread(delta, datas, Flow.AUTO);
			}
		}

		for (int idx = 0; idx < datas.size(); idx++) {
			ColData data = datas.get(idx);
			// Log.debug("Built [%s] %s", idx, data);
			columns.get(idx).apply(data);
		}
	}

	private LinkedList<ColData> colDatas(boolean recalc) {
		LinkedList<ColData> datas = new LinkedList<>();
		columns.forEach(c -> datas.add(c.getData(recalc)));
		return datas;
	}

	/**
	 * Spread delta equally to the given flow types, subject to min limits. Returns any
	 * un-spread portion of the delta.
	 */
	private int spread(int delta, LinkedList<ColData> datas, Flow... flows) {
		List<Flow> affected = Arrays.asList(flows);
		int adj = delta / count(datas, affected);
		int rem = 0;
		for (ColData data : datas) {
			if (affected.contains(data.flow)) {
				int alt = limit(adj, data);
				data.computed += alt;
				rem += adj - alt;
			}
		}
		return rem;
	}

	// computed:10; min:2; adj:+02 => ret:+2
	// computed:10; min:2; adj:-12 => ret:-8
	// computed:10; min:2; adj:-02 => ret:-2
	// computed:04; min:2; adj:-02 => ret:-2
	// computed:03; min:2; adj:-02 => ret:-1
	// computed:02; min:2; adj:-02 => ret:-0
	// computed:02; min:2; adj:-03 => ret:-0
	private int limit(int adj, ColData data) {
		if (data.computed < data.min) throw ERR_COMPUTED.on(data.computed, data.min);
		if (data.computed + adj >= data.min) return adj;
		return Maths.constrain(data.min - data.computed, adj, 0);
	}

	private int count(List<ColData> datas, List<Flow> flows) {
		return (int) datas.stream().filter(d -> flows.contains(d.flow)).count();
	}

	/** Print to {@code System.out} */
	public void out() {
		System.out.println(print());
	}

	public String print() {
		TextStringBuilder sb = new TextStringBuilder();

		int tableMin = columns.stream().mapToInt(c -> c.minimumWidth()).sum();
		if (width < tableMin) {
			Log.warn("Assigned width is less than sum of column minimums: %s < %s", width, tableMin);
			setWidth(tableMin);
		}

		sheetHeader(sb);
		columnHeaders(sb);
		divider(sb);
		fmtCells(sb);
		divider(sb);

		return sb.toString();
	}

	// |----<lpad> sheetname <rpad>-------|
	private void sheetHeader(TextStringBuilder sb) {
		int lpad = columns.get(0).lpad();
		int rpad = columns.get(0).rpad();

		int total = columns.stream().mapToInt(c -> c.computed() + 1).sum() - 1;
		int lead = 4;
		int tail = total - lead - name.length() - lpad - rpad;

		sb.append(Strings.PIPE + Strings.DASH.repeat(lead) + Strings.SPACE.repeat(lpad));
		sb.append(name + Strings.SPACE.repeat(rpad) + Strings.DASH.repeat(tail));
		sb.appendln(Strings.PIPE);
	}

	// |<lpad> col name <rpad>|...|
	private void columnHeaders(TextStringBuilder sb) {
		for (Column column : columns) {
			int lpad = column.lpad();
			int rpad = column.rpad();
			int limit = column.computed() - lpad - rpad;
			String title = column.name();
			title = Strings.ellipsize(title, limit);
			title = Strings.padr(title, limit);
			sb.append(Strings.PIPE + Strings.SPACE.repeat(lpad) + title + Strings.SPACE.repeat(rpad));
		}
		sb.appendln(Strings.PIPE);
	}

	// |------------|...|
	private void divider(TextStringBuilder sb) {
		for (Column column : columns) {
			int limit = column.computed();
			// Log.debug("Div width %s", limit);
			sb.append(Strings.PIPE + Strings.DASH.repeat(limit));
		}
		sb.appendln(Strings.PIPE);
	}

	private void fmtCells(TextStringBuilder sb) {
		LinkedList<ColData> datas = colDatas(false);
		int rows = rowSize();
		int cols = colSize();

		for (int row = 0; row < rows; row++) {
			for (int col = 0; col < cols; col++) {
				Cell cell = getCell(row, col);
				ColData data = datas.get(col);
				fmtCell(sb, row, col, cell, data);
			}
			sb.appendln(Strings.PIPE);
		}
	}

	private void fmtCell(TextStringBuilder sb, int row, int col, Cell cell, ColData data) {
		sb.append(Strings.PIPE + Strings.SPACE.repeat(data.lpad));

		String txt = cell.content();
		if (data.indent != 0) {
			if (Align.LEFT == data.align) {
				txt = Strings.SPACE.repeat(data.indent) + txt;
			} else if (Align.RIGHT == data.align) {
				txt += Strings.SPACE.repeat(data.indent);
			}
		}

		// Log.debug("Cell width %s", data.computed);

		int limit = data.computed - data.lpad - data.rpad;
		txt = Strings.ellipsize(txt, limit);
		if (Align.LEFT == data.align) {
			txt = Strings.padr(txt, limit);
		} else if (Align.RIGHT == data.align) {
			txt = Strings.padl(txt, limit);
		} else {
			txt = Strings.padc(txt, limit);
		}

		sb.append(txt + Strings.SPACE.repeat(data.rpad));
	}

	public Builder builder() {
		return new Builder();
	}

	public class Builder {

		public Column col() {
			Column col = new Column(colSize());
			columns.add(col);
			return col;
		}
	}

	public String describe() {
		MsgBuilder mb = new MsgBuilder();
		mb.nl().append("Sheet %s [%s] ----", name, idx);
		for (int col = 0; col < colSize(); col++) {
			Column column = columns.get(col);
			mb.nl().indent("%s: %s %s", col, column.name(), column.getData(false));
		}
		return mb.toString();
	}

	@Override
	public int hashCode() {
		return Objects.hash(columns);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) return true;
		if (obj == null) return false;
		if (getClass() != obj.getClass()) return false;
		Sheet other = (Sheet) obj;
		return Objects.equals(columns, other.columns);
	}

	@Override
	public String toString() {
		MsgBuilder mb = new MsgBuilder();
		mb.append("[%s] %s (%s)", idx, name, columns.size());
		mb.nl().indent("%s", columns);
		return mb.toString();
	}
}
