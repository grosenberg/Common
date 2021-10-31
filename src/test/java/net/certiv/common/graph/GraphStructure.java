package net.certiv.common.graph;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import net.certiv.common.graph.demo.DemoEdge;
import net.certiv.common.graph.demo.DemoGraph;
import net.certiv.common.graph.demo.DemoNode;
import net.certiv.common.util.FsUtil;

class GraphStructure {

	@Test
	void testStructure() {
		DemoGraph graph = new DemoGraph("Structure");

		/* DemoEdge ab = */ graph.createEdge("A", "B");
		DemoEdge bc = graph.createEdge("B", "C");
		/* DemoEdge cd = */ graph.createEdge("C", "D");
		/* DemoEdge de = */ graph.createEdge("D", "E");

		DemoEdge cf = graph.createEdge("C", "F");
		/* DemoEdge fg = */ graph.createEdge("F", "G");

		DemoNode b = bc.beg();
		graph.reterminate(b, cf, false);

		String dot = graph.dot();
		String txt = FsUtil.loadResourceStringChecked(getClass(), "dotStructure.txt");
		assertEquals(dot, txt);
	}
}
