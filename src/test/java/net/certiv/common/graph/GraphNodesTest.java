package net.certiv.common.graph;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import net.certiv.common.CommonSupport;
import net.certiv.common.diff.Differ;
import net.certiv.common.graph.Edge.Sense;
import net.certiv.common.graph.demo.DemoEdge;
import net.certiv.common.graph.demo.DemoNode;
import net.certiv.common.stores.UniqueList;
import net.certiv.common.util.test.CommonTestBase;

class GraphNodesTest extends CommonTestBase {

	static final boolean FORCE = false;
	private final CommonSupport CS = new CommonSupport();

	DemoNode a;
	DemoNode b;
	DemoNode c;
	DemoNode d;
	DemoNode e;
	DemoNode f;
	DemoNode g;

	@BeforeEach
	public void setup() {
		CS.setup();
		CS.graph.put(Graph.GRAPH_ID, "Node Test");

		CS.builder.createAndAddEdges("A->B->C->D->E");
		CS.builder.createAndAddEdges("C->F->G");
		CS.builder.createAndAddEdges("C->[B,C,E]");

		a = CS.builder.getNode("A");
		b = CS.builder.getNode("B");
		c = CS.builder.getNode("C");
		d = CS.builder.getNode("D");
		e = CS.builder.getNode("E");
		f = CS.builder.getNode("F");
		g = CS.builder.getNode("G");
	}

	@AfterEach
	public void teardown() {
		CS.teardown();
	}

	@Test
	void verifyStructure() {
		String dot = CS.graph.render();
		writeResource(getClass(), "node1.md", dot, FORCE);

		String txt = loadResource(getClass(), "node1.md");
		Differ.diff((String) CS.graph.get(Graph.GRAPH_ID), txt, dot).sdiff(true, 120).out();
		assertEquals(txt, dot);
	}

	@Test
	void testContains() {
		assertTrue(CS.graph.contains(a));
		DemoNode z = CS.builder.createNode("Z");
		assertFalse(CS.graph.contains(z));
	}

	@Test
	void testIsAdjacent() {
		assertTrue(a.isAdjacent(b));
		assertFalse(a.isAdjacent(c));
	}

	@Test
	void testAdjacent() {
		UniqueList<DemoNode> nodes = c.adjacent();
		assertEquals(nodes.size(), 4);
	}

	@Test
	void testAdjacentSense() {
		UniqueList<DemoNode> nodes = c.adjacent(Sense.IN);
		assertEquals(nodes.size(), 1);
		nodes = c.adjacent(Sense.OUT);
		assertEquals(nodes.size(), 4);
		nodes = c.adjacent(Sense.BOTH);
		assertEquals(nodes.size(), 4);
	}

	@Test
	void testAdjacentSenseBoolean() {
		UniqueList<DemoNode> nodes = c.adjacent(Sense.IN, true);
		assertEquals(nodes.size(), 2);
		nodes = c.adjacent(Sense.OUT, true);
		assertEquals(nodes.size(), 5);
		nodes = c.adjacent(Sense.BOTH, true);
		assertEquals(nodes.size(), 5);
	}

	@Test
	void testAdjacentPredicate() {
		UniqueList<DemoNode> nodes = c.adjacent(Sense.OUT, n -> n.isAdjacent(b));
		assertEquals(nodes.size(), 1);
		nodes = c.adjacent(Sense.OUT, n -> n.isAdjacent(a));
		assertEquals(nodes.size(), 1);
		nodes = c.adjacent(Sense.OUT, n -> !n.isAdjacent(g));
		assertEquals(nodes.size(), 4);
	}

	@Test
	void testAncestorOf() {
		assertTrue(a.ancestorOf(b));
		assertTrue(a.ancestorOf(c));
		assertTrue(c.ancestorOf(b));
		assertFalse(b.ancestorOf(a));
	}

	@Test
	void testTo() {
		UniqueList<DemoEdge> bc = b.to(c);
		UniqueList<DemoEdge> cb = c.to(b);
		UniqueList<DemoEdge> cc = c.to(c);
		UniqueList<DemoEdge> cg = c.to(g);

		assertEquals(bc.size(), 1);
		assertEquals(cb.size(), 1);
		assertNotEquals(bc.getFirst(), cb.getFirst());
		assertEquals(cc.size(), 1);
		assertEquals(cg.size(), 0);
	}

	@Test
	void testFrom() {
		UniqueList<DemoEdge> bc = b.from(c);
		UniqueList<DemoEdge> cb = c.from(b);
		UniqueList<DemoEdge> cc = c.from(c);
		UniqueList<DemoEdge> cd = c.from(d);
		UniqueList<DemoEdge> cg = c.from(g);

		assertEquals(bc.size(), 1);
		assertEquals(cb.size(), 1);
		assertNotEquals(bc.getFirst(), cb.getFirst());
		assertEquals(cc.size(), 1);
		assertEquals(cd.size(), 0);
		assertEquals(cg.size(), 0);
	}

	@Test
	void testIsRoot() {
		assertTrue(a.isRoot());
		assertFalse(e.isRoot());
		assertFalse(f.isRoot());
		assertFalse(g.isRoot());
	}

	@Test
	void testHasEdges() {
		assertFalse(a.hasEdges(Sense.IN, false));
		assertTrue(a.hasEdges(Sense.OUT, false));
	}

	@Test
	void testSize() {
		assertEquals(b.size(), 3);
		assertEquals(c.size(), 6);
		assertEquals(e.size(), 2);
	}

	@Test
	void testSizeSenseBoolean() {
		assertEquals(b.size(Sense.OUT, false), 1);
		assertEquals(c.size(Sense.IN, true), 2);
		assertEquals(c.size(Sense.IN, false), 1);
		assertEquals(c.size(Sense.OUT, true), 5);
		assertEquals(c.size(Sense.OUT, false), 4);
	}

	@Test
	void testSizePredicateOf() {
		assertEquals(c.size(Sense.BOTH, edge -> edge.connectsTo(b)), 2);
	}

	@Test
	void testEdges() {
		UniqueList<DemoEdge> edges = c.edges();
		assertEquals(edges.size(), 5);

		edges = c.edges(Sense.BOTH);
		assertEquals(edges.size(), 5);

		edges = c.edges(Sense.IN);
		assertEquals(edges.size(), 1);

		edges = c.edges(Sense.OUT);
		assertEquals(edges.size(), 4);

		edges = c.edges(Sense.BOTH, true);
		assertEquals(edges.size(), 6);

		edges = c.edges(Sense.BOTH, edge -> edge.connectsTo(b));
		assertEquals(edges.size(), 2);
	}
}
