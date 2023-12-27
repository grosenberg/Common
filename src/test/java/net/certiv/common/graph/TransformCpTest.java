package net.certiv.common.graph;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.LinkedList;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import net.certiv.common.CommonSupport;
import net.certiv.common.diff.Differ;
import net.certiv.common.graph.demo.DemoEdge;
import net.certiv.common.graph.demo.DemoNode;
import net.certiv.common.graph.paths.SubGraph;
import net.certiv.common.graph.paths.SubGraphFinder;
import net.certiv.common.stores.Result;
import net.certiv.common.util.test.CommonTestBase;

public class TransformCpTest extends CommonTestBase {

	static final boolean FORCE = true;
	private final CommonSupport CS = new CommonSupport();

	@BeforeEach
	public void setup() {
		CS.setup();
		CS.builder.createAndAddEdges("A->B->C->D->E");
		CS.builder.createAndAddEdges("C->F->G");
		CS.builder.createAndAddEdges("C->[B,C,E]");

		CS.builder.createAndAddEdges("U->X->Y");
		CS.builder.createAndAddEdges("U->Z");
	}

	@AfterEach
	public void teardown() {
		CS.teardown();
	}

	@Test
	void testCopyEnd() {
		CS.graph.setId(CS.factory.make("Copy End"));

		DemoNode g = CS.builder.getNode("G");
		DemoNode u = CS.builder.getNode("U");

		SubGraphFinder<DemoNode, DemoEdge> sgf = SubGraphFinder.in(CS.graph);
		SubGraph<DemoNode, DemoEdge> sg = sgf.find(u);

		Transformer<DemoNode, DemoEdge> xf = new Transformer<>(CS.graph);
		xf.copy(sg, g, true);	// copy in like =>G=>, removing G
		xf.remove(sg, true);	// remove source subgraph

		String dot = CS.graph.render();
		writeResource(getClass(), CommonSupport.XForm + "CopyEnd.md", dot, FORCE);

		String txt = loadResource(getClass(), CommonSupport.XForm + "CopyEnd.md");
		Differ.diff(CS.graph.displayName(), txt, dot).sdiff(true, 120).out();

		assertEquals(txt, dot);
	}

	@Test
	void testCopyMid() {
		CS.graph.setId(CS.factory.make("Copy Mid"));

		DemoNode f = CS.builder.getNode("F");
		DemoNode u = CS.builder.getNode("U");

		SubGraphFinder<DemoNode, DemoEdge> sgf = SubGraphFinder.in(CS.graph);
		SubGraph<DemoNode, DemoEdge> sg = sgf.find(u);

		Transformer<DemoNode, DemoEdge> xf = new Transformer<>(CS.graph);
		Result<LinkedList<DemoEdge>> res = xf.copy(sg, f, false); // copy in like =>F=>
		assertTrue(res.valid());

		String dot = CS.graph.render();
		writeResource(getClass(), CommonSupport.XForm + "CopyMid.md", dot, FORCE);

		String txt = loadResource(getClass(), CommonSupport.XForm + "CopyMid.md");
		Differ.diff(CS.graph.displayName(), txt, dot).sdiff(true, 120).out();
		assertEquals(txt, dot);
	}

	@Test
	void testCopyRemove() {
		CS.graph.setId(CS.factory.make("Copy Remove"));

		DemoNode f = CS.builder.getNode("F");
		DemoNode u = CS.builder.getNode("U");

		SubGraphFinder<DemoNode, DemoEdge> sgf = SubGraphFinder.in(CS.graph);
		SubGraph<DemoNode, DemoEdge> sg = sgf.find(u);

		Transformer<DemoNode, DemoEdge> xf = new Transformer<>(CS.graph);
		// copy in like =>F=>, removing F
		Result<LinkedList<DemoEdge>> res = xf.copy(sg, f, true);
		assertTrue(res.valid());

		String dot = CS.graph.render();
		writeResource(getClass(), CommonSupport.XForm + "CopyRm.md", dot, FORCE);

		String txt = loadResource(getClass(), CommonSupport.XForm + "CopyRm.md");
		Differ.diff(CS.graph.displayName(), txt, dot).sdiff(true, 120).out();
		assertEquals(txt, dot);
	}

	@Test
	void testCopyRemoveReduce() {
		CS.graph.setId(CS.factory.make("Copy RemoveReduce"));

		DemoNode f = CS.builder.getNode("F");
		DemoNode u = CS.builder.getNode("U");

		SubGraphFinder<DemoNode, DemoEdge> sgf = SubGraphFinder.in(CS.graph);
		SubGraph<DemoNode, DemoEdge> sg = sgf.find(u);

		Transformer<DemoNode, DemoEdge> xf = new Transformer<>(CS.graph);
		// copy in like =>F=>, removing F
		Result<LinkedList<DemoEdge>> res = xf.copy(sg, f, true);
		assertTrue(res.valid());

		// remove & clear source subgraph nodes
		Result<Boolean> rm = xf.remove(sg, true);
		assertTrue(rm.valid());

		String dot = CS.graph.render();
		writeResource(getClass(), CommonSupport.XForm + "CopyRmRd.md", dot, FORCE);

		String txt = loadResource(getClass(), CommonSupport.XForm + "CopyRmRd.md");
		Differ.diff(CS.graph.displayName(), txt, dot).sdiff(true, 120).out();
		assertEquals(txt, dot);
	}

}
