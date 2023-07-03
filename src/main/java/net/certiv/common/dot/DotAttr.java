package net.certiv.common.dot;

public enum DotAttr implements IDotStr {

	ARROWHEAD("arrowhead"),
	ARROWSIZE("arrowsize"),
	ARROWTAIL("arrowtail"),
	BB("bb"),
	BGCOLOR("bgcolor"),
	CENTER("center"),
	CLUSTERRANK("clusterrank"),
	COLOR("color"),
	COLORSCHEME("colorscheme"),
	COMMENT("comment"),
	COMPOUND("compound"),
	CONCENTRATE("concentrate"),
	CONSTRAINT("constraint"),
	DECORATE("decorate"),
	DIR("dir"),
	DISTORTION("distortion"),
	FILLCOLOR("fillcolor"),
	FIXEDSIZE("fixedsize"),
	FONTCOLOR("fontcolor"),
	FONTNAME("fontname"),
	FONTSIZE("fontsize"),
	FORCELABELS("forcelabels"),
	GRADIENTANGLE("gradientangle"),
	GROUP("group"),
	HEADCLIP("headclip"),
	HEADLABEL("headlabel"),
	HEADPORT("headport"),
	HEIGHT("height"),

	LABEL("label"),
	LABELFONTCOLOR("labelfontcolor"),
	LABELANGLE("labelangle"),
	LABELDISTANCE("labeldistance"),
	LABELFLOAT("labelfloat"),
	LABELFONTNAME("labelfontname"),
	LABELFONTSIZE("labelfontsize"),
	LABELJUST("labeljust"),
	LABELLOC("labelloc"),

	LANDSCAPE("landscape"),
	MARGIN("margin"),
	NODESEP("nodesep"),
	NOJUSTIFY("nojustify"),

	ORDERING("ordering"),
	OUTPUTORDER("outputorder"),
	PAGEDIR("pagedir"),
	PENCOLOR("pencolor"),
	PENWIDTH("penwidth"),
	POS("pos"),

	RANK("rank"),
	RANKDIR("rankdir"),
	RANKSEP("ranksep"),
	REGULAR("regular"),
	SHAPE("shape"),
	SIDES("sides"),
	SKEW("skew"),
	SPLINES("splines"),
	STYLE("style"),

	TAILCLIP("tailclip"),
	TAILLABEL("taillabel"),
	TAILPORT("tailport"),
	WEIGHT("weight"),
	WIDTH("width"),
	XLABEL("xlabel"),

	INVALID("invalid"),

	;

	private String attr;

	DotAttr(String attr) {
		this.attr = attr;
	}

	@Override
	public String toString() {
		return attr;
	}
}
