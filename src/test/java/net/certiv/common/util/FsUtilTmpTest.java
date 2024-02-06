package net.certiv.common.util;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import net.certiv.common.util.test.CommonTestBase;

class FsUtilTmpTest extends CommonTestBase {

	static final String TmpDir = System.getProperty("java.io.tmpdir");

	@BeforeEach
	void setup() {
		// Log.setTestMode(true);
	}

	@Test
	void testGetSysTmp() {
		File tmp1 = FsUtil.getSysTmp();
		File tmp2 = new File(TmpDir);

		// Log.debug("%s", tmp1);
		// Log.debug("%s", tmp2);

		assertEquals(tmp1, tmp2);
		assertTrue(tmp2.exists() && tmp2.isDirectory());
	}

	@Test
	void testCreateTmpFolder() {
		Path path = Path.of(TmpDir, "test");
		File tmp = path.toFile();

		File dir = assertDoesNotThrow(() -> FsUtil.createTmpFolder("test"));
		assertEquals(tmp, dir);

		assertDoesNotThrow(() -> FsUtil.deleteFolderOnExit(dir));

		assertFalse(dir.mkdirs());
		assertTrue(dir.exists());
		assertTrue(dir.isDirectory());
	}

	@Test
	void testRealPath() throws IOException {
		Path path = Path.of(TmpDir, "test");
		Path real = path.toRealPath();
		assertEquals(path, real);

		// Path path = Path.of(System.getProperty("java.io.tmpdir"), "test");
		// Path real = path.toRealPath();
		//
		// System.out.print(String.format("Path is '%s'\n", path));
		// System.out.print(String.format("Real is '%s'\n", real));

	}
}
