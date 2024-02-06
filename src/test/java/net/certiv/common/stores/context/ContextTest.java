package net.certiv.common.stores.context;

import static org.junit.jupiter.api.Assertions.*;

import java.util.UUID;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import net.certiv.common.KVSupport;

class ContextTest {

	private final KVSupport KS = new KVSupport();

	@BeforeEach
	void setUp() throws Exception {
		KS.setUp();
	}

	@AfterEach
	void tearDown() throws Exception {
		KS.tearDown();
	}

	@Test
	void testOf() {
		KS.context = Context.of(1);
		assertEquals(1, KS.context.depth());
		assertEquals(1, KS.context.maxDepth());
	}

	@Test
	void testMaxDepth() {
		KS.context = Context.of(3);
		assertEquals(1, KS.context.depth());
		assertEquals(3, KS.context.maxDepth());

		KS.context.mergeFirst(KS.a);
		KS.context.mergeFirst(KS.b);
		KS.context.mergeFirst(KS.c);
		KS.context.mergeFirst(KS.d);
		KS.context.mergeFirst(KS.e);
		KS.context.mergeFirst(KS.f);

		assertEquals(7, KS.context.depth());
		assertEquals(7, KS.context.maxDepth());

	}

	@Test
	void testAdjustMaxDepth() {
		KS.context = Context.of(1);
		KS.context.mergeFirst(KS.a);
		KS.context.mergeFirst(KS.b);
		KS.context.mergeFirst(KS.c);
		KS.context.mergeFirst(KS.d);
		KS.context.mergeFirst(KS.e);
		KS.context.mergeFirst(KS.f);

		assertEquals(7, KS.context.depth());
		assertEquals(7, KS.context.maxDepth());

		KS.context.adjustMaxDepth(10, false);

		assertEquals(7, KS.context.depth());
		assertEquals(10, KS.context.maxDepth());
		assertEquals(8, KS.context.size());
	}

	@Test
	void testAdjustMaxDepthTrim() {
		KS.context = Context.of(1);
		KS.context.mergeFirst(KS.a);
		KS.context.mergeFirst(KS.b);
		KS.context.mergeFirst(KS.c);
		KS.context.mergeFirst(KS.d);
		KS.context.mergeFirst(KS.e);
		KS.context.mergeFirst(KS.f);

		KS.context.adjustMaxDepth(3, true); // trim [4,5,6,7]

		assertEquals(3, KS.context.depth());
		assertEquals(3, KS.context.maxDepth());
		assertEquals(5, KS.context.size());
	}

	@Test
	void testAdjustMaxDepthFlatten() {
		KS.context = Context.of(1);
		KS.context.mergeFirst(KS.a);
		KS.context.mergeFirst(KS.b);
		KS.context.mergeFirst(KS.c);
		KS.context.mergeFirst(KS.d);
		KS.context.mergeFirst(KS.e);
		KS.context.mergeFirst(KS.f);

		KS.context.adjustMaxDepth(3, false); // flatten [4,5,6,7] into 3

		assertEquals(3, KS.context.depth());
		assertEquals(3, KS.context.maxDepth());
		assertEquals(8, KS.context.size());
	}

	@Test
	void testDelta() {
		KS.context = Context.of(1);
		assertEquals(0, KS.context.size());
		assertEquals(1, KS.context.depth());

		KS.context.mergeFirst(KS.a);
		assertEquals(KS.a, KS.context.delta());

		KS.context.mergeFirst(KS.b);
		assertEquals(KS.b, KS.context.delta());
	}

	@Test
	void testMerge() {
		KS.context = Context.of(3);
		KS.context.mergeFirst(KS.a);

		assertEquals(KS.a, KS.context.delta());

		assertEquals(2, KS.context.depth());
		assertEquals(3, KS.context.maxDepth());

		KS.context.mergeFirst(KS.b);
		assertEquals(3, KS.context.depth());
		assertEquals(3, KS.context.maxDepth());

		KS.context.mergeFirst(KS.c);
		assertEquals(4, KS.context.depth());
		assertEquals(4, KS.context.maxDepth());
	}

	@Test
	void testMerge2() {
		KS.context = Context.of(2, false);
		KS.context.mergeFirst(KS.a);
		KS.context.mergeFirst(KS.b);

		Context other = Context.of(2, false);
		other.mergeFirst(KS.c);
		other.mergeFirst(KS.d);

		KS.context.mergeFirst(other);

		assertEquals(4, KS.context.depth());	// scopes
		assertEquals(6, KS.context.size());	// keys

		String existing = KS.context.keys().toString();
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
		KS.context = Context.of(4);
		KS.context.mergeFirst(KS.a);
		KS.context.mergeFirst(KS.b);
		KS.context.mergeFirst(KS.c);
		KS.context.mergeFirst(KS.d);

		assertEquals(6, KS.context.size());
		assertEquals(5, KS.context.depth());

		String visible = "[" //
				+ "context.text.name, context.text.data, " //
				+ "context.text.name.layer[3], " //
				+ "context.text.name.layer[2], " //
				+ "context.text.name.layer[1], " //
				+ "context.text.name.layer[0]]"; //
		assertEquals(visible, KS.context.keys().toString());
	}

	@Test
	void testRestore() {
		KS.context = Context.of(4);
		KS.context.mergeFirst(KS.a);
		KS.context.mergeFirst(KS.b);
		String nameB = KS.context.get(KS.NAME);

		UUID mark = KS.context.mergeFirst(KS.c);
		KS.context.mergeFirst(KS.d);
		assertNotEquals(nameB, KS.context.get(KS.NAME));

		boolean ok = KS.context.restore(mark);

		assertTrue(ok);
		assertEquals(nameB, KS.context.get(KS.NAME));
	}

	@Test
	void testContains() {
		KS.a.put(KS.KeyA, "StrA");

		KS.context = Context.of(2, false);
		KS.context.mergeFirst(KS.a);
		KS.context.mergeFirst(KS.b);

		assertTrue(KS.a.contains(KS.KeyA));
		assertFalse(KS.b.contains(KS.KeyA));
		assertTrue(KS.context.contains(KS.KeyA));

		KS.context.put(KS.KeyA, "StrB");
		assertTrue(KS.context.contains(KS.KeyA));
		assertEquals("StrB", KS.context.get(KS.KeyA));
	}

	@Test
	void testPutIfAbsent() {
		KS.context = Context.of(2, false);
		KS.context.mergeFirst(KS.a);

		assertNull(KS.context.get(KS.KeyA));
		KS.context.putIfAbsent(KS.KeyA, "StrA");
		assertTrue(KS.context.contains(KS.KeyA));

		KS.context.mergeFirst(KS.b);
		assertTrue(KS.context.contains(KS.KeyA));

		KS.context.putIfAbsent(KS.KeyA, "StrB");
		assertTrue(KS.context.contains(KS.KeyA));
		assertNotEquals("StrB", KS.context.get(KS.KeyA));
		assertEquals("StrA", KS.context.get(KS.KeyA));

		KS.context.put(KS.KeyA, "StrB");
		assertTrue(KS.context.contains(KS.KeyA));
		assertNotEquals("StrA", KS.context.get(KS.KeyA));
		assertEquals("StrB", KS.context.get(KS.KeyA));
	}
}
