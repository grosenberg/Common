package net.certiv.common.dot;

import java.util.regex.Pattern;

import org.apache.commons.text.TextStringBuilder;

import net.certiv.common.dot.Dictionary.ON;
import net.certiv.common.stores.Table;
import net.certiv.common.util.Strings;

public class DotStyle {

	/** Well-defined property name. */
	public static final String PropName = "DotStyleProperty";
	/** Default font color. */
	public static final String BLACK = "black";
	/** Default font families. */
	public static final String FONTS = "roboto, fira sans, lucida sans, segoe ui";

	private final Table<DotAttr, ON, Object> table = new Table<>();

	private final ON category;

	public DotStyle(ON category) {
		this.category = category;
	}

	/**
	 * Returns {@code true} if a value is associated with the given key against the
	 * default {@code ON} category.
	 *
	 * @param key the dot attribute key
	 * @return {@code true} if a value is associated with key
	 */
	public final boolean has(DotAttr key) {
		return table.contains(key, category);
	}

	/**
	 * Returns {@code true} if a value is associated with the given key and {@code ON}
	 * category.
	 *
	 * @param key      the dot attribute key
	 * @param category the dot defining category
	 * @return {@code true} if a value is associated with key
	 */
	public final boolean has(DotAttr key, ON category) {
		return table.contains(key, category);
	}

	/**
	 * Returns the value for the specified key and the default {@code ON} category, or
	 * {@code null} if not found.
	 *
	 * @param key the dot attribute key
	 * @return the associated value, or {@code null} if not found
	 */
	public final Object get(DotAttr key) {
		return table.get(key, category);
	}

	/**
	 * Returns the value for the specified key and given {@code ON} category, or
	 * {@code null} if not found.
	 *
	 * @param key      the dot attribute key
	 * @param category the dot defining category
	 * @return the associated value, or {@code null} if not found
	 */
	public final Object get(DotAttr key, ON category) {
		return table.get(key, category);
	}

	/**
	 * Adds a dot attribute/value mapping for the default {@code ON} category. If the
	 * value is {@code null}, all mappings are removed.
	 *
	 * @param key   the dot attribute key
	 * @param value the new value
	 */
	public final void put(DotAttr key, Object value) {
		if (Dictionary.valid(key)) {
			if (value != null) {
				table.put(key, category, value);
			} else {
				table.remove(key, category);
			}
		}
	}

	/**
	 * Adds a dot attribute/value mapping for the given {@code ON} category. If the value
	 * is {@code null}, the mapping will be removed.
	 *
	 * @param key      the dot attribute key
	 * @param category the dot defining category
	 * @param value    the new value
	 */
	public final void put(DotAttr key, ON category, Object value) {
		if (Dictionary.valid(key)) {
			if (value != null) {
				table.put(key, category, value);
			} else {
				table.remove(key, category);
			}
		}
	}

	/**
	 * Adds a dot attribute/value mapping to the default {@code ON} category iff the given
	 * key is not already defined for that category.
	 *
	 * @param key   the dot attribute key
	 * @param value the new value
	 * @return {@code true} if the given attribute/value mapping was added
	 */
	public final boolean putIfAbsent(DotAttr key, Object value) {
		if (has(key)) return false;
		put(key, value);
		return true;
	}

	/**
	 * Adds a dot attribute/value mapping for the given category iff the given key is not
	 * already defined for that category.
	 *
	 * @param key      the dot attribute key
	 * @param category the dot defining category
	 * @param value    the new value
	 * @return {@code true} if the given attribute/value mapping was added
	 */
	public final boolean putIfAbsent(DotAttr key, ON category, Object value) {
		if (has(key, category)) return false;
		put(key, category, value);
		return true;
	}

	/** Returns a category titled style string, including newline. */
	public String titledAttributes(ON category, String dent) {
		String attrs = inlineAttributes(category);
		if (attrs.isEmpty()) return attrs;
		return String.format("%s%s%s" + Strings.EOL, dent, category.title(), attrs);
	}

	/** Returns an untitled style string, without newline. */
	public String inlineAttributes(ON category) {
		TextStringBuilder sb = attributes(category, Strings.SPACE).trim();
		if (sb.isEmpty()) return Strings.EMPTY;
		return String.format(" [%s]", sb);
	}

	public TextStringBuilder attributes(ON category, String dent) {
		TextStringBuilder sb = new TextStringBuilder();
		for (DotAttr key : table.rowSet()) {
			Object value = table.get(key, category);
			if (value != null) sb.append(dent + fmt(key, value));
		}
		return sb.trim();
	}

	@Override
	public String toString() {
		if (table.isEmpty()) return Strings.EMPTY;

		TextStringBuilder sb = new TextStringBuilder();
		table.forEach((key, value) -> sb.append(fmt(key, value)));
		return sb.toString().trim();
	}

	private static final Pattern LIST = Pattern.compile(".*?[\\h,].*");

	private String fmt(DotAttr key, Object value) {
		String fmt = "%s=%s";
		if (Dictionary.isCompoundType(key) && LIST.matcher(value.toString()).matches()) {
			fmt = "%s=\"%s\"";
		}
		return String.format(fmt, key, value);
	}
}
