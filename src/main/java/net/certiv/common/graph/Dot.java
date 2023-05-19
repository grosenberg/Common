package net.certiv.common.graph;

import net.certiv.common.dot.Dictionary.ON;
import net.certiv.common.stores.props.Props;
import net.certiv.common.dot.DotStyle;

/**
 * Helper for accessing the specialized Dot Styles record structure.
 */
public class Dot {

	private Dot() {}

	/**
	 * Returns the {@code DotStyle} store for the {@code Graph}, {@code Node} or
	 * {@code Edge} containing this properties store. Creates and adds a {@code DotStyle}
	 * store, using the given {@code ON} category, if a store does not exist.
	 *
	 * @param props    the target property store
	 * @param category a default {@code ON} category
	 * @return the dot style store
	 */
	@SuppressWarnings("unchecked")
	public static <K, V> DotStyle getStyles(Props props, ON category) {
		DotStyle ds = (DotStyle) props.get((K) DotStyle.PropName);
		if (ds == null) {
			ds = new DotStyle(category);
			props.put((K) DotStyle.PropName, ds);
		}
		return ds;
	}

	/**
	 * Removes the Dot Style store from the given property store.
	 *
	 * @param props the target property store
	 */
	@SuppressWarnings("unchecked")
	public static <K, V> void clearDotStyle(Props props) {
		props.put((K) DotStyle.PropName, null);
	}
}
