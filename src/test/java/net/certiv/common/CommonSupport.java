package net.certiv.common;

import static net.certiv.common.dot.DotAttr.LABEL;

import net.certiv.common.dot.DotStyle;
import net.certiv.common.graph.demo.DemoBuilder;
import net.certiv.common.graph.demo.DemoEdge;
import net.certiv.common.graph.demo.DemoGraph;
import net.certiv.common.graph.id.IdFactory;
import net.certiv.common.log.Log;
import net.certiv.common.stores.context.Key;

public class CommonSupport {

	public static final Key<String> MARK = Key.of("mark");

	public static final String XForm = "xform/";
	public static final String XFuture = "xfuture/";

	public IdFactory factory;
	public DemoGraph graph;
	public DemoBuilder builder;

	public void setup() {
		Log.setTestMode(true);
		factory = new IdFactory("Demo");
		graph = new DemoGraph(factory.make("Names"));
		builder = new DemoBuilder(graph);
	}

	public void teardown() {
		builder.clear();
		graph.clear();
		graph.reset();
		graph = null;
		builder = null;
	}

	public void createMinimalNetwork() {
		builder.createAndAddEdges("A->B->C");
		builder.createAndAddEdges("B->D->E");
		nameEdges();
	}

	public void createMinimalCyclicNetwork() {
		builder.createAndAddEdges("A->B->C->D");
		builder.createAndAddEdges("C->C");
		nameEdges();
	}

	public void createMultiNetwork() {
		builder.createAndAddEdges("A->B->C");
		builder.createAndAddEdges("B->D->E");
		builder.createAndAddEdges("C->F->G");
		builder.createAndAddEdges("C->[B,C,E]");
		builder.createAndAddEdges("E->H");
		nameEdges();
	}

	public void createMultiRootNetwork() {
		builder.createAndAddEdges("A->B->C->D->E");
		builder.createAndAddEdges("C->F->G->H->I");
		builder.createAndAddEdges("C->[B,C,E]");

		builder.createAndAddEdges("U->X->Y");
		builder.createAndAddEdges("U->Z");
		nameEdges();
	}

	public void nameEdges() {
		for (DemoEdge edge : graph.getEdges(true)) {
			DotStyle ds = edge.getDotStyle();
			ds.put(LABEL, edge.name());
		}
	}
}
