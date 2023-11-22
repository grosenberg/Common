package net.certiv.common.event;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import net.certiv.common.CommonSupport;
import net.certiv.common.graph.GraphEvent;
import net.certiv.common.graph.GraphEvent.GraphEvtType;
import net.certiv.common.graph.demo.DemoEdge;
import net.certiv.common.graph.demo.DemoListener;
import net.certiv.common.graph.demo.DemoNode;
import net.certiv.common.stores.LinkedHashList;
import net.certiv.common.util.test.CommonTestBase;

class GraphListenerTest extends CommonTestBase {

	private final CommonSupport CS = new CommonSupport();
	private final LinkedHashList<GraphEvtType, DemoNode> nodes = new LinkedHashList<>();
	private final LinkedHashList<GraphEvtType, DemoEdge> edges = new LinkedHashList<>();

	@BeforeEach
	public void setup() {
		CS.setup();
	}

	@AfterEach
	public void teardown() {
		nodes.clear();
		edges.clear();
		CS.teardown();
	}

	@Test
	void test() {
		DemoListener.of(GraphEvtType.changeTypes()) //
				.action(e -> handleEvt(e)) //
				.addTo(CS.graph);
		CS.createMinimalNetwork();

		assertEquals(5, nodes.sizeValues());
		assertEquals(4, edges.sizeValues());
	}

	private void handleEvt(GraphEvent<?, ?> e) {
		// Log.debug("Event [%s] %s", e.type(), e.value());
		GraphEvtType type = e.type();
		Object val = e.value();
		if (val instanceof DemoNode) {
			nodes.put(type, (DemoNode) val);
		} else {
			edges.put(type, (DemoEdge) val);
		}
	}
}
