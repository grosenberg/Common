package net.certiv.common.graph;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import net.certiv.common.dot.DotAttr;
import net.certiv.common.dot.DotStyle;
import net.certiv.common.graph.demo.DemoEdge;
import net.certiv.common.graph.demo.DemoGraph;
import net.certiv.common.graph.demo.DemoNode;
import net.certiv.common.util.FsUtil;

class GraphTestDot {

	@Test
	void testStyles() {
		DemoGraph graph = new DemoGraph("Test Dot Styles");
		graph.defineStyle();

		DotStyle ds = graph.getDotStyle();
		ds.put(DotAttr.FONTCOLOR, "teal");
		ds.put(DotAttr.FONTSIZE, 32);

		DemoNode a = graph.createNode("A", graph.nextId());
		ds = a.getDotStyle();
		ds.put(DotAttr.LABEL, "Node " + a.name());
		ds.put(DotAttr.COLOR, "blue");
		ds.put(DotAttr.FILLCOLOR, "lightgrey");

		DemoNode b = graph.createNode("B", graph.nextId());
		ds = b.getDotStyle();
		ds.put(DotAttr.LABEL, "Node " + b.name());
		ds.put(DotAttr.COLOR, "red");
		ds.put(DotAttr.FILLCOLOR, "white");
		ds.put(DotAttr.SHAPE, "circle");

		DemoNode c = graph.createNode("C", graph.nextId());

		DemoEdge ab = graph.createEdge(a, b);
		ds = ab.getDotStyle();
		ds.put(DotAttr.LABEL, "Edge " + ab.name());
		ds.put(DotAttr.COLOR, "blue");
		ds.put(DotAttr.PENWIDTH, 2);

		DemoEdge bc = graph.createEdge(b, c);
		ds = bc.getDotStyle();
		ds.put(DotAttr.LABEL, "Edge " + bc.name());
		ds.put(DotAttr.ARROWHEAD, "vee");
		ds.put(DotAttr.ARROWTAIL, "inv");
		ds.put(DotAttr.ARROWSIZE, "0.7");
		ds.put(DotAttr.COLOR, "maroon");
		ds.put(DotAttr.FONTSIZE, "11");
		ds.put(DotAttr.FONTCOLOR, "navy");
		ds.put(DotAttr.STYLE, "dotted");

		String dot = graph.dot();

		String dottxt = FsUtil.loadResourceStringChecked(getClass(), "dot4.txt");
		assertEquals(dot, dottxt);
	}
}
