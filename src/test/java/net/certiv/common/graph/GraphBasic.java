package net.certiv.common.graph;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import net.certiv.common.diff.Differ;
import net.certiv.common.util.FsUtil;

class GraphBasic extends TestBase {

	@Test
	void testGraphMinimal() {
		graph.put(Graph.GRAPH_NAME, "Minimal");

		graph.createAndAddEdge("A", "B");

		String dot = graph.dot();
		String txt = FsUtil.loadCheckedResource(getClass(), "dotMinimal1.txt").result;
		Differ.diff((String) graph.get(Graph.GRAPH_NAME), dot, txt).sdiff(true, 120);
		assertEquals(dot, txt);
	}

	@Test
	void testGraphSimple() {
		graph.put(Graph.GRAPH_NAME, "Simple");

		graph.createAndAddEdge("A", "B");
		graph.createAndAddEdge("B", "C");
		graph.createAndAddEdge("B", "D");
		graph.createAndAddEdge("D", "E");

		String dump = graph.dump();
		String dot = graph.dot();

		String txt = FsUtil.loadCheckedResource(getClass(), "dotSimple1.txt").result;
		Differ.diff((String) graph.get(Graph.GRAPH_NAME), dot, txt).sdiff(true, 120);
		assertEquals(dot, txt);

		String dtxt = FsUtil.loadCheckedResource(getClass(), "dumpSimple1.txt").result;
		assertEquals(dump, dtxt);
	}

	@Test
	void testMultiRoots() {
		graph.put(Graph.GRAPH_NAME, "Two-root Test");

		graph.createAndAddEdge("A", "B");
		graph.createAndAddEdge("B", "C");
		graph.createAndAddEdge("D", "E");

		String dot = graph.dot();
		String dump = graph.dump();

		String txt = FsUtil.loadCheckedResource(getClass(), "dotMultiRoot1.txt").result;
		Differ.diff((String) graph.get(Graph.GRAPH_NAME), dot, txt).sdiff(true, 120);
		assertEquals(dot, txt);

		String dtxt = FsUtil.loadCheckedResource(getClass(), "dumpMultiRoot1.txt").result;
		assertEquals(dump, dtxt);
	}

	@Test
	void testCyclesSelf() {
		graph.put(Graph.GRAPH_NAME, "Cyclic");

		graph.createAndAddEdge("A", "B");
		graph.createAndAddEdge("B", "B");
		graph.createAndAddEdge("B", "C");

		String dot = graph.dot();

		String txt = FsUtil.loadCheckedResource(getClass(), "dotCyclic1.txt").result;
		Differ.diff((String) graph.get(Graph.GRAPH_NAME), dot, txt).sdiff(true, 120);
		assertEquals(dot, txt);
	}

	@Test
	void testLoops() {
		graph.put(Graph.GRAPH_NAME, "Loop Test");

		graph.createAndAddEdge("A", "B");
		graph.createAndAddEdge("B", "C");
		graph.createAndAddEdge("C", "D");
		graph.createAndAddEdge("D", "E");

		graph.createAndAddEdge("D", "F");
		graph.createAndAddEdge("F", "B");

		String dump = graph.dump();
		String dot = graph.dot();

		String txt = FsUtil.loadCheckedResource(getClass(), "dotLoop1.txt").result;
		Differ.diff((String) graph.get(Graph.GRAPH_NAME), dot, txt).sdiff(true, 120);
		assertEquals(dot, txt);

		String dtxt = FsUtil.loadCheckedResource(getClass(), "dumpLoop1.txt").result;
		assertEquals(dump, dtxt);
	}

	@Test
	void testMultiEdges() {
		graph.put(Graph.GRAPH_NAME, "Multiple Edges Test");

		graph.createAndAddEdge("A", "B");
		graph.createAndAddEdge("B", "C");
		graph.createAndAddEdge("B", "C");
		graph.createAndAddEdge("B", "C");
		graph.createAndAddEdge("B", "C");
		graph.createAndAddEdge("C", "D");

		String dot = graph.dot();

		String txt = FsUtil.loadCheckedResource(getClass(), "dotMultiEdges1.txt").result;
		Differ.diff((String) graph.get(Graph.GRAPH_NAME), dot, txt).sdiff(true, 120);
		assertEquals(dot, txt);
	}
}
