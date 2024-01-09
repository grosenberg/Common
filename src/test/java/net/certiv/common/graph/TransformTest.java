package net.certiv.common.graph;

import static net.certiv.common.dot.DotAttr.COLOR;
import static net.certiv.common.dot.DotAttr.LABEL;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import net.certiv.common.CommonSupport;
import net.certiv.common.diff.Differ;
import net.certiv.common.dot.DotStyle;
import net.certiv.common.graph.demo.DemoEdge;
import net.certiv.common.graph.demo.DemoNode;
import net.certiv.common.graph.id.Id;
import net.certiv.common.stores.UniqueList;
import net.certiv.common.util.test.CommonTestBase;

class TransformTest extends CommonTestBase {

	static final boolean FORCE = false;
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
	void testReduceNode() {
		CS.graph.setId(CS.factory.make("Reduce"));

		CS.builder.createAndAddEdge("A", "B");
		CS.builder.createAndAddEdge("B", "C");

		DemoNode b = CS.builder.getNode("B");

		Transformer<Id, DemoNode, DemoEdge> xf = new Transformer<>(CS.graph);
		xf.reduce(b);

		b = CS.builder.getNode("B");
		assertNull(b);

		String dot = CS.graph.render();
		writeResource(getClass(), CommonSupport.XForm + "Reduce1.md", dot, FORCE);

		String txt = loadResource(getClass(), CommonSupport.XForm + "Reduce1.md");
		Differ.diff(CS.graph.displayName(), txt, dot).sdiff(true, 120).out();
		assertEquals(txt, dot);
	}

	@Test
	void testReduceEdge() {
		CS.graph.setId(CS.factory.make("ReduceEdge"));

		CS.builder.createAndAddEdge("R", "A");
		CS.builder.createAndAddEdge("A", "B");
		CS.builder.createAndAddEdge("R", "C");
		CS.builder.createAndAddEdge("C", "D");

		UniqueList<DemoEdge> ab = CS.builder.getEdges("A", "B");
		UniqueList<DemoEdge> cd = CS.builder.getEdges("C", "D");

		Transformer<Id, DemoNode, DemoEdge> xf = new Transformer<>(CS.graph);
		xf.reduce(ab.getFirst(), cd.getFirst());

		DemoNode b = CS.builder.getNode("B");
		assertNull(b);

		String dot = CS.graph.render();
		writeResource(getClass(), CommonSupport.XForm + "ReduceEdge1.md", dot, FORCE);

		String txt = loadResource(getClass(), CommonSupport.XForm + "ReduceEdge1.md");
		Differ.diff(CS.graph.displayName(), txt, dot).sdiff(true, 120).out();
		assertEquals(txt, dot);
	}

	@Test
	void testTransfer() {
		CS.graph.setId(CS.factory.make("Transfer"));

		CS.builder.createAndAddEdge("A", "B");
		CS.builder.createAndAddEdge("B", "C");
		CS.builder.createAndAddEdge("C", "D");
		CS.builder.createAndAddEdge("D", "E");

		CS.builder.createAndAddEdge("C", "F");
		CS.builder.createAndAddEdge("F", "G");

		DemoNode b = CS.builder.getNode("B");
		UniqueList<DemoEdge> cf = CS.builder.getEdges("C", "F");

		Transformer<Id, DemoNode, DemoEdge> xf = new Transformer<>(CS.graph);
		xf.transfer(cf, b); // cf becomes bf

		String dot = CS.graph.render();
		writeResource(getClass(), CommonSupport.XForm + "Transfer1.md", dot, FORCE);

		String txt = loadResource(getClass(), CommonSupport.XForm + "Transfer1.md");
		Differ.diff(CS.graph.displayName(), txt, dot).sdiff(true, 120).out();
		assertEquals(txt, dot);
	}

	@Test
	void testMove() {
		CS.graph.setId(CS.factory.make("Move"));

		CS.builder.createAndAddEdges("A->B->C->D->E");

		DemoEdge ce = CS.builder.createAndAddEdge("C", "E");
		DotStyle ds = ce.getDotStyle();
		ds.put(LABEL, "Edge " + ce.name());
		ds.put(COLOR, "blue");

		DemoNode b = CS.builder.getNode("B");
		DemoNode d = CS.builder.getNode("D");

		Transformer<Id, DemoNode, DemoEdge> xf = new Transformer<>(CS.graph);
		xf.move(ce, b, d);

		String dot = CS.graph.render();
		writeResource(getClass(), CommonSupport.XForm + "Move1.md", dot, FORCE);

		String txt = loadResource(getClass(), CommonSupport.XForm + "Move1.md");
		Differ.diff(CS.graph.displayName(), txt, dot).sdiff(true, 120).out();
		assertEquals(txt, dot);
	}

	@Test
	void testReplicate() {
		CS.graph.setId(CS.factory.make("Replicate"));

		CS.builder.createAndAddEdges("A => B => C");
		UniqueList<DemoNode> targets = CS.builder.findOrCreateNodes("[B,X,Y,Z]");

		DemoNode b = CS.builder.getNode("B");

		Transformer<Id, DemoNode, DemoEdge> xf = new Transformer<>(CS.graph);
		xf.replicateEdges(b, targets); // replicate =>B=> to =>[targets-B]=>

		String dot = CS.graph.render();
		writeResource(getClass(), CommonSupport.XForm + "Replicate1.md", dot, FORCE);

		String txt = loadResource(getClass(), CommonSupport.XForm + "Replicate1.md");
		Differ.diff(CS.graph.displayName(), txt, dot).sdiff(true, 120).out();
		assertEquals(txt, dot);
	}

	@Test
	void testReplicateReduce() {
		CS.graph.setId(CS.factory.make("Replicate Remove"));

		CS.builder.createAndAddEdges("A => B => C");
		UniqueList<DemoNode> targets = CS.builder.findOrCreateNodes("[X,Y,Z]");

		DemoNode b = CS.builder.getNode("B");

		Transformer<Id, DemoNode, DemoEdge> xf = new Transformer<>(CS.graph);
		xf.replicateEdges(b, targets, true);

		String dot = CS.graph.render();
		writeResource(getClass(), CommonSupport.XForm + "Replicate2.md", dot, FORCE);

		String txt = loadResource(getClass(), CommonSupport.XForm + "Replicate2.md");
		Differ.diff(CS.graph.displayName(), txt, dot).sdiff(true, 120).out();
		assertEquals(txt, dot);
	}
}
