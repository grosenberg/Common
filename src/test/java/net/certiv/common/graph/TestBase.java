package net.certiv.common.graph;

import org.junit.jupiter.api.AfterEach;

import net.certiv.common.graph.demo.DemoBuilder;
import net.certiv.common.graph.demo.DemoGraph;

public class TestBase {

	DemoGraph graph = new DemoGraph("Names");
	DemoBuilder builder = new DemoBuilder(graph);

	@AfterEach
	void teardown() {
		builder.clear();
		graph.clear();
		graph.reset();
	}
}
