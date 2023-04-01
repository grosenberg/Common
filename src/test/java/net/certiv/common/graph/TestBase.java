package net.certiv.common.graph;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;

import net.certiv.common.graph.demo.DemoGraph;

public class TestBase {

	DemoGraph graph = new DemoGraph("Names");

	@BeforeEach
	void setup() {}

	@AfterEach
	void teardown() {
		graph.clear();
		graph.reset();
	}

}
