/*
 * Created on Sep 24, 2004
 */
package net.certiv.common.util;

import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.util.ArrayList;

import javax.swing.JFrame;
import javax.swing.JTable;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;
import javax.swing.table.TableModel;

public class Utils {

	public static final int RIGHT = 0;
	public static final int LEFT = 1;

	private Utils() {}

	public static JFrame getFrame(Container obj) {
		Container o = obj;
		while (o != null && !(o instanceof JFrame)) {
			o = o.getParent();
		}
		return (JFrame) o;
	}

	public static String[] appendArray(String[] one, String two) {
		return appendArray(one, new String[] { two });
	}

	public static String[] appendArray(String one, String[] two) {
		return appendArray(new String[] { one }, two);
	}

	public static String[] appendArray(String[] one, String[] two) {
		String[] result = new String[one.length + two.length];
		System.arraycopy(one, 0, result, 0, one.length - 1);
		System.arraycopy(two, 0, result, one.length, two.length - 1);
		return result;
	}

	public static final boolean isANumber(String val) {
		return !isNaN(val);
	}

	public static final boolean isNaN(String val) {
		boolean result = false;
		try {
			@SuppressWarnings("unused")
			int i = Integer.parseInt(val);
		} catch (NumberFormatException nex) {
			result = true;
		}
		return result;
	}

	/**
	 * Determines whether the first character of the given word is upper case.
	 *
	 * @param word String to be checked for capitalization.
	 * @returns true if first character is upper case.
	 */
	public static final boolean isCapitalLetter(String word) {
		return !Character.isDigit(word.charAt(0)) && Character.isUpperCase(word.charAt(0));
	}

	/**
	 * Determines whether all of the characters in the given word are upper case.
	 *
	 * @param word String to be checked for capitalization.
	 * @returns true if all characters are is upper case.
	 */
	public static final boolean isCapitalWord(String word) {
		for (int i = 0; i < word.length(); i++) {
			if (Character.isDigit(word.charAt(i)) || Character.isLowerCase(word.charAt(i))) {
				return false;
			}
		}
		return true;
	}

	/**
	 * Determines if all of the word in the given line are uppper case.
	 *
	 * @param line the line to be checked for upper case
	 * @return true if the line is all upper case words
	 */
	public static final boolean isCapitalLine(String line) {
		if (line == null || line.length() == 0) {
			return false;
		}
		String[] words = line.split("\\s");
		for (int i = 0; i < words.length; i++) {
			if (!isCapitalWord(words[i])) {
				return false;
			}
		}
		return true;
	}

	// //////////////////////////////////////////////////////////////////////

	/**
	 * Remove Xml and puctuation from the input string.
	 *
	 * @param input the string to filter
	 * @return the filtered string
	 */
	public static String tokenFilterAll(String input) {
		String s1 = input.replaceAll("<Indent/>", "      ");
		String s2 = s1.replaceAll("<.*?>", " ");
		String s3 = s2.replaceAll("\\p{Punct}", "");
		String s4 = s3.replaceAll("[\u2018\u2019\u201C\u201D]", ""); // quotes,
		// etc.
		return s4;
	}

	/**
	 * Remove standard Xml strings from the input
	 *
	 * @param input the string to filter
	 * @return the filtered string
	 */
	public static String tokenFilterXml(String input) {
		String s1 = input.replaceAll("<Indent/>", "      ");
		String s2 = s1.replaceAll("<.*?>", " ");
		return s2;
	}

	/**
	 * Remove just the simple xml strings from the input
	 *
	 * @param input the string to filter
	 * @return the filtered string
	 */
	public static String tokenFilterXmlSimple(String input) {
		String s1 = input.replaceAll("<Indent/>", "      ");
		String s2 = s1.replaceAll("<.*?\\w/>", "");
		return s2;
	}

	// //////////////////////////////////////////////////////////////////////
	public static String numberAlign(String s, int len) {
		return pad(s, len, '0', RIGHT);
	}

	public static String rightAlign(String s, int len) {
		return pad(s, len, ' ', RIGHT);
	}

	public static String rightAlign(String s, int len, char c) {
		return pad(s, len, c, RIGHT);
	}

	/**
	 * Pads the string to the given length with spaces. Reasonable corner conditions
	 * are applied.
	 */
	public static String leftAlign(String s, int len) {
		return pad(s, len, ' ', LEFT);
	}

	public static String leftAlign(String s, int len, char c) {
		return pad(s, len, c, LEFT);
	}

	/**
	 * Pads the string to the given length with the given character. Reasonable
	 * corner conditions are applied.
	 */
	public static String pad(String s, int len, char c, int align) {
		if (s == null) {
			s = "";
		}
		if (len < s.length()) {
			return s;
		} else if (len < 0) {
			throw new IllegalArgumentException("Negative len");
		} else {
			int delta = len - s.length();
			if (align == LEFT) {
				return s + filler(c, delta);
			} else {
				return filler(c, delta) + s;
			}
		}
	}

	/**
	 * Creates a padding string of character c to a length len.
	 *
	 * @param c The padding character.
	 * @param len The target string length.
	 * @return The padding string.
	 */
	public static String filler(char c, int len) {
		if (len == 0) {
			return "";
		} else if (len < 0) {
			throw new IllegalArgumentException("Negative len");
		} else {
			StringBuffer sb = new StringBuffer(len);
			for (int i = 0; i < len; ++i) {
				sb.append(c);
			}
			return sb.toString();
		}
	}

	/**
	 * Searches for the first occurence of the element in the list within a given
	 * variance.
	 *
	 * @param elem a float value.
	 * @param variance an float value.
	 * @return the index of the first occurrence of the elem in this list; returns
	 *             <tt>-1</tt> if the object is not found within the given variance.
	 */
	public static int closeTo(ArrayList<? extends Number> list, float elem, float variance) {
		for (int i = 0; i < list.size(); i++) {
			if (within(((Integer) list.get(i)).floatValue(), elem, variance)) return i;
		}
		return -1;
	}

	/**
	 * Determines whether two float values are within a specified percent
	 * difference.
	 *
	 * @param value The first number to compare to.
	 * @param center The second number to compare to.
	 * @param percent The allowed percent variance.
	 */
	public static boolean bounded(float value, float center, float percent) {
		float ubound = value * (1f + percent);
		float lbound = value * (1f - percent);
		if (center < ubound && center > lbound) {
			return true;
		}
		return false;
	}

	/**
	 * Determines whether two Integer numbers are within a specified variance.
	 *
	 * @param first The first number to compare to.
	 * @param second The second number to compare to.
	 * @param variance The allowed variance.
	 */
	public static boolean within(Integer first, Integer second, float variance) {
		return within(first.floatValue(), second.floatValue(), variance);
	}

	/**
	 * Determines whether two floating point numbers are within a specified
	 * variance.
	 *
	 * @param first The first number to compare to.
	 * @param second The second number to compare to.
	 * @param variance The allowed variance.
	 */
	public static boolean within(float first, float second, float variance) {
		float firstMin = first - variance;
		float firstMax = first + variance;
		return second > firstMin && second < firstMax;
	}

	// public static void centerDisplayOnScreen(Component window) {
	// Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
	// int w = window.getSize().width;
	// int h = window.getSize().height;
	// int x = (dim.width - w) / 2;
	// int y = (dim.height - h) / 2;
	// window.setLocation(x, y);
	// }

	public static boolean calcColumnWidthsCalled = false;

	public static void calcColumnWidths(JTable table, int columnsToProcess, int padding) {
		calcColumnWidthsCalled = true;

		JTableHeader header = table.getTableHeader();
		TableCellRenderer defaultHeaderRenderer = null;

		if (header != null) defaultHeaderRenderer = header.getDefaultRenderer();

		TableColumnModel columns = table.getColumnModel();
		TableModel data = table.getModel();

		int margin = columns.getColumnMargin();
		int rowCount = data.getRowCount();
		int totalWidth = 0;

		for (int i = 0, count = columns.getColumnCount(); i < count && i < columnsToProcess; i++) {
			TableColumn column = columns.getColumn(i);
			int columnIndex = column.getModelIndex();
			int width = -1;

			TableCellRenderer h = column.getHeaderRenderer();
			if (h == null) h = defaultHeaderRenderer;

			if (h != null) {
				Object headerValue = column.getHeaderValue();
				Component c = h.getTableCellRendererComponent(table, headerValue, false, false, -1, i);
				width = c.getPreferredSize().width;
			}

			for (int row = rowCount - 1; row >= 0; --row) {
				Component c = table.getCellRenderer(row, i).getTableCellRendererComponent(table,
						data.getValueAt(row, columnIndex), false, false, row, i);
				int w = c.getPreferredSize().width;
				width = Math.max(width, w);
			}

			if (width >= 0) {
				int preferredWidth = width + margin + (2 * padding);
				column.setPreferredWidth(preferredWidth);
				column.setWidth(preferredWidth);
			}

			if (i < count - 1) totalWidth += column.getPreferredWidth();
		}
		Dimension size = table.getPreferredScrollableViewportSize();
		size.width = totalWidth * 2;
		table.setPreferredScrollableViewportSize(size);
	}

	public static String encode(String str) {
		if (str == null) return null;
		if (str.isBlank()) return str;

		StringBuffer returnString = new StringBuffer(str.length() * 2);

		for (int i = 0; i < str.length(); i++) {
			char stringChar = str.charAt(i);

			switch (stringChar) {
				case '\'':
					returnString.append("&apos;");
					break;
				case '\"':
					returnString.append("&quot;");
					break;
				case '<':
					returnString.append("&lt;");
					break;
				case '>':
					returnString.append("&gt;");
					break;
				case '&':
					returnString.append("&amp;");
					break;
				default:
					returnString.append(stringChar);
					break;
			}
		}

		return returnString.toString();
	}
}
