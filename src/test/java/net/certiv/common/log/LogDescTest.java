package net.certiv.common.log;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

class LogDescTest {

	static final String MSG0 = "[DEBUG] -- Test";
	static final String MSG1 = "[INFO ] net.certiv.common.log.LogDescTest                   : 23   -- Test";
	static final String MSG2 = "[INFO ] n.c.c.log.LogDescTest$LogDescriptorInnerProducer    : 54   -- Test";
	static final String MSG3 = "[INFO ] n.c.c.log.LogDescTest$LogDescriptorInnerCaller      : 47   -- Test";

	@Test
	void testDesc0() {
		LogDesc desc = LogDesc.of(Level.DEBUG, "Test");
		assertEquals(MSG0, desc.toString());
	}

	@Test
	void testDesc1() {
		LogDescriptorInnerProducer prod = new LogDescriptorInnerProducer();
		LogDesc desc = prod.desc(this, Level.INFO, "Test");
		assertEquals(MSG1, desc.toString());
	}

	@Test
	void testDesc2() {
		LogDescriptorInnerProducer prod = new LogDescriptorInnerProducer();
		LogDesc desc = prod.desc(prod, Level.INFO, "Test");
		assertEquals(MSG2, desc.toString());
	}

	@Test
	void testDesc3() {
		LogDescriptorInnerCaller caller = new LogDescriptorInnerCaller();
		LogDesc desc = caller.desc(caller, Level.INFO, "Test");
		assertEquals(MSG3, desc.toString());
	}

	// --------------------------------

	static class LogDescriptorInnerCaller {

		LogDesc desc(Object loc, Level level, String msg) {
			LogDescriptorInnerProducer prod = new LogDescriptorInnerProducer();
			return prod.desc(loc, level, msg);
		}
	}

	static class LogDescriptorInnerProducer {

		LogDesc desc(Object loc, Level level, String msg) {
			return LogDesc.of(loc, level, msg);
		}
	}
}
