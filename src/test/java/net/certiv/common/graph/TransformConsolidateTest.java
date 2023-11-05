package net.certiv.common.graph;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import net.certiv.common.TestCommon;
import net.certiv.common.diff.Differ;
import net.certiv.common.graph.demo.DemoEdge;
import net.certiv.common.graph.demo.DemoNode;
import net.certiv.common.stores.Result;
import net.certiv.common.stores.UniqueList;

public class TransformConsolidateTest extends TestCommon {

	static final boolean FORCE = false;

	@BeforeEach
	void setup() {
		builder.createAndAddEdges("A->B->C->D->E");
		builder.createAndAddEdges("C->F->G->H->I");
		builder.createAndAddEdges("C->[B,C,E]");

		builder.createAndAddEdges("U->X->Y");
		builder.createAndAddEdges("U->Z");
	}

	@Test
	void testConsolidateEnd() {
		graph.put(Graph.GRAPH_NAME, "Consolidate End");
		nameEdges();

		DemoNode g = builder.getNode("G");
		DemoNode u = builder.getNode("U");

		Transformer<DemoNode, DemoEdge> xf = new Transformer<>(graph);
		xf.consolidateEdges(List.of(g), u);	// edges =>G=> to =>U=>

		String dot = graph.render();
		writeResource(getClass(), XForm + "ConsolidateEnd.md", dot, FORCE);

		String txt = loadResource(getClass(), XForm + "ConsolidateEnd.md");
		Differ.diff((String) graph.get(Graph.GRAPH_NAME), txt, dot).sdiff(true, 120).out();

		assertEquals(txt, dot);
	}

	@Test
	void testConsolidateMid() {
		graph.put(Graph.GRAPH_NAME, "Consolidate Mid");
		nameEdges();

		DemoNode d = builder.getNode("D");
		DemoNode f = builder.getNode("F");
		DemoNode u = builder.getNode("U");

		Transformer<DemoNode, DemoEdge> xf = new Transformer<>(graph);
		Result<Boolean> res = xf.consolidateEdges(List.of(f, u), d);
		assertTrue(res.valid());

		String dot = graph.render();
		writeResource(getClass(), XForm + "ConsolidateMid.md", dot, FORCE);

		String txt = loadResource(getClass(), XForm + "ConsolidateMid.md");
		Differ.diff((String) graph.get(Graph.GRAPH_NAME), txt, dot).sdiff(true, 120).out();
		assertEquals(txt, dot);
	}

	@Test
	void testConsolidateCyclic() {
		setupBase();
		graph.reset();

		graph.put(Graph.GRAPH_NAME, "Consolidate Cyclic");
		builder.createAndAddEdges("A->B->C->D");
		builder.createAndAddEdges("C->C");
		nameEdges();

		DemoNode b = builder.getNode("B");
		DemoNode c = builder.getNode("C");

		Transformer<DemoNode, DemoEdge> xf = new Transformer<>(graph);
		Result<Boolean> res = xf.consolidateEdges(List.of(c), b);
		assertTrue(res.valid());

		String dot = graph.render();
		writeResource(getClass(), XForm + "ConsolidateCyclic.md", dot, FORCE);

		String txt = loadResource(getClass(), XForm + "ConsolidateCyclic.md");
		Differ.diff((String) graph.get(Graph.GRAPH_NAME), txt, dot).sdiff(true, 120).out();
		assertEquals(txt, dot);
	}

	@Test
	void testConsolidateComplex() {
		graph.put(Graph.GRAPH_NAME, "Consolidate Complex");
		nameEdges();

		UniqueList<DemoNode> sources = builder.findNodes("[B,E,H]");
		DemoNode b = builder.getNode("B");

		Transformer<DemoNode, DemoEdge> xf = new Transformer<>(graph);
		xf.consolidateEdges(sources, b); // edges =>[B,E,H]=> to =>B=>

		String dot = graph.render();
		writeResource(getClass(), XForm + "ConsolidateComplex.md", dot, FORCE);

		String txt = loadResource(getClass(), XForm + "ConsolidateComplex.md");
		Differ.diff((String) graph.get(Graph.GRAPH_NAME), txt, dot).sdiff(true, 120).out();
		assertEquals(txt, dot);
	}

	@Test
	void testConsolidateComplexCyclic() {
		graph.put(Graph.GRAPH_NAME, "Consolidate Complex Cyclic");
		nameEdges();

		UniqueList<DemoNode> sources = builder.findNodes("[C,E,F]");
		DemoNode b = builder.getNode("B");

		Transfuture<DemoNode, DemoEdge> xf = new Transfuture<>(graph);
		xf.consolidateEdges(sources, b);
		xf.apply();

		String dot = graph.render();
		writeResource(getClass(), XForm + "ConsolidateComplexCyclic.md", dot, FORCE);

		String txt = loadResource(getClass(), XForm + "ConsolidateComplexCyclic.md");
		Differ.diff((String) graph.get(Graph.GRAPH_NAME), txt, dot).sdiff(true, 120).out();
		assertEquals(txt, dot);
	}

}
