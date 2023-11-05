package net.certiv.common.graph;

import static net.certiv.common.dot.DotAttr.*;
import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import net.certiv.common.TestCommon;
import net.certiv.common.diff.Differ;
import net.certiv.common.dot.DotStyle;
import net.certiv.common.dot.LineStyle;
import net.certiv.common.graph.demo.DemoEdge;
import net.certiv.common.graph.demo.DemoNode;

class DotStyleTest extends TestCommon {

	static final boolean FORCE = true;

	@Test
	void testStyles() {

		graph.put(Graph.GRAPH_NAME, "Dot Styles");
		graph.defineStyle();

		DotStyle ds = graph.getDotStyle();
		ds.put(FONTCOLOR, "teal");
		ds.put(FONTSIZE, 32);

		DemoNode a = builder.findOrCreateNode("A");
		ds = a.getDotStyle();
		ds.put(LABEL, "Node " + a.name());
		ds.put(COLOR, "blue");
		ds.put(FILLCOLOR, "lightblue");
		ds.put(STYLE, LineStyle.filled);

		DemoNode b = builder.findOrCreateNode("B");
		ds = b.getDotStyle();
		ds.put(LABEL, "Node " + b.name());
		ds.put(COLOR, "red");
		ds.put(FILLCOLOR, "orange");
		ds.put(SHAPE, "rectangle");
		ds.put(STYLE, LineStyle.filled, LineStyle.rounded);

		DemoNode c = builder.findOrCreateNode("C");

		DemoEdge ab = graph.addEdge(a, b);
		ds = ab.getDotStyle();
		ds.put(LABEL, "Edge " + ab.name());
		ds.put(COLOR, "blue");
		ds.put(PENWIDTH, 2);

		DemoEdge bc = graph.addEdge(b, c);
		ds = bc.getDotStyle();
		ds.put(LABEL, "Edge " + bc.name());
		ds.put(ARROWHEAD, "vee");
		ds.put(ARROWTAIL, "inv");
		ds.put(ARROWSIZE, "0.7");
		ds.put(COLOR, "maroon");
		ds.put(FONTSIZE, "11");
		ds.put(FONTCOLOR, "navy");
		ds.put(STYLE, "dashed");

		String dot = graph.render();
		writeResource(getClass(), "dotStyle.md", dot, FORCE);

		String txt = loadResource(getClass(), "dotStyle.md");
		Differ.diff((String) graph.get(Graph.GRAPH_NAME), txt, dot).sdiff(true, 120).out();

		assertEquals(txt, dot);
	}
}
