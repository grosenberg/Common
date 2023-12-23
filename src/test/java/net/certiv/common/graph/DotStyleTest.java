package net.certiv.common.graph;

import static net.certiv.common.dot.DotAttr.*;
import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import net.certiv.common.CommonSupport;
import net.certiv.common.diff.Differ;
import net.certiv.common.dot.DotStyle;
import net.certiv.common.dot.LineStyle;
import net.certiv.common.graph.demo.DemoEdge;
import net.certiv.common.graph.demo.DemoNode;
import net.certiv.common.util.test.CommonTestBase;

class DotStyleTest extends CommonTestBase {

	static final boolean FORCE = true;
	private final CommonSupport CS = new CommonSupport();

	@BeforeEach
	public void setup() {
		CS.setup();
	}

	@AfterEach
	public void teardown() {
		CS.teardown();
	}

	@Test
	void testStyles() {
		CS.graph.put(Graph.GRAPH_ID, "Dot Styles");
		CS.graph.defineStyle();

		DotStyle ds = CS.graph.getDotStyle();
		ds.put(FONTCOLOR, "teal");
		ds.put(FONTSIZE, 32);

		DemoNode a = CS.builder.findOrCreateNode("A");
		ds = a.getDotStyle();
		ds.put(LABEL, "Node " + a.name());
		ds.put(COLOR, "blue");
		ds.put(FILLCOLOR, "lightblue");
		ds.put(STYLE, LineStyle.filled);

		DemoNode b = CS.builder.findOrCreateNode("B");
		ds = b.getDotStyle();
		ds.put(LABEL, "Node " + b.name());
		ds.put(COLOR, "red");
		ds.put(FILLCOLOR, "orange");
		ds.put(SHAPE, "rectangle");
		ds.put(STYLE, LineStyle.filled, LineStyle.rounded);

		DemoNode c = CS.builder.findOrCreateNode("C");

		DemoEdge ab = CS.graph.addEdge(a, b);
		ds = ab.getDotStyle();
		ds.put(LABEL, "Edge " + ab.name());
		ds.put(COLOR, "blue");
		ds.put(PENWIDTH, 2);

		DemoEdge bc = CS.graph.addEdge(b, c);
		ds = bc.getDotStyle();
		ds.put(LABEL, "Edge " + bc.name());
		ds.put(ARROWHEAD, "vee");
		ds.put(ARROWTAIL, "inv");
		ds.put(ARROWSIZE, "0.7");
		ds.put(COLOR, "maroon");
		ds.put(FONTSIZE, "11");
		ds.put(FONTCOLOR, "navy");
		ds.put(STYLE, "dashed");

		String dot = CS.graph.render();
		writeResource(getClass(), "dotStyle.md", dot, FORCE);

		String txt = loadResource(getClass(), "dotStyle.md");
		Differ.diff((String) CS.graph.get(Graph.GRAPH_ID), txt, dot).sdiff(true, 120).out();

		assertEquals(txt, dot);
	}
}
