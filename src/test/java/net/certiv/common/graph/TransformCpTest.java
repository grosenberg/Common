package net.certiv.common.graph;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.LinkedHashMap;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import net.certiv.common.diff.Differ;
import net.certiv.common.graph.algorithms.GraphPath;
import net.certiv.common.graph.algorithms.SubgraphFinder;
import net.certiv.common.graph.demo.DemoEdge;
import net.certiv.common.graph.demo.DemoNode;
import net.certiv.common.stores.Result;
import net.certiv.common.util.FsUtil;

public class TransformCpTest extends TestBase {

	@BeforeEach
	void setup() {
		builder.createAndAddEdges("A->B->C->D->E");
		builder.createAndAddEdges("C->F->G");
		builder.createAndAddEdges("C->[B,C,E]");

		builder.createAndAddEdges("U->X->Y");
		builder.createAndAddEdges("U->Z");
	}

	@Test
	void testCopy() {
		graph.put(Graph.GRAPH_NAME, "Copy");

		DemoNode f = builder.getNode("F");
		DemoNode u = builder.getNode("U");

		SubgraphFinder<DemoNode, DemoEdge> finder = new SubgraphFinder<>(graph);
		LinkedHashMap<DemoNode, GraphPath<DemoNode, DemoEdge>> sg = finder.subset(u);

		Transformer<DemoNode, DemoEdge> xf = new Transformer<>(graph);
		Result<Boolean> res = xf.copy(sg, f, false);
		assertTrue(res.valid());

		String dot = graph.render();
		// FsUtil.writeResource(getClass(), XForm + "Copy.md", dot);

		String txt = FsUtil.loadResource(getClass(), XForm + "Copy.md").value;
		Differ.diff((String) graph.get(Graph.GRAPH_NAME), txt, dot).sdiff(true, 120).out();
		assertEquals(txt, dot);
	}

	@Test
	void testCopyRemove() {
		graph.put(Graph.GRAPH_NAME, "Copy Remove");

		DemoNode f = builder.getNode("F");
		DemoNode u = builder.getNode("U");

		SubgraphFinder<DemoNode, DemoEdge> finder = new SubgraphFinder<>(graph);
		LinkedHashMap<DemoNode, GraphPath<DemoNode, DemoEdge>> sg = finder.subset(u);

		Transformer<DemoNode, DemoEdge> xf = new Transformer<>(graph);
		Result<Boolean> res = xf.copy(sg, f, true);
		assertTrue(res.valid());

		String dot = graph.render();
		// FsUtil.writeResource(getClass(), XForm + "CopyRm.md", dot);

		String txt = FsUtil.loadResource(getClass(), XForm + "CopyRm.md").value;
		Differ.diff((String) graph.get(Graph.GRAPH_NAME), txt, dot).sdiff(true, 120).out();
		assertEquals(txt, dot);
	}

	@Test
	void testCopyRemoveReduce() {
		graph.put(Graph.GRAPH_NAME, "Copy RemoveReduce");

		DemoNode f = builder.getNode("F");
		DemoNode u = builder.getNode("U");

		SubgraphFinder<DemoNode, DemoEdge> finder = new SubgraphFinder<>(graph);
		LinkedHashMap<DemoNode, GraphPath<DemoNode, DemoEdge>> sg = finder.subset(u);

		Transformer<DemoNode, DemoEdge> xf = new Transformer<>(graph);
		Result<Boolean> res = xf.copy(sg, f, true);
		assertTrue(res.valid());

		res = xf.remove(sg, true);
		assertTrue(res.valid());

		String dot = graph.render();
		// FsUtil.writeResource(getClass(), XForm + "CopyRmRd.md", dot);

		String txt = FsUtil.loadResource(getClass(), XForm + "CopyRmRd.md").value;
		Differ.diff((String) graph.get(Graph.GRAPH_NAME), txt, dot).sdiff(true, 120).out();
		assertEquals(txt, dot);
	}
}
