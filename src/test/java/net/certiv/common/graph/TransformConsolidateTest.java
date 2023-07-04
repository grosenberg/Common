package net.certiv.common.graph;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import net.certiv.common.diff.Differ;
import net.certiv.common.graph.demo.DemoEdge;
import net.certiv.common.graph.demo.DemoNode;
import net.certiv.common.stores.Result;
import net.certiv.common.stores.UniqueList;
import net.certiv.common.util.FsUtil;

public class TransformConsolidateTest extends TestBase {

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
		// FsUtil.writeResource(getClass(), XForm + "ConsolidateEnd.md", dot);

		String txt = FsUtil.loadResource(getClass(), XForm + "ConsolidateEnd.md").value;
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
		// FsUtil.writeResource(getClass(), XForm + "ConsolidateMid.md", dot);

		String txt = FsUtil.loadResource(getClass(), XForm + "ConsolidateMid.md").value;
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
		// FsUtil.writeResource(getClass(), XForm + "ConsolidateCyclic.md", dot);

		String txt = FsUtil.loadResource(getClass(), XForm + "ConsolidateCyclic.md").value;
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
		FsUtil.writeResource(getClass(), XForm + "ConsolidateComplex.md", dot);

		String txt = FsUtil.loadResource(getClass(), XForm + "ConsolidateComplex.md").value;
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
		FsUtil.writeResource(getClass(), XForm + "ConsolidateComplexCyclic.md", dot);

		String txt = FsUtil.loadResource(getClass(), XForm + "ConsolidateComplexCyclic.md").value;
		Differ.diff((String) graph.get(Graph.GRAPH_NAME), txt, dot).sdiff(true, 120).out();
		assertEquals(txt, dot);
	}

}
