package net.certiv.common.graph;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import net.certiv.common.TestCommon;
import net.certiv.common.diff.Differ;
import net.certiv.common.graph.Edge.Sense;
import net.certiv.common.graph.demo.DemoEdge;
import net.certiv.common.graph.demo.DemoNode;
import net.certiv.common.stores.Result;
import net.certiv.common.stores.UniqueList;

class TransformRmTest extends TestCommon {

	static final boolean FORCE = false;

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
		String dot = graph.render();
		writeResource(getClass(), XForm + "Structure.md", dot, FORCE);

		String txt = loadResource(getClass(), XForm + "Structure.md");
		Differ.diff((String) graph.get(Graph.GRAPH_NAME), txt, dot).sdiff(true, 120).out();
		assertEquals(txt, dot);
	}

	@Test
	void testRemoveNode() {
		int cntNodes = graph.size();
		int cntEdges = graph.getEdges().size();

		DemoNode b = builder.getNode("B");

		Transformer<DemoNode, DemoEdge> xf = new Transformer<>(graph);
		Result<Boolean> res = xf.removeNode(b);

		assertTrue(res.valid());
		assertEquals(graph.size(), cntNodes - 2);
		assertEquals(graph.getEdges().size(), cntEdges - 3);
	}

	@Test
	void testRemoveEdge() {
		int nodes = graph.size();
		int edges = graph.getEdges().size();

		DemoEdge ab = builder.getEdges("A", "B").getFirst();

		Transformer<DemoNode, DemoEdge> xf = new Transformer<>(graph);
		Result<Boolean> res = xf.removeEdge(ab, false);

		assertTrue(res.valid());
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

		Transformer<DemoNode, DemoEdge> xf = new Transformer<>(graph);
		Result<Boolean> res = xf.removeEdges(bc, false);

		assertTrue(res.valid());
		assertEquals(graph.size(), nodes);
		assertEquals(graph.getEdges().size(), edges - 2);
	}

	@Test
	void testRemoveEdgeIf() {
		DemoEdge cc = builder.getEdges("C", "C").getFirst();

		Transformer<DemoNode, DemoEdge> xf = new Transformer<>(graph);
		Result<Boolean> res = xf.removeEdgeIf(cc, false, e -> !e.cyclic());
		assertTrue(res.valid());
		assertTrue(graph.contains(cc));

		res = xf.removeEdgeIf(cc, false, e -> e.cyclic());
		assertTrue(res.valid());
		assertFalse(graph.contains(cc));
	}

	@Test
	void testRemoveEdgesNNBoolean() {
		int nodes = graph.size();
		int edges = graph.getEdges().size();
		DemoNode b = builder.getNode("B");
		DemoNode c = builder.getNode("C");

		Transformer<DemoNode, DemoEdge> xf = new Transformer<>(graph);
		Result<Boolean> res = xf.removeEdges(Sense.OUT, b, c, false);

		assertTrue(res.valid());
		assertEquals(graph.size(), nodes);
		assertEquals(graph.getEdges().size(), edges - 1);
	}

	@Test
	void testRemoveEdgesIf() {
		DemoNode c = builder.getNode("C");
		int edges = builder.getEdges("C", "C").size();

		Transformer<DemoNode, DemoEdge> xf = new Transformer<>(graph);
		Result<Boolean> res = xf.removeEdgesIf(Sense.OUT, c, c, false, e -> e.cyclic());
		assertTrue(res.valid());

		assertEquals(builder.getEdges("C", "C").size(), edges - 1);
	}
}
