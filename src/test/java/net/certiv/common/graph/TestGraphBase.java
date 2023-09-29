package net.certiv.common.graph;

import static net.certiv.common.dot.DotAttr.LABEL;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;

import net.certiv.common.dot.DotStyle;
import net.certiv.common.graph.demo.DemoBuilder;
import net.certiv.common.graph.demo.DemoEdge;
import net.certiv.common.graph.demo.DemoGraph;
import net.certiv.common.util.test.TestBase;

public class TestGraphBase extends TestBase {

	static final boolean FORCE = false;

	static final String XForm = "xform/";
	static final String XFuture = "xfuture/";

	DemoGraph graph;
	DemoBuilder builder;

	@BeforeEach
	void setupBase() {
		global(FORCE);
		graph = new DemoGraph("Names");
		builder = new DemoBuilder(graph);
	}

	@AfterEach
	void teardownBase() {
		reset();
	}

	public void reset() {
		builder.clear();
		graph.clear();
		graph.reset();
	}

	public void nameEdges() {
		for (DemoEdge edge : graph.getEdges(true)) {
			DotStyle ds = edge.getDotStyle();
			ds.put(LABEL, edge.name());
		}
	}
}
