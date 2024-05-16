package net.certiv.common.util;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import net.certiv.common.stores.Result;

class FsUtilLoadResourceTest {

	@Test
	void testLoadRoot() {
		Result<String> txt = FsUtil.loadResource(getClass(), "LoadResource.txt");
		assertTrue(txt.valid());
		assertEquals("Root resource", txt.get().trim());
	}

	@Test
	void testLoadFullyQualified() {
		Result<String> txt = FsUtil.loadResource(getClass(), "net/certiv/common/util/LoadResource.txt");
		assertTrue(txt.valid());
		assertEquals("FQ resource", txt.get().trim());
	}

	@Test
	void testLoadRelative() {
		Result<String> txt = FsUtil.loadResource(getClass(), "LoadResource1.txt");
		assertTrue(txt.valid());
		assertEquals("Load resource 1", txt.get().trim());
	}
}
