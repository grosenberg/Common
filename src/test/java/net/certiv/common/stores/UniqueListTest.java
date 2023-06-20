package net.certiv.common.stores;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class UniqueListTest {

	private static final Class<UnsupportedOperationException> ERR_CLS0 = UnsupportedOperationException.class;

	private UniqueList<String> test;

	@BeforeEach
	void setup() {
		test = new UniqueList<>();
	}

	@Test
	void testEmpty() {
		assertTrue(UniqueList.empty().isEmpty());
		assertTrue(UniqueList.empty().isUnmodifiable());
	}

	@Test
	void testUnmodifiable() {
		assertFalse(test.isUnmodifiable());
		assertTrue(test.add("A"));
		assertTrue(test.size() == 1);
		test.unmodifiable();
		assertThrows(ERR_CLS0, () -> { test.add("B"); });
	}

	@Test
	void testAdd() {
		assertTrue(test.add("A"));
		assertTrue(test.add("B"));
		assertFalse(test.add("B"));
		assertTrue(test.size() == 2);
	}

	@Test
	void testAddInt() {
		test.add("A");
		test.add("B");
		test.add("C");
		test.add("D");

		test.add(1, "X");
		assertEquals(test.toString(), "[A, X, B, C, D]");
		test.add(3, "X");
		assertEquals(test.toString(), "[A, B, C, X, D]");
		test.add(1, "X");
		assertEquals(test.toString(), "[A, X, B, C, D]");
		test.add(4, "X");
		assertEquals(test.toString(), "[A, B, C, D, X]");
		test.add(0, "X");
		assertEquals(test.toString(), "[X, A, B, C, D]");
	}

	@Test
	void testAddFirst() {
		test.add("A");
		test.add("B");

		test.addFirst("X");
		assertEquals(test.toString(), "[X, A, B]");
	}

	@Test
	void testAddLast() {
		test.add("A");
		test.add("B");

		test.addLast("X");
		assertEquals(test.toString(), "[A, B, X]");
	}

	@Test
	void testAddAllCollectionOfQextendsE() {
		test.add("A");
		test.add("B");

		assertTrue(test.addAll(List.of("X", "Y")));
		assertEquals(test.toString(), "[A, B, X, Y]");
	}

	@Test
	void testAddAllIntCollectionOfQextendsE() {
		test.add("A");
		test.add("B");

		assertTrue(test.addAll(1, List.of("X", "Y")));
		assertEquals(test.toString(), "[A, X, Y, B]");
	}

	@Test
	void testOfferE() {

	}

	@Test
	void testSetInt() {
		test.add("A");
		test.add("B");
		test.add("C");
		test.add("D");

		test.set(1, "W");
		assertEquals(test.toString(), "[A, W, C, D]");
		test.set(3, "X");
		assertEquals(test.toString(), "[A, W, C, X]");
		test.set(1, "Y");
		assertEquals(test.toString(), "[A, Y, C, X]");
		test.set(3, "Y");
		assertEquals(test.toString(), "[A, C, Y]");
		test.set(0, "Y");
		assertEquals(test.toString(), "[Y, C]");
	}

	@Test
	void testRemoveObject() {

	}

	@Test
	void testRemoveInt() {

	}

	@Test
	void testRemoveFirst() {

	}

	@Test
	void testRemoveLast() {

	}

	@Test
	void testRemoveFirstOccurrenceObject() {

	}

	@Test
	void testRemoveLastOccurrenceObject() {

	}

	@Test
	void testRemoveIf() {

	}

	@Test
	void testRemoveAllCollectionOfQ() {

	}

	@Test
	void testReplaceAll() {

	}

	@Test
	void testRetainAllCollectionOfQ() {

	}

}
