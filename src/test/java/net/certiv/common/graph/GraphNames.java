package net.certiv.common.graph;

import static org.junit.Assert.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import net.certiv.common.diff.Differ;
import net.certiv.common.graph.Edge.Sense;
import net.certiv.common.graph.demo.DemoEdge;
import net.certiv.common.graph.demo.DemoNode;
import net.certiv.common.stores.UniqueDeque;
import net.certiv.common.util.FsUtil;

class GraphNames extends TestBase {

	@Test
	void testNames() {

		graph.put(Graph.GRAPH_NAME, "Names");

		graph.createAndAddEdge("A", "B");
		graph.createAndAddEdge("B", "C");
		graph.createAndAddEdge("C", "D");
		graph.createAndAddEdge("D", "E");

		graph.createAndAddEdge("C", "F");
		graph.createAndAddEdge("F", "G");

		graph.createAndAddEdge("C", "C");

		DemoNode b = graph.getNode("B");
		assertNotNull(b);

		// get all edges between C & F:
		UniqueDeque<DemoEdge> cf = graph.getEdges("C", "F");
		assertEquals(cf.size(), 1);

		DemoNode c = graph.getNode("C");
		assertNotNull(c);

		// get all edges between C & C:
		UniqueDeque<DemoEdge> cc = graph.getEdges("C", "C");
		assertEquals(cc.size(), 1);

		// get all non-cyclic edges outbound from C:
		UniqueDeque<DemoEdge> cout = c.edges(Sense.OUT);
		assertEquals(cout.size(), 2);

		// get all edges, including cyclic, outbound from C:
		cout = c.edges(Sense.OUT, true);
		assertEquals(cout.size(), 3);

		String dot = graph.dot();

		String txt = FsUtil.loadCheckedResource(getClass(), "dotNames1.txt").result;
		Differ.diff((String) graph.get(Graph.GRAPH_NAME), dot, txt).sdiff(true, 120);
		assertEquals(dot, txt);
	}
}
