package net.certiv.common.graph;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import net.certiv.common.graph.demo.DemoEdge;
import net.certiv.common.graph.demo.DemoGraph;
import net.certiv.common.graph.demo.DiffUtil;
import net.certiv.common.util.FsUtil;

class GraphStructure {

	@Test
	void testStructure() {
		DemoGraph graph = new DemoGraph("Structure");

		graph.createEdge("A", "B");
		DemoEdge bc = graph.createEdge("B", "C");
		graph.createEdge("C", "D");
		graph.createEdge("D", "E");

		DemoEdge cf = graph.createEdge("C", "F");
		graph.createEdge("F", "G");

		graph.reterminate(bc.beg(), cf, false);

		String dot = graph.dot();
		String txt = FsUtil.loadResourceStringChecked(getClass(), "dotStructure.txt");
		System.out.println(DiffUtil.diff(dot, txt));
		assertEquals(dot, txt);
	}
}
