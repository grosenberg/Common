package net.certiv.common.event;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Set;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import net.certiv.common.CommonSupport;
import net.certiv.common.graph.Graph;
import net.certiv.common.graph.GraphEvent.GraphEvtType;
import net.certiv.common.graph.demo.DemoListener;
import net.certiv.common.util.test.CommonTestBase;

class TypedEventDispatcherTest extends CommonTestBase {

	private final Set<GraphEvtType> Types = GraphEvtType.allTypes();
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
		CS.graph.put(Graph.GRAPH_ID, "Dispatcher");

		DemoListener.of(GraphEvtType.allTypes()) //
				.action(e -> {
					assertTrue(e.type().isChangeType());
					assertFalse(e.type().isEnum());
					assertTrue(Types.contains(e.type()));
				}) //
				.addTo(CS.graph);

		CS.createMultiNetwork(); // should only cause change events
	}
}
