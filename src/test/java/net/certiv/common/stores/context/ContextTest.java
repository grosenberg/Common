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

		context.mergeFirst(a);
		context.mergeFirst(b);
		context.mergeFirst(c);
		context.mergeFirst(d);
		context.mergeFirst(e);
		context.mergeFirst(f);

		assertEquals(context.depth(), 7);
		assertEquals(context.maxDepth(), 7);

	}

	@Test
	void testAdjustMaxDepth() {
		context = Context.of(1);
		context.mergeFirst(a);
		context.mergeFirst(b);
		context.mergeFirst(c);
		context.mergeFirst(d);
		context.mergeFirst(e);
		context.mergeFirst(f);

		assertEquals(context.depth(), 7);
		assertEquals(context.maxDepth(), 7);

		context.adjustMaxDepth(10, false);

		assertEquals(context.depth(), 7);
		assertEquals(context.maxDepth(), 10);
		assertEquals(context.size(), 9);
	}

	@Test
	void testAdjustMaxDepthTrim() {
		context = Context.of(1);
		context.mergeFirst(a);
		context.mergeFirst(b);
		context.mergeFirst(c);
		context.mergeFirst(d);
		context.mergeFirst(e);
		context.mergeFirst(f);

		context.adjustMaxDepth(3, true); // trim [4,5,6,7]

		assertEquals(context.depth(), 3);
		assertEquals(context.maxDepth(), 3);
		assertEquals(context.size(), 6);
	}

	@Test
	void testAdjustMaxDepthFlatten() {
		context = Context.of(1);
		context.mergeFirst(a);
		context.mergeFirst(b);
		context.mergeFirst(c);
		context.mergeFirst(d);
		context.mergeFirst(e);
		context.mergeFirst(f);

		context.adjustMaxDepth(3, false); // flatten [4,5,6,7] into 3

		assertEquals(context.depth(), 3);
		assertEquals(context.maxDepth(), 3);
		assertEquals(context.size(), 9);
	}

	@Test
	void testDelta() {
		context = Context.of();
		assertEquals(context.size(), 1);

		context.mergeFirst(a);
		assertEquals(context.delta(), a);

		context.mergeFirst(b);
		assertEquals(context.delta(), b);
	}

	@Test
	void testMerge() {
		context = Context.of(3);
		context.mergeFirst(a);

		assertEquals(context.delta(), a);

		assertEquals(context.depth(), 2);
		assertEquals(context.maxDepth(), 3);

		context.mergeFirst(b);
		assertEquals(context.depth(), 3);
		assertEquals(context.maxDepth(), 3);

		context.mergeFirst(c);
		assertEquals(context.depth(), 4);
		assertEquals(context.maxDepth(), 4);
	}

	@Test
	void textMerge2() {
		context = Context.of(2, false);
		context.mergeFirst(a);
		context.mergeFirst(b);

		Context other = Context.of(2, false);
		other.mergeFirst(c);
		other.mergeFirst(d);

		context.mergeFirst(other);

		assertEquals(context.depth(), 4);	// scopes
		assertEquals(context.size(), 7);	// keys

		String existing = context.keys().toString();
		String expected = "[kvstore.uuid, " //
				+ "context.text.name, context.text.data, " //
				+ "context.text.name.layer[3], " //
				+ "context.text.name.layer[2], " //
				+ "context.text.name.layer[1], " //
				+ "context.text.name.layer[0]]"; //
		assertEquals(existing, expected);
	}

	@Test
	void testShade() {
		context = Context.of(4);
		context.mergeFirst(a);
		context.mergeFirst(b);
		context.mergeFirst(c);
		context.mergeFirst(d);

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
		context.mergeFirst(a);
		context.mergeFirst(b);
		String nameB = context.get(NAME);

		UUID mark = context.mergeFirst(c);
		context.mergeFirst(d);
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
