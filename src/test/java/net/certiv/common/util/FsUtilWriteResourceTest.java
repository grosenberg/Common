package net.certiv.common.util;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.net.URI;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;

@TestInstance(Lifecycle.PER_CLASS)
class FsUtilWriteResourceTest {

	private static final String ROOT = "file:/D:/DevFiles/Eclipse/Tools/Certiv/net.certiv.common/";
	private static final String TGT = "target/classes/";
	private static final String TGT_TST = "target/test-classes/";
	private static final String TST = "src/test/resources/";
	private static final String PKG = "net/certiv/common/util";

	URI exp_tgt = null;
	URI exp_tgt_tst = null;
	URI exp_tst = null;
	URI exp_pkg = null;

	@BeforeAll
	void setup() {
		exp_tgt = URI.create(ROOT + TGT);
		exp_tgt_tst = URI.create(ROOT + TGT_TST);
		exp_tst = URI.create(ROOT + TST);
		exp_pkg = URI.create(ROOT + TST + PKG);
	}

	@Test
	void testLocate() {
		URI at = FsUtil.locate(getClass());
		assertEquals(exp_tgt_tst, at);

		at = FsUtil.locate(FsUtil.class);
		assertEquals(exp_tgt, at);
	}

	@Test
	void testLocateTest() {
		URI at = FsUtil.locateTest(getClass());
		assertEquals(exp_pkg, at);

		at = FsUtil.locateTest(FsUtil.class);
		assertEquals(exp_pkg, at);
	}

	@Test
	void testWriteResource() {
		FsUtil.writeResource(getClass(), "WriteResource.txt", "FsUtil location.");
	}
}
