package net.certiv.common.stores.context;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.UUID;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class ContextTest {

	Key<String> NAME = Key.of("context.text.name");
	Key<Integer> DATA = Key.of("context.text.data");

	Context context;

	IKVScope a;
	IKVScope b;
	IKVScope c;
	IKVScope d;
	IKVScope e;
	IKVScope f;

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
		context = Context.of(1);
		assertEquals(1, context.depth());
		assertEquals(1, context.maxDepth());
	}

	@Test
	void testMaxDepth() {
		context = Context.of(3);
		assertEquals(1, context.depth());
		assertEquals(3, context.maxDepth());

		context.mergeFirst(a);
		context.mergeFirst(b);
		context.mergeFirst(c);
		context.mergeFirst(d);
		context.mergeFirst(e);
		context.mergeFirst(f);

		assertEquals(7, context.depth());
		assertEquals(7, context.maxDepth());

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

		assertEquals(7, context.depth());
		assertEquals(7, context.maxDepth());

		context.adjustMaxDepth(10, false);

		assertEquals(7, context.depth());
		assertEquals(10, context.maxDepth());
		assertEquals(8, context.size());
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

		assertEquals(3, context.depth());
		assertEquals(3, context.maxDepth());
		assertEquals(5, context.size());
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

		assertEquals(3, context.depth());
		assertEquals(3, context.maxDepth());
		assertEquals(8, context.size());
	}

	@Test
	void testDelta() {
		context = Context.of(1);
		assertEquals(0, context.size());
		assertEquals(1, context.depth());

		context.mergeFirst(a);
		assertEquals(a, context.delta());

		context.mergeFirst(b);
		assertEquals(b, context.delta());
	}

	@Test
	void testMerge() {
		context = Context.of(3);
		context.mergeFirst(a);

		assertEquals(a, context.delta());

		assertEquals(2, context.depth());
		assertEquals(3, context.maxDepth());

		context.mergeFirst(b);
		assertEquals(3, context.depth());
		assertEquals(3, context.maxDepth());

		context.mergeFirst(c);
		assertEquals(4, context.depth());
		assertEquals(4, context.maxDepth());
	}

	@Test
	void testMerge2() {
		context = Context.of(2, false);
		context.mergeFirst(a);
		context.mergeFirst(b);

		Context other = Context.of(2, false);
		other.mergeFirst(c);
		other.mergeFirst(d);

		context.mergeFirst(other);

		assertEquals(4, context.depth());	// scopes
		assertEquals(6, context.size());	// keys

		String existing = context.keys().toString();
		String expected = "[" //
				+ "context.text.name, context.text.data, " //
				+ "context.text.name.layer[3], " //
				+ "context.text.name.layer[2], " //
				+ "context.text.name.layer[1], " //
				+ "context.text.name.layer[0]]"; //
		assertEquals(expected, existing);
	}

	@Test
	void testShade() {
		context = Context.of(4);
		context.mergeFirst(a);
		context.mergeFirst(b);
		context.mergeFirst(c);
		context.mergeFirst(d);

		assertEquals(6, context.size());
		assertEquals(5, context.depth());

		String visible = "[" //
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
		assertNotEquals(nameB, context.get(NAME));

		boolean ok = context.restore(mark);

		assertTrue(ok);
		assertEquals(nameB, context.get(NAME));
	}

	IKVScope createStore(String name, Integer data) {
		IKVScope store = new KVStore();

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
