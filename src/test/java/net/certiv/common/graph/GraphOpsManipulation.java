package net.certiv.common.graph;

import static net.certiv.common.dot.DotAttr.COLOR;
import static net.certiv.common.dot.DotAttr.LABEL;
import static org.junit.Assert.assertNull;
import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import net.certiv.common.diff.Differ;
import net.certiv.common.dot.DotStyle;
import net.certiv.common.graph.demo.DemoEdge;
import net.certiv.common.graph.demo.DemoNode;
import net.certiv.common.stores.UniqueList;
import net.certiv.common.util.FsUtil;

class GraphOpsManipulation extends TestBase {

	@Test
	void testReduceNode() {
		graph.put(Graph.GRAPH_NAME, "Reduce");

		builder.createAndAddEdge("A", "B");
		builder.createAndAddEdge("B", "C");

		DemoNode b = builder.getNode("B");
		graph.reduce(b);

		b = builder.getNode("B");
		assertNull(b);

		String dot = graph.dot();
		String txt = FsUtil.loadCheckedResource(getClass(), "opsReduce1.md").result;
		Differ.diff((String) graph.get(Graph.GRAPH_NAME), dot, txt).sdiff(true, 120);
		assertEquals(dot, txt);
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

		graph.reduce(ab.getFirst(), cd.getFirst());

		DemoNode b = builder.getNode("B");
		assertNull(b);

		String dot = graph.dot();
		String txt = FsUtil.loadCheckedResource(getClass(), "opsReduceEdge1.md").result;
		Differ.diff((String) graph.get(Graph.GRAPH_NAME), dot, txt).sdiff(true, 120);
		assertEquals(dot, txt);
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

		graph.transfer(cf, b); // cf becomes bf

		String dot = graph.dot();
		String txt = FsUtil.loadCheckedResource(getClass(), "opsTransfer1.md").result;

		Differ.diff((String) graph.get(Graph.GRAPH_NAME), dot, txt).sdiff(true, 120);
		assertEquals(dot, txt);
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

		graph.move(ce, b, d);

		String dot = graph.dot();
		String txt = FsUtil.loadCheckedResource(getClass(), "opsMove1.md").result;

		Differ.diff((String) graph.get(Graph.GRAPH_NAME), dot, txt).sdiff(true, 120);
		assertEquals(dot, txt);
	}

	@Test
	void testConsolidate() {
		graph.put(Graph.GRAPH_NAME, "Consolidate");

		builder.createAndAddEdges("Root->[A,D,G]");
		builder.createAndAddEdges("A => B => C");
		builder.createAndAddEdges("D => E => F");
		builder.createAndAddEdges("G => H => I");

		UniqueList<DemoNode> nodes = builder.findNodes("[B,E,H]");
		DemoNode b = builder.getNode("B");

		graph.consolidateEdges(nodes, b);

		String dot = graph.dot();
		String txt = FsUtil.loadCheckedResource(getClass(), "opsConsolidate1.md").result;

		Differ.diff((String) graph.get(Graph.GRAPH_NAME), dot, txt).sdiff(true, 120);
		assertEquals(dot, txt);
	}

	@Test
	void testReplicate() {
		graph.put(Graph.GRAPH_NAME, "Replicate");

		builder.createAndAddEdges("A => B => C");
		UniqueList<DemoNode> targets = builder.findOrCreateNodes("[B,X,Y,Z]");

		DemoNode b = builder.getNode("B");

		graph.replicateEdges(b, targets);

		String dot = graph.dot();
		String txt = FsUtil.loadCheckedResource(getClass(), "opsReplicate1.md").result;

		Differ.diff((String) graph.get(Graph.GRAPH_NAME), dot, txt).sdiff(true, 120);
		assertEquals(dot, txt);
	}

	@Test
	void testReplicateReduce() {
		graph.put(Graph.GRAPH_NAME, "Replicate Remove");

		builder.createAndAddEdges("A => B => C");
		UniqueList<DemoNode> targets = builder.findOrCreateNodes("[X,Y,Z]");

		DemoNode b = builder.getNode("B");

		graph.replicateEdges(b, targets, true);

		String dot = graph.dot();
		String txt = FsUtil.loadCheckedResource(getClass(), "opsReplicate2.md").result;

		Differ.diff((String) graph.get(Graph.GRAPH_NAME), dot, txt).sdiff(true, 120);
		assertEquals(dot, txt);
	}

	@Test
	void testTranspose() {
		graph.put(Graph.GRAPH_NAME, "Transpose");

		builder.createAndAddEdges("A => B => C => D");
		builder.createAndAddEdges("B => E => F => [C,G]");

		/*
		 * NOT IMPLEMENTED
		 */

		// DemoNode b = graph.getNode("B");
		// DemoNode c = graph.getNode("C");
		// graph.transpose(b, c);

		String dot = graph.dot();
		String txt = FsUtil.loadCheckedResource(getClass(), "opsTranspose1.md").result;

		Differ.diff((String) graph.get(Graph.GRAPH_NAME), dot, txt).sdiff(true, 120);
		assertEquals(dot, txt);
	}
}
