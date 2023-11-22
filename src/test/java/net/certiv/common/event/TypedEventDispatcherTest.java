package net.certiv.common.event;

import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import net.certiv.common.CommonSupport;
import net.certiv.common.graph.Graph;
import net.certiv.common.graph.GraphEvent.GraphEvtType;
import net.certiv.common.graph.demo.DemoListener;
import net.certiv.common.util.test.CommonTestBase;

class TypedEventDispatcherTest extends CommonTestBase {

	private final List<GraphEvtType> Types = Arrays.asList(GraphEvtType.values());
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
		CS.graph.put(Graph.GRAPH_NAME, "Dispatcher");

		DemoListener.of(GraphEvtType.values()) //
				.action(e -> {
					assertTrue(e.type().isChangeType());
					assertTrue(e.type().isEnum());
					assertTrue(Types.contains(e.type()));
				}) //
				.addTo(CS.graph);

		CS.createMultiNetwork(); // should only cause change events
	}
}
