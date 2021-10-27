package net.certiv.common.graph;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import net.certiv.common.graph.demo.DemoGraph;
import net.certiv.common.graph.demo.DemoNode;
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
	void testMultiRoots() {
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
	void testCycles() {
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
}
