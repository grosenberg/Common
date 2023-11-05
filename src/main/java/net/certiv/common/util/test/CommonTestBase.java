package net.certiv.common.util.test;

import net.certiv.common.stores.Result;
import net.certiv.common.util.FsUtil;

public class CommonTestBase {

	private boolean force = false;

	/**
	 * Globally defines whether test resource files will be recreated/overwriten.
	 *
	 * @param force {@code true} forces recreation/overwrite of all test resource files
	 */
	public void global(boolean force) {
		this.force = force;
	}

	/**
	 * Conditionally writes the given data to a resource file having the given name in the
	 * package folder, defined by the package of the given class, under the project
	 * {@code <resource>} directory.
	 * <p>
	 * A write is forced if either the global or given local force values are
	 * {@code true}.
	 * <p>
	 * For a {@code <resource>} directory of {@code <project>/src/test/resources},
	 * reference class of {@code a.b.c.D.class}, and name {@code y/Z.txt}, data will be
	 * read from {@code <project>/src/test/resources/a/b/c/y/Z.txt}.
	 *
	 * @param cls   a resource classloader relative class
	 * @param name  the resource filename
	 * @param data  the data to write
	 * @param force local write force
	 * @throws a runtime exception on an attempted write failure
	 */
	public void writeResource(Class<?> cls, String name, String data, boolean force) {
		if (this.force || force || !FsUtil.checkResource(cls, name)) {
			Result<Boolean> res = FsUtil.writeResource(getClass(), name, data);
			if (res.err()) throw new RuntimeException(res.err);
		}
	}

	/**
	 * Loads the contents of the resource file having the given name in the package
	 * folder, defined by the package of the given class, under the project
	 * {@code <resource>} directory.
	 * <p>
	 * For a {@code <resource>} directory of {@code <project>/src/test/resources},
	 * reference class of {@code a.b.c.D.class}, and name {@code y/Z.txt}, data will be
	 * read from {@code <project>/src/test/resources/a/b/c/y/Z.txt}.
	 *
	 * @param cls  a resource classloader relative class
	 * @param name the resource filename
	 * @return read data
	 * @throws a runtime exception on a read failure
	 */
	public static String loadResource(Class<?> cls, String name) {
		Result<String> res = FsUtil.loadResource(cls, name);
		if (res.err()) throw new RuntimeException(res.err);
		return res.value;
	}
}
