package net.certiv.common.graph;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import net.certiv.common.CommonSupport;
import net.certiv.common.diff.Differ;
import net.certiv.common.graph.demo.DemoNode;
import net.certiv.common.util.test.CommonTestBase;

class BuilderOpsTest extends CommonTestBase {

	static final boolean FORCE = false;
	private final CommonSupport CS = new CommonSupport();

	@BeforeEach
	public void setup() {
		CS.setup();
	}

	@AfterEach
	public void teardown() {
		CS.teardown();
	}

	@Test
	void testGraph() {
		CS.graph.put(Graph.GRAPH_NAME, "Build Minimal");

		CS.builder.createAndAddEdge("A", "B");
		CS.builder.createAndAddEdge("B", "C");

		DemoNode a = CS.builder.getNode("A");
		DemoNode b = CS.builder.getNode("B");
		DemoNode c = CS.builder.getNode("C");

		assertNotNull(a);
		assertNotNull(b);
		assertNotNull(c);

		String dot = CS.graph.render();
		writeResource(getClass(), "build0.md", dot, FORCE);

		String txt = loadResource(getClass(), "build0.md");
		Differ.diff((String) CS.graph.get(Graph.GRAPH_NAME), txt, dot).sdiff(true, 120).out();
		assertEquals(txt, dot);
	}

	@Test
	void testGraphSimple() {
		CS.graph.put(Graph.GRAPH_NAME, "Simple");

		CS.builder.createAndAddEdge("A", "B");
		CS.builder.createAndAddEdge("B", "C");
		CS.builder.createAndAddEdge("B", "D");
		CS.builder.createAndAddEdge("D", "E");

		String dot = CS.graph.render();
		writeResource(getClass(), "build1.md", dot, FORCE);

		String txt = loadResource(getClass(), "build1.md");
		Differ.diff((String) CS.graph.get(Graph.GRAPH_NAME), txt, dot).sdiff(true, 120).out();
		assertEquals(txt, dot);

		String dump = CS.graph.dump();
		String dtxt = loadResource(getClass(), "build1dump.txt");
		assertEquals(dump, dtxt);
	}

	@Test
	void testMultiRoots() {
		CS.graph.put(Graph.GRAPH_NAME, "Two-root Test");

		CS.builder.createAndAddEdge("A", "B");
		CS.builder.createAndAddEdge("B", "C");
		CS.builder.createAndAddEdge("D", "E");

		String dot = CS.graph.render();
		writeResource(getClass(), "build2.md", dot, FORCE);

		String txt = loadResource(getClass(), "build2.md");
		Differ.diff((String) CS.graph.get(Graph.GRAPH_NAME), txt, dot).sdiff(true, 120).out();
		assertEquals(txt, dot);

		String dump = CS.graph.dump();
		String dtxt = loadResource(getClass(), "build2dump.txt");
		assertEquals(dump, dtxt);
	}

	@Test
	void testCyclesSelf() {
		CS.graph.put(Graph.GRAPH_NAME, "Cyclic");

		CS.builder.createAndAddEdge("A", "B");
		CS.builder.createAndAddEdge("B", "B");
		CS.builder.createAndAddEdge("B", "C");

		String dot = CS.graph.render();
		writeResource(getClass(), "build3.md", dot, FORCE);

		String txt = loadResource(getClass(), "build3.md");
		Differ.diff((String) CS.graph.get(Graph.GRAPH_NAME), txt, dot).sdiff(true, 120).out();
		assertEquals(txt, dot);
	}

	@Test
	void testLoops() {
		CS.graph.put(Graph.GRAPH_NAME, "Loop Test");

		CS.builder.createAndAddEdge("A", "B");
		CS.builder.createAndAddEdge("B", "C");
		CS.builder.createAndAddEdge("C", "D");
		CS.builder.createAndAddEdge("D", "E");

		CS.builder.createAndAddEdge("D", "F");
		CS.builder.createAndAddEdge("F", "B");

		String dot = CS.graph.render();
		writeResource(getClass(), "build4.md", dot, FORCE);

		String txt = loadResource(getClass(), "build4.md");
		Differ.diff((String) CS.graph.get(Graph.GRAPH_NAME), txt, dot).sdiff(true, 120).out();
		assertEquals(txt, dot);

		String dump = CS.graph.dump();
		String dtxt = loadResource(getClass(), "build4dump.txt");
		assertEquals(dump, dtxt);
	}

	@Test
	void testMultiEdges() {
		CS.graph.put(Graph.GRAPH_NAME, "Multiple Edges Test");

		CS.builder.createAndAddEdge("A", "B");
		CS.builder.createAndAddEdge("B", "C");
		CS.builder.createAndAddEdge("B", "C");
		CS.builder.createAndAddEdge("B", "C");
		CS.builder.createAndAddEdge("B", "C");
		CS.builder.createAndAddEdge("C", "D");

		String dot = CS.graph.render();
		writeResource(getClass(), "build5.md", dot, FORCE);

		String txt = loadResource(getClass(), "build5.md");
		Differ.diff((String) CS.graph.get(Graph.GRAPH_NAME), txt, dot).sdiff(true, 120).out();
		assertEquals(txt, dot);
	}

	@Test
	void testBuilder() {
		CS.graph.put(Graph.GRAPH_NAME, "Builder");

		CS.builder.createAndAddEdges("[A,B] => C => [Delta, Eta] -> [Z]");

		String dot = CS.graph.render();
		writeResource(getClass(), "build6.md", dot, FORCE);

		String txt = loadResource(getClass(), "build6.md");
		Differ.diff((String) CS.graph.get(Graph.GRAPH_NAME), txt, dot).sdiff(true, 120).out();
		assertEquals(txt, dot);
	}

	@Test
	void testBuilderMulti() {
		CS.graph.put(Graph.GRAPH_NAME, "Builder Multi");

		CS.builder.createAndAddEdges("Root -> [A,B]");
		CS.builder.createAndAddEdges("[A,B] => C => [Delta, Eta] -> [Z]");
		CS.builder.createAndAddEdges("C => [B,Z]");
		CS.builder.createAndAddEdges("C => [B,C]");

		String dot = CS.graph.render();
		writeResource(getClass(), "build7.md", dot, FORCE);

		String txt = loadResource(getClass(), "build7.md");
		Differ.diff((String) CS.graph.get(Graph.GRAPH_NAME), txt, dot).sdiff(true, 120).out();
		assertEquals(txt, dot);
	}
}
