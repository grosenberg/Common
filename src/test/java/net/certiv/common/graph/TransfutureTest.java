package net.certiv.common.graph;

import static net.certiv.common.dot.DotAttr.COLOR;
import static net.certiv.common.dot.DotAttr.LABEL;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.LinkedList;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import net.certiv.common.CommonSupport;
import net.certiv.common.diff.Differ;
import net.certiv.common.dot.DotStyle;
import net.certiv.common.graph.demo.DemoEdge;
import net.certiv.common.graph.demo.DemoNode;
import net.certiv.common.graph.paths.SubGraph;
import net.certiv.common.graph.paths.SubGraphFinder;
import net.certiv.common.stores.Result;
import net.certiv.common.stores.UniqueList;
import net.certiv.common.util.test.CommonTestBase;

class TransfutureTest extends CommonTestBase {

	static final boolean FORCE = false;
	private final CommonSupport CS = new CommonSupport();

	@BeforeEach
	public void setup() {
		CS.setup();
		CS.builder.createAndAddEdges("A->B->C->D->E");
		CS.builder.createAndAddEdges("C->F->G");
		CS.builder.createAndAddEdges("C->[B,C,E]");
	}

	@AfterEach
	public void teardown() {
		CS.teardown();
	}

	@Test
	void testCopy() {
		CS.graph.setId(CS.factory.make("Transfuture Copy"));

		CS.builder.createAndAddEdges("U->X->Y");
		CS.builder.createAndAddEdges("U->Z");

		DemoNode f = CS.builder.getNode("F");
		DemoNode u = CS.builder.getNode("U");

		SubGraphFinder<DemoNode, DemoEdge> sgf = SubGraphFinder.in(CS.graph);
		SubGraph<DemoNode, DemoEdge> sg = sgf.find(u);

		Transfuture<DemoNode, DemoEdge> xf = new Transfuture<>(CS.graph);
		Result<LinkedList<DemoEdge>> cp = xf.copy(sg, f, true);
		assertTrue(cp.valid());

		Result<Boolean> res = xf.apply();
		assertTrue(res.valid());

		String dot = CS.graph.render();
		writeResource(getClass(), CommonSupport.XFuture + "Copy.md", dot, FORCE);

		String txt = loadResource(getClass(), CommonSupport.XFuture + "Copy.md");
		Differ.diff(CS.graph.displayName(), txt, dot).sdiff(true, 120).out();

		assertEquals(txt, dot);
	}

	@Test
	void testCopy1() {
		CS.graph.setId(CS.factory.make("Transfuture Copy1"));

		CS.builder.createAndAddEdges("U->X->Y");
		CS.builder.createAndAddEdges("U->Z");

		DemoNode g = CS.builder.getNode("G");
		DemoNode u = CS.builder.getNode("U");

		SubGraphFinder<DemoNode, DemoEdge> sgf = SubGraphFinder.in(CS.graph);
		SubGraph<DemoNode, DemoEdge> sg = sgf.find(u);

		Transfuture<DemoNode, DemoEdge> xf = new Transfuture<>(CS.graph);
		xf.copy(sg, g, true);
		xf.remove(sg, true);
		xf.apply();

		String dot = CS.graph.render();
		writeResource(getClass(), CommonSupport.XFuture + "Copy1.md", dot, FORCE);

		String txt = loadResource(getClass(), CommonSupport.XFuture + "Copy1.md");
		Differ.diff(CS.graph.displayName(), txt, dot).sdiff(true, 120).out();

		assertEquals(txt, dot);
	}

	@Test
	void testRemoveNode() {
		CS.graph.setId(CS.factory.make("Transfuture Remove Node"));

		DemoNode f = CS.builder.getNode("F");

		Transfuture<DemoNode, DemoEdge> xf = new Transfuture<>(CS.graph);
		xf.removeNode(f);
		xf.apply();

		String dot = CS.graph.render();
		writeResource(getClass(), CommonSupport.XFuture + "RemoveNode.md", dot, FORCE);

		String txt = loadResource(getClass(), CommonSupport.XFuture + "RemoveNode.md");
		Differ.diff(CS.graph.displayName(), txt, dot).sdiff(true, 120).out();
		assertEquals(txt, dot);
	}

	@Test
	void testRemoveEdge() {
		CS.graph.setId(CS.factory.make("Transfuture Remove Edge"));

		DemoNode c = CS.builder.getNode("C");
		DemoNode f = CS.builder.getNode("F");
		UniqueList<DemoEdge> edges = CS.builder.getEdges(c, f);

		Transfuture<DemoNode, DemoEdge> xf = new Transfuture<>(CS.graph);
		for (DemoEdge edge : edges) {
			xf.removeEdge(edge, true);
		}
		xf.apply();

		String dot = CS.graph.render();
		writeResource(getClass(), CommonSupport.XFuture + "RemoveEdge.md", dot, FORCE);

		String txt = loadResource(getClass(), CommonSupport.XFuture + "RemoveEdge.md");
		Differ.diff(CS.graph.displayName(), txt, dot).sdiff(true, 120).out();
		assertEquals(txt, dot);
	}

	@Test
	void testReduceNode() {
		CS.graph.setId(CS.factory.make("Transfuture Reduce Node"));

		DemoNode f = CS.builder.getNode("F");

		Transfuture<DemoNode, DemoEdge> xf = new Transfuture<>(CS.graph);
		xf.reduce(f);
		xf.apply();

		String dot = CS.graph.render();
		writeResource(getClass(), CommonSupport.XFuture + "ReduceNode.md", dot, FORCE);

		String txt = loadResource(getClass(), CommonSupport.XFuture + "ReduceNode.md");
		Differ.diff(CS.graph.displayName(), txt, dot).sdiff(true, 120).out();
		assertEquals(txt, dot);
	}

	@Test
	void testTransfer() {
		CS.graph.setId(CS.factory.make("Transfuture Transfer"));

		DemoNode b = CS.builder.getNode("B");
		UniqueList<DemoEdge> cf = CS.builder.getEdges("C", "F");

		Transfuture<DemoNode, DemoEdge> xf = new Transfuture<>(CS.graph);
		xf.transfer(cf, b); // cf becomes bf
		xf.apply();

		String dot = CS.graph.render();
		writeResource(getClass(), CommonSupport.XFuture + "Transfer.md", dot, FORCE);

		String txt = loadResource(getClass(), CommonSupport.XFuture + "Transfer.md");
		Differ.diff(CS.graph.displayName(), txt, dot).sdiff(true, 120).out();
		assertEquals(txt, dot);
	}

	@Test
	void testMove() {
		CS.graph.setId(CS.factory.make("Transfuture Move"));

		DemoNode b = CS.builder.getNode("B");
		DemoNode c = CS.builder.getNode("C");
		DemoNode d = CS.builder.getNode("D");
		DemoNode e = CS.builder.getNode("E");
		UniqueList<DemoEdge> ce = CS.builder.getEdges(c, e);

		for (DemoEdge edge : ce) {
			DotStyle ds = edge.getDotStyle();
			ds.put(LABEL, "Edge " + edge.name());
			ds.put(COLOR, "blue");
		}

		Transfuture<DemoNode, DemoEdge> xf = new Transfuture<>(CS.graph);
		xf.move(ce, b, d, false);
		xf.apply();

		String dot = CS.graph.render();
		writeResource(getClass(), CommonSupport.XFuture + "Move.md", dot, FORCE);

		String txt = loadResource(getClass(), CommonSupport.XFuture + "Move.md");
		Differ.diff(CS.graph.displayName(), txt, dot).sdiff(true, 120).out();
		assertEquals(txt, dot);
	}

	@Test
	void testReplicate() {
		CS.graph.setId(CS.factory.make("Transfuture Replicate"));

		DemoNode b = CS.builder.getNode("B");
		UniqueList<DemoNode> targets = CS.builder.findOrCreateNodes("[B,X,Y,Z]");

		Transfuture<DemoNode, DemoEdge> xf = new Transfuture<>(CS.graph);
		xf.replicateEdges(b, targets);
		xf.apply();

		String dot = CS.graph.render();
		writeResource(getClass(), CommonSupport.XFuture + "Replicate.md", dot, FORCE);

		String txt = loadResource(getClass(), CommonSupport.XFuture + "Replicate.md");
		Differ.diff(CS.graph.displayName(), txt, dot).sdiff(true, 120).out();
		assertEquals(txt, dot);
	}

	@Test
	void testReplicateReduce() {
		CS.graph.setId(CS.factory.make("Transfuture Replicate Reduce"));

		DemoNode b = CS.builder.getNode("B");
		UniqueList<DemoNode> targets = CS.builder.findOrCreateNodes("[X,Y,Z]");

		Transfuture<DemoNode, DemoEdge> xf = new Transfuture<>(CS.graph);
		xf.replicateEdges(b, targets, true);
		xf.apply();

		String dot = CS.graph.render();
		writeResource(getClass(), CommonSupport.XFuture + "ReplicateReduce.md", dot, FORCE);

		String txt = loadResource(getClass(), CommonSupport.XFuture + "ReplicateReduce.md");
		Differ.diff(CS.graph.displayName(), txt, dot).sdiff(true, 120).out();
		assertEquals(txt, dot);
	}
}
