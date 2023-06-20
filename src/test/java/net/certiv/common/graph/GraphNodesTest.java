package net.certiv.common.graph;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import net.certiv.common.diff.Differ;
import net.certiv.common.graph.Edge.Sense;
import net.certiv.common.graph.demo.DemoEdge;
import net.certiv.common.graph.demo.DemoNode;
import net.certiv.common.stores.UniqueList;
import net.certiv.common.util.FsUtil;

class GraphNodesTest extends TestBase {

	DemoNode a;
	DemoNode b;
	DemoNode c;
	DemoNode d;
	DemoNode e;
	DemoNode f;
	DemoNode g;

	@BeforeEach
	void setup() {
		graph.put(Graph.GRAPH_NAME, "Node Test");

		builder.createAndAddEdges("A->B->C->D->E");
		builder.createAndAddEdges("C->F->G");
		builder.createAndAddEdges("C->[B,C,E]");

		a = builder.getNode("A");
		b = builder.getNode("B");
		c = builder.getNode("C");
		d = builder.getNode("D");
		e = builder.getNode("E");
		f = builder.getNode("F");
		g = builder.getNode("G");
	}

	@Test
	void verifyStructure() {
		String dot = graph.render();
		// FsUtil.writeResource(getClass(), "node1.md", dot);

		String txt = FsUtil.loadResource(getClass(), "node1.md").value;
		Differ.diff((String) graph.get(Graph.GRAPH_NAME), dot, txt).sdiff(true, 120).out();
		assertEquals(dot, txt);
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
