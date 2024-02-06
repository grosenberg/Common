package net.certiv.common.util;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.LinkedList;
import java.util.List;

import org.junit.jupiter.api.Test;

import net.certiv.common.stores.Result;

class ReflectInvokeTest {

	@Test
	void testInvokePrivate() {
		LinkedList<String> list = new LinkedList<>(List.of("strings"));
		// private String AbstractList#outOfBoundsMsg(int index)

		String methodName = "outOfBoundsMsg";
		Class<?>[] params = Reflect.params(Integer.TYPE);
		Object[] args = Reflect.args(2);
		Result<String> res = Reflect.invoke(list, methodName, params, args);
		assertTrue(res.err()); // cross-module will fail

		Item item = new Item("Test");
		methodName = "getFin";

		res = Reflect.invoke(item, methodName);
		assertTrue(res.valid()); // same-module works
		assertEquals("Test", res.get());
	}

	// --------------------------------

	static class Item {

		private String name;
		private final String fin;

		public Item(String name) {
			this.name = name;
			this.fin = name;
		}

		public String getName() {
			return name;
		}

		public void setName(String name) {
			this.name = name;
		}

		@SuppressWarnings("unused")
		private String getFin() {
			return fin;
		}
	}
}
