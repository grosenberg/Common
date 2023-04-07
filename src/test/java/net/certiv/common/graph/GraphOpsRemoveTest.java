package net.certiv.common.graph;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import net.certiv.common.diff.Differ;
import net.certiv.common.graph.demo.DemoEdge;
import net.certiv.common.graph.demo.DemoNode;
import net.certiv.common.stores.UniqueList;
import net.certiv.common.util.FsUtil;

class GraphOpsRemoveTest extends TestBase {

	@BeforeEach
	void testRemoveOps() {
		// Log2.setName("RemoveOps", "../../src/test/resources/logs");

		graph.put(Graph.GRAPH_NAME, "RemoveOps");

		builder.createAndAddEdges("A->B->C->D->E");
		builder.createAndAddEdges("C->F->G");
		builder.createAndAddEdges("C->[B,C,E]");
	}

	@Test
	void verifyStructure() {
		String dot = graph.dot();
		String txt = FsUtil.loadCheckedResource(getClass(), "opsRemove1.md").result;
		Differ.diff((String) graph.get(Graph.GRAPH_NAME), dot, txt).sdiff(true, 120);
		assertEquals(dot, txt);
	}

	@Test
	void testRemoveNode() {
		int cntNodes = graph.size();
		int cntEdges = graph.getEdges().size();

		DemoNode b = builder.getNode("B");

		assertTrue(graph.removeNode(b));
		assertEquals(graph.size(), cntNodes - 2);
		assertEquals(graph.getEdges().size(), cntEdges - 3);
	}

	@Test
	void testRemoveEdge() {
		int nodes = graph.size();
		int edges = graph.getEdges().size();

		DemoEdge ab = builder.getEdges("A", "B").getFirst();
		assertTrue(graph.removeEdge(ab, false));
		assertEquals(graph.size(), nodes - 1);
		assertEquals(graph.getEdges().size(), edges - 1);

		assertTrue(graph.addEdge(ab));
		assertEquals(graph.size(), nodes);
		assertEquals(graph.getEdges().size(), edges);
	}

	@Test
	void testRemoveEdgesCollection() {
		int nodes = graph.size();
		int edges = graph.getEdges().size();

		UniqueList<DemoEdge> bc = builder.getEdges("B", "C");
		assertTrue(graph.removeEdges(bc, false));
		assertEquals(graph.size(), nodes);
		assertEquals(graph.getEdges().size(), edges - 2);
	}

	@Test
	void testRemoveEdgeIf() {
		DemoEdge cc = builder.getEdges("C", "C").getFirst();
		assertFalse(graph.removeEdgeIf(cc, false, e -> !e.cyclic()));
		assertTrue(graph.removeEdgeIf(cc, false, e -> e.cyclic()));
	}

	@Test
	void testRemoveEdgesNNBoolean() {
		int nodes = graph.size();
		int edges = graph.getEdges().size();
		DemoNode b = builder.getNode("B");
		DemoNode c = builder.getNode("C");

		assertTrue(graph.removeEdges(b, c, false));
		assertEquals(graph.size(), nodes);
		assertEquals(graph.getEdges().size(), edges - 1);
	}

	@Test
	void testRemoveEdgesIf() {
		DemoNode c = builder.getNode("C");
		int edges = builder.getEdges("C", "C").size();

		graph.removeEdgesIf(c, c, false, e -> e.cyclic());
		assertEquals(builder.getEdges("C", "C").size(), edges - 1);
	}
}
