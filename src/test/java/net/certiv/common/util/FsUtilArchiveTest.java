package net.certiv.common.util;

import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;

import org.apache.commons.io.FileUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import net.certiv.common.stores.Result;
import net.certiv.common.util.test.CommonTestBase;

class FsUtilArchiveTest extends CommonTestBase {

	private static final String DATA = "src/test/data";
	private static final File ROOT = new File(DATA).getAbsoluteFile();

	@BeforeEach
	void setup() {
		// Log.setTestMode(true);
	}

	@Test
	void testGz() throws IOException {
		File base = new File(ROOT, "testGz");
		try {
			base.mkdirs();

			File dir = Path.of(FsUtil.locateTest(getClass())).toFile();
			File archive = new File(dir, "Archive.gz");
			Result<File> res = FsUtil.extractArchive(archive, base);
			assertTrue(res.valid());

			File binary = res.get();
			assertTrue(binary.isFile());

		} finally {
			FileUtils.deleteDirectory(base);
		}
	}

	@Test
	void testTarGz() throws IOException {
		File base = new File(ROOT, "testGz");
		try {
			base.mkdirs();

			File dir = Path.of(FsUtil.locateTest(getClass())).toFile();
			File archive = new File(dir, "Archive.tar.gz");
			Result<File> res = FsUtil.extractArchive(archive, base);
			assertTrue(res.valid());

			File binary = new File(base, "binary.jpg");
			assertTrue(binary.isFile());

		} finally {
			FileUtils.deleteDirectory(base);
		}
	}
}
