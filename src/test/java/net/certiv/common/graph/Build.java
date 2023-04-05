package net.certiv.common.graph;

import static org.junit.Assert.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import net.certiv.common.diff.Differ;
import net.certiv.common.graph.demo.DemoNode;
import net.certiv.common.util.FsUtil;

class Build extends TestBase {

	@Test
	void testGraph() {
		graph.put(Graph.GRAPH_NAME, "Build Minimal");

		graph.createAndAddEdge("A", "B");
		graph.createAndAddEdge("B", "C");

		DemoNode a = graph.getNode("A");
		DemoNode b = graph.getNode("B");
		DemoNode c = graph.getNode("C");

		assertNotNull(a);
		assertNotNull(b);
		assertNotNull(c);

		String dot = graph.dot();
		String txt = FsUtil.loadCheckedResource(getClass(), "build0.md").result;
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

		String txt = FsUtil.loadCheckedResource(getClass(), "build1.md").result;
		Differ.diff((String) graph.get(Graph.GRAPH_NAME), dot, txt).sdiff(true, 120);
		assertEquals(dot, txt);

		String dtxt = FsUtil.loadCheckedResource(getClass(), "build1dump.txt").result;
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

		String txt = FsUtil.loadCheckedResource(getClass(), "build2.md").result;
		Differ.diff((String) graph.get(Graph.GRAPH_NAME), dot, txt).sdiff(true, 120);
		assertEquals(dot, txt);

		String dtxt = FsUtil.loadCheckedResource(getClass(), "build2dump.txt").result;
		assertEquals(dump, dtxt);
	}

	@Test
	void testCyclesSelf() {
		graph.put(Graph.GRAPH_NAME, "Cyclic");

		graph.createAndAddEdge("A", "B");
		graph.createAndAddEdge("B", "B");
		graph.createAndAddEdge("B", "C");

		String dot = graph.dot();

		String txt = FsUtil.loadCheckedResource(getClass(), "build3.md").result;
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

		String txt = FsUtil.loadCheckedResource(getClass(), "build4.md").result;
		Differ.diff((String) graph.get(Graph.GRAPH_NAME), dot, txt).sdiff(true, 120);
		assertEquals(dot, txt);

		String dtxt = FsUtil.loadCheckedResource(getClass(), "build4dump.txt").result;
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

		String txt = FsUtil.loadCheckedResource(getClass(), "build5.md").result;
		Differ.diff((String) graph.get(Graph.GRAPH_NAME), dot, txt).sdiff(true, 120);
		assertEquals(dot, txt);
	}

	@Test
	void testBuilder() {
		graph.put(Graph.GRAPH_NAME, "Builder");

		builder.createAndAddEdges("[A,B] => C => [Delta, Eta] -> [Z]");

		String dot = graph.dot();
		String txt = FsUtil.loadCheckedResource(getClass(), "build6.md").result;
		Differ.diff((String) graph.get(Graph.GRAPH_NAME), dot, txt).sdiff(true, 120);
		assertEquals(dot, txt);
	}

	@Test
	void testBuilderMulti() {
		graph.put(Graph.GRAPH_NAME, "Builder Multi");

		builder.createAndAddEdges("Root -> [A,B]");
		builder.createAndAddEdges("[A,B] => C => [Delta, Eta] -> [Z]");
		builder.createAndAddEdges("C => [B,Z]");
		builder.createAndAddEdges("C => [B,C]");

		String dot = graph.dot();
		String txt = FsUtil.loadCheckedResource(getClass(), "build7.md").result;
		Differ.diff((String) graph.get(Graph.GRAPH_NAME), dot, txt).sdiff(true, 120);
		assertEquals(dot, txt);
	}
}
