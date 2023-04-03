package net.certiv.common.check;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

class CheckTest extends CheckBase {

	@Test
	void testEmptyString() {
		assertTrue(Check.empty(strx));
		assertTrue(Check.empty(str0));
		assertFalse(Check.empty(str1));
		assertFalse(Check.empty(str2));
	}

	@Test
	void testEmptyArrayPrimitive() {
		assertTrue(Check.empty(intsx));
		assertTrue(Check.empty(ints0));
		assertFalse(Check.empty(ints1));
		assertFalse(Check.empty(ints2));
	}

	@Test
	void testEmptyArrayObject() {
		assertTrue(Check.empty(objsx));
		assertTrue(Check.empty(objs0));
		assertFalse(Check.empty(objs1));
		assertFalse(Check.empty(objs2));
		assertTrue(Check.empty(objs3));
		assertTrue(Check.empty(objsn));
	}

	@Test
	void testEmptyCollection() {
		assertTrue(Check.empty(listx));
		assertTrue(Check.empty(list0));
		assertTrue(Check.empty(list1));
		assertTrue(Check.empty(list2));
		assertFalse(Check.empty(list3));
		assertTrue(Check.empty(list4));
		assertTrue(Check.empty(listn));
	}
}
