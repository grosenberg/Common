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

		graph.createEdge("A", "B");
		graph.createEdge("B", "C");

		DemoNode b = graph.getNode("B");
		graph.reduce(b);

		b = graph.getNode("B");
		assertNull(b);

		String dot = graph.dot();
		String txt = FsUtil.loadResourceStringChecked(getClass(), "reduce1.txt");
		Differ.diff((String) graph.get(Graph.GRAPH_NAME), dot, txt).sdiff(true, 120);
		assertEquals(dot, txt);
	}

	@Test
	void testReduceEdge() {
		graph.put(Graph.GRAPH_NAME, "ReduceEdge");

		graph.createEdge("R", "A");
		graph.createEdge("A", "B");
		graph.createEdge("R", "C");
		graph.createEdge("C", "D");

		UniqueDeque<DemoEdge> ab = graph.getEdges("A", "B");
		UniqueDeque<DemoEdge> cd = graph.getEdges("C", "D");

		graph.reduce(ab.getFirst(), cd.getFirst());

		DemoNode b = graph.getNode("B");
		assertNull(b);

		String dot = graph.dot();
		String txt = FsUtil.loadResourceStringChecked(getClass(), "reduceEdge1.txt");
		Differ.diff((String) graph.get(Graph.GRAPH_NAME), dot, txt).sdiff(true, 120);
		assertEquals(dot, txt);
	}

	@Test
	void testTransfer() {
		graph.put(Graph.GRAPH_NAME, "Transfer");

		graph.createEdge("A", "B");
		graph.createEdge("B", "C");
		graph.createEdge("C", "D");
		graph.createEdge("D", "E");

		graph.createEdge("C", "F");
		graph.createEdge("F", "G");

		DemoNode b = graph.getNode("B");
		UniqueDeque<DemoEdge> cf = graph.getEdges("C", "F");

		graph.transfer(cf, b);

		String dot = graph.dot();
		String txt = FsUtil.loadResourceStringChecked(getClass(), "transfer1.txt");

		Differ.diff((String) graph.get(Graph.GRAPH_NAME), dot, txt).sdiff(true, 120);
		assertEquals(dot, txt);
	}
}
