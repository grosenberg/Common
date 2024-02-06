package net.certiv.common.log;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

class LogDescTest {

	static final String MSG0 = "[DEBUG] -- Test";
	static final String MSG1 = "[INFO ] net.certiv.common.log.LogDescTest                   : 22   -- Test";
	static final String MSG2 = "[INFO ] n.c.c.log.LogDescTest$LogDescriptorInnerCaller      : 39   -- Test";

	@Test
	void testDesc0() {
		LogDesc desc = LogDesc.of(Level.DEBUG, "Test");
		assertEquals(MSG0, desc.toString());
	}

	@Test
	void testDesc1() {
		LogDescriptorInnerProducer prod = new LogDescriptorInnerProducer();
		LogDesc desc = prod.descProducer(Level.INFO, "Test");
		assertEquals(MSG1, desc.toString());
	}

	@Test
	void testDesc2() {
		LogDescriptorInnerCaller caller = new LogDescriptorInnerCaller();
		LogDesc desc = caller.descCall(Level.INFO, "Test");
		assertEquals(MSG2, desc.toString());
	}

	// --------------------------------

	static class LogDescriptorInnerCaller {

		LogDesc descCall(Level level, String msg) {
			LogDescriptorInnerProducer prod = new LogDescriptorInnerProducer();
			return prod.descProducer(level, msg);
		}
	}

	static class LogDescriptorInnerProducer {

		LogDesc descProducer(Level level, String msg) {
			return LogDesc.of(this, level, msg);
		}
	}
}
