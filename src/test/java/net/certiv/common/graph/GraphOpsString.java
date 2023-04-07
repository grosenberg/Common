package net.certiv.common.graph;

import static org.junit.Assert.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import net.certiv.common.diff.Differ;
import net.certiv.common.graph.demo.DemoEdge;
import net.certiv.common.graph.demo.DemoNode;
import net.certiv.common.stores.UniqueList;
import net.certiv.common.util.FsUtil;

class GraphOpsString extends TestBase {

	@BeforeEach
	void testStringOps() {
		graph.put(Graph.GRAPH_NAME, "StringOps");

		builder.createAndAddEdges("A->B->C->D->E");
		builder.createAndAddEdges("C->F->G");
		builder.createAndAddEdges("C->[B,C,E]");
	}

	@Test
	void verifyStructure() {
		String dot = graph.dot();
		String txt = FsUtil.loadCheckedResource(getClass(), "opsString1.md").result;
		Differ.diff((String) graph.get(Graph.GRAPH_NAME), dot, txt).sdiff(true, 120);
		assertEquals(dot, txt);
	}

	@Test
	void testFindOrCreateNode() {
		DemoNode a = builder.findOrCreateNode("A");
		assertNotNull(a);
		assertTrue(graph.contains(a));

		DemoNode z = builder.findOrCreateNode("Z");
		assertNotNull(z);
		assertFalse(graph.contains(z));
	}

	@Test
	void testCreateEdgeStringString() {
		DemoEdge ab = builder.createEdge("A", "B");
		assertNotNull(ab);
		assertFalse(graph.contains(ab));

		graph.addEdge(ab);
		assertTrue(graph.contains(ab));
		assertEquals(graph.getEdges(ab.beg(), ab.end()).size(), 2);
	}

	@Test
	void testCreateAndAddEdgeStringString() {
		DemoEdge abOld = builder.getEdges("A", "B").getFirst();
		assertNotNull(abOld);

		DemoEdge abNew = builder.createAndAddEdge("A", "B");
		assertNotNull(abNew);
		assertTrue(graph.contains(abNew));
		assertEquals(graph.getEdges(abNew.beg(), abNew.end()).size(), 2);

		graph.addEdge(abNew);
		assertTrue(graph.contains(abOld));
		assertTrue(graph.contains(abNew));
		assertEquals(graph.getEdges(abNew.beg(), abNew.end()).size(), 2);
	}

	@Test
	void testGetNode() {
		DemoNode b = builder.getNode("B");
		assertNotNull(b);

		DemoNode c = builder.getNode("C");
		assertNotNull(c);

		DemoNode e = builder.getNode("E");
		assertNotNull(e);
	}

	@Test
	void testVerifyUnique() {
		assertTrue(builder.verifyUnique("Z"));
	}

	@Test
	void testGetEdgesStringString() {
		UniqueList<DemoEdge> bc = builder.getEdges("B", "C");
		assertEquals(bc.size(), 2);

		UniqueList<DemoEdge> ce = builder.getEdges("C", "E");
		assertEquals(ce.size(), 1);

		UniqueList<DemoEdge> cc = builder.getEdges("C", "C");
		assertEquals(cc.size(), 1);
	}
}
