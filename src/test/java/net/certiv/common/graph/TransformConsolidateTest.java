package net.certiv.common.graph;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import net.certiv.common.CommonSupport;
import net.certiv.common.diff.Differ;
import net.certiv.common.graph.demo.DemoEdge;
import net.certiv.common.graph.demo.DemoNode;
import net.certiv.common.graph.id.Id;
import net.certiv.common.stores.Result;
import net.certiv.common.stores.UniqueList;
import net.certiv.common.util.test.CommonTestBase;

public class TransformConsolidateTest extends CommonTestBase {

	static final boolean FORCE = false;
	private final CommonSupport CS = new CommonSupport();

	@BeforeEach
	public void setup() {
		CS.setup();
	}

	@AfterEach
	public void teardown() {
		CS.teardown();
	}

	@Test
	void testConsolidateEnd() {
		CS.graph.setId(CS.factory.make("Consolidate End"));
		CS.createMultiRootNetwork();

		DemoNode g = CS.builder.getNode("G");
		DemoNode u = CS.builder.getNode("U");

		Transformer<Id, DemoNode, DemoEdge> xf = new Transformer<>(CS.graph);
		xf.consolidateEdges(List.of(g), u);	// edges =>G=> to =>U=>

		String dot = CS.graph.render();
		writeResource(getClass(), CommonSupport.XForm + "ConsolidateEnd.md", dot, FORCE);

		String txt = loadResource(getClass(), CommonSupport.XForm + "ConsolidateEnd.md");
		Differ.diff(CS.graph.displayName(), txt, dot).sdiff(true, 120).out();

		assertEquals(txt, dot);
	}

	@Test
	void testConsolidateMid() {
		CS.graph.setId(CS.factory.make("Consolidate Mid"));
		CS.createMultiRootNetwork();

		DemoNode d = CS.builder.getNode("D");
		DemoNode f = CS.builder.getNode("F");
		DemoNode u = CS.builder.getNode("U");

		Transformer<Id, DemoNode, DemoEdge> xf = new Transformer<>(CS.graph);
		Result<Boolean> res = xf.consolidateEdges(List.of(f, u), d);
		assertTrue(res.valid());

		String dot = CS.graph.render();
		writeResource(getClass(), CommonSupport.XForm + "ConsolidateMid.md", dot, FORCE);

		String txt = loadResource(getClass(), CommonSupport.XForm + "ConsolidateMid.md");
		Differ.diff(CS.graph.displayName(), txt, dot).sdiff(true, 120).out();
		assertEquals(txt, dot);
	}

	@Test
	void testConsolidateCyclic() {
		CS.graph.setId(CS.factory.make("Consolidate Cyclic"));
		CS.createMinimalCyclicNetwork();

		DemoNode b = CS.builder.getNode("B");
		DemoNode c = CS.builder.getNode("C");

		Transformer<Id, DemoNode, DemoEdge> xf = new Transformer<>(CS.graph);
		Result<Boolean> res = xf.consolidateEdges(List.of(c), b);
		assertTrue(res.valid());

		String dot = CS.graph.render();
		writeResource(getClass(), CommonSupport.XForm + "ConsolidateCyclic.md", dot, FORCE);

		String txt = loadResource(getClass(), CommonSupport.XForm + "ConsolidateCyclic.md");
		Differ.diff(CS.graph.displayName(), txt, dot).sdiff(true, 120).out();
		assertEquals(txt, dot);
	}

	@Test
	void testConsolidateComplex() {
		CS.graph.setId(CS.factory.make("Consolidate Complex"));
		CS.createMultiRootNetwork();

		UniqueList<DemoNode> sources = CS.builder.findNodes("[B,E,H]");
		DemoNode b = CS.builder.getNode("B");

		Transformer<Id, DemoNode, DemoEdge> xf = new Transformer<>(CS.graph);
		xf.consolidateEdges(sources, b); // edges =>[B,E,H]=> to =>B=>

		String dot = CS.graph.render();
		writeResource(getClass(), CommonSupport.XForm + "ConsolidateComplex.md", dot, FORCE);

		String txt = loadResource(getClass(), CommonSupport.XForm + "ConsolidateComplex.md");
		Differ.diff(CS.graph.displayName(), txt, dot).sdiff(true, 120).out();
		assertEquals(txt, dot);
	}

	@Test
	void testConsolidateComplexCyclic() {
		CS.graph.setId(CS.factory.make("Consolidate Complex Cyclic"));
		CS.createMultiRootNetwork();

		UniqueList<DemoNode> sources = CS.builder.findNodes("[C,E,F]");
		DemoNode b = CS.builder.getNode("B");

		Transfuture<Id, DemoNode, DemoEdge> xf = new Transfuture<>(CS.graph);
		xf.consolidateEdges(sources, b);
		xf.apply();

		String dot = CS.graph.render();
		writeResource(getClass(), CommonSupport.XForm + "ConsolidateComplexCyclic.md", dot, FORCE);

		String txt = loadResource(getClass(), CommonSupport.XForm + "ConsolidateComplexCyclic.md");
		Differ.diff(CS.graph.displayName(), txt, dot).sdiff(true, 120).out();
		assertEquals(txt, dot);
	}
}
