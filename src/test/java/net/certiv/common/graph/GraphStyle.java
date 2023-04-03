package net.certiv.common.graph;

import static net.certiv.common.dot.DotAttr.*;
import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import net.certiv.common.diff.Differ;
import net.certiv.common.dot.DotStyle;
import net.certiv.common.graph.demo.DemoEdge;
import net.certiv.common.graph.demo.DemoNode;
import net.certiv.common.util.FsUtil;

class GraphStyle extends TestBase {

	@Test
	void testStyles() {

		graph.put(Graph.GRAPH_NAME, "Dot Styles");
		graph.defineStyle();

		DotStyle ds = graph.getDotStyle();
		ds.put(FONTCOLOR, "teal");
		ds.put(FONTSIZE, 32);

		DemoNode a = graph.findOrCreateNode("A");
		ds = a.getDotStyle();
		ds.put(LABEL, "Node " + a.name());
		ds.put(COLOR, "blue");
		ds.put(FILLCOLOR, "lightblue");
		ds.put(STYLE, "filled");

		DemoNode b = graph.findOrCreateNode("B");
		ds = b.getDotStyle();
		ds.put(LABEL, "Node " + b.name());
		ds.put(COLOR, "red");
		ds.put(FILLCOLOR, "orange");
		ds.put(SHAPE, "rectangle");
		ds.put(STYLE, "filled, rounded");

		DemoNode c = graph.findOrCreateNode("C");

		DemoEdge ab = graph.createAndAddEdge(a, b);
		ds = ab.getDotStyle();
		ds.put(LABEL, "Edge " + ab.name());
		ds.put(COLOR, "blue");
		ds.put(PENWIDTH, 2);

		DemoEdge bc = graph.createAndAddEdge(b, c);
		ds = bc.getDotStyle();
		ds.put(LABEL, "Edge " + bc.name());
		ds.put(ARROWHEAD, "vee");
		ds.put(ARROWTAIL, "inv");
		ds.put(ARROWSIZE, "0.7");
		ds.put(COLOR, "maroon");
		ds.put(FONTSIZE, "11");
		ds.put(FONTCOLOR, "navy");
		ds.put(STYLE, "dashed");

		String dot = graph.dot();
		String txt = FsUtil.loadCheckedResource(getClass(), "dotStyle.txt").result;
		Differ.diff((String) graph.get(Graph.GRAPH_NAME), dot, txt).sdiff(true, 120);

		assertEquals(dot, txt);
	}
}
