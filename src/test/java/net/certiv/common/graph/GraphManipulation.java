package net.certiv.common.graph;

import static org.junit.Assert.assertNull;
import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import net.certiv.common.diff.Differ;
import net.certiv.common.graph.demo.DemoEdge;
import net.certiv.common.graph.demo.DemoNode;
import net.certiv.common.stores.UniqueDeque;
import net.certiv.common.util.FsUtil;

class GraphManipulation extends TestBase {

	@Test
	void testReduceNode() {
		graph.put(Graph.GRAPH_NAME, "Reduce");

		graph.createAndAddEdge("A", "B");
		graph.createAndAddEdge("B", "C");

		DemoNode b = graph.getNode("B");
		graph.reduce(b);

		b = graph.getNode("B");
		assertNull(b);

		String dot = graph.dot();
		String txt = FsUtil.loadCheckedResource(getClass(), "reduce1.txt").result;
		Differ.diff((String) graph.get(Graph.GRAPH_NAME), dot, txt).sdiff(true, 120);
		assertEquals(dot, txt);
	}

	@Test
	void testReduceEdge() {
		graph.put(Graph.GRAPH_NAME, "ReduceEdge");

		graph.createAndAddEdge("R", "A");
		graph.createAndAddEdge("A", "B");
		graph.createAndAddEdge("R", "C");
		graph.createAndAddEdge("C", "D");

		UniqueDeque<DemoEdge> ab = graph.getEdges("A", "B");
		UniqueDeque<DemoEdge> cd = graph.getEdges("C", "D");

		graph.reduce(ab.getFirst(), cd.getFirst());

		DemoNode b = graph.getNode("B");
		assertNull(b);

		String dot = graph.dot();
		String txt = FsUtil.loadCheckedResource(getClass(), "reduceEdge1.txt").result;
		Differ.diff((String) graph.get(Graph.GRAPH_NAME), dot, txt).sdiff(true, 120);
		assertEquals(dot, txt);
	}

	@Test
	void testTransfer() {
		graph.put(Graph.GRAPH_NAME, "Transfer");

		graph.createAndAddEdge("A", "B");
		graph.createAndAddEdge("B", "C");
		graph.createAndAddEdge("C", "D");
		graph.createAndAddEdge("D", "E");

		graph.createAndAddEdge("C", "F");
		graph.createAndAddEdge("F", "G");

		DemoNode b = graph.getNode("B");
		UniqueDeque<DemoEdge> cf = graph.getEdges("C", "F");

		graph.transfer(cf, b);

		String dot = graph.dot();
		String txt = FsUtil.loadCheckedResource(getClass(), "transfer1.txt").result;

		Differ.diff((String) graph.get(Graph.GRAPH_NAME), dot, txt).sdiff(true, 120);
		assertEquals(dot, txt);
	}
}
