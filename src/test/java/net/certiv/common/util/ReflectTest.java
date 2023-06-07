package net.certiv.common.util;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;

import org.junit.jupiter.api.Test;

import net.certiv.common.stores.Result;

class ReflectTest {

	List<String> list = List.of("strings");

	@Test
	void testTypeOf() {
		Result<Class<String>> res = Reflect.typeOf(this, "list");

		assertTrue(res.valid());
		assertEquals(res.value, String.class);
	}
}
