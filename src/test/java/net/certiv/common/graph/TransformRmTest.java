package net.certiv.common.graph;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import net.certiv.common.CommonSupport;
import net.certiv.common.diff.Differ;
import net.certiv.common.graph.Edge.Sense;
import net.certiv.common.graph.demo.DemoEdge;
import net.certiv.common.graph.demo.DemoNode;
import net.certiv.common.stores.Result;
import net.certiv.common.stores.UniqueList;
import net.certiv.common.util.test.CommonTestBase;

class TransformRmTest extends CommonTestBase {

	static final boolean FORCE = false;
	private final CommonSupport CS = new CommonSupport();

	@BeforeEach
	public void setup() {
		CS.setup();
		CS.graph.setId(CS.factory.make("RemoveOps"));
		CS.builder.createAndAddEdges("A->B->C->D->E");
		CS.builder.createAndAddEdges("C->F->G");
		CS.builder.createAndAddEdges("C->[B,C,E]");
	}

	@AfterEach
	public void teardown() {
		CS.teardown();
	}

	@Test
	void verifyStructure() {
		String dot = CS.graph.render();
		writeResource(getClass(), CommonSupport.XForm + "Structure.md", dot, FORCE);

		String txt = loadResource(getClass(), CommonSupport.XForm + "Structure.md");
		Differ.diff(CS.graph.displayName(), txt, dot).sdiff(true, 120).out();
		assertEquals(txt, dot);
	}

	@Test
	void testRemoveNode() {
		int cntNodes = CS.graph.size();
		int cntEdges = CS.graph.getEdges().size();

		DemoNode b = CS.builder.getNode("B");

		Transformer<DemoNode, DemoEdge> xf = new Transformer<>(CS.graph);
		Result<Boolean> res = xf.removeNode(b);

		assertTrue(res.valid());
		assertEquals(CS.graph.size(), cntNodes - 2);
		assertEquals(CS.graph.getEdges().size(), cntEdges - 3);
	}

	@Test
	void testRemoveEdge() {
		int nodes = CS.graph.size();
		int edges = CS.graph.getEdges().size();

		DemoEdge ab = CS.builder.getEdges("A", "B").getFirst();

		Transformer<DemoNode, DemoEdge> xf = new Transformer<>(CS.graph);
		Result<Boolean> res = xf.removeEdge(ab, false);

		assertTrue(res.valid());
		assertEquals(CS.graph.size(), nodes - 1);
		assertEquals(CS.graph.getEdges().size(), edges - 1);

		assertTrue(CS.graph.addEdge(ab));
		assertEquals(CS.graph.size(), nodes);
		assertEquals(CS.graph.getEdges().size(), edges);
	}

	@Test
	void testRemoveEdgesCollection() {
		int nodes = CS.graph.size();
		int edges = CS.graph.getEdges().size();

		UniqueList<DemoEdge> bc = CS.builder.getEdges("B", "C");

		Transformer<DemoNode, DemoEdge> xf = new Transformer<>(CS.graph);
		Result<Boolean> res = xf.removeEdges(bc, false);

		assertTrue(res.valid());
		assertEquals(CS.graph.size(), nodes);
		assertEquals(CS.graph.getEdges().size(), edges - 2);
	}

	@Test
	void testRemoveEdgeIf() {
		DemoEdge cc = CS.builder.getEdges("C", "C").getFirst();

		Transformer<DemoNode, DemoEdge> xf = new Transformer<>(CS.graph);
		Result<Boolean> res = xf.removeEdgeIf(cc, false, e -> !e.cyclic());
		assertTrue(res.valid());
		assertTrue(CS.graph.contains(cc));

		res = xf.removeEdgeIf(cc, false, e -> e.cyclic());
		assertTrue(res.valid());
		assertFalse(CS.graph.contains(cc));
	}

	@Test
	void testRemoveEdgesNNBoolean() {
		int nodes = CS.graph.size();
		int edges = CS.graph.getEdges().size();
		DemoNode b = CS.builder.getNode("B");
		DemoNode c = CS.builder.getNode("C");

		Transformer<DemoNode, DemoEdge> xf = new Transformer<>(CS.graph);
		Result<Boolean> res = xf.removeEdges(Sense.OUT, b, c, false);

		assertTrue(res.valid());
		assertEquals(CS.graph.size(), nodes);
		assertEquals(CS.graph.getEdges().size(), edges - 1);
	}

	@Test
	void testRemoveEdgesIf() {
		DemoNode c = CS.builder.getNode("C");
		int edges = CS.builder.getEdges("C", "C").size();

		Transformer<DemoNode, DemoEdge> xf = new Transformer<>(CS.graph);
		Result<Boolean> res = xf.removeEdgesIf(Sense.OUT, c, c, false, e -> e.cyclic());
		assertTrue(res.valid());

		assertEquals(CS.builder.getEdges("C", "C").size(), edges - 1);
	}
}
