package net.certiv.common.graph;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import net.certiv.common.graph.demo.DemoNode;
import net.certiv.common.stores.UniqueList;
import net.certiv.common.stores.context.Key;

class FinderTest extends TestGraphBase {

	private static final Key<String> MARK = Key.of("mark");

	private DemoNode c;
	private DemoNode d;
	private DemoNode h;
	private DemoNode o;
	private DemoNode t;

	@BeforeEach
	void setUp() {
		builder.createAndAddEdges("A->B->C->D->E");
		builder.createAndAddEdges("C->F->G");
		builder.createAndAddEdges("D->H->I->J");

		builder.createAndAddEdges("M->N->O->P->Q");
		builder.createAndAddEdges("O->R->S");
		builder.createAndAddEdges("P->T->U->V");

		c = builder.getNode("C");
		d = builder.getNode("D");
		h = builder.getNode("H");

		o = builder.getNode("O");
		t = builder.getNode("T");

		c.put(MARK, "M");
		h.put(MARK, "M");

		o.put(MARK, "M");
		t.put(MARK, "M");
	}

	@Test
	void testAll() {
		UniqueList<DemoNode> nodes = Finder.in(graph) //
				.include(n -> n.get(MARK, "").equals("M")) //
				.all();
		assertEquals(List.of(c, h, o, t), nodes);
	}

	@Test
	void testAllWhilst() {
		UniqueList<DemoNode> nodes = Finder.in(graph) //
				.include(n -> n.get(MARK, "").equals("M")) //
				.whilst(n -> n.name().matches("[A-N]")) //
				.all();
		assertEquals(List.of(c, h), nodes);
	}

	@Test
	void testAllNot() {
		UniqueList<DemoNode> nodes = Finder.in(graph) //
				.include(n -> n.get(MARK, "").equals("M")) //
				.exclude(n -> n.name().equals(c.name())) //
				.all();
		assertEquals(List.of(h, o, t), nodes);
	}

	@Test
	void testAllNotFrom() {
		UniqueList<DemoNode> nodes = Finder.in(graph) //
				.include(n -> n.get(MARK, "").equals("M")) //
				.exclude(n -> n.name().equals(c.name())) //
				.all(d);
		assertEquals(List.of(h), nodes);
	}

	@Test
	void testFirst() {
		DemoNode node = Finder.in(graph) //
				.include(n -> n.get(MARK, "").equals("M")) //
				.first();
		assertEquals(c, node);
	}

	@Test
	void testFirstNot() {
		DemoNode node = Finder.in(graph) //
				.include(n -> n.get(MARK, "").equals("M")) //
				.exclude(n -> n.name().equals(c.name())) //
				.first();
		assertEquals(h, node);
	}

	@Test
	void testFirstFrom() {
		DemoNode node = Finder.in(graph) //
				.include(n -> n.get(MARK, "").equals("M")) //
				.first(d);
		assertEquals(h, node);
	}
}
