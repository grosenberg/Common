/*******************************************************************************
 * Copyright (c) 2017, 2019 Certiv Analytics. All rights reserved.
 * Use of this file is governed by the Eclipse Public License v1.0
 * that can be found in the LICENSE.txt file in the project root,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package net.certiv.common.stores;

import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.Set;
import java.util.function.BiConsumer;

import net.certiv.common.stores.TableList.TableRow;

/**
 * A table data-structure implemented as a LinkedHashMap row -> LinkedHashMap column ->
 * LinkedList values.
 */
public class TableList<R, C, V> implements Iterable<TableRow<R, C, LinkedList<V>>> {

	private final LinkedHashMap<R, LinkedHashMap<C, LinkedList<V>>> table;

	public TableList() {
		table = new LinkedHashMap<>();
	}

	public TableList(TableList<R, C, V> table) {
		this();
		this.table.putAll(table.rowMap());
	}

	public TableList(Map<R, LinkedHashMap<C, LinkedList<V>>> keyMap) {
		this();
		table.putAll(keyMap);
	}

	public LinkedHashMap<C, LinkedList<V>> get(R row) {
		return table.get(row);
	}

	public LinkedList<V> get(R row, C col) {
		LinkedHashMap<C, LinkedList<V>> r = table.get(row);
		if (r == null) return null;
		return r.get(col);
	}

	/**
	 * Put the given row key into the table. Does nothing if the row pre-exists.
	 *
	 * @param row the table row
	 * @return @true if a new row was added
	 */
	public boolean put(R row) {
		LinkedHashMap<C, LinkedList<V>> cols = get(row);
		if (cols == null) {
			cols = new LinkedHashMap<>();
			table.put(row, cols);
			return true;
		}
		return false;
	}

	/**
	 * Put the given row/column keys into the table. Does nothing if the row/column
	 * pre-exists.
	 *
	 * @param row the table row
	 * @param col the table column
	 * @return @true if a new row/column was added
	 */
	public boolean put(R row, C col) {
		LinkedHashMap<C, LinkedList<V>> cols = get(row);
		if (cols == null) {
			cols = new LinkedHashMap<>();
			table.put(row, cols);
		}
		LinkedList<V> values = cols.get(col);
		if (values == null) {
			values = new LinkedList<>();
			cols.put(col, values);
			return true;
		}
		return false;
	}

	/**
	 * Appends the given value at the given row/column in this table.
	 *
	 * @param row   table row
	 * @param col   table column
	 * @param value value to append
	 * @return @true if this collection changed as a result of the call
	 */
	public boolean put(R row, C col, V value) {
		LinkedHashMap<C, LinkedList<V>> cols = get(row);
		if (cols == null) {
			cols = new LinkedHashMap<>();
			table.put(row, cols);
		}
		LinkedList<V> values = cols.get(col);
		if (values == null) {
			values = new LinkedList<>();
			cols.put(col, values);
		}
		return values.add(value);
	}

	/**
	 * Adds the given value to the list at the given row and column if the value is not
	 * already present.
	 *
	 * @param row   the table row
	 * @param col   the table column
	 * @param value a value to add to the value list if not present
	 * @return {@code true} if this collection changed as a value of thecall
	 */
	public boolean putIfAbsent(R row, C col, V value) {
		LinkedHashMap<C, LinkedList<V>> colList = get(row);
		if (colList == null) {
			colList = new LinkedHashMap<>();
			table.put(row, colList);
		}
		LinkedList<V> list = colList.get(col);
		if (list == null) {
			list = new LinkedList<>();
			colList.put(col, list);
		}
		return !list.contains(value) ? list.add(value) : false;
	}

	/**
	 * Appends the given values at the given row/column in this table.
	 *
	 * @param row    table row
	 * @param col    table column
	 * @param values collection of values to append
	 * @return {@code true} if this collection changed as a result of the call
	 */
	public boolean putAll(R row, C col, Collection<V> values) {
		LinkedHashMap<C, LinkedList<V>> cols = get(row);
		if (cols == null) {
			cols = new LinkedHashMap<>();
			table.put(row, cols);
		}
		LinkedList<V> values_ = cols.get(col);
		if (values_ == null) {
			values_ = new LinkedList<>();
			cols.put(col, values_);
		}
		return values_.addAll(values);
	}

	/**
	 * Puts all entries from the given map into the table at the given row and the columns
	 * defined by the map. The entries are added to the end of the value lists at the
	 * given row and map columns.
	 */
	public void putAll(R row, Map<C, List<V>> map) {
		LinkedHashMap<C, LinkedList<V>> colList = table.get(row);
		if (colList == null) {
			colList = new LinkedHashMap<>();
			table.put(row, colList);
		}
		for (Entry<C, List<V>> entry : map.entrySet()) {
			C col = entry.getKey();
			if (colList.containsKey(col)) {
				colList.get(col).addAll(entry.getValue());
			} else {
				colList.put(col, new LinkedList<>(entry.getValue()));
			}
		}
	}

	/**
	 * Replaces any existing value lists at the given row and columns defined by the given
	 * map. If a value list does not exist, the value list is added at the given row and
	 * map column.
	 */
	public void replace(R row, Map<C, List<V>> map) {
		LinkedHashMap<C, LinkedList<V>> colList = table.get(row);
		if (colList == null) {
			colList = new LinkedHashMap<>();
			table.put(row, colList);
		}
		for (Entry<C, List<V>> entry : map.entrySet()) {
			C col = entry.getKey();
			if (colList.containsKey(col)) {
				LinkedList<V> list = colList.get(col);
				list.clear();
				list.addAll(entry.getValue());
			} else {
				colList.put(col, new LinkedList<>(entry.getValue()));
			}
		}
	}

	public boolean contains(R row) {
		return table.containsKey(row);
	}

	public boolean contains(R row, C col) {
		LinkedHashMap<C, LinkedList<V>> cols = table.get(row);
		if (cols == null) return false;
		return cols.containsKey(col);
	}

	public void forEach(BiConsumer<? super R, ? super LinkedHashMap<C, LinkedList<V>>> action) {
		table.forEach(action);
	}

	public LinkedList<R> rows() {
		return new LinkedList<>(table.keySet());
	}

	public LinkedList<C> cols() {
		LinkedList<C> cols = new LinkedList<>();
		for (LinkedHashMap<C, LinkedList<V>> col : table.values()) {
			cols.addAll(col.keySet());
		}
		return cols;
	}

	public Set<R> rowSet() {
		return table.keySet();
	}

	public LinkedHashMap<R, LinkedHashMap<C, LinkedList<V>>> rowMap() {
		return table;
	}

	public Set<Entry<R, LinkedHashMap<C, LinkedList<V>>>> entrySet() {
		return table.entrySet();
	}

	@Override
	public Iterator<TableRow<R, C, LinkedList<V>>> iterator() {
		return new TableIterator();
	}

	private class TableIterator implements Iterator<TableRow<R, C, LinkedList<V>>> {

		Iterator<Entry<R, LinkedHashMap<C, LinkedList<V>>>> rowItr = table.entrySet().iterator();
		Iterator<Entry<C, LinkedList<V>>> colItr = null;
		Entry<R, LinkedHashMap<C, LinkedList<V>>> row;

		@Override
		public boolean hasNext() {
			return rowItr.hasNext() || colItr != null && colItr.hasNext();
		}

		@Override
		public TableRow<R, C, LinkedList<V>> next() {
			if (colItr == null || !colItr.hasNext()) {
				row = rowItr.next();
				colItr = row.getValue().entrySet().iterator();
			}
			Entry<C, LinkedList<V>> col = colItr.next();
			return new TableRow<>(row.getKey(), col.getKey(), col.getValue());
		}

		@Override
		public void remove() {
			colItr.remove();
			if (row.getValue().isEmpty()) {
				rowItr.remove();
				row = null;
			}
		}
	}

	/** {@code Table} support class. */
	public static class TableRow<R, C, V> {

		private final R row;
		private final C col;
		private final V val;

		public TableRow(R row, C col, V val) {
			this.row = row;
			this.col = col;
			this.val = val;
		}

		public R row() {
			return row;
		}

		public C col() {
			return col;
		}

		public V val() {
			return val;
		}

		@Override
		public int hashCode() {
			return Objects.hash(col, row, val);
		}

		@Override
		public boolean equals(Object obj) {
			if (this == obj) return true;
			if (!(obj instanceof TableList.TableRow)) return false;
			TableRow<?, ?, ?> other = (TableRow<?, ?, ?>) obj;
			return Objects.equals(col, other.col) && Objects.equals(row, other.row)
					&& Objects.equals(val, other.val);
		}

		@Override
		public String toString() {
			return String.format("(%s, %s)=%s", row, col, val);
		}
	}

	public void remove(R row) {
		table.remove(row);
	}

	public void remove(R row, C col) {
		LinkedHashMap<C, LinkedList<V>> r = table.get(row);
		if (r != null) r.remove(col);
	}

	public void remove(R row, C col, V val) {
		LinkedHashMap<C, LinkedList<V>> r = table.get(row);
		if (r != null) {
			LinkedList<V> c = r.get(col);
			if (c != null) c.remove(val);
		}
	}

	public boolean isEmpty() {
		return table.isEmpty();
	}

	/** Returns the number of rows in this table. */
	public int rowSize() {
		return table.size();
	}

	/** Returns the number of columns in this table for the given row. */
	public int colSize(R row) {
		LinkedHashMap<C, LinkedList<V>> cols = table.get(row);
		return cols != null ? cols.size() : 0;
	}

	/** Returns the number of values in this table for the given row and column. */
	public int valuesSize(R row, C col) {
		LinkedHashMap<C, LinkedList<V>> cols = table.get(row);
		if (cols == null) return 0;
		LinkedList<V> values = cols.get(col);
		return values != null ? values.size() : 0;
	}

	/** Returns the size of this Table (total number of value lists held). */
	public int size() {
		int cnt = 0;
		for (LinkedHashMap<C, LinkedList<V>> cols : table.values()) {
			for (C col : cols.keySet()) {
				LinkedList<V> values = cols.get(col);
				if (values != null) {
					cnt += values.size();
				}
			}
		}
		return cnt;
	}

	public void clear() {
		table.clear();
	}
}
