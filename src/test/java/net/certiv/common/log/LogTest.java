package net.certiv.common.log;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class LogTest {

	// defaults to "./logs"
	// D:\DevFiles\Eclipse\Tools\Certiv\net.certiv.common\target\classes\logs
	// change by prefix to
	// D:\DevFiles\Eclipse\Tools\Certiv\net.certiv.common\src\test\resources\logs

	@BeforeEach
	void setup() {
		Log.setName("Test", "../../src/test/resources/logs");
	}

	@Test
	void testDebug() {
		Log.setName("Debugs");
		Log.debug("Debug");
	}

	@Test
	void testPrintf() {
		Log.setName("Printf");
		Log.printf(Level.WARN, "Print %s", "now.");
	}

	@Test
	void testLog() {}

	@Test
	void testDefLevelLevel() {}

	@Test
	void testSetLevelLevel() {}

}
