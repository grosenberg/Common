package net.certiv.common.graph;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import net.certiv.common.CommonSupport;
import net.certiv.common.graph.Edge.Sense;
import net.certiv.common.graph.demo.DemoEdge;
import net.certiv.common.graph.demo.DemoNode;
import net.certiv.common.graph.id.Id;
import net.certiv.common.graph.paths.SubGraph;
import net.certiv.common.graph.paths.SubGraphFinder;
import net.certiv.common.stores.UniqueList;
import net.certiv.common.util.test.CommonTestBase;

class SubgraphTest extends CommonTestBase {

	private final CommonSupport CS = new CommonSupport();

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

	SubGraphFinder<Id, DemoNode, DemoEdge> sgf;
	// String dot;

	@BeforeEach
	public void setup() {
		CS.setup();
		CS.builder.createAndAddEdges("A->B->C->D->E");
		CS.builder.createAndAddEdges("C->F->G");
		CS.builder.createAndAddEdges("C->[B,C,E]");
		CS.builder.createAndAddEdges("D->H->I->J");

		CS.builder.createAndAddEdges("M->N->O->P->Q");
		CS.builder.createAndAddEdges("O->R->S");
		CS.builder.createAndAddEdges("P->T->U->V");

		a = CS.builder.getNode("A");
		b = CS.builder.getNode("B");
		c = CS.builder.getNode("C");
		d = CS.builder.getNode("D");
		e = CS.builder.getNode("E");
		f = CS.builder.getNode("F");
		g = CS.builder.getNode("G");
		h = CS.builder.getNode("H");
		j = CS.builder.getNode("J");

		m = CS.builder.getNode("M");
		n = CS.builder.getNode("N");
		o = CS.builder.getNode("O");
		q = CS.builder.getNode("Q");
		r = CS.builder.getNode("S");
		s = CS.builder.getNode("S");
		t = CS.builder.getNode("T");
		v = CS.builder.getNode("V");

		f.put(CommonSupport.MARK, "M");
		h.put(CommonSupport.MARK, "M");

		r.put(CommonSupport.MARK, "M");
		t.put(CommonSupport.MARK, "M");

		sgf = SubGraphFinder.in(CS.graph);
		// dot = CS.graph.render();
	}

	@AfterEach
	public void teardown() {
		CS.teardown();
	}

	@Test
	void testFind() {
		SubGraph<Id, DemoNode, DemoEdge> sg = sgf.find();

		assertEquals(2, sg.size());
		assertEquals(21, sg.stream().mapToInt(p -> p.size()).sum());
		assertEquals(Set.of(e, g, j), Set.copyOf(sg.getPath(a).terminals()));
		assertEquals(Set.of(e, g, j, q, s, v), Set.copyOf(sg.terminals()));
	}

	@Test
	void testFindA() {
		SubGraph<Id, DemoNode, DemoEdge> sg = sgf.find(a);

		assertEquals(1, sg.size());
		assertEquals(12, sg.getPath(a).size());
		assertEquals(Set.of(e, g, j), Set.copyOf(sg.getPath(a).terminals()));
		assertEquals(Set.of(e, g, j), Set.copyOf(sg.terminals()));
	}

	@Test
	void testFindC() {
		SubGraph<Id, DemoNode, DemoEdge> sg = sgf.find(c);

		assertEquals(11, sg.getPath(c).size());
	}

	@Test
	void testFindD() {
		CS.builder.createAndAddEdges("D->[X,Y]->Z");

		SubGraph<Id, DemoNode, DemoEdge> sg = sgf.find(d);

		assertEquals(8, sg.getPath(d).size());
	}

	@Test
	void testFindEndPredicate() {
		List<DemoNode> stops = List.of(d, f);

		SubGraph<Id, DemoNode, DemoEdge> sg = sgf //
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

		SubGraph<Id, DemoNode, DemoEdge> sg = sgf //
				.begin(n -> starts.contains(n)) //
				.include(n -> n.get(CommonSupport.MARK, "").isEmpty()) //
				.end(n -> stops.contains(n)) //
				.whilst(n -> n.name().matches("[A-N]")) //
				.find();

		assertEquals(1, sg.size());
		assertEquals(5, sg.getPath(b).size());
	}

	@Test
	void testFindTerminals() {
		List<DemoNode> stops = List.of(d, f);

		SubGraph<Id, DemoNode, DemoEdge> sg = sgf //
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
		SubGraph<Id, DemoNode, DemoEdge> sg = sgf.find(b);
		LinkedList<DemoEdge> shortest = sg.getPath(b).shortestPathTo(e);
		assertEquals(shortest.size(), 2);

		UniqueList<DemoEdge> actual = CS.graph.getEdges(Sense.OUT, b, c).dup();
		actual.addAll(CS.graph.getEdges(Sense.OUT, c, e));
		assertEquals(actual, shortest);
	}

	@Test
	void testFindShortestAE() {
		SubGraph<Id, DemoNode, DemoEdge> sg = sgf.find(a);
		LinkedList<DemoEdge> shortest = sg.getPath(a).shortestPathTo(e);
		assertEquals(shortest.size(), 3);

		UniqueList<DemoEdge> actual = CS.graph.getEdges(Sense.OUT, a, b).dup();
		actual.addAll(CS.graph.getEdges(Sense.OUT, b, c));
		actual.addAll(CS.graph.getEdges(Sense.OUT, c, e));
		assertEquals(actual, shortest);
	}

}
