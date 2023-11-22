package net.certiv.common.graph;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.List;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import net.certiv.common.CommonSupport;
import net.certiv.common.graph.demo.DemoNode;
import net.certiv.common.stores.UniqueList;
import net.certiv.common.util.test.CommonTestBase;

class FinderTest extends CommonTestBase {

	private final CommonSupport CS = new CommonSupport();

	private DemoNode c;
	private DemoNode d;
	private DemoNode h;
	private DemoNode o;
	private DemoNode t;

	@BeforeEach
	public void setup() {
		CS.setup();
		CS.builder.createAndAddEdges("A->B->C->D->E");
		CS.builder.createAndAddEdges("C->F->G");
		CS.builder.createAndAddEdges("D->H->I->J");

		CS.builder.createAndAddEdges("M->N->O->P->Q");
		CS.builder.createAndAddEdges("O->R->S");
		CS.builder.createAndAddEdges("P->T->U->V");

		c = CS.builder.getNode("C");
		d = CS.builder.getNode("D");
		h = CS.builder.getNode("H");

		o = CS.builder.getNode("O");
		t = CS.builder.getNode("T");

		c.put(CommonSupport.MARK, "M");
		h.put(CommonSupport.MARK, "M");

		o.put(CommonSupport.MARK, "M");
		t.put(CommonSupport.MARK, "M");
	}

	@AfterEach
	public void teardown() {
		CS.teardown();
	}

	@Test
	void testAll() {
		UniqueList<DemoNode> nodes = Finder.in(CS.graph) //
				.include(n -> n.get(CommonSupport.MARK, "").equals("M")) //
				.all();
		assertEquals(List.of(c, h, o, t), nodes);
	}

	@Test
	void testAllWhilst() {
		UniqueList<DemoNode> nodes = Finder.in(CS.graph) //
				.include(n -> n.get(CommonSupport.MARK, "").equals("M")) //
				.whilst(n -> n.name().matches("[A-N]")) //
				.all();
		assertEquals(List.of(c, h), nodes);
	}

	@Test
	void testAllNot() {
		UniqueList<DemoNode> nodes = Finder.in(CS.graph) //
				.include(n -> n.get(CommonSupport.MARK, "").equals("M")) //
				.exclude(n -> n.name().equals(c.name())) //
				.all();
		assertEquals(List.of(h, o, t), nodes);
	}

	@Test
	void testAllNotFrom() {
		UniqueList<DemoNode> nodes = Finder.in(CS.graph) //
				.include(n -> n.get(CommonSupport.MARK, "").equals("M")) //
				.exclude(n -> n.name().equals(c.name())) //
				.all(d);
		assertEquals(List.of(h), nodes);
	}

	@Test
	void testFirst() {
		DemoNode node = Finder.in(CS.graph) //
				.include(n -> n.get(CommonSupport.MARK, "").equals("M")) //
				.first();
		assertEquals(c, node);
	}

	@Test
	void testFirstNot() {
		DemoNode node = Finder.in(CS.graph) //
				.include(n -> n.get(CommonSupport.MARK, "").equals("M")) //
				.exclude(n -> n.name().equals(c.name())) //
				.first();
		assertEquals(h, node);
	}

	@Test
	void testFirstFrom() {
		DemoNode node = Finder.in(CS.graph) //
				.include(n -> n.get(CommonSupport.MARK, "").equals("M")) //
				.first(d);
		assertEquals(h, node);
	}
}
