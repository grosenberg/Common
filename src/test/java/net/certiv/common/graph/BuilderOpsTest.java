package net.certiv.common.graph;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.junit.jupiter.api.Test;

import net.certiv.common.diff.Differ;
import net.certiv.common.graph.demo.DemoNode;
import net.certiv.common.util.FsUtil;

class BuilderOpsTest extends TestGraphBase {

	static final boolean FORCE = false;

	@Test
	void testGraph() {
		graph.put(Graph.GRAPH_NAME, "Build Minimal");

		builder.createAndAddEdge("A", "B");
		builder.createAndAddEdge("B", "C");

		DemoNode a = builder.getNode("A");
		DemoNode b = builder.getNode("B");
		DemoNode c = builder.getNode("C");

		assertNotNull(a);
		assertNotNull(b);
		assertNotNull(c);

		String dot = graph.render();
		writeResource(getClass(), "build0.md", dot, FORCE);

		String txt = FsUtil.loadResource(getClass(), "build0.md").value;
		Differ.diff((String) graph.get(Graph.GRAPH_NAME), txt, dot).sdiff(true, 120).out();
		assertEquals(txt, dot);
	}

	@Test
	void testGraphSimple() {
		graph.put(Graph.GRAPH_NAME, "Simple");

		builder.createAndAddEdge("A", "B");
		builder.createAndAddEdge("B", "C");
		builder.createAndAddEdge("B", "D");
		builder.createAndAddEdge("D", "E");

		String dot = graph.render();
		writeResource(getClass(), "build1.md", dot, FORCE);

		String txt = FsUtil.loadResource(getClass(), "build1.md").value;
		Differ.diff((String) graph.get(Graph.GRAPH_NAME), txt, dot).sdiff(true, 120).out();
		assertEquals(txt, dot);

		String dump = graph.dump();
		String dtxt = FsUtil.loadResource(getClass(), "build1dump.txt").value;
		assertEquals(dump, dtxt);
	}

	@Test
	void testMultiRoots() {
		graph.put(Graph.GRAPH_NAME, "Two-root Test");

		builder.createAndAddEdge("A", "B");
		builder.createAndAddEdge("B", "C");
		builder.createAndAddEdge("D", "E");

		String dot = graph.render();
		writeResource(getClass(), "build2.md", dot, FORCE);

		String txt = FsUtil.loadResource(getClass(), "build2.md").value;
		Differ.diff((String) graph.get(Graph.GRAPH_NAME), txt, dot).sdiff(true, 120).out();
		assertEquals(txt, dot);

		String dump = graph.dump();
		String dtxt = FsUtil.loadResource(getClass(), "build2dump.txt").value;
		assertEquals(dump, dtxt);
	}

	@Test
	void testCyclesSelf() {
		graph.put(Graph.GRAPH_NAME, "Cyclic");

		builder.createAndAddEdge("A", "B");
		builder.createAndAddEdge("B", "B");
		builder.createAndAddEdge("B", "C");

		String dot = graph.render();
		writeResource(getClass(), "build3.md", dot, FORCE);

		String txt = FsUtil.loadResource(getClass(), "build3.md").value;
		Differ.diff((String) graph.get(Graph.GRAPH_NAME), txt, dot).sdiff(true, 120).out();
		assertEquals(txt, dot);
	}

	@Test
	void testLoops() {
		graph.put(Graph.GRAPH_NAME, "Loop Test");

		builder.createAndAddEdge("A", "B");
		builder.createAndAddEdge("B", "C");
		builder.createAndAddEdge("C", "D");
		builder.createAndAddEdge("D", "E");

		builder.createAndAddEdge("D", "F");
		builder.createAndAddEdge("F", "B");

		String dot = graph.render();
		writeResource(getClass(), "build4.md", dot, FORCE);

		String txt = FsUtil.loadResource(getClass(), "build4.md").value;
		Differ.diff((String) graph.get(Graph.GRAPH_NAME), txt, dot).sdiff(true, 120).out();
		assertEquals(txt, dot);

		String dump = graph.dump();
		String dtxt = FsUtil.loadResource(getClass(), "build4dump.txt").value;
		assertEquals(dump, dtxt);
	}

	@Test
	void testMultiEdges() {
		graph.put(Graph.GRAPH_NAME, "Multiple Edges Test");

		builder.createAndAddEdge("A", "B");
		builder.createAndAddEdge("B", "C");
		builder.createAndAddEdge("B", "C");
		builder.createAndAddEdge("B", "C");
		builder.createAndAddEdge("B", "C");
		builder.createAndAddEdge("C", "D");

		String dot = graph.render();
		writeResource(getClass(), "build5.md", dot, FORCE);

		String txt = FsUtil.loadResource(getClass(), "build5.md").value;
		Differ.diff((String) graph.get(Graph.GRAPH_NAME), txt, dot).sdiff(true, 120).out();
		assertEquals(txt, dot);
	}

	@Test
	void testBuilder() {
		graph.put(Graph.GRAPH_NAME, "Builder");

		builder.createAndAddEdges("[A,B] => C => [Delta, Eta] -> [Z]");

		String dot = graph.render();
		writeResource(getClass(), "build6.md", dot, FORCE);

		String txt = FsUtil.loadResource(getClass(), "build6.md").value;
		Differ.diff((String) graph.get(Graph.GRAPH_NAME), txt, dot).sdiff(true, 120).out();
		assertEquals(txt, dot);
	}

	@Test
	void testBuilderMulti() {
		graph.put(Graph.GRAPH_NAME, "Builder Multi");

		builder.createAndAddEdges("Root -> [A,B]");
		builder.createAndAddEdges("[A,B] => C => [Delta, Eta] -> [Z]");
		builder.createAndAddEdges("C => [B,Z]");
		builder.createAndAddEdges("C => [B,C]");

		String dot = graph.render();
		writeResource(getClass(), "build7.md", dot, FORCE);

		String txt = FsUtil.loadResource(getClass(), "build7.md").value;
		Differ.diff((String) graph.get(Graph.GRAPH_NAME), txt, dot).sdiff(true, 120).out();
		assertEquals(txt, dot);
	}
}
