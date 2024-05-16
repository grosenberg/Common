package net.certiv.common.grid;

import java.util.Arrays;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.regex.Pattern;
import java.util.stream.Stream;

import net.certiv.common.check.Assert;
import net.certiv.common.ex.IllegalArgsEx;
import net.certiv.common.stores.Result;
import net.certiv.common.util.Maths;
import net.certiv.common.util.MsgBuilder;
import net.certiv.common.util.Strings;

/**
 * Two-dimensional data structure. Supports alignment functions.
 *
 * @param <T> cell content type; internally wrapped in an instance of {@link Cell} made
 *            from the supplied {@code nil} value.
 */
public class Grid<T> implements Iterable<T> {

	public static final Pattern SEPR = Pattern.compile("\\h*,\\h*");

	private static final IllegalArgsEx ERR_COL_RANGE = IllegalArgsEx.of("Column index [%d] out of range");
	private static final IllegalArgsEx ERR_COMPUTED = IllegalArgsEx.of("Illegal computed value: %s < %s");
	private static final IllegalArgsEx ERR_PADDING = IllegalArgsEx.of("Padding widths [%d:%d] invalid.");
	private static final IllegalArgsEx ERR_WIDTH = IllegalArgsEx.of("Grid width [%d] invalid.");

	private static final IllegalArgsEx ErrInsertCols = IllegalArgsEx
			.of("Column insert index exceeds Grid width [%d > %d].");
	private static final IllegalArgsEx ErrInsertColValues = IllegalArgsEx
			.of("Failed inserting %d row values in excess of available columns [%d].");

	private static final IllegalArgsEx ERR_ROW_FILL = IllegalArgsEx
			.of("Too many row values for column [%d > %d].");

	private static final IllegalArgsEx ERR_VALIDATE = IllegalArgsEx
			.of("Grid is not regular at column %d [%d x %d].");

	private final LinkedList<Column<T>> columns = new LinkedList<>();

	/** Grid index; nominally, a z-axis */
	private int index;
	/** Grid name */
	private String name = Strings.EMPTY;

	/** Grid overall width hint */
	private int widthHint = 120;

	/** Grid reference Cell instance. */
	private final Cell<T> ref;

	/**
	 * Constructor.
	 *
	 * @param ref reference Cell instance
	 */
	public Grid(Cell<T> ref) {
		this(0, Strings.EMPTY, ref);
	}

	/**
	 * Construct an grid with the given name.
	 *
	 * @param name descriptive name
	 * @param ref  reference Cell instance
	 */
	public Grid(String name, Cell<T> ref) {
		this(0, name, ref);
	}

	/**
	 * Construct a grid with the given index and name.
	 *
	 * @param index grid index; nominally, a z-axis
	 * @param name  descriptive name
	 * @param ref   reference Cell instance
	 */
	public Grid(int index, String name, Cell<T> ref) {
		this.index = index;
		this.name = name;
		this.ref = ref;
	}

	/**
	 * Define the grid columns using a CSV of column specifications, where each is of the
	 * form
	 *
	 * <pre>
	 * align:name:flow:width[indent:lpad:tpad]
	 *
	 * where
	 *   'align' {@link Align} value; default: {@link Align#LEFT},
	 *   'name' descriptive column name; default: ""
	 *   'flow' {@link Flow} value; default: {@link Flow#AUTO},
	 *   'width' column width; 0..n; default: 60
	 *   'indent' intra-column indent; 0..n (spaces); default: 0
	 *   'lpad' intra-column lead padding; 0..n (spaces); default: 0
	 *   'tpad' intra-column trail padding; 0..n (spaces); default: 0
	 *
	 * where
	 *   all elements are optional, except lpad and tpad must be
	 *   present together or not all
	 * </pre>
	 *
	 * @param specs CSV of column specifications
	 * @return this
	 */
	@SuppressWarnings("unchecked")
	public <G extends Grid<T>> G define(String specs) {
		String[] segs = SEPR.split(Strings.deQuote(specs.trim()).trim());
		for (int idx = 0; idx < segs.length; idx++) {
			String spec = segs[idx].trim();
			Column<T> col = new Column<>(idx, spec, ref);
			columns.add(col);
		}
		return (G) this;
	}

	/**
	 * Hint the desired overall grid width.
	 *
	 * @param hint desired overall width in characters
	 * @return this
	 */
	@SuppressWarnings("unchecked")
	public <G extends Grid<T>> G gridWidth(int hint) {
		setGridWidth(hint);
		return (G) this;
	}

	/**
	 * Define the column inner padding widths.
	 *
	 * @param prefix intra-column prefix padding width in spaces
	 * @param suffix intra-column suffix width in spaces
	 * @return this
	 */
	@SuppressWarnings("unchecked")
	public <G extends Grid<T>> G padding(int prefix, int suffix) {
		setPadding(prefix, suffix);
		return (G) this;
	}

	/**
	 * Return a reference Cell instance.
	 *
	 * @return cell reference
	 */
	@SuppressWarnings("unchecked")
	public <C extends Cell<T>> C ref() {
		return (C) ref.nil();
	}

	public int index() {
		return index;
	}

	public void setIndex(int num) {
		this.index = num;
	}

	public String name() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public void setPadding(int prefix, int suffix) {
		Assert.isTrue(ERR_PADDING.formatted(prefix, suffix), prefix >= 0 && suffix >= 0);
		columns.forEach(col -> col.setPadding(prefix, suffix));
	}

	/**
	 * Get the grid width hint.
	 *
	 * @return the grid width hint
	 */
	public int getGridWidth() {
		return widthHint;
	}

	/**
	 * Set the grid width hint.
	 *
	 * @param hint the grid width hint
	 */
	public void setGridWidth(int hint) {
		Assert.isTrue(ERR_WIDTH.formatted(hint), hint > 0);
		this.widthHint = hint;
	}

	// --------------------------------

	public void appendRow(@SuppressWarnings("unchecked") T... values) {
		appendRow(Arrays.asList(values));
	}

	public void appendRow(List<T> values) {
		int len = columns.size();
		int end = values.size();
		if (end > len) throw IllegalArgsEx.of("Failed adding row values in excess of available columns.");

		for (int idx = 0; idx < len; idx++) {
			Column<T> col = columns.get(idx);
			if (idx < end) {
				col.append(values.get(idx));
			} else {
				col.append(ref.value());
			}
		}
	}

	public boolean insertRow(int num, @SuppressWarnings("unchecked") T... values) {
		return insertRow(num, Arrays.asList(values));
	}

	public boolean insertRow(int num, List<T> values) {
		int cols = colSize();
		int last = values.size();
		if (last > cols) throw ErrInsertColValues.formatted(last, cols);

		for (int idx = 0; idx < cols; idx++) {
			Column<T> col = columns.get(idx);
			if (idx < last) {
				col.insert(num, values.get(idx));
			} else {
				col.insert(num, ref.value());
			}
		}
		return true;
	}

	// --------------------------------

	public Column<T> appendCol(String spec, @SuppressWarnings("unchecked") T... values) {
		Column<T> col = new Column<>(colSize(), spec, ref);
		fill(col, Arrays.asList(values));
		columns.add(col);
		return col;
	}

	public Column<T> appendCol(ColData data) {
		return appendCol(data, List.of());
	}

	public Column<T> appendCol(ColData data, List<T> values) {
		Column<T> col = new Column<>(colSize(), data, ref);
		fill(col, values);
		columns.add(col);
		return col;
	}

	private void fill(Column<T> col, List<T> values) {
		int len = rowSize();
		int end = values.size();
		if (end > len) throw IllegalArgsEx.of("Failure: append column values exceed available rows.");

		for (int idx = 0; idx < len; idx++) {
			if (idx < end) {
				col.append(values.get(idx));
			} else {
				col.append(ref.value());
			}
		}
	}

	public boolean insertCol(int num, String spec, @SuppressWarnings("unchecked") T... values) {
		return insertCol(num, spec, Arrays.asList(values));
	}

	public boolean insertCol(int num, String name, ColData data, @SuppressWarnings("unchecked") T... values) {
		return insertCol(num, name, data, Arrays.asList(values));
	}

	public boolean insertCol(int num, String spec, List<T> values) {
		int cols = colSize();
		Assert.isTrue(ErrInsertCols.formatted(num, cols), num >= cols);

		int rows = rowSize();
		int last = values.size();
		Assert.isTrue(ERR_ROW_FILL.formatted(last, rows), last > rows);

		Column<T> col = new Column<>(num, spec, ref);
		fill(col, values);
		columns.add(num, col);
		cols++;
		for (int idx = num; idx < cols; idx++) {
			columns.get(idx).setIndex(idx);
		}
		return true;
	}

	public boolean insertCol(int num, String name, ColData data, List<T> values) {
		int cols = colSize();
		Assert.isTrue(ErrInsertCols.formatted(num, cols), num >= cols);

		int rows = rowSize();
		int last = values.size();
		Assert.isTrue(ERR_ROW_FILL.formatted(last, rows), last > rows);

		Column<T> col = new Column<>(num, data, ref);
		col.setName(name);
		fill(col, values);
		columns.add(num, col);
		cols++;
		for (int idx = num; idx < cols; idx++) {
			columns.get(idx).setIndex(idx);
		}
		return true;
	}

	// --------------------------------

	/**
	 * Returns the value at the given cell row and column indexs.
	 *
	 * @param row grid row index
	 * @param col grid column index
	 * @return grid cell value
	 */
	public T get(int row, int col) {
		Cell<T> cell = cell(row, col);
		return cell.value();
	}

	/**
	 * Returns the row values at the given row index.
	 *
	 * @param row grid row index
	 * @return grid row values
	 */
	public List<T> getRow(int row) {
		List<T> values = new LinkedList<>();
		for (int col = 0; col < colSize(); col++) {
			Cell<T> cell = cell(row, col);
			values.add(cell.value());
		}
		return values;
	}

	/**
	 * Returns the column values at the given column index.
	 *
	 * @param col grid column index
	 * @return grid column values
	 */
	public List<T> getCol(int col) {
		List<T> values = new LinkedList<>();
		for (int row = 0; row < rowSize(); row++) {
			Cell<T> cell = cell(row, col);
			values.add(cell.value());
		}
		return values;
	}

	// --------------------------------

	/**
	 * Returns the column at the given index position in this sheet.
	 *
	 * @param idx column index
	 * @return the indexed column
	 * @throws IllegalArgumentException on index error (negative or >= columns size)
	 */
	protected Column<T> column(int idx) {
		Assert.isTrue(ERR_COL_RANGE.formatted(idx), idx >= 0 && idx < columns.size());
		return columns.get(idx);
	}

	/**
	 * Returns the cell at the given row and column numbers.
	 *
	 * @param row grid row index
	 * @param col grid column index
	 * @return grid cell
	 */
	@SuppressWarnings("unchecked")
	protected <C extends Cell<T>> C cell(int row, int col) {
		Column<T> c = column(col);
		return (C) c.getCell(row);
	}

	// --------------------------------

	/**
	 * Puts the given value into the cell at the given row and column numbers.
	 *
	 * @param row   grid row index
	 * @param col   grid column index
	 * @param value new cell value
	 * @return prior cell value
	 */
	public T put(int row, int col, T value) {
		Column<T> c = column(col);
		return c.put(row, value);
	}

	/** Removes the row at the given row number from this sheet. */
	public void remove(int row) {
		columns.forEach(c -> c.remove(row));
	}

	/** Returns the number of rows in this Grid. */
	public int rowSize() {
		if (columns.isEmpty()) return 0;
		return columns.get(0).rowSize();
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

	// --------------------------------

	/**
	 * Layout the grid structure. Functions to assign a layout size to each cell.
	 *
	 * @return this
	 */
	public Grid<T> layout() {
		LinkedList<ColData> datas = colDatas(true);

		int totalPrefs = datas.stream().mapToInt(d -> d.pref).sum();
		int delta = widthHint - totalPrefs;
		if (delta != 0) {
			delta = spread(delta, datas, Flow.AUTO, Flow.MIN, Flow.VALUE);
			if (delta != 0) {
				spread(delta, datas, Flow.AUTO);
			}
		}

		for (int idx = 0; idx < datas.size(); idx++) {
			ColData data = datas.get(idx);
			// Log.debug("Layout [%s] %s", idx, data);
			columns.get(idx).apply(data);
		}
		return this;
	}

	protected LinkedList<ColData> colDatas(boolean recalc) {
		LinkedList<ColData> datas = new LinkedList<>();
		columns.forEach(c -> datas.add(c.getColData(recalc)));
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
		if (data.computed < data.min) throw ERR_COMPUTED.formatted(data.computed, data.min);
		if (data.computed + adj >= data.min) return adj;
		return Maths.constrain(data.min - data.computed, adj, 0);
	}

	private int count(List<ColData> datas, List<Flow> flows) {
		return (int) datas.stream().filter(d -> flows.contains(d.flow)).count();
	}

	// --------------------------------

	// public Builder builder() {
	// return new Builder();
	// }
	//
	// public class Builder {
	//
	// public Column<T> col() {
	// Column<T> col = new Column<>(colSize(), ref);
	// columns.add(col);
	// return col;
	// }
	// }

	public Result<Boolean> validate() {
		int cols = colSize();
		if (cols > 0) {
			int rows = rowSize();
			for (int idx = 1; idx < colSize(); idx++) {
				Column<T> col = columns.get(idx);
				if (rows != col.rowSize()) return Result.of(ERR_VALIDATE.formatted(idx, rows, cols));
			}
		}
		return Result.OK;
	}

	/**
	 * Returns a descrption of the grid column structure.
	 *
	 * @return grid description
	 */
	public String describe() {
		MsgBuilder mb = new MsgBuilder();
		mb.nl().append("Grid %s [%s] ----", name, index);
		for (int col = 0; col < colSize(); col++) {
			Column<T> column = columns.get(col);
			mb.nl().indent("%s: %s %s", col, column.name(), column.getColData(false));
		}
		return mb.toString();
	}

	// --------------------------------

	/**
	 * Returns a sequential {@code Stream} with the column list as its source.
	 *
	 * @return a sequential {@code Stream} over the column list
	 */
	public Stream<Column<T>> stream() {
		return columns.stream();
	}

	/**
	 * Returns a row-first iterator over the contents of this grid.
	 *
	 * @return row-first grid iterator
	 */
	@Override
	public Iterator<T> iterator() {
		return new GridIterator(columns);
	}

	private class GridIterator implements Iterator<T>, Iterable<T> {

		private List<Column<T>> columns;

		private int rows;
		private int cols;
		private int cnt;
		private int idx;
		private int row;
		private int col;

		public GridIterator(List<Column<T>> columns) {
			this.columns = columns;
			rows = rowSize();
			cols = colSize();
			cnt = rows * cols;
		}

		@Override
		public boolean hasNext() {
			return idx < cnt;
		}

		@Override
		public T next() {
			if (idx >= cnt) throw new NoSuchElementException();

			T val = columns.get(col).get(row);
			row = row < rows ? row + 1 : 0;
			col = col < cols ? cols + 1 : 0;
			return val;
		}

		@Override
		public void remove() {
			throw new UnsupportedOperationException();
		}

		@Override
		public Iterator<T> iterator() {
			return this;
		}
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
		Grid<?> other = (Grid<?>) obj;
		return Objects.equals(columns, other.columns);
	}

	@Override
	public String toString() {
		MsgBuilder mb = new MsgBuilder();
		mb.append("[%s] %s (%s)", index, name, columns.size());
		mb.nl().indent("%s", columns);
		return mb.toString();
	}
}
