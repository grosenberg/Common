package net.certiv.common.grid;

import java.util.Arrays;
import java.util.List;

import net.certiv.common.stores.Result;

public class Builder<T> {

	private Grid<T> grid;
	private Column<T> col;

	protected Builder(Grid<T> grid) {
		this.grid = grid;
	}

	// --------------------------------
	// ---- Grid functions ------------

	public String name() {
		return grid.name();
	}

	/**
	 * Returns a descrption of the grid column structure.
	 *
	 * @return grid description
	 */
	public String describe() {
		return grid.describe();
	}

	/**
	 * Validate the grid structure.
	 *
	 * @return {@link Result#OK} on valid, or an {@link Result} containing an exception
	 *         message if the grid is not regular
	 */
	public Result<Boolean> validate() {
		return grid.validate();
	}

	/**
	 * Return the grid.
	 *
	 * @param layout perform grid layout
	 * @return grid
	 */
	@SuppressWarnings("unchecked")
	public <G extends Grid<T>> G get(boolean layout) {
		return layout ? (G) grid.layout() : (G) grid;
	}

	// --------------------------------
	// ---- Grid operators ------------

	/**
	 * Hint the desired overall grid width.
	 *
	 * @param hint desired overall width in characters
	 * @return this
	 */
	@SuppressWarnings("unchecked")
	public <B extends Builder<T>> B gridWidth(int hint) {
		grid.gridWidth(hint);
		return (B) this;
	}

	// --------------------------------
	// ---- Bulk column construction

	@SuppressWarnings("unchecked")
	public <B extends Builder<T>> B define(String spec) {
		grid.define(spec);
		return (B) this;
	}

	// --------------------------------
	// ---- Single column construction

	@SuppressWarnings("unchecked")
	public <B extends Builder<T>> B col() {
		col = grid.appendCol(new ColData());
		return (B) this;
	}

	@SuppressWarnings("unchecked")
	public <B extends Builder<T>> B left() {
		col.setAlign(Align.LEFT);
		return (B) this;
	}

	@SuppressWarnings("unchecked")
	public <B extends Builder<T>> B center() {
		col.setAlign(Align.CENTER);
		return (B) this;
	}

	@SuppressWarnings("unchecked")
	public <B extends Builder<T>> B right() {
		col.setAlign(Align.RIGHT);
		return (B) this;
	}

	@SuppressWarnings("unchecked")
	public <B extends Builder<T>> B name(String name) {
		col.setName(name);
		return (B) this;
	}

	@SuppressWarnings("unchecked")
	public <B extends Builder<T>> B auto() {
		col.setFlow(Flow.AUTO);
		return (B) this;
	}

	@SuppressWarnings("unchecked")
	public <B extends Builder<T>> B fixed() {
		col.setFlow(Flow.FIXED);
		return (B) this;
	}

	@SuppressWarnings("unchecked")
	public <B extends Builder<T>> B min() {
		col.setFlow(Flow.MIN);
		return (B) this;
	}

	@SuppressWarnings("unchecked")
	public <B extends Builder<T>> B value() {
		col.setFlow(Flow.VALUE);
		return (B) this;
	}

	@SuppressWarnings("unchecked")
	public <B extends Builder<T>> B width(int width) {
		col.setWidth(width);
		return (B) this;
	}

	@SuppressWarnings("unchecked")
	public <B extends Builder<T>> B indent(int indent) {
		col.setIndent(indent);
		return (B) this;
	}

	@SuppressWarnings("unchecked")
	public <B extends Builder<T>> B padding(int lpad, int tpad) {
		col.setPadding(lpad, tpad);
		return (B) this;
	}

	// --------------------------------
	// ---- Row construction

	/**
	 * Appends the given values as the last row in this grid.
	 *
	 * @param values row values
	 */
	public void appendRow(@SuppressWarnings("unchecked") T... values) {
		appendRow(Arrays.asList(values));
	}

	/**
	 * Appends the given values as the last row in this grid.
	 *
	 * @param values row values
	 */
	public void appendRow(List<T> values) {
		grid.appendRow(values);
	}

	/**
	 * Inserts the given values in this grid at the given row index. Shifts the row
	 * currently at that position (if any) and any subsequent rows down (adds one to their
	 * indices).
	 *
	 * @param index  row insertion point index
	 * @param values row values
	 */
	public boolean insertRow(int index, @SuppressWarnings("unchecked") T... values) {
		return insertRow(index, Arrays.asList(values));
	}

	/**
	 * Inserts the given values in this grid at the given row index. Shifts the row
	 * currently at that position (if any) and any subsequent rows down (adds one to their
	 * indices).
	 *
	 * @param index  row insertion point index
	 * @param values row values
	 */
	public boolean insertRow(int index, List<T> values) {
		return grid.insertRow(index, values);
	}

}
