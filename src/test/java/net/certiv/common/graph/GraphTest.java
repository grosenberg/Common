package net.certiv.common.graph;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import net.certiv.common.dot.DotAttr;
import net.certiv.common.dot.DotStyle;
import net.certiv.common.util.FsUtil;

class GraphTest {

	@Test
	void testGraph() {
		DemoGraph graph = new DemoGraph("Test");

		DemoNode a = graph.createNode("A", graph.nextId());
		DemoNode b = graph.createNode("B", graph.nextId());
		DemoNode c = graph.createNode("C", graph.nextId());
		DemoNode d = graph.createNode("D", graph.nextId());
		DemoNode e = graph.createNode("E", graph.nextId());

		graph.createEdge(a, b);
		graph.createEdge(b, c);
		graph.createEdge(b, d);
		graph.createEdge(d, e);

		String dump = graph.dump();
		String dot = graph.dot();

		String dumptxt = FsUtil.loadResourceStringChecked(getClass(), "dump1.txt");
		assertEquals(dump, dumptxt);

		String dottxt = FsUtil.loadResourceStringChecked(getClass(), "dot1.txt");
		assertEquals(dot, dottxt);
	}

	@Test
	void testGraph_2Roots() {
		DemoGraph graph = new DemoGraph("Two-root Test");

		DemoNode a = graph.createNode("A", graph.nextId());
		DemoNode b = graph.createNode("B", graph.nextId());
		DemoNode c = graph.createNode("C", graph.nextId());
		DemoNode d = graph.createNode("D", graph.nextId());
		DemoNode e = graph.createNode("E", graph.nextId());

		graph.createEdge(a, b);
		graph.createEdge(b, c);
		graph.createEdge(d, e);

		String dump = graph.dump();
		String dot = graph.dot();

		String dumptxt = FsUtil.loadResourceStringChecked(getClass(), "dump2.txt");
		assertEquals(dump, dumptxt);

		String dottxt = FsUtil.loadResourceStringChecked(getClass(), "dot2.txt");
		assertEquals(dot, dottxt);
	}

	@Test
	void testGraph_cycles() {
		DemoGraph graph = new DemoGraph("Cycle Test");

		DemoNode a = graph.createNode("A", graph.nextId());
		DemoNode b = graph.createNode("B", graph.nextId());
		DemoNode c = graph.createNode("C", graph.nextId());
		DemoNode d = graph.createNode("D", graph.nextId());
		DemoNode e = graph.createNode("E", graph.nextId());
		DemoNode f = graph.createNode("F", graph.nextId());

		graph.createEdge(a, b);
		graph.createEdge(b, c);
		graph.createEdge(c, d);
		graph.createEdge(d, e);

		graph.createEdge(d, f);
		graph.createEdge(f, b);

		String dump = graph.dump();
		String dot = graph.dot();

		String dumptxt = FsUtil.loadResourceStringChecked(getClass(), "dump3.txt");
		assertEquals(dump, dumptxt);

		String dottxt = FsUtil.loadResourceStringChecked(getClass(), "dot3.txt");
		assertEquals(dot, dottxt);
	}

	@Test
	void testGraph_styles() {
		DemoGraph graph = new DemoGraph("Test Styles");

		DotStyle gs = new DotStyle();
		gs.put(DotAttr.LABEL, graph.name());
		gs.put(DotAttr.FONTCOLOR, "teal");
		gs.put(DotAttr.FONTSIZE, 18);
		graph.putProperty(DotStyle.Property, gs);

		DemoNode a = graph.createNode("A", graph.nextId());

		DotStyle as = new DotStyle();
		as.put(DotAttr.LABEL, "Styled " + a.name());
		as.put(DotAttr.COLOR, "blue");
		as.put(DotAttr.FILLCOLOR, "lightgrey");
		a.putProperty(DotStyle.Property, as);

		DemoNode b = graph.createNode("B", graph.nextId());

		DotStyle bs = new DotStyle();
		bs.put(DotAttr.LABEL, "Styled " + b.name());
		bs.put(DotAttr.COLOR, "red");
		bs.put(DotAttr.FILLCOLOR, "white");
		bs.put(DotAttr.SHAPE, "circle");
		b.putProperty(DotStyle.Property, bs);

		DemoNode c = graph.createNode("C", graph.nextId());

		DemoEdge ab = graph.createEdge(a, b);

		DotStyle abs = new DotStyle();
		abs.put(DotAttr.LABEL, "Styled Edge " + ab.name());
		abs.put(DotAttr.COLOR, "blue");
		abs.put(DotAttr.PENWIDTH, 2);
		ab.putProperty(DotStyle.Property, abs);

		DemoEdge bc = graph.createEdge(b, c);

		DotStyle bcs = new DotStyle();
		bcs.put(DotAttr.LABEL, "Styled Edge " + bc.name());
		bcs.put(DotAttr.ARROWHEAD, "vee");
		bcs.put(DotAttr.ARROWTAIL, "inv");
		bcs.put(DotAttr.ARROWSIZE, "0.7");
		bcs.put(DotAttr.COLOR, "maroon");
		bcs.put(DotAttr.FONTSIZE, "11");
		bcs.put(DotAttr.FONTCOLOR, "navy");
		bcs.put(DotAttr.STYLE, "dotted");
		bc.putProperty(DotStyle.Property, bcs);

		String dot = graph.dot();

		String dottxt = FsUtil.loadResourceStringChecked(getClass(), "dot4.txt");
		assertEquals(dot, dottxt);
	}
}
