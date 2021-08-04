package net.certiv.common.util;

import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;

import org.junit.jupiter.api.Test;

import net.certiv.common.stores.Result;

class ReflectTest {

	// @Test
	// void testFieldTypeN() {
	// fail("Not yet implemented");
	// }
	//
	// @Test
	// void testHasField() {
	// fail("Not yet implemented");
	// }

	List<String> strings = List.of("strings");

	@Test
	void testTypeOf() {

		Result<Class<?>> res = Reflect.typeOf(this, "strings");

		assertTrue(res.valid());
	}
}
