package net.certiv.common.dot;

import java.util.LinkedHashMap;
import java.util.Map;

import org.apache.commons.text.TextStringBuilder;

import net.certiv.common.dot.Dictionary.Entry;
import net.certiv.common.dot.Dictionary.ON;
import net.certiv.common.dot.Dictionary.TYPE;
import net.certiv.common.util.Strings;

public class DotStyle {

	public static final String Property = "DotStyleProperty";

	public final Map<DotAttr, Object> AttrMap = new LinkedHashMap<>();

	/**
	 * Adds a dot attribute/value mapping. If value is {@code null}, the mapping
	 * will be removed.
	 *
	 * @param key the dot attribute key
	 * @param value the new value
	 * @return the previous value associated with key, or {@code null} if there was
	 *             no mapping for the key
	 */
	public final Object put(DotAttr key, Object value) {
		if (value == null) return AttrMap.remove(key);
		return AttrMap.put(key, value);
	}

	/**
	 * Returns the value for the specified key. Only attributes added with
	 * {@code put} will return a non-null value.
	 *
	 * @param key the dot attribute key
	 * @return the property value associated with key, or {@code null} if there was
	 *             no mapping for the key
	 */
	public final Object get(DotAttr key) {
		return AttrMap.get(key);
	}

	/**
	 * Returns {@code true} if a value is associated with the given key.
	 *
	 * @param key the dot attribute key
	 * @return {@code true} if a value is associated with key
	 */
	public final boolean has(DotAttr key) {
		return AttrMap.containsKey(key);
	}

	public String graphAttributes(String dent) {
		TextStringBuilder graph = new TextStringBuilder();
		TextStringBuilder nodes = new TextStringBuilder();

		for (DotAttr attr : AttrMap.keySet()) {
			Entry entry = Dictionary.lookup(attr);
			if (entry.where(ON.GRAPHS)) {
				graph.appendln(dent + fmt(attr, AttrMap.get(attr)));

			} else if (entry.where(ON.NODES)) {
				nodes.append(fmt(attr, AttrMap.get(attr)));
			}
		}

		if (nodes.isNotEmpty()) {
			nodes.trim();
			nodes.insert(0, dent + "node [");
			nodes.appendln("]");
		}

		if (graph.isNotEmpty()) graph.appendNewLine();
		return graph.append(nodes).toString();
	}

	public String nodeAttributes() {
		TextStringBuilder sb = new TextStringBuilder();

		for (DotAttr attr : AttrMap.keySet()) {
			Entry entry = Dictionary.lookup(attr);
			if (entry.where(ON.NODES)) {
				sb.append(fmt(attr, AttrMap.get(attr)));
			}
		}

		if (sb.isEmpty()) return Strings.EMPTY;
		return String.format(" [%s]", sb.trim());
	}

	public String edgeAttributes() {
		TextStringBuilder sb = new TextStringBuilder();

		for (DotAttr attr : AttrMap.keySet()) {
			Entry entry = Dictionary.lookup(attr);
			if (entry.where(ON.EDGES)) {
				sb.append(fmt(attr, AttrMap.get(attr)));
			}
		}

		if (sb.isEmpty()) return Strings.EMPTY;
		return String.format(" [%s]", sb.trim());
	}

	@Override
	public String toString() {
		if (AttrMap.isEmpty()) return Strings.EMPTY;

		TextStringBuilder sb = new TextStringBuilder();
		AttrMap.forEach((key, value) -> sb.append(fmt(key, value)));
		return sb.toString().trim();
	}

	private String fmt(DotAttr key, Object value) {
		if (value == null) return Strings.EMPTY;

		boolean quote = Dictionary.lookup(key).type() == TYPE.STRING;
		if (quote) return String.format("%s=\"%s\" ", key, value);
		return String.format("%s=%s ", key, value);
	}
}
