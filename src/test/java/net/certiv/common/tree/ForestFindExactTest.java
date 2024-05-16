package net.certiv.common.tree;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;

import org.junit.jupiter.api.Test;

import net.certiv.common.tree.Forest.Policy;
import net.certiv.common.util.test.CommonTestBase;

class ForestFindExactTest extends CommonTestBase {

	static final boolean FORCE = true;

	static final List<String> V1 = List.of("A", "B", "D", "F");
	static final List<String> V2 = List.of("A", "B", "M", "N", "G", "Z");
	static final List<String> V3 = List.of("N", "R", "P", "S", "Y", "Z");
	static final List<String> V4 = List.of("B", "P", "C", "D", "E", "F", "G");

	@Test
	void testFindV1() {
		Forest<String> f = TestUtil.buildForest1a3d();
		TreePath<String> path = f.findPath(Policy.EXACT, V1);

		assertEquals(V1, path.values());
		assertTrue(path.complete());
		assertEquals(List.of(), path.missing());
	}

	@Test
	void testFindV2() {
		Forest<String> f = TestUtil.buildForest1a3d();
		TreePath<String> path = f.findPath(Policy.EXACT, V2);

		assertEquals(List.of("A", "B"), path.values());
		assertTrue(!path.complete());
		assertEquals(List.of("M", "N", "G", "Z"), path.missing());
	}

	@Test
	void testFindV3() {
		Forest<String> f = TestUtil.buildForest1a3d();
		TreePath<String> path = f.findPath(Policy.EXACT, V3);

		assertEquals(List.of("N"), path.values());
		assertTrue(!path.complete());
		assertEquals(List.of("R", "P", "S", "Y", "Z"), path.missing());
	}

	@Test
	void testFindV4() {
		Forest<String> f = TestUtil.buildForest1a3d();
		TreePath<String> path = f.findPath(Policy.EXACT, V4);

		assertEquals(List.of(), path.values());
		assertTrue(!path.complete());
		assertTrue(path.isEmpty());
		assertEquals(V4, path.missing());
	}
}
