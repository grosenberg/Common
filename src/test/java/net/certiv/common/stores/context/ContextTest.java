package net.certiv.common.stores.context;

import static org.junit.Assert.assertTrue;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

import java.util.UUID;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class ContextTest {

	Key<String> NAME = Key.of("context.text.name");
	Key<Integer> DATA = Key.of("context.text.data");

	Context context;

	IKVStore a;
	IKVStore b;
	IKVStore c;
	IKVStore d;
	IKVStore e;
	IKVStore f;

	@BeforeEach
	void setUp() throws Exception {
		a = createStore("A", 0);
		b = createStore("B", 1);
		c = createStore("C", 2);
		d = createStore("D", 3);
		e = createStore("E", 4);
		f = createStore("F", 5);
	}

	@AfterEach
	void tearDown() throws Exception {}

	@Test
	void testOf() {
		context = Context.of();
		assertEquals(context.depth(), 1);
		assertEquals(context.maxDepth(), 1);
	}

	@Test
	void testMaxDepth() {
		context = Context.of(3);
		assertEquals(context.depth(), 1);
		assertEquals(context.maxDepth(), 3);

		context.merge(a);
		context.merge(b);
		context.merge(c);
		context.merge(d);
		context.merge(e);
		context.merge(f);

		assertEquals(context.depth(), 3);
		assertEquals(context.maxDepth(), 3);

	}

	@Test
	void testAdjustMaxDepthUp() {
		context = Context.of(2);
		context.merge(a);
		context.merge(b);
		context.merge(c);

		assertEquals(context.depth(), 2);
		assertEquals(context.maxDepth(), 2);

		context.adjustMaxDepth(4, false);

		assertEquals(context.depth(), 2);
		assertEquals(context.maxDepth(), 4);

		context.merge(a);
		context.merge(b);
		context.merge(c);
		context.merge(d);
		context.merge(e);
		context.merge(f);

		assertEquals(context.depth(), 4);
		assertEquals(context.maxDepth(), 4);

		assertEquals(context.delta(), f);
	}

	@Test
	void testAdjustMaxDepthDownTrim() {
		context = Context.of(4);
		context.merge(a);
		context.merge(b);
		context.merge(c);
		context.merge(d);

		assertEquals(context.depth(), 4);
		assertEquals(context.maxDepth(), 4);

		context.adjustMaxDepth(2, true);

		assertEquals(context.depth(), 2);
		assertEquals(context.maxDepth(), 2);

		assertEquals(context.delta(), d);
	}

	@Test
	void testAdjustMaxDepthDownFlatten() {
		context = Context.of(4);
		context.merge(a);
		context.merge(b);
		context.merge(c);
		context.merge(d);

		assertEquals(context.depth(), 4);
		assertEquals(context.maxDepth(), 4);

		context.adjustMaxDepth(2, false);

		assertEquals(context.depth(), 2);
		assertEquals(context.maxDepth(), 2);

		assertEquals(context.delta(), d);
	}

	@Test
	void testDelta() {
		context = Context.of();
		assertEquals(context.size(), 1);

		context.merge(a);
		assertEquals(context.delta(), a);

		context.merge(b);
		assertEquals(context.delta(), b);
	}

	@Test
	void testMerge() {
		context = Context.of(3);
		context.merge(a);

		assertEquals(context.delta(), a);

		assertEquals(context.depth(), 2);
		assertEquals(context.maxDepth(), 3);

		context.merge(b);
		assertEquals(context.depth(), 3);
		assertEquals(context.maxDepth(), 3);

		context.merge(c);
		assertEquals(context.depth(), 3);
		assertEquals(context.maxDepth(), 3);
	}

	@Test
	void testShade() {
		context = Context.of(4);
		context.merge(a);
		context.merge(b);
		context.merge(c);
		context.merge(d);

		assertEquals(context.size(), 7);

		String visible = "[kvstore.uuid, " //
				+ "context.text.name, context.text.data, " //
				+ "context.text.name.layer[3], " //
				+ "context.text.name.layer[2], " //
				+ "context.text.name.layer[1], " //
				+ "context.text.name.layer[0]]"; //
		assertEquals(context.keys().toString(), visible);
	}

	@Test
	void testRestore() {
		context = Context.of(4);
		context.merge(a);
		context.merge(b);
		String nameB = context.get(NAME);

		UUID mark = context.merge(c);
		context.merge(d);
		assertNotEquals(context.get(NAME), nameB);

		boolean ok = context.restore(mark);

		assertTrue(ok);
		assertEquals(context.get(NAME), nameB);
	}

	IKVStore createStore(String name, Integer data) {
		IKVStore store = new KVStore();

		// will shadow
		store.put(NAME, name);
		store.put(DATA, data);

		// will be visible
		String id = String.format("%s[%s]", NAME.name + ".layer", data);
		Key<String> IDENT = Key.of(id);
		store.put(IDENT, id);
		return store;
	}

}
