package net.certiv.common.graph;

import static net.certiv.common.dot.DotAttr.COLOR;
import static net.certiv.common.dot.DotAttr.LABEL;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import org.junit.jupiter.api.Test;

import net.certiv.common.diff.Differ;
import net.certiv.common.dot.DotStyle;
import net.certiv.common.graph.demo.DemoEdge;
import net.certiv.common.graph.demo.DemoNode;
import net.certiv.common.stores.UniqueList;
import net.certiv.common.util.FsUtil;

class TransformTest extends TestBase {

	@Test
	void testReduceNode() {
		graph.put(Graph.GRAPH_NAME, "Reduce");

		builder.createAndAddEdge("A", "B");
		builder.createAndAddEdge("B", "C");

		DemoNode b = builder.getNode("B");

		Transformer<DemoNode, DemoEdge> xf = new Transformer<>(graph);
		xf.reduce(b);

		b = builder.getNode("B");
		assertNull(b);

		String dot = graph.render();
		// FsUtil.writeResource(getClass(), XFuture+"Reduce1.md", dot);

		String txt = FsUtil.loadResource(getClass(), XForm + "Reduce1.md").value;
		Differ.diff((String) graph.get(Graph.GRAPH_NAME), txt, dot).sdiff(true, 120).out();
		assertEquals(txt, dot);
	}

	@Test
	void testReduceEdge() {
		graph.put(Graph.GRAPH_NAME, "ReduceEdge");

		builder.createAndAddEdge("R", "A");
		builder.createAndAddEdge("A", "B");
		builder.createAndAddEdge("R", "C");
		builder.createAndAddEdge("C", "D");

		UniqueList<DemoEdge> ab = builder.getEdges("A", "B");
		UniqueList<DemoEdge> cd = builder.getEdges("C", "D");

		Transformer<DemoNode, DemoEdge> xf = new Transformer<>(graph);
		xf.reduce(ab.getFirst(), cd.getFirst());

		DemoNode b = builder.getNode("B");
		assertNull(b);

		String dot = graph.render();
		// FsUtil.writeResource(getClass(), XFuture+"ReduceEdge1.md", dot);

		String txt = FsUtil.loadResource(getClass(), XForm + "ReduceEdge1.md").value;
		Differ.diff((String) graph.get(Graph.GRAPH_NAME), txt, dot).sdiff(true, 120).out();
		assertEquals(txt, dot);
	}

	@Test
	void testTransfer() {
		graph.put(Graph.GRAPH_NAME, "Transfer");

		builder.createAndAddEdge("A", "B");
		builder.createAndAddEdge("B", "C");
		builder.createAndAddEdge("C", "D");
		builder.createAndAddEdge("D", "E");

		builder.createAndAddEdge("C", "F");
		builder.createAndAddEdge("F", "G");

		DemoNode b = builder.getNode("B");
		UniqueList<DemoEdge> cf = builder.getEdges("C", "F");

		Transformer<DemoNode, DemoEdge> xf = new Transformer<>(graph);
		xf.transfer(cf, b); // cf becomes bf

		String dot = graph.render();
		// FsUtil.writeResource(getClass(), XFuture+"Transfer1.md", dot);

		String txt = FsUtil.loadResource(getClass(), XForm + "Transfer1.md").value;
		Differ.diff((String) graph.get(Graph.GRAPH_NAME), txt, dot).sdiff(true, 120).out();
		assertEquals(txt, dot);
	}

	@Test
	void testMove() {
		graph.put(Graph.GRAPH_NAME, "Move");

		builder.createAndAddEdges("A->B->C->D->E");

		DemoEdge ce = builder.createAndAddEdge("C", "E");
		DotStyle ds = ce.getDotStyle();
		ds.put(LABEL, "Edge " + ce.name());
		ds.put(COLOR, "blue");

		DemoNode b = builder.getNode("B");
		DemoNode d = builder.getNode("D");

		Transformer<DemoNode, DemoEdge> xf = new Transformer<>(graph);
		xf.move(ce, b, d);

		String dot = graph.render();
		// FsUtil.writeResource(getClass(), XFuture+"Move1.md", dot);

		String txt = FsUtil.loadResource(getClass(), XForm + "Move1.md").value;
		Differ.diff((String) graph.get(Graph.GRAPH_NAME), txt, dot).sdiff(true, 120).out();
		assertEquals(txt, dot);
	}

	@Test
	void testReplicate() {
		graph.put(Graph.GRAPH_NAME, "Replicate");

		builder.createAndAddEdges("A => B => C");
		UniqueList<DemoNode> targets = builder.findOrCreateNodes("[B,X,Y,Z]");

		DemoNode b = builder.getNode("B");

		Transformer<DemoNode, DemoEdge> xf = new Transformer<>(graph);
		xf.replicateEdges(b, targets); // replicate =>B=> to =>[targets-B]=>

		String dot = graph.render();
		// FsUtil.writeResource(getClass(), XFuture+"Replicate1.md", dot);

		String txt = FsUtil.loadResource(getClass(), XForm + "Replicate1.md").value;
		Differ.diff((String) graph.get(Graph.GRAPH_NAME), txt, dot).sdiff(true, 120).out();
		assertEquals(txt, dot);
	}

	@Test
	void testReplicateReduce() {
		graph.put(Graph.GRAPH_NAME, "Replicate Remove");

		builder.createAndAddEdges("A => B => C");
		UniqueList<DemoNode> targets = builder.findOrCreateNodes("[X,Y,Z]");

		DemoNode b = builder.getNode("B");

		Transformer<DemoNode, DemoEdge> xf = new Transformer<>(graph);
		xf.replicateEdges(b, targets, true);

		String dot = graph.render();
		// FsUtil.writeResource(getClass(), XFuture+"Replicate2.md", dot);

		String txt = FsUtil.loadResource(getClass(), XForm + "Replicate2.md").value;
		Differ.diff((String) graph.get(Graph.GRAPH_NAME), txt, dot).sdiff(true, 120).out();
		assertEquals(txt, dot);
	}
}
