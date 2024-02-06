package net.certiv.common.util;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;

import org.junit.jupiter.api.Test;

import net.certiv.common.stores.Result;

class ReflectMakeTest {

	List<String> list = List.of("strings");

	@Test
	void testTypeOf() {
		Result<Class<String>> res = Reflect.typeOf(this, "list");

		assertTrue(res.valid());
		assertEquals(res.get(), String.class);
	}

	@Test
	void testMake0() {
		Result<Arg0> res = Reflect.make(Arg0.class);
		assertTrue(res.valid());
		assertInstanceOf(Arg0.class, res.get());
	}

	@Test
	void testMake1() {
		Result<Arg1> res = Reflect.make(Arg1.class, "hello");
		assertTrue(res.valid());
		assertInstanceOf(Arg1.class, res.get());
	}

	@Test
	void testMake2() {
		Result<Arg2> res = Reflect.make(Arg2.class, "hello", 2);
		assertTrue(res.valid());
		assertInstanceOf(Arg2.class, res.get());
	}

	@Test
	void testMake2Prim() {
		Class<?>[] params = new Class<?>[] { String.class, int.class };
		Object[] args = List.of("hello", 2).toArray();

		Result<Arg2Prim> res = Reflect.make(Arg2Prim.class, params, args);
		assertTrue(res.valid());
		assertInstanceOf(Arg2Prim.class, res.get());
	}

	// --------------------------------

	static class Arg0 {

		public Arg0() {}
	}

	static class Arg1 {

		final String arg;

		public Arg1(String arg) {
			this.arg = arg;
		}
	}

	static class Arg2 {

		final String arg;
		final Integer num;

		public Arg2(String arg, Integer num) {
			this.arg = arg;
			this.num = num;
		}
	}

	static class Arg2Prim {

		final String arg;
		final int num;

		public Arg2Prim(String arg, int num) {
			this.arg = arg;
			this.num = num;
		}
	}
}
