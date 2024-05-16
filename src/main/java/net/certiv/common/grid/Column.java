package net.certiv.common.grid;

import java.util.LinkedList;
import java.util.Objects;
import java.util.stream.Stream;

import net.certiv.common.check.Assert;
import net.certiv.common.ex.IllegalArgsEx;
import net.certiv.common.util.MsgBuilder;
import net.certiv.common.util.Strings;

public class Column<T> {

	private static final IllegalArgsEx ERR_ROW_RANGE = IllegalArgsEx.of("%s at row [%d:%d] out of range");

	protected final LinkedList<Cell<T>> cells = new LinkedList<>();

	/** Column index */
	private int index;
	/** Column name */
	private String name = Strings.EMPTY;

	/** Column description including layout data */
	private ColData data;

	/** Reference Cell instance. */
	private final Cell<T> ref;

	// --------------------------------

	/**
	 * Constructor, nominally used by the {@link Builder}.
	 *
	 * @param idx column index
	 * @param ref reference Cell instance
	 */
	public Column(int idx, Cell<T> ref) {
		this(idx, Strings.EMPTY, ref);
	}

	/**
	 * Constructor for defining the column using using a specification of the general form
	 *
	 * <pre>
	 * 	align:name:flow:width[indent:lpad:tpad]
	 * </pre>
	 *
	 * The default is
	 *
	 * <pre>
	 * 	L:A:60 // Align.LEFT, name="", Flow.Auto, width=60, indent=0, lpad=0, tpad=0
	 * </pre>
	 *
	 * @param idx  column index
	 * @param spec column spec
	 * @param ref  reference Cell instance
	 */
	public Column(int idx, String spec, Cell<T> ref) {
		this.index = idx;
		this.data = parse(spec);
		this.ref = ref;
	}

	/**
	 * Constructor using column descriptor data.
	 *
	 * @param idx  column index
	 * @param data column descriptor data
	 * @param ref  reference Cell instance
	 */
	public Column(int idx, ColData data, Cell<T> ref) {
		this.index = idx;
		this.data = data;
		this.ref = ref;
	}

	// --------------------------------

	// align:name:flow:width[indent:lpad:tpad]
	// defaults to 'Align.LEFT:"":Flow.AUTO:60[0:0:0]'

	private ColData parse(String spec) {
		ColData data = new ColData(Align.LEFT, Flow.AUTO, 60, 0, 0, 0);
		if (spec == null || spec.isBlank()) return data;

		String[] m = spec.split("\\[", -1);
		if (m.length > 2) throw IllegalArgsEx.of("Invalid spec %s", spec);

		if (m.length == 2) {
			String pads = m[1].split("\\]", -1)[0].trim();
			String[] parts = pads.split("\\:", -1);
			switch (parts.length) {
				case 1:
					data.indent = numOf(parts[0], "Bad indent value '%s' in '%s'", spec);
					break;
				case 2:
					data.lpad = numOf(parts[0], "Bad lead pad value '%s' in '%s'", spec);
					data.tpad = numOf(parts[1], "Bad trail pad value '%s' in '%s'", spec);
					break;
				case 3:
					data.indent = numOf(parts[0], "Bad indent value '%s' in '%s'", spec);
					data.lpad = numOf(parts[1], "Bad lead pad value '%s' in '%s'", spec);
					data.tpad = numOf(parts[2], "Bad trail pad value '%s' in '%s'", spec);
					break;
				default:
					throw IllegalArgsEx.of("Invalid indent/padding value '%s' in '%s'", pads, spec);
			}
		}

		String primary = m[0].trim();
		String[] parts = primary.split("\\:", -1);

		for (int cur = 0; cur < parts.length; cur++) {
			String part = parts[cur].trim();

			if (Align.is(part)) {
				data.align = Align.of(part);

			} else if (Flow.is(part)) {
				data.flow = Flow.of(part);

			} else if (isNum(part)) {
				data.width = Integer.valueOf(part);

			} else {
				name = part;
			}
		}

		return data;
	}

	private boolean isNum(String txt) {
		try {
			return Integer.valueOf(txt) > -1;
		} catch (Exception e) {
			return false;
		}
	}

	private int numOf(String txt, String errFmt, String spec) {
		try {
			int num = Integer.valueOf(txt.trim());
			if (num < 0) throw new Exception();
			return num;
		} catch (Exception e) {
			throw IllegalArgsEx.of(errFmt, spec, txt);
		}
	}

	// --------------------------------

	/**
	 * Returns the current column index.
	 *
	 * @return the column index
	 */
	public int index() {
		return index;
	}

	/**
	 * Sets the current column index.
	 *
	 * @return the column index
	 */
	void setIndex(int num) {
		this.index = num;
	}

	public String name() {
		return name;
	}

	public void setName(String name) {
		this.name = name != null ? name : Strings.EMPTY;
	}

	/**
	 * Returns the column data for this column.
	 *
	 * @param recalc perform layout recalculation
	 * @return {@link ColData} copy
	 */
	public ColData getColData(boolean recalc) {
		if (recalc) calc();
		return new ColData(data);
	}

	// --------------------------------
	// ---- layout computation --------

	private void calc() {
		switch (data.flow) {
			default:
			case AUTO:
				data.min = data.pref = data.lpad + data.tpad;
				for (Cell<T> cell : cells) {
					data.pref = data.computed = Math.max(data.pref, cell.width() + data.lpad + data.tpad);
				}
				break;

			case FIXED:
				data.min = data.pref = data.computed = data.width + data.lpad + data.tpad;
				break;

			case MIN:
				data.min = data.pref = data.width + data.lpad + data.tpad;
				for (Cell<T> cell : cells) {
					data.pref = data.computed = Math.max(data.pref, cell.width() + data.lpad + data.tpad);
				}
				break;

			case VALUE:
				data.pref = data.width + data.lpad + data.tpad;
				for (Cell<T> cell : cells) {
					data.min = data.pref = data.computed = Math.max(data.pref,
							cell.width() + data.lpad + data.tpad);
				}
				break;
		}
	}

	protected void apply(ColData data) {
		this.data.min = data.min;
		this.data.pref = data.pref;
		this.data.computed = data.computed;
	}

	// --------------------------------
	// ---- layout computed values ----

	protected int minimumWidth() {
		return data.min;
	}

	protected int preferredWidth() {
		return data.pref;
	}

	protected int computed() {
		return data.computed;
	}

	// --------------------------------

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

	public void setWidth(int width) {
		data.width = width;
	}

	public int indent() {
		return data.indent;
	}

	public void setIndent(int indent) {
		data.indent = indent;
	}

	public int lpad() {
		return data.lpad;
	}

	public int tpad() {
		return data.tpad;
	}

	public void setPadding(int lpad, int tpad) {
		data.lpad = lpad;
		data.tpad = tpad;
	}

	// --------------------------------

	public int rowSize() {
		return cells.size();
	}

	/**
	 * Appends a new cell with the given value after the last row in this column.
	 *
	 * @param value value to be appended
	 */
	void append(T value) {
		cells.add(ref.make(value)); // TODO: include column index
	}

	/**
	 * Inserts a new cell with the given value at the given row number in this column.
	 * Shifts the row currently at that position (if any) and any subsequent rows down
	 * (adds one to their indices).
	 *
	 * @param row   row insertion point index
	 * @param value value to be inserted
	 * @throws IndexOutOfBoundsException {@inheritDoc}
	 */
	void insert(int row, T value) {
		int rows = rowSize();
		Assert.isTrue(ERR_ROW_RANGE.formatted(value, row, rows), row >= 0 && row <= rows);
		cells.add(row, ref.make(value)); // TODO: include column index
	}

	/**
	 * Returns the cell container at the given row number.
	 *
	 * @param row row index
	 * @return cell container
	 */
	Cell<T> getCell(int row) {
		Assert.isTrue(ERR_ROW_RANGE.formatted("Get cell", row, rowSize()),
				row >= 0 && row < rowSize() && !cells.isEmpty());
		return cells.get(row);
	}

	/**
	 * Returns the contained value from the cell at the given row number.
	 *
	 * @param row row index
	 * @return cell value
	 */
	T get(int row) {
		return getCell(row).value();
	}

	/**
	 * Puts the given value into the cell at the given row number.
	 *
	 * @param row   grid row number
	 * @param value new cell value
	 * @return prior cell value
	 */
	T put(int row, T value) {
		Assert.isTrue(ERR_ROW_RANGE.formatted("Put", row, rowSize()),
				row >= 0 && row < rowSize() && !cells.isEmpty());
		Cell<T> cell = cells.get(row);
		return cell.setValue(value);
	}

	/**
	 * Functionally removes the cell at the given row number by setting to the
	 * {@link Cell#nil()} value.
	 *
	 * @param row grid row number
	 * @return prior cell value; may be nil
	 */
	T remove(int row) {
		return put(row, ref.nil().value());
	}

	void clear() {
		cells.clear(); // TODO: reset other column values?
	}

	/**
	 * Returns a sequential {@code Stream} with the cell list as its source.
	 *
	 * @return a sequential {@code Stream} over the cell list
	 */
	public Stream<Cell<T>> stream() {
		return cells.stream();
	}

	@Override
	public int hashCode() {
		return Objects.hash(cells, index);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) return true;
		if (obj == null) return false;
		if (getClass() != obj.getClass()) return false;
		Column<?> other = (Column<?>) obj;
		return Objects.equals(cells, other.cells) && index == other.index;
	}

	@Override
	public String toString() {
		MsgBuilder mb = new MsgBuilder();
		mb.append("[%s] %s %s ", index, name, data);
		mb.append(" (rows: %d)", cells.size());

		for (int row = 0; row < cells.size(); row++) {
			mb.nl().indent("[%02d] %s", row, cells.get(row).value());
		}

		return mb.toString();
	}
}
