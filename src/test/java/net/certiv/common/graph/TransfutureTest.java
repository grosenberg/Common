package net.certiv.common.graph;

import static net.certiv.common.dot.DotAttr.COLOR;
import static net.certiv.common.dot.DotAttr.LABEL;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.LinkedHashMap;
import java.util.LinkedList;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import net.certiv.common.diff.Differ;
import net.certiv.common.dot.DotStyle;
import net.certiv.common.graph.algorithms.GraphPath;
import net.certiv.common.graph.algorithms.SubgraphFinder;
import net.certiv.common.graph.demo.DemoEdge;
import net.certiv.common.graph.demo.DemoNode;
import net.certiv.common.stores.Result;
import net.certiv.common.stores.UniqueList;
import net.certiv.common.util.FsUtil;

class TransfutureTest extends TestBase {

	@BeforeEach
	void setUp() {
		builder.createAndAddEdges("A->B->C->D->E");
		builder.createAndAddEdges("C->F->G");
		builder.createAndAddEdges("C->[B,C,E]");
	}

	@Test
	void testCopy() {
		graph.put(Graph.GRAPH_NAME, "Transfuture Copy");

		builder.createAndAddEdges("U->X->Y");
		builder.createAndAddEdges("U->Z");

		DemoNode f = builder.getNode("F");
		DemoNode u = builder.getNode("U");

		SubgraphFinder<DemoNode, DemoEdge> finder = new SubgraphFinder<>(graph);
		LinkedHashMap<DemoNode, GraphPath<DemoNode, DemoEdge>> sg = finder.subset(u);

		Transfuture<DemoNode, DemoEdge> xf = new Transfuture<>(graph);
		Result<LinkedList<DemoEdge>> cp = xf.copy(sg, f, true);
		assertTrue(cp.valid());

		Result<Boolean> res = xf.apply();
		assertTrue(res.valid());

		String dot = graph.render();
		// FsUtil.writeResource(getClass(), XFuture+"Copy.md", dot);

		String txt = FsUtil.loadResource(getClass(), XFuture + "Copy.md").value;
		Differ.diff((String) graph.get(Graph.GRAPH_NAME), txt, dot).sdiff(true, 120).out();

		assertEquals(txt, dot);
	}

	@Test
	void testCopy1() {
		graph.put(Graph.GRAPH_NAME, "Transfuture Copy1");

		builder.createAndAddEdges("U->X->Y");
		builder.createAndAddEdges("U->Z");

		DemoNode g = builder.getNode("G");
		DemoNode u = builder.getNode("U");

		SubgraphFinder<DemoNode, DemoEdge> finder = new SubgraphFinder<>(graph);
		LinkedHashMap<DemoNode, GraphPath<DemoNode, DemoEdge>> sg = finder.subset(u);

		Transfuture<DemoNode, DemoEdge> xf = new Transfuture<>(graph);
		xf.copy(sg, g, true);
		xf.remove(sg, true);
		xf.apply();

		String dot = graph.render();
		// FsUtil.writeResource(getClass(), XFuture+"Copy1.md", dot);

		String txt = FsUtil.loadResource(getClass(), XFuture + "Copy1.md").value;
		Differ.diff((String) graph.get(Graph.GRAPH_NAME), txt, dot).sdiff(true, 120).out();

		assertEquals(txt, dot);
	}

	@Test
	void testRemoveNode() {
		graph.put(Graph.GRAPH_NAME, "Transfuture Remove Node");

		DemoNode f = builder.getNode("F");

		Transfuture<DemoNode, DemoEdge> xf = new Transfuture<>(graph);
		xf.removeNode(f);
		xf.apply();

		String dot = graph.render();
		String txt = FsUtil.loadResource(getClass(), XFuture + "RemoveNode.md").value;
		Differ.diff((String) graph.get(Graph.GRAPH_NAME), txt, dot).sdiff(true, 120).out();
		assertEquals(txt, dot);
	}

	@Test
	void testRemoveEdge() {
		graph.put(Graph.GRAPH_NAME, "Transfuture Remove Edge");

		DemoNode c = builder.getNode("C");
		DemoNode f = builder.getNode("F");
		UniqueList<DemoEdge> edges = builder.getEdges(c, f);

		Transfuture<DemoNode, DemoEdge> xf = new Transfuture<>(graph);
		for (DemoEdge edge : edges) {
			xf.removeEdge(edge, true);
		}
		xf.apply();

		String dot = graph.render();
		String txt = FsUtil.loadResource(getClass(), XFuture + "RemoveEdge.md").value;
		Differ.diff((String) graph.get(Graph.GRAPH_NAME), txt, dot).sdiff(true, 120).out();
		assertEquals(txt, dot);
	}

	@Test
	void testReduceNode() {
		graph.put(Graph.GRAPH_NAME, "Transfuture Reduce Node");

		DemoNode f = builder.getNode("F");

		Transfuture<DemoNode, DemoEdge> xf = new Transfuture<>(graph);
		xf.reduce(f);
		xf.apply();

		String dot = graph.render();
		// FsUtil.writeResource(getClass(), XFuture+"ReduceNode.md", dot);
		String txt = FsUtil.loadResource(getClass(), XFuture + "ReduceNode.md").value;
		Differ.diff((String) graph.get(Graph.GRAPH_NAME), txt, dot).sdiff(true, 120).out();
		assertEquals(txt, dot);
	}

	@Test
	void testTransfer() {
		graph.put(Graph.GRAPH_NAME, "Transfuture Transfer");

		DemoNode b = builder.getNode("B");
		UniqueList<DemoEdge> cf = builder.getEdges("C", "F");

		Transfuture<DemoNode, DemoEdge> xf = new Transfuture<>(graph);
		xf.transfer(cf, b); // cf becomes bf
		xf.apply();

		String dot = graph.render();
		String txt = FsUtil.loadResource(getClass(), XFuture + "Transfer.md").value;
		Differ.diff((String) graph.get(Graph.GRAPH_NAME), txt, dot).sdiff(true, 120).out();
		assertEquals(txt, dot);
	}

	@Test
	void testMove() {
		graph.put(Graph.GRAPH_NAME, "Transfuture Move");

		DemoNode b = builder.getNode("B");
		DemoNode c = builder.getNode("C");
		DemoNode d = builder.getNode("D");
		DemoNode e = builder.getNode("E");
		UniqueList<DemoEdge> ce = builder.getEdges(c, e);

		for (DemoEdge edge : ce) {
			DotStyle ds = edge.getDotStyle();
			ds.put(LABEL, "Edge " + edge.name());
			ds.put(COLOR, "blue");
		}

		Transfuture<DemoNode, DemoEdge> xf = new Transfuture<>(graph);
		xf.move(ce, b, d, false);
		xf.apply();

		String dot = graph.render();
		// FsUtil.writeResource(getClass(), XFuture+"Move.md", dot);

		String txt = FsUtil.loadResource(getClass(), XFuture + "Move.md").value;
		Differ.diff((String) graph.get(Graph.GRAPH_NAME), txt, dot).sdiff(true, 120).out();
		assertEquals(txt, dot);
	}

	@Test
	void testReplicate() {
		graph.put(Graph.GRAPH_NAME, "Transfuture Replicate");

		DemoNode b = builder.getNode("B");
		UniqueList<DemoNode> targets = builder.findOrCreateNodes("[B,X,Y,Z]");

		Transfuture<DemoNode, DemoEdge> xf = new Transfuture<>(graph);
		xf.replicateEdges(b, targets);
		xf.apply();

		String dot = graph.render();
		// FsUtil.writeResource(getClass(), XFuture+"Replicate.md", dot);

		String txt = FsUtil.loadResource(getClass(), XFuture + "Replicate.md").value;
		Differ.diff((String) graph.get(Graph.GRAPH_NAME), txt, dot).sdiff(true, 120).out();
		assertEquals(txt, dot);
	}

	@Test
	void testReplicateReduce() {
		graph.put(Graph.GRAPH_NAME, "Transfuture Replicate Reduce");

		DemoNode b = builder.getNode("B");
		UniqueList<DemoNode> targets = builder.findOrCreateNodes("[X,Y,Z]");

		Transfuture<DemoNode, DemoEdge> xf = new Transfuture<>(graph);
		xf.replicateEdges(b, targets, true);
		xf.apply();

		String dot = graph.render();
		// FsUtil.writeResource(getClass(), XFuture+"ReplicateReduce.md", dot);

		String txt = FsUtil.loadResource(getClass(), XFuture + "ReplicateReduce.md").value;
		Differ.diff((String) graph.get(Graph.GRAPH_NAME), txt, dot).sdiff(true, 120).out();
		assertEquals(txt, dot);
	}
}
