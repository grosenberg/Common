package net.certiv.common.stores;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import java.util.LinkedList;

import org.junit.jupiter.api.Test;

class LimitListTest {

	LimitList<String> list;

	final String A = "A";
	final String B = "B";
	final String C = "C";
	final String D = "D";
	final String E = "E";
	final String F = "F";

	@Test
	void testLimit() {
		list = new LimitList<>(4);

		String res = list.add(A);
		assertNull(res);

		res = list.add(B);
		assertNull(res);

		res = list.add(C);
		assertNull(res);

		res = list.add(D);
		assertNull(res);

		res = list.add(E);
		assertEquals(res, A);
	}

	@Test
	void testAdjustLimit() {
		list = new LimitList<>(4);

		list.addLast(A);
		list.addLast(B);
		list.addLast(C);
		list.addLast(D);

		LinkedList<String> res = list.adjustLimit(2);
		assertEquals(res.size(), 2);
		assertEquals(res.toString(), "[C, D]");
	}

	@Test
	void testAdd() {

	}

	@Test
	void testAddLast() {
		list = new LimitList<>(2);

		list.addLast(A);
		list.addLast(B);

		String res = list.addLast(C);
		assertEquals(res, B);
		assertEquals(list.toString(), "[A, C]");
	}

	@Test
	void testAddInt() {
		list = new LimitList<>(4);

		list.addLast(A);
		list.addLast(B);
		list.addLast(C);
		list.addLast(D);

		String res = list.add(2, E);
		assertEquals(res, D);
		assertEquals(list.toString(), "[A, B, E, C]");
	}

	@Test
	void testAddInt1() {
		list = new LimitList<>(4);

		list.addLast(A);
		list.addLast(B);

		String res = list.add(2, E);
		assertNull(res);
		assertEquals(list.toString(), "[A, B, E]");
	}
}
