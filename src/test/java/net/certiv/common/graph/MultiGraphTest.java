package net.certiv.common.graph;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import net.certiv.common.diff.Differ;
import net.certiv.common.graph.demo.DemoNode;
import net.certiv.common.util.FsUtil;

class MultiGraphTest extends TestGraphBase {

	static final boolean FORCE = false;

	@BeforeEach
	void setup() {
		graph.put(Graph.GRAPH_NAME, "Multigraph Test");
	}

	@Test
	void multiTest() {
		builder.createAndAddEdges("A->B->C");
		builder.createAndAddEdges("A->B->C");
		builder.createAndAddEdges("C->B->A");
		builder.createAndAddEdges("C->B->A");
		builder.createAndAddEdges("C->A");
		nameEdges();

		String dot = graph.render();
		writeResource(getClass(), "multi1.md", dot, FORCE);

		String txt = FsUtil.loadResource(getClass(), "multi1.md").value;
		Differ.diff((String) graph.get(Graph.GRAPH_NAME), txt, dot).sdiff(true, 120).out();
		assertEquals(txt, dot);
	}

	@Test
	void multiRootTest() {
		builder.createAndAddEdges("A->A");
		builder.createAndAddEdges("B->C->D");
		builder.createAndAddEdges("E->F->G->E");
		nameEdges();

		DemoNode a = builder.getNode("A");
		DemoNode b = builder.getNode("B");
		DemoNode e = builder.getNode("E");
		assertEquals(List.of(a, b, e), graph.getRoots());

		String dot = graph.render();
		writeResource(getClass(), "multi2.md", dot, FORCE);

		String txt = FsUtil.loadResource(getClass(), "multi2.md").value;
		Differ.diff((String) graph.get(Graph.GRAPH_NAME), txt, dot).sdiff(true, 120).out();
		assertEquals(txt, dot);
	}
}
