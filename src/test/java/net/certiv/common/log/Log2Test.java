package net.certiv.common.log;

import org.apache.logging.log4j.Level;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class Log2Test {

	// defaults to "./logs"
	// D:\DevFiles\Eclipse\Tools\Certiv\net.certiv.common\target\classes\logs
	// change by prefix to
	// D:\DevFiles\Eclipse\Tools\Certiv\net.certiv.common\src\test\resources\logs

	@BeforeEach
	void setup() {
		Log2.setName("Test", "../../src/test/resources/logs");
	}

	@Test
	void testDebug() {
		Log2.setName("Debugs");
		Log2.debug("Debug");
	}

	@Test
	void testPrintf() {
		Log2.setName("Printf");
		Log2.printf(Level.WARN, "Print %s", "now.");
	}

	@Test
	void testLog() {}

	@Test
	void testDefLevelLevel() {}

	@Test
	void testSetLevelLevel() {}

}
