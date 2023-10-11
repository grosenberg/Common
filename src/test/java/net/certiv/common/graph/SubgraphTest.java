package net.certiv.common.graph;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import net.certiv.common.graph.Edge.Sense;
import net.certiv.common.graph.algorithms.GraphPath;
import net.certiv.common.graph.algorithms.PathFinder;
import net.certiv.common.graph.demo.DemoEdge;
import net.certiv.common.graph.demo.DemoNode;
import net.certiv.common.stores.UniqueList;

class SubgraphTest extends TestGraphBase {

	static final boolean FORCE = false;

	DemoNode a;
	DemoNode b;
	DemoNode c;
	DemoNode d;
	DemoNode e;
	DemoNode f;
	DemoNode g;

	PathFinder<DemoNode, DemoEdge> finder;

	@BeforeEach
	void setUp() {
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

		finder = new PathFinder<>(graph);
	}

	@AfterEach
	void tearDown() {
		finder.clear();
		finder = null;
	}

	@Test
	void testSubsetA() {
		LinkedHashMap<DemoNode, GraphPath<DemoNode, DemoEdge>> subgraph = finder.subset(a);

		assertEquals(subgraph.size(), 1);
		assertEquals(subgraph.get(a).size(), 9);
	}

	@Test
	void testSubsetB() {
		LinkedHashMap<DemoNode, GraphPath<DemoNode, DemoEdge>> subgraph = finder.subset(c);

		assertEquals(subgraph.size(), 1);
		assertEquals(subgraph.get(c).size(), 8);
	}

	@Test
	void testSubsetC() {
		builder.createAndAddEdges("D->[X,Y]->Z");

		LinkedHashMap<DemoNode, GraphPath<DemoNode, DemoEdge>> subgraph = finder.subset(d);

		assertEquals(subgraph.size(), 1);
		assertEquals(subgraph.get(d).size(), 5);
	}

	@Test
	void testSubsetEndPredicate() {
		List<DemoNode> stops = List.of(d, f);

		LinkedHashMap<DemoNode, GraphPath<DemoNode, DemoEdge>> subgraph = finder //
				.subset(a, n -> n.equals(b), n -> stops.contains(n));

		assertEquals(subgraph.size(), 1);
		assertEquals(subgraph.get(b).size(), 6);
	}

	@Test
	void testSubsetTerminals() {
		List<DemoNode> stops = List.of(d, f);

		LinkedHashMap<DemoNode, GraphPath<DemoNode, DemoEdge>> subgraph = finder //
				.subset(a, n -> n.equals(b), n -> stops.contains(n));

		List<DemoNode> terminals = subgraph.get(b).terminals().dup();
		assertEquals(terminals.size(), 3);

		terminals.removeAll(List.of(d, e, f));
		assertTrue(terminals.isEmpty());
	}

	@Test
	void testSubsetShortestBE() {
		UniqueList<DemoEdge> actual = graph.getEdges(Sense.OUT, b, c).dup();
		actual.addAll(graph.getEdges(Sense.OUT, c, e));

		LinkedHashMap<DemoNode, GraphPath<DemoNode, DemoEdge>> subgraph = finder.subset(b);
		LinkedList<DemoEdge> shortest = subgraph.get(b).shortestPathTo(e);

		assertEquals(shortest.size(), 2);
		shortest.removeAll(actual);
		assertTrue(shortest.isEmpty());
	}

	@Test
	void testSubsetShortestAE() {
		UniqueList<DemoEdge> actual = graph.getEdges(Sense.OUT, a, b).dup();
		actual.addAll(graph.getEdges(Sense.OUT, b, c));
		actual.addAll(graph.getEdges(Sense.OUT, c, e));

		LinkedHashMap<DemoNode, GraphPath<DemoNode, DemoEdge>> subgraph = finder.subset(a);
		LinkedList<DemoEdge> shortest = subgraph.get(a).shortestPathTo(e);

		assertEquals(shortest.size(), 3);
		shortest.removeAll(actual);
		assertTrue(shortest.isEmpty());
	}

}
