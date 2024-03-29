package net.certiv.common.util;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

import java.io.IOException;

import org.junit.jupiter.api.Test;

import net.certiv.common.stores.Result;

class FsUtilLoadResourceTest {

	@Test
	void testLoadRoot() {
		try {
			byte[] bytes = FsUtil.loadByteResource(getClass(), "LoadResource.txt");
			String text = new String(bytes).trim();
			assertEquals(text, "Root resource");

		} catch (IOException e) {
			fail("Not found", e);
		}
	}

	@Test
	void testLoadFullyQualified() {
		try {
			byte[] bytes = FsUtil.loadByteResource(getClass(), "net/certiv/common/util/LoadResource.txt");
			String text = new String(bytes).trim();
			assertEquals(text, "FQ resource");

		} catch (IOException e) {
			fail("Not found", e);
		}
	}

	@Test
	void testLoadRelative() {
		try {
			byte[] bytes = FsUtil.loadByteResource(getClass(), "LoadResource1.txt");
			String text = new String(bytes).trim();
			assertEquals(text, "Load resource 1");

		} catch (IOException e) {
			fail("Not found", e);
		}
	}

	@Test
	void testLoadRelativeChecked() {
		Result<String> res = FsUtil.loadResource(getClass(), "LoadResource1.txt");
		assertTrue(res.validNonNull());
		assertEquals(res.get().trim(), "Load resource 1");
	}
}
