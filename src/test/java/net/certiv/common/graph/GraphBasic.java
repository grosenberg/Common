package net.certiv.common.graph;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import net.certiv.common.diff.Differ;
import net.certiv.common.util.FsUtil;

class GraphBasic extends TestBase {

	@Test
	void testGraphMinimal() {
		graph.put(Graph.GRAPH_NAME, "Minimal");

		graph.createEdge("A", "B");

		String dot = graph.dot();
		String txt = FsUtil.loadResourceStringChecked(getClass(), "dot0.txt");
		Differ.diff((String) graph.get(Graph.GRAPH_NAME), dot, txt).sdiff(true, 120);
		assertEquals(dot, txt);
	}

	@Test
	void testGraph() {
		graph.put(Graph.GRAPH_NAME, "Test");

		graph.createEdge("A", "B");
		graph.createEdge("B", "C");
		graph.createEdge("B", "D");
		graph.createEdge("D", "E");

		String dump = graph.dump();
		String dot = graph.dot();

		String txt = FsUtil.loadResourceStringChecked(getClass(), "dot1.txt");
		Differ.diff((String) graph.get(Graph.GRAPH_NAME), dot, txt).sdiff(true, 120);
		assertEquals(dot, txt);

		String dtxt = FsUtil.loadResourceStringChecked(getClass(), "dump1.txt");
		assertEquals(dump, dtxt);
	}

	@Test
	void testMultiRoots() {
		graph.put(Graph.GRAPH_NAME, "Two-root Test");

		graph.createEdge("A", "B");
		graph.createEdge("B", "C");
		graph.createEdge("D", "E");

		String dot = graph.dot();
		String dump = graph.dump();

		String txt = FsUtil.loadResourceStringChecked(getClass(), "dot2.txt");
		Differ.diff((String) graph.get(Graph.GRAPH_NAME), dot, txt).sdiff(true, 120);
		assertEquals(dot, txt);

		String dtxt = FsUtil.loadResourceStringChecked(getClass(), "dump2.txt");
		assertEquals(dump, dtxt);
	}

	@Test
	void testCycles() {
		graph.put(Graph.GRAPH_NAME, "Cycle Test");

		graph.createEdge("A", "B");
		graph.createEdge("B", "C");
		graph.createEdge("C", "D");
		graph.createEdge("D", "E");

		graph.createEdge("D", "F");
		graph.createEdge("F", "B");

		String dump = graph.dump();
		String dot = graph.dot();

		String txt = FsUtil.loadResourceStringChecked(getClass(), "dot3.txt");
		Differ.diff((String) graph.get(Graph.GRAPH_NAME), dot, txt).sdiff(true, 120);
		assertEquals(dot, txt);

		String dtxt = FsUtil.loadResourceStringChecked(getClass(), "dump3.txt");
		assertEquals(dump, dtxt);
	}
}
