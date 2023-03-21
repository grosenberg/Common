package net.certiv.common.graph.util;

import net.certiv.common.dot.Dictionary.ON;
import net.certiv.common.dot.DotStyle;
import net.certiv.common.graph.Props;

public class DotUtil {

	private DotUtil() {}

	/**
	 * Returns the {@code DotStyle} store for the {@code Node} or {@code Edge} containing
	 * this properties store. Creates and adds a {@code DotStyle} store, using the given
	 * default {@code ON} category, if a store does not exist.
	 *
	 * @param category a default {@code ON} category
	 * @return the dot style store
	 */
	public static DotStyle getDotStyle(Props props, ON category) {
		DotStyle ds = (DotStyle) props.getProperty(DotStyle.PropName);
		if (ds == null) {
			ds = new DotStyle(category);
			props.putProperty(DotStyle.PropName, ds);
		}
		return ds;
	}

	public static void clearDotStyle(Props props) {
		props.putProperty(DotStyle.PropName, null);
	}
}
