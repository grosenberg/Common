package net.certiv.common.graph;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.LinkedList;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import net.certiv.common.TestCommon;
import net.certiv.common.diff.Differ;
import net.certiv.common.graph.demo.DemoEdge;
import net.certiv.common.graph.demo.DemoNode;
import net.certiv.common.graph.paths.SubGraph;
import net.certiv.common.graph.paths.SubGraphFinder;
import net.certiv.common.stores.Result;

public class TransformCpTest extends TestCommon {

	static final boolean FORCE = true;

	@BeforeEach
	void setup() {
		builder.createAndAddEdges("A->B->C->D->E");
		builder.createAndAddEdges("C->F->G");
		builder.createAndAddEdges("C->[B,C,E]");

		builder.createAndAddEdges("U->X->Y");
		builder.createAndAddEdges("U->Z");
	}

	@Test
	void testCopyEnd() {
		graph.put(Graph.GRAPH_NAME, "Copy End");

		DemoNode g = builder.getNode("G");
		DemoNode u = builder.getNode("U");

		SubGraphFinder<DemoNode, DemoEdge> sgf = SubGraphFinder.in(graph);
		SubGraph<DemoNode, DemoEdge> sg = sgf.find(u);

		Transformer<DemoNode, DemoEdge> xf = new Transformer<>(graph);
		xf.copy(sg, g, true);	// copy in like =>G=>, removing G
		xf.remove(sg, true);	// remove source subgraph

		String dot = graph.render();
		writeResource(getClass(), XForm + "CopyEnd.md", dot, FORCE);

		String txt = loadResource(getClass(), XForm + "CopyEnd.md");
		Differ.diff((String) graph.get(Graph.GRAPH_NAME), txt, dot).sdiff(true, 120).out();

		assertEquals(txt, dot);
	}

	@Test
	void testCopyMid() {
		graph.put(Graph.GRAPH_NAME, "Copy Mid");

		DemoNode f = builder.getNode("F");
		DemoNode u = builder.getNode("U");

		SubGraphFinder<DemoNode, DemoEdge> sgf = SubGraphFinder.in(graph);
		SubGraph<DemoNode, DemoEdge> sg = sgf.find(u);

		Transformer<DemoNode, DemoEdge> xf = new Transformer<>(graph);
		Result<LinkedList<DemoEdge>> res = xf.copy(sg, f, false); // copy in like =>F=>
		assertTrue(res.valid());

		String dot = graph.render();
		writeResource(getClass(), XForm + "CopyMid.md", dot, FORCE);

		String txt = loadResource(getClass(), XForm + "CopyMid.md");
		Differ.diff((String) graph.get(Graph.GRAPH_NAME), txt, dot).sdiff(true, 120).out();
		assertEquals(txt, dot);
	}

	@Test
	void testCopyRemove() {
		graph.put(Graph.GRAPH_NAME, "Copy Remove");

		DemoNode f = builder.getNode("F");
		DemoNode u = builder.getNode("U");

		SubGraphFinder<DemoNode, DemoEdge> sgf = SubGraphFinder.in(graph);
		SubGraph<DemoNode, DemoEdge> sg = sgf.find(u);

		Transformer<DemoNode, DemoEdge> xf = new Transformer<>(graph);
		// copy in like =>F=>, removing F
		Result<LinkedList<DemoEdge>> res = xf.copy(sg, f, true);
		assertTrue(res.valid());

		String dot = graph.render();
		writeResource(getClass(), XForm + "CopyRm.md", dot, FORCE);

		String txt = loadResource(getClass(), XForm + "CopyRm.md");
		Differ.diff((String) graph.get(Graph.GRAPH_NAME), txt, dot).sdiff(true, 120).out();
		assertEquals(txt, dot);
	}

	@Test
	void testCopyRemoveReduce() {
		graph.put(Graph.GRAPH_NAME, "Copy RemoveReduce");

		DemoNode f = builder.getNode("F");
		DemoNode u = builder.getNode("U");

		SubGraphFinder<DemoNode, DemoEdge> sgf = SubGraphFinder.in(graph);
		SubGraph<DemoNode, DemoEdge> sg = sgf.find(u);

		Transformer<DemoNode, DemoEdge> xf = new Transformer<>(graph);
		// copy in like =>F=>, removing F
		Result<LinkedList<DemoEdge>> res = xf.copy(sg, f, true);
		assertTrue(res.valid());

		// remove & clear source subgraph nodes
		Result<Boolean> rm = xf.remove(sg, true);
		assertTrue(rm.valid());

		String dot = graph.render();
		writeResource(getClass(), XForm + "CopyRmRd.md", dot, FORCE);

		String txt = loadResource(getClass(), XForm + "CopyRmRd.md");
		Differ.diff((String) graph.get(Graph.GRAPH_NAME), txt, dot).sdiff(true, 120).out();
		assertEquals(txt, dot);
	}

}
