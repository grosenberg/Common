package net.certiv.common.event;

import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.Test;

import net.certiv.common.TestCommon;
import net.certiv.common.graph.Graph;
import net.certiv.common.graph.GraphEvent.GraphChgType;
import net.certiv.common.graph.GraphListener;

class TypedEventDispatcherTest extends TestCommon {

	static final boolean FORCE = false;

	List<GraphChgType> types = Arrays.asList(GraphChgType.values());

	@Test
	void testGraph() {
		graph.put(Graph.GRAPH_NAME, "Build Minimal");

		GraphListener.of(GraphChgType.values()) //
				.action(e -> {
					assertTrue(e.type().isChangeType());
					assertTrue(e.type().isEnum());
					assertTrue(types.contains(e.type()));
				}) //
				.addTo(graph);

		builder.createAndAddEdge("A", "B");
		builder.createAndAddEdge("B", "C");
	}
}
