package net.certiv.common.graph;

import static net.certiv.common.dot.DotAttr.COLOR;
import static net.certiv.common.dot.DotAttr.LABEL;
import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import net.certiv.common.diff.Differ;
import net.certiv.common.dot.DotStyle;
import net.certiv.common.graph.demo.DemoEdge;
import net.certiv.common.graph.demo.DemoNode;
import net.certiv.common.stores.UniqueList;
import net.certiv.common.util.FsUtil;

class TransformerTest extends TestBase {

	DemoNode a;
	DemoNode b;
	DemoNode c;
	DemoNode d;
	DemoNode e;
	DemoNode f;
	DemoNode g;

	@BeforeEach
	void setUp() {
		builder.createAndAddEdges("A->B->C->D->E");
		builder.createAndAddEdges("C->F->G");
		builder.createAndAddEdges("C->[B,C,E]");

		a = builder.getNode("A");
		b = builder.getNode("B");
		c = builder.getNode("C");
		d = builder.getNode("D");
		e = builder.getNode("E");
		f = builder.getNode("F");
		g = builder.getNode("G");

	}

	@Test
	void testRemoveNode() {
		graph.put(Graph.GRAPH_NAME, "Transformer Remove Node");

		Transformer<DemoNode, DemoEdge> xf = new Transformer<>(graph);
		xf.removeNode(f);
		xf.exec();

		String dot = graph.render();
		String txt = FsUtil.loadResource(getClass(), "xfRemoveNode.md").value;
		Differ.diff((String) graph.get(Graph.GRAPH_NAME), dot, txt).sdiff(true, 120).out();
		assertEquals(dot, txt);
	}

	@Test
	void testRemoveEdge() {
		graph.put(Graph.GRAPH_NAME, "Transformer Remove Edge");

		UniqueList<DemoEdge> edges = builder.getEdges(c, f);
		Transformer<DemoNode, DemoEdge> xf = new Transformer<>(graph);
		for (DemoEdge edge : edges) {
			xf.removeEdge(edge, true);
		}
		xf.exec();

		String dot = graph.render();
		String txt = FsUtil.loadResource(getClass(), "xfRemoveEdge.md").value;
		Differ.diff((String) graph.get(Graph.GRAPH_NAME), dot, txt).sdiff(true, 120).out();
		assertEquals(dot, txt);
	}

	@Test
	void testReduceNode() {
		graph.put(Graph.GRAPH_NAME, "Transformer Reduce Node");

		Transformer<DemoNode, DemoEdge> xf = new Transformer<>(graph);
		xf.reduce(f);
		xf.exec();

		String dot = graph.render();
		// FsUtil.writeResource(getClass(), "xfReduceNode.md", dot);
		String txt = FsUtil.loadResource(getClass(), "xfReduceNode.md").value;
		Differ.diff((String) graph.get(Graph.GRAPH_NAME), dot, txt).sdiff(true, 120).out();
		assertEquals(dot, txt);
	}

	@Test
	void testTransfer() {
		graph.put(Graph.GRAPH_NAME, "Transformer Transfer");

		UniqueList<DemoEdge> cf = builder.getEdges("C", "F");
		Transformer<DemoNode, DemoEdge> xf = new Transformer<>(graph);
		xf.transfer(cf, b); // cf becomes bf
		xf.exec();

		String dot = graph.render();
		String txt = FsUtil.loadResource(getClass(), "xfTransfer.md").value;
		Differ.diff((String) graph.get(Graph.GRAPH_NAME), dot, txt).sdiff(true, 120).out();
		assertEquals(dot, txt);
	}

	@Test
	void testMove() {
		graph.put(Graph.GRAPH_NAME, "Transformer Move");

		UniqueList<DemoEdge> ce = builder.getEdges(c, e);
		for (DemoEdge edge : ce) {
			DotStyle ds = edge.getDotStyle();
			ds.put(LABEL, "Edge " + edge.name());
			ds.put(COLOR, "blue");
		}

		Transformer<DemoNode, DemoEdge> xf = new Transformer<>(graph);
		for (DemoEdge edge : ce) {
			xf.move(edge, b, d);
		}
		xf.exec();

		String dot = graph.render();
		// FsUtil.writeResource(getClass(), "xfMove.md", dot);
		String txt = FsUtil.loadResource(getClass(), "xfMove.md").value;
		Differ.diff((String) graph.get(Graph.GRAPH_NAME), dot, txt).sdiff(true, 120).out();
		assertEquals(dot, txt);
	}

	@Test
	void testConsolidate() {
		graph.put(Graph.GRAPH_NAME, "Transformer Consolidate");

		UniqueList<DemoNode> nodes = builder.findNodes("[C,E,F]");
		Transformer<DemoNode, DemoEdge> xf = new Transformer<>(graph);
		xf.consolidateEdges(nodes, b);
		xf.exec();

		String dot = graph.render();
		// FsUtil.writeResource(getClass(), "xfConsolidate.md", dot);
		String txt = FsUtil.loadResource(getClass(), "xfConsolidate.md").value;
		Differ.diff((String) graph.get(Graph.GRAPH_NAME), dot, txt).sdiff(true, 120).out();
		assertEquals(dot, txt);
	}

	@Test
	void testReplicate() {
		graph.put(Graph.GRAPH_NAME, "Transformer Replicate");

		UniqueList<DemoNode> targets = builder.findOrCreateNodes("[B,X,Y,Z]");

		Transformer<DemoNode, DemoEdge> xf = new Transformer<>(graph);
		xf.replicateEdges(b, targets);
		xf.exec();

		String dot = graph.render();
		// FsUtil.writeResource(getClass(), "xfReplicate.md", dot);
		String txt = FsUtil.loadResource(getClass(), "xfReplicate.md").value;
		Differ.diff((String) graph.get(Graph.GRAPH_NAME), dot, txt).sdiff(true, 120).out();
		assertEquals(dot, txt);
	}

	@Test
	void testReplicateReduce() {
		graph.put(Graph.GRAPH_NAME, "Transformer Replicate Reduce");

		UniqueList<DemoNode> targets = builder.findOrCreateNodes("[X,Y,Z]");
		Transformer<DemoNode, DemoEdge> xf = new Transformer<>(graph);
		xf.replicateEdges(b, targets, true);
		xf.exec();

		String dot = graph.render();
		// FsUtil.writeResource(getClass(), "xfReplicateReduce.md", dot);
		String txt = FsUtil.loadResource(getClass(), "xfReplicateReduce.md").value;
		Differ.diff((String) graph.get(Graph.GRAPH_NAME), dot, txt).sdiff(true, 120).out();
		assertEquals(dot, txt);
	}
}
