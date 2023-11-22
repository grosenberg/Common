package net.certiv.common.graph;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.List;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import net.certiv.common.CommonSupport;
import net.certiv.common.diff.Differ;
import net.certiv.common.graph.demo.DemoNode;
import net.certiv.common.util.test.CommonTestBase;

class MultiGraphTest extends CommonTestBase {

	static final boolean FORCE = false;
	private final CommonSupport CS = new CommonSupport();

	@BeforeEach
	public void setup() {
		CS.setup();
		CS.graph.put(Graph.GRAPH_NAME, "Multigraph Test");
	}

	@AfterEach
	public void teardown() {
		CS.teardown();
	}

	@Test
	void multiTest() {
		CS.builder.createAndAddEdges("A->B->C");
		CS.builder.createAndAddEdges("A->B->C");
		CS.builder.createAndAddEdges("C->B->A");
		CS.builder.createAndAddEdges("C->B->A");
		CS.builder.createAndAddEdges("C->A");
		CS.nameEdges();

		String dot = CS.graph.render();
		writeResource(getClass(), "multi1.md", dot, FORCE);

		String txt = loadResource(getClass(), "multi1.md");
		Differ.diff((String) CS.graph.get(Graph.GRAPH_NAME), txt, dot).sdiff(true, 120).out();
		assertEquals(txt, dot);
	}

	@Test
	void multiRootTest() {
		CS.builder.createAndAddEdges("A->A");
		CS.builder.createAndAddEdges("B->C->D");
		CS.builder.createAndAddEdges("E->F->G->E");
		CS.nameEdges();

		DemoNode a = CS.builder.getNode("A");
		DemoNode b = CS.builder.getNode("B");
		DemoNode e = CS.builder.getNode("E");
		assertEquals(List.of(a, b, e), CS.graph.getRoots());

		String dot = CS.graph.render();
		writeResource(getClass(), "multi2.md", dot, FORCE);

		String txt = loadResource(getClass(), "multi2.md");
		Differ.diff((String) CS.graph.get(Graph.GRAPH_NAME), txt, dot).sdiff(true, 120).out();
		assertEquals(txt, dot);
	}
}
