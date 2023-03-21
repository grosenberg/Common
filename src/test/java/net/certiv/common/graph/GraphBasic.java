package net.certiv.common.graph;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import net.certiv.common.graph.demo.DemoGraph;
import net.certiv.common.graph.demo.DiffUtil;
import net.certiv.common.util.FsUtil;

class GraphBasic {

	@Test
	void testGraphMinimal() {
		DemoGraph graph = new DemoGraph("Test");

		graph.createEdge("A", "B");

		String dot = graph.dot();
		String txt = FsUtil.loadResourceStringChecked(getClass(), "dot0.txt");

		System.out.println(DiffUtil.diff(dot, txt));

		assertEquals(dot, txt);
	}

	@Test
	void testGraph() {
		DemoGraph graph = new DemoGraph("Test");

		graph.createEdge("A", "B");
		graph.createEdge("B", "C");
		graph.createEdge("B", "D");
		graph.createEdge("D", "E");

		String dump = graph.dump();
		String dot = graph.dot();

		String dumptxt = FsUtil.loadResourceStringChecked(getClass(), "dump1.txt");
		String dottxt = FsUtil.loadResourceStringChecked(getClass(), "dot1.txt");

		System.out.println(DiffUtil.diff(dot, dottxt));

		assertEquals(dump, dumptxt);
		assertEquals(dot, dottxt);
	}

	@Test
	void testMultiRoots() {
		DemoGraph graph = new DemoGraph("Two-root Test");

		graph.createEdge("A", "B");
		graph.createEdge("B", "C");
		graph.createEdge("D", "E");

		String dump = graph.dump();
		String dot = graph.dot();

		String dumptxt = FsUtil.loadResourceStringChecked(getClass(), "dump2.txt");
		assertEquals(dump, dumptxt);

		String dottxt = FsUtil.loadResourceStringChecked(getClass(), "dot2.txt");
		System.out.println(DiffUtil.diff(dot, dottxt));
		assertEquals(dot, dottxt);
	}

	@Test
	void testCycles() {
		DemoGraph graph = new DemoGraph("Cycle Test");

		graph.createEdge("A", "B");
		graph.createEdge("B", "C");
		graph.createEdge("C", "D");
		graph.createEdge("D", "E");

		graph.createEdge("D", "F");
		graph.createEdge("F", "B");

		String dump = graph.dump();
		String dot = graph.dot();

		String dumptxt = FsUtil.loadResourceStringChecked(getClass(), "dump3.txt");
		assertEquals(dump, dumptxt);

		String dottxt = FsUtil.loadResourceStringChecked(getClass(), "dot3.txt");
		System.out.println(DiffUtil.diff(dot, dottxt));
		assertEquals(dot, dottxt);
	}
}
