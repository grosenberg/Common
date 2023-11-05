package net.certiv.common.graph;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import net.certiv.common.TestCommon;
import net.certiv.common.graph.Edge.Sense;
import net.certiv.common.graph.demo.DemoEdge;
import net.certiv.common.graph.demo.DemoNode;
import net.certiv.common.graph.paths.SubGraph;
import net.certiv.common.graph.paths.SubGraphFinder;
import net.certiv.common.stores.UniqueList;

class SubgraphTest extends TestCommon {

	DemoNode a;
	DemoNode b;
	DemoNode c;
	DemoNode d;
	DemoNode e;
	DemoNode f;
	DemoNode g;
	DemoNode h;
	DemoNode j;

	DemoNode m;
	DemoNode n;
	DemoNode o;
	DemoNode q;
	DemoNode r;
	DemoNode s;
	DemoNode t;
	DemoNode v;

	SubGraphFinder<DemoNode, DemoEdge> sgf;
	// String dot;

	@BeforeEach
	void setUp() {
		builder.createAndAddEdges("A->B->C->D->E");
		builder.createAndAddEdges("C->F->G");
		builder.createAndAddEdges("C->[B,C,E]");
		builder.createAndAddEdges("D->H->I->J");

		builder.createAndAddEdges("M->N->O->P->Q");
		builder.createAndAddEdges("O->R->S");
		builder.createAndAddEdges("P->T->U->V");

		a = builder.getNode("A");
		b = builder.getNode("B");
		c = builder.getNode("C");
		d = builder.getNode("D");
		e = builder.getNode("E");
		f = builder.getNode("F");
		g = builder.getNode("G");
		h = builder.getNode("H");
		j = builder.getNode("J");

		m = builder.getNode("M");
		n = builder.getNode("N");
		o = builder.getNode("O");
		q = builder.getNode("Q");
		r = builder.getNode("S");
		s = builder.getNode("S");
		t = builder.getNode("T");
		v = builder.getNode("V");

		f.put(MARK, "M");
		h.put(MARK, "M");

		r.put(MARK, "M");
		t.put(MARK, "M");

		sgf = SubGraphFinder.in(graph);
		// dot = graph.render();
	}

	@Test
	void testFind() {
		SubGraph<DemoNode, DemoEdge> sg = sgf.find();

		assertEquals(2, sg.size());
		assertEquals(21, sg.stream().mapToInt(p -> p.size()).sum());
		assertEquals(Set.of(e, g, j), Set.copyOf(sg.getPath(a).terminals()));
		assertEquals(Set.of(e, g, j, q, s, v), Set.copyOf(sg.terminals()));
	}

	@Test
	void testFindA() {
		SubGraph<DemoNode, DemoEdge> sg = sgf.find(a);

		assertEquals(1, sg.size());
		assertEquals(12, sg.getPath(a).size());
		assertEquals(Set.of(e, g, j), Set.copyOf(sg.getPath(a).terminals()));
		assertEquals(Set.of(e, g, j), Set.copyOf(sg.terminals()));
	}

	@Test
	void testFindC() {
		SubGraph<DemoNode, DemoEdge> sg = sgf.find(c);

		assertEquals(11, sg.getPath(c).size());
	}

	@Test
	void testFindD() {
		builder.createAndAddEdges("D->[X,Y]->Z");

		SubGraph<DemoNode, DemoEdge> sg = sgf.find(d);

		assertEquals(8, sg.getPath(d).size());
	}

	@Test
	void testFindEndPredicate() {
		List<DemoNode> stops = List.of(d, f);

		SubGraph<DemoNode, DemoEdge> sg = sgf //
				.begin(n -> n.equals(b)) //
				.end(n -> stops.contains(n)) //
				.find(a);

		assertEquals(1, sg.size());
		assertEquals(6, sg.getPath(b).size());
	}

	@Test
	void testFindPredicates() {
		List<DemoNode> starts = List.of(b, n);
		List<DemoNode> stops = List.of(d, f);

		SubGraph<DemoNode, DemoEdge> sg = sgf //
				.begin(n -> starts.contains(n)) //
				.include(n -> n.get(MARK, "").isEmpty()) //
				.end(n -> stops.contains(n)) //
				.whilst(n -> n.name().matches("[A-N]")) //
				.find();

		assertEquals(1, sg.size());
		assertEquals(5, sg.getPath(b).size());
	}

	@Test
	void testFindTerminals() {
		List<DemoNode> stops = List.of(d, f);

		SubGraph<DemoNode, DemoEdge> sg = sgf //
				.begin(n -> n.equals(b)) //
				.end(n -> stops.contains(n)) //
				.find();

		assertEquals(2, sg.terminals().size());

		Set<DemoNode> terminals = Set.copyOf(sg.getPath(b).terminals());
		assertEquals(2, terminals.size());
		assertEquals(Set.of(e, f), terminals);
	}

	@Test
	void testFindShortestBE() {
		SubGraph<DemoNode, DemoEdge> sg = sgf.find(b);
		LinkedList<DemoEdge> shortest = sg.getPath(b).shortestPathTo(e);
		assertEquals(shortest.size(), 2);

		UniqueList<DemoEdge> actual = graph.getEdges(Sense.OUT, b, c).dup();
		actual.addAll(graph.getEdges(Sense.OUT, c, e));
		assertEquals(actual, shortest);
	}

	@Test
	void testFindShortestAE() {
		SubGraph<DemoNode, DemoEdge> sg = sgf.find(a);
		LinkedList<DemoEdge> shortest = sg.getPath(a).shortestPathTo(e);
		assertEquals(shortest.size(), 3);

		UniqueList<DemoEdge> actual = graph.getEdges(Sense.OUT, a, b).dup();
		actual.addAll(graph.getEdges(Sense.OUT, b, c));
		actual.addAll(graph.getEdges(Sense.OUT, c, e));
		assertEquals(actual, shortest);
	}

}
