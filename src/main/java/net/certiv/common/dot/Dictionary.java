package net.certiv.common.dot;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import net.certiv.common.util.Strings;

public class Dictionary {

	public static class Entry {

		private final DotAttr attr;
		private final TYPE type;
		private final Set<String> values;
		private final String defval;
		private final Set<ON> where;

		/**
		 * A dictionary entry.
		 *
		 * @param attr the entry defining attribute
		 * @param type value type
		 * @param values permitted values or {@code Any}
		 * @param defval default value or {@code Empty}
		 * @param where valid locations
		 */
		private Entry(DotAttr attr, TYPE type, String[] values, String defval, ON... where) {
			this.attr = attr;
			this.type = type;
			this.values = new LinkedHashSet<>(Arrays.asList(values));
			this.defval = defval;
			this.where = new LinkedHashSet<>(Arrays.asList(where));
		}

		/** Returns {@code true} if the value is permitted. */
		public boolean permits(String value) {
			if (values.isEmpty()) return true; // Any
			return values.contains(value);
		}

		/** Returns {@code true} if the location is permitted. */
		public boolean where(ON loc) {
			return where.contains(loc);
		}

		public DotAttr attr() {
			return attr;
		}

		public TYPE type() {
			return type;
		}

		public List<String> values() {
			return new ArrayList<>(values);
		}

		public String defval() {
			return defval;
		}

		public List<ON> where() {
			return new ArrayList<>(where);
		}
	}

	/** Attribute value types. */
	public enum TYPE {
		COLOR,  // single color
		COLORS, // color or colorlist
		LIST,	// constrained to values, unquoted
		NUMBER,
		POINT,
		RECT,
		SPLINE,	// point or splineType
		STRING,	// quoted text

		INVALID;
	}

	public enum ON {
		GRAPHS("graph"),
		CLUSTERS("graph"), // same as subgraph
		NODES("node"),
		EDGES("edge");

		private final String title;

		ON(String title) {
			this.title = title;
		}

		public String title() {
			return title;
		}
	}

	private static final String[] Any = Strings.EMPTY_STRINGS;
	private static final String Empty = Strings.EMPTY;

	private static final String[] ArrowType = { "box", "crow", "curve", "icurve", "diamond", "dot", "odot",
			"ediamond", "empty", "inv", "invdot", "invodot", "invempty", "none", "normal", "open", "tee",
			"vee", "o", "l", "r" };
	private static final String[] Boolean = { "true", "false" };
	private static final String[] ClusterMode = { "local", "global", "none" };
	// private static final String[] ColorNames = DotColors.getColorNames();
	private static final String[] DirType = { "forward", "back", "both", "none" };
	private static final String[] JustType = { "l", "r", "c" };
	private static final String[] LocType = { "t", "b", "c" };
	private static final String[] Ordering = { "in", "out" };
	private static final String[] OutputMode = { "breadthfirst", "nodesfirst", "edgesfirst" };
	// private static final String[] PageDir = { "BL", "BR", "TL", "TR", "RB", "RT",
	// "LB", "LT" };
	private static final String[] RankType = { "same", "min", "source", "max", "sink" };
	private static final String[] RankDir = { "TB", "LR", "BT", "RL" };
	private static final String[] Schemes = { "x11", "svg", "brewer" };
	private static final String[] Shape = { "assembly", "box", "box3d", "cds", "circle", "component",
			"cylinder", "diamond", "doublecircle", "doubleoctagon", "egg", "ellipse", "fivepoverhang",
			"folder", "hexagon", "house", "insulator", "invhouse", "invtrapezium", "invtriange", "larrow",
			"lpromoter", "Mcircle", "Mdiamond", "Mrecord", "Mhouse", "none", "note", "noverhang", "octagon",
			"oval", "parallelogram", "pentagon", "plain", "plaintext", "point", "polygon", "primersite",
			"promotor", "proteasite", "proteinstab", "rarrow", "record", "rect", "rectangle",
			"restrictionsite", "ribosite", "rnastab", "rpromoter", "septagon", "signature", "square", "star",
			"tab", "terminator", "threepoverhang", "trapezium", "triange", "tripleoctagon", "underline",
			"utr" };
	private static final String[] Splines = { "true", "false", "none", "line", "spline", "polyline", "ortho",
			"curved" };
	private static final String[] Style = { "bold", "dashed", "diagonals", "dotted", "filled", "invis",
			"radial", "rounded", "solid", "striped", "tapered", "wedged" };

	private static final Map<DotAttr, Entry> AttrMap = new LinkedHashMap<>();
	private static final Map<String, DotAttr> NameMap = new LinkedHashMap<>();

	static {
		put(DotAttr.INVALID, TYPE.INVALID, Any, Empty);

		put(DotAttr.ARROWHEAD, TYPE.LIST, ArrowType, "normal", ON.EDGES);
		put(DotAttr.ARROWSIZE, TYPE.NUMBER, Any, "1.0", ON.EDGES);
		put(DotAttr.ARROWTAIL, TYPE.LIST, ArrowType, "normal", ON.EDGES);
		put(DotAttr.BB, TYPE.RECT, Any, Empty, ON.GRAPHS);
		put(DotAttr.BGCOLOR, TYPE.COLORS, Any, Empty, ON.GRAPHS, ON.CLUSTERS);
		put(DotAttr.CENTER, TYPE.LIST, Boolean, "false", ON.GRAPHS);
		put(DotAttr.CLUSTERRANK, TYPE.LIST, ClusterMode, Empty, ON.GRAPHS);
		put(DotAttr.COLOR, TYPE.COLORS, Any, "black", ON.CLUSTERS, ON.NODES, ON.EDGES);
		put(DotAttr.COLORSCHEME, TYPE.LIST, Schemes, "x11", ON.GRAPHS, ON.CLUSTERS, ON.NODES, ON.EDGES);
		put(DotAttr.COMMENT, TYPE.STRING, Any, Empty, ON.GRAPHS, ON.NODES, ON.EDGES);
		put(DotAttr.COMPOUND, TYPE.LIST, Boolean, "false", ON.GRAPHS);
		put(DotAttr.CONCENTRATE, TYPE.LIST, Boolean, "false", ON.GRAPHS);
		put(DotAttr.CONSTRAINT, TYPE.LIST, Boolean, "true", ON.EDGES);
		put(DotAttr.DECORATE, TYPE.LIST, Boolean, "true", ON.EDGES);
		put(DotAttr.DIR, TYPE.LIST, DirType, "forward", ON.EDGES);
		put(DotAttr.DISTORTION, TYPE.NUMBER, Any, "0.0", ON.NODES);
		put(DotAttr.FILLCOLOR, TYPE.COLORS, Any, "lightgrey", ON.CLUSTERS, ON.NODES, ON.EDGES);
		put(DotAttr.FIXEDSIZE, TYPE.STRING, Any, "false", ON.NODES);
		put(DotAttr.FONTCOLOR, TYPE.COLOR, Any, "black", ON.GRAPHS, ON.CLUSTERS, ON.NODES, ON.EDGES);
		put(DotAttr.FONTNAME, TYPE.STRING, Any, "Times-Roman", ON.GRAPHS, ON.CLUSTERS, ON.NODES, ON.EDGES);
		put(DotAttr.FONTSIZE, TYPE.NUMBER, Any, "14.0", ON.GRAPHS, ON.CLUSTERS, ON.NODES, ON.EDGES);
		put(DotAttr.FORCELABELS, TYPE.LIST, Boolean, "true", ON.GRAPHS);
		put(DotAttr.GRADIENTANGLE, TYPE.NUMBER, Any, "0.0", ON.GRAPHS, ON.CLUSTERS, ON.NODES);
		put(DotAttr.GROUP, TYPE.STRING, Any, Empty, ON.NODES);
		put(DotAttr.HEADCLIP, TYPE.STRING, Any, "true", ON.EDGES);
		put(DotAttr.HEADLABEL, TYPE.STRING, Any, Empty, ON.EDGES);
		put(DotAttr.HEADPORT, TYPE.STRING, Any, "center", ON.EDGES);
		put(DotAttr.HEIGHT, TYPE.NUMBER, Any, "0.5", ON.NODES);
		put(DotAttr.LABEL, TYPE.STRING, Any, Empty, ON.GRAPHS, ON.CLUSTERS, ON.NODES, ON.EDGES);
		put(DotAttr.LABELFONTCOLOR, TYPE.COLOR, Any, "black", ON.GRAPHS, ON.CLUSTERS, ON.NODES, ON.EDGES);
		put(DotAttr.LABELANGLE, TYPE.NUMBER, Any, "-25.0", ON.EDGES);
		put(DotAttr.LABELDISTANCE, TYPE.NUMBER, Any, "1.0", ON.EDGES);
		put(DotAttr.LABELFLOAT, TYPE.LIST, Boolean, "false", ON.EDGES);
		put(DotAttr.LABELFONTCOLOR, TYPE.COLOR, Any, "black", ON.EDGES);
		put(DotAttr.LABELFONTNAME, TYPE.STRING, Any, "Times-Roman", ON.EDGES);
		put(DotAttr.LABELFONTSIZE, TYPE.NUMBER, Any, "14.0", ON.EDGES);
		put(DotAttr.LABELJUST, TYPE.LIST, JustType, "c", ON.EDGES);
		put(DotAttr.LABELLOC, TYPE.LIST, LocType, Empty, ON.GRAPHS, ON.CLUSTERS, ON.NODES);
		put(DotAttr.LANDSCAPE, TYPE.LIST, Boolean, "false", ON.GRAPHS);
		put(DotAttr.MARGIN, TYPE.POINT, Any, Empty, ON.GRAPHS, ON.CLUSTERS, ON.NODES);
		put(DotAttr.NODESEP, TYPE.NUMBER, Any, "0.25", ON.GRAPHS);
		put(DotAttr.NOJUSTIFY, TYPE.LIST, Boolean, "false", ON.GRAPHS, ON.CLUSTERS, ON.NODES);
		put(DotAttr.ORDERING, TYPE.LIST, Ordering, Empty, ON.GRAPHS, ON.NODES);
		put(DotAttr.OUTPUTORDER, TYPE.LIST, OutputMode, "breadthfirst", ON.GRAPHS);
		put(DotAttr.PENCOLOR, TYPE.COLOR, Any, "black", ON.CLUSTERS);
		put(DotAttr.PENWIDTH, TYPE.NUMBER, Any, "1.0", ON.CLUSTERS, ON.NODES, ON.EDGES);
		put(DotAttr.POS, TYPE.SPLINE, Any, Empty, ON.NODES, ON.EDGES);
		put(DotAttr.RANK, TYPE.STRING, RankType, Empty, ON.CLUSTERS);
		put(DotAttr.RANKDIR, TYPE.LIST, RankDir, Empty, ON.GRAPHS);
		put(DotAttr.RANKSEP, TYPE.NUMBER, Any, "0.5", ON.GRAPHS);
		put(DotAttr.REGULAR, TYPE.LIST, Boolean, "false", ON.NODES);
		put(DotAttr.SHAPE, TYPE.LIST, Shape, "ellipse", ON.NODES);
		put(DotAttr.SIDES, TYPE.NUMBER, Any, "4", ON.NODES);
		put(DotAttr.SKEW, TYPE.NUMBER, Any, "0.0", ON.NODES);
		put(DotAttr.SPLINES, TYPE.STRING, Splines, Empty, ON.GRAPHS);
		put(DotAttr.STYLE, TYPE.LIST, Style, Empty, ON.GRAPHS, ON.CLUSTERS, ON.NODES, ON.EDGES);
		put(DotAttr.TAILCLIP, TYPE.STRING, Any, "true", ON.EDGES);
		put(DotAttr.TAILLABEL, TYPE.STRING, Any, Empty, ON.EDGES);
		put(DotAttr.TAILPORT, TYPE.STRING, Any, "center", ON.EDGES);
		put(DotAttr.WEIGHT, TYPE.NUMBER, Any, "0", ON.EDGES);
		put(DotAttr.WIDTH, TYPE.NUMBER, Any, "0.75", ON.NODES);
		put(DotAttr.XLABEL, TYPE.STRING, Any, Empty, ON.NODES, ON.EDGES);
	}

	private static void put(DotAttr name, TYPE type, String[] values, String defval, ON... where) {
		AttrMap.put(name, new Entry(name, type, values, defval, where));
		if (name != DotAttr.INVALID) NameMap.put(name.toString(), name);
	}

	private static Set<DotAttr> GraphAttrs;
	private static Set<DotAttr> ClusterAttrs;
	private static Set<DotAttr> NodeAttrs;
	private static Set<DotAttr> EdgeAttrs;

	private Dictionary() {}

	/** Returns {@code true} if the given key is defined and not {@code INVALID}. */
	public static boolean valid(DotAttr key) {
		return DotAttr.INVALID != key && AttrMap.containsKey(key);
	}

	/** Lookup the dictionary {@code Entry} for the given name. */
	public static Entry lookup(String name) {
		try {
			return lookup(DotAttr.valueOf(name.toUpperCase()));
		} catch (Exception e) {
			return AttrMap.get(DotAttr.INVALID);
		}
	}

	/** Lookup the dictionary {@code Entry} for the given {@code DotAttr}. */
	public static Entry lookup(DotAttr attr) {
		if (AttrMap.containsKey(attr)) return AttrMap.get(attr);
		return AttrMap.get(DotAttr.INVALID);
	}

	/** Find the {@code Attr} corresponding to the given name. */
	public static DotAttr find(String name) {
		DotAttr attr = NameMap.get(name);
		return attr != null ? attr : DotAttr.INVALID;
	}

	/** Returns {@code true} if the given key defines a {@code String} type. */
	public static boolean isStringType(DotAttr key) {
		return lookup(key).type == TYPE.STRING;
	}

	/** Returns all known entry names. */
	public static Set<String> getAttrNames() {
		return NameMap.keySet();
	}

	public static Set<DotAttr> getGraphAttibutes() {
		if (GraphAttrs == null) {
			GraphAttrs = new LinkedHashSet<>();
			for (DotAttr attr : AttrMap.keySet()) {
				if (lookup(attr).where().contains(ON.GRAPHS)) {
					GraphAttrs.add(attr);
				}
			}
		}
		return GraphAttrs;
	}

	public static Set<DotAttr> getClusterAttibutes() {
		if (ClusterAttrs == null) {
			ClusterAttrs = new LinkedHashSet<>();
			for (DotAttr attr : AttrMap.keySet()) {
				if (lookup(attr).where().contains(ON.CLUSTERS)) {
					ClusterAttrs.add(attr);
				}
			}
		}
		return ClusterAttrs;
	}

	public static Set<DotAttr> getNodeAttibutes() {
		if (NodeAttrs == null) {
			NodeAttrs = new LinkedHashSet<>();
			for (DotAttr attr : AttrMap.keySet()) {
				if (lookup(attr).where().contains(ON.NODES)) {
					NodeAttrs.add(attr);
				}
			}
		}
		return NodeAttrs;
	}

	public static Set<DotAttr> getEdgeAttibutes() {
		if (EdgeAttrs == null) {
			EdgeAttrs = new LinkedHashSet<>();
			for (DotAttr attr : AttrMap.keySet()) {
				if (lookup(attr).where().contains(ON.EDGES)) {
					EdgeAttrs.add(attr);
				}
			}
		}
		return EdgeAttrs;
	}
}
