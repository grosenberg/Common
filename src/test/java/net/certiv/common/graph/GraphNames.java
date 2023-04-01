package net.certiv.common.graph;

import static org.junit.Assert.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import net.certiv.common.graph.Edge.Sense;
import net.certiv.common.graph.demo.DemoEdge;
import net.certiv.common.graph.demo.DemoNode;
import net.certiv.common.stores.UniqueDeque;

class GraphNames extends TestBase {

	@Test
	void testNames() {

		graph.put(Graph.GRAPH_NAME, "Names");

		graph.createEdge("A", "B");
		graph.createEdge("B", "C");
		graph.createEdge("C", "D");
		graph.createEdge("D", "E");

		graph.createEdge("C", "F");
		graph.createEdge("F", "G");

		DemoNode b = graph.getNode("B");
		assertNotNull(b);

		// get all edges between C & F:
		UniqueDeque<DemoEdge> cf = graph.getEdges("C", "F");
		assertEquals(cf.size(), 1);

		DemoNode c = graph.getNode("C");
		assertNotNull(c);

		// get all edges outbound from C:
		UniqueDeque<DemoEdge> cout = c.edges(Sense.OUT, false);
		assertEquals(cout.size(), 2);
	}
}
