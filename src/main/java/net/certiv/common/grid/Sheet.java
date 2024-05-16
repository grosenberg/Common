package net.certiv.common.grid;

import java.util.LinkedList;

import org.apache.commons.text.TextStringBuilder;

import net.certiv.common.util.Strings;

/**
 * A {@link Grid} specialized for string values
 */
public class Sheet extends Grid<String> {

	/**
	 * Return a Sheet.
	 *
	 * @return an empty sheet
	 */
	public static Sheet of() {
		return new Sheet(0, Strings.EMPTY);
	}

	/**
	 * Return a Sheet with the given name.
	 *
	 * @param name descriptive name
	 * @return an empty sheet
	 */
	public static Sheet of(String name) {
		return new Sheet(0, name);
	}

	/**
	 * Return a Sheet with the given index and name.
	 *
	 * @param index sheet index; nominally, a z-axis
	 * @param name  descriptive name
	 * @return an empty sheet
	 */
	public static Sheet of(int index, String name) {
		return new Sheet(index, name);
	}

	// --------------------------------

	/**
	 * Construct a Sheet.
	 */
	protected Sheet() {
		this(0, Strings.EMPTY);
	}

	/**
	 * Construct a Sheet with the given name.
	 *
	 * @param name descriptive name
	 */
	protected Sheet(String name) {
		this(0, name);
	}

	/**
	 * Construct a Sheet with the given index and name.
	 *
	 * @param index sheet index; nominally, a z-axis
	 * @param name  descriptive name
	 */
	protected Sheet(int index, String name) {
		super(index, name, StrCell.Inst.nil());
		setPadding(1, 1);
	}

	/**
	 * Render this sheet to {@link System#out}. Performs a sheet layout prior to
	 * rendering.
	 */
	public void out() {
		System.out.println(render(true));
	}

	/**
	 * Render this sheet.
	 *
	 * @param layout perform grid layout
	 * @return rendered sheet
	 */
	public String render(boolean layout) {
		if (layout) layout();
		return render();
	}

	/**
	 * Render this sheet.
	 *
	 * @return rendered sheet
	 */
	public String render() {
		TextStringBuilder sb = new TextStringBuilder();

		int gridWidth = getGridWidth();
		int tableMin = stream().mapToInt(c -> c.minimumWidth()).sum();
		if (gridWidth < tableMin) {
			setGridWidth(tableMin);
		}

		sheetHeader(sb);
		columnHeaders(sb);
		divider(sb);
		fmtCells(sb);
		divider(sb);

		return sb.toString();
	}

	/**
	 * Header line that spans the entire sheet.
	 *
	 * <pre>{@code
	 * |-------      sheetname       -------|
	 * ^ lead ^ lgap ^        ^ tgap ^ tail ^
	 * }</pre>
	 *
	 * @param sb string builder
	 */
	private void sheetHeader(TextStringBuilder sb) {
		int total = stream().mapToInt(c -> c.computed() + 1).sum() - 1;
		int lgap = 1;
		int tgap = 1;
		int lead = 4;
		int tail = total - lead - name().length() - lgap - tgap;

		sb.append(Strings.PIPE + Strings.DASH.repeat(lead) + Strings.SPACE.repeat(lgap));
		sb.append(name() + Strings.SPACE.repeat(tgap) + Strings.DASH.repeat(tail));
		sb.appendln(Strings.PIPE);
	}

	// |<lpad> col name <rpad>|...|
	private void columnHeaders(TextStringBuilder sb) {
		stream().forEach(column -> {
			int lpad = column.lpad();
			int tpad = column.tpad();
			int limit = column.computed() - lpad - tpad;
			String title = column.name();
			title = Strings.ellipsize(title, limit);
			title = Strings.padr(title, limit);
			sb.append(Strings.PIPE + Strings.SPACE.repeat(lpad) + title + Strings.SPACE.repeat(tpad));
		});
		sb.appendln(Strings.PIPE);
	}

	// |------------|...|
	private void divider(TextStringBuilder sb) {
		stream().forEach(column -> {
			int limit = column.computed();
			// Log.debug("Div width %s", limit);
			sb.append(Strings.PIPE + Strings.DASH.repeat(limit));
		});
		sb.appendln(Strings.PIPE);
	}

	private void fmtCells(TextStringBuilder sb) {
		LinkedList<ColData> datas = colDatas(false);
		int rows = rowSize();
		int cols = colSize();

		for (int row = 0; row < rows; row++) {
			for (int col = 0; col < cols; col++) {
				Cell<String> cell = cell(row, col);
				ColData data = datas.get(col);
				fmtCell(sb, row, col, cell, data);
			}
			sb.appendln(Strings.PIPE);
		}
	}

	private void fmtCell(TextStringBuilder sb, int row, int col, Cell<String> cell, ColData data) {
		sb.append(Strings.PIPE + Strings.SPACE.repeat(data.lpad));

		String txt = cell.value();
		if (data.indent != 0) {
			if (Align.LEFT == data.align) {
				txt = Strings.SPACE.repeat(data.indent) + txt;
			} else if (Align.RIGHT == data.align) {
				txt += Strings.SPACE.repeat(data.indent);
			}
		}

		// Log.debug("Cell width %s", data.computed);

		int limit = data.computed - data.lpad - data.tpad;
		txt = Strings.ellipsize(txt, limit);
		if (Align.LEFT == data.align) {
			txt = Strings.padr(txt, limit);
		} else if (Align.RIGHT == data.align) {
			txt = Strings.padl(txt, limit);
		} else {
			txt = Strings.padc(txt, limit);
		}

		sb.append(txt + Strings.SPACE.repeat(data.tpad));
	}

	// --------------------------------

	static class StrCell extends Cell<String> {

		public static final StrCell Inst = new StrCell(Strings.EMPTY);

		/** Cell content internal indent level (in spaces). */
		private int indent;

		public StrCell(String value) {
			super(value);
		}

		/**
		 * Constructs a new Cell with the given indent and value.
		 *
		 * @param indent cell indent
		 * @param value  cell value
		 */
		public StrCell(int indent, String value) {
			super(value);
			this.indent = indent;
		}

		@Override
		public Cell<String> nil() {
			return make(Strings.EMPTY);
		}

		@Override
		public StrCell make(String val) {
			return new StrCell(val);
		}

		public Cell<String> make(int indent, String value) {
			return new StrCell(indent, value);
		}

		/**
		 * Return the cell indent.
		 *
		 * @return cell indent
		 */
		public int indent() {
			return indent;
		}

		/**
		 * Set the cell indent.
		 *
		 * @param indent cell indent
		 * @return {@code true}
		 */
		public boolean setIndent(int indent) {
			this.indent = indent;
			return true;
		}

		@Override
		public int width() {
			return indent + strWidth();
		}

		/** Returns the total width of the indent and the content. */
		public int strWidth() {
			return value().length();
		}

		@Override
		public int height() {
			return 1;
		}
	}
}
