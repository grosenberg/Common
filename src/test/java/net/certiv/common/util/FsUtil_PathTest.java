package net.certiv.common.util;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.nio.file.Path;

import org.junit.jupiter.api.Test;

class FsUtil_PathTest {

	static Path path() {
		String pathname = "net.certiv.common.util";
		return Path.of(FsUtil.slashify(pathname), "Test.java");
	}

	@Test
	void testGetExtString() {
		String ext = FsUtil.getExt(path().toString());

		assertEquals(ext, "java");
	}

	@Test
	void testGetExtPath() {
		String ext = FsUtil.getExt(path());

		assertEquals(ext, "java");
	}

	@Test
	void testReplaceExt() {
		Path path = FsUtil.replaceExt(path(), ".json.gz");

		String str = path.toString().replace("\\", "/");
		assertEquals(str, "net/certiv/common/util/Test.json.gz");
	}

}
