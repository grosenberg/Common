package net.certiv.common.graph;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import net.certiv.common.CommonSupport;
import net.certiv.common.diff.Differ;
import net.certiv.common.graph.Edge.Sense;
import net.certiv.common.graph.demo.DemoEdge;
import net.certiv.common.graph.demo.DemoNode;
import net.certiv.common.stores.UniqueList;
import net.certiv.common.util.test.CommonTestBase;

class BuilderStrOpsTest extends CommonTestBase {

	static final boolean FORCE = false;
	private final CommonSupport CS = new CommonSupport();

	@BeforeEach
	public void setup() {
		CS.setup();
		CS.graph.put(Graph.GRAPH_NAME, "StringOps");
	}

	@AfterEach
	public void teardown() {
		CS.teardown();
	}

	@Test
	void verifyMultiNetwork() {
		CS.createMultiNetwork();

		String dot = CS.graph.render();
		writeResource(getClass(), "Structure.md", dot, FORCE);

		String txt = loadResource(getClass(), "Structure.md");
		Differ.diff((String) CS.graph.get(Graph.GRAPH_NAME), txt, dot).sdiff(true, 120).out();
		assertEquals(txt, dot);
	}

	@Test
	void testFindOrCreateNode() {
		CS.createMultiNetwork();

		DemoNode a = CS.builder.findOrCreateNode("A");
		assertNotNull(a);
		assertTrue(CS.graph.contains(a));

		DemoNode z = CS.builder.findOrCreateNode("Z");
		assertNotNull(z);
		assertFalse(CS.graph.contains(z));
	}

	@Test
	void testCreateThenAddEdge() {
		DemoEdge ab = CS.builder.createEdge("A", "B");
		assertNotNull(ab);
		assertFalse(CS.graph.contains(ab));

		CS.graph.addEdge(ab);
		assertTrue(CS.graph.contains(ab));
	}

	@Test
	void testCreateAndAddEdge() {
		CS.createMultiNetwork();

		UniqueList<DemoEdge> abList = CS.builder.getEdges("A", "B");
		assertEquals(1, abList.size());

		DemoEdge ab1 = abList.getFirst();
		DemoEdge ab2 = CS.builder.createAndAddEdge("A", "B");
		assertNotNull(ab1);
		assertNotNull(ab2);
		assertTrue(CS.graph.contains(ab1));
		assertTrue(CS.graph.contains(ab2));
		assertEquals(2, CS.graph.getEdges(ab2.beg(), ab2.end()).size());

		DemoEdge ab3 = CS.builder.createAndAddEdge("B", "A");
		assertEquals(1, ab3.beg().to(ab3.end()).size());

		abList = CS.graph.getEdges(Sense.BOTH, ab3.beg(), ab3.end());
		assertEquals(3, abList.size());
	}

	@Test
	void testGetNode() {
		CS.createMinimalNetwork();
		DemoNode b = CS.builder.getNode("B");
		assertNotNull(b);

		DemoNode c = CS.builder.getNode("C");
		assertNotNull(c);

		DemoNode f = CS.builder.getNode("F");
		assertTrue(f == null);
	}

	@Test
	void testVerifyUnique() {
		assertTrue(CS.builder.verifyUnique("A"));
		assertTrue(CS.builder.verifyUnique("Z"));
	}

	@Test
	void testGetEdges() {
		CS.createMultiNetwork();
		UniqueList<DemoEdge> bc = CS.builder.getEdges("B", "C");
		assertEquals(2, bc.size());

		UniqueList<DemoEdge> ce = CS.builder.getEdges("C", "E");
		assertEquals(1, ce.size());

		UniqueList<DemoEdge> cc = CS.builder.getEdges("C", "C");
		assertEquals(1, cc.size());
	}
}
