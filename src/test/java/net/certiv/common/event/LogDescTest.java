package net.certiv.common.event;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import net.certiv.common.log.Level;

class LogDescTest {

	static final String NAME1 = "java.util.concurrent.ThreadPerTaskExecutorSomething";
	static final String NAME2 = "j.u.c.ThreadPerTaskExecutorSomething";

	static final String NAME3 = "java.util.concurrent.ThreadPerTaskExecutor$ThreadBoundFuture";
	static final String NAME4 = "j.u.c.ThreadPerTaskExecutor$ThreadBoundFuture";

	private LogDesc desc;

	@BeforeEach
	void setup() {
		desc = LogDesc.of(Level.DEBUG, "Test");
	}

	@Test
	void testAbbr() {
		String abbr = desc.abbr(NAME1);
		assertEquals(NAME2, abbr);
	}

	@Test
	void testAdj() {
		String adj = desc.adj(NAME3);
		assertEquals(NAME4, adj);
	}
}
