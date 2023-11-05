package net.certiv.common;

import static net.certiv.common.dot.DotAttr.LABEL;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;

import net.certiv.common.dot.DotStyle;
import net.certiv.common.graph.demo.DemoBuilder;
import net.certiv.common.graph.demo.DemoEdge;
import net.certiv.common.graph.demo.DemoGraph;
import net.certiv.common.stores.context.Key;
import net.certiv.common.util.test.CommonTestBase;

public class TestCommon extends CommonTestBase {

	protected static final boolean FORCE = false;
	protected static final Key<String> MARK = Key.of("mark");

	protected static final String XForm = "xform/";
	protected static final String XFuture = "xfuture/";

	protected DemoGraph graph;
	protected DemoBuilder builder;

	@BeforeEach
	public void setupBase() {
		// Log.setTestMode(true);
		global(FORCE);
		graph = new DemoGraph("Names");
		builder = new DemoBuilder(graph);
	}

	@AfterEach
	public void teardownBase() {
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
