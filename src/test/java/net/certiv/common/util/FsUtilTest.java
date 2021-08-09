package net.certiv.common.util;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;

import java.io.IOException;

import org.junit.jupiter.api.Test;

class FsUtilTest {

	@Test
	void testLoadRootResource() {
		try {
			byte[] bytes = FsUtil.loadResource(getClass(), "LoadResource.txt");
			String text = new String(bytes).trim();
			assertEquals(text, "Root resource");

		} catch (IOException e) {
			fail("Not found", e);
		}
	}

	@Test
	void testLoadFQResource() {
		try {
			byte[] bytes = FsUtil.loadResource(getClass(), "net/certiv/common/util/LoadResource.txt");
			String text = new String(bytes).trim();
			assertEquals(text, "FQ resource");

		} catch (IOException e) {
			fail("Not found", e);
		}
	}

	@Test
	void testLoadResource() {
		try {
			byte[] bytes = FsUtil.loadResource(getClass(), "LoadResource1.txt");
			String text = new String(bytes).trim();
			assertEquals(text, "Load resource 1");

		} catch (IOException e) {
			fail("Not found", e);
		}
	}
}
