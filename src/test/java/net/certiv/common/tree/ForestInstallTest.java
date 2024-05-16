package net.certiv.common.tree;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import net.certiv.common.diff.Differ;
import net.certiv.common.util.test.CommonTestBase;

class ForestInstallTest extends CommonTestBase {

	static final boolean FORCE = false;

	@Test
	void testInstallDump1ab() {
		Forest<String> f = TestUtil.buildForest1ab();
		String dmp = ForestRender.dump(f);
		writeResource(getClass(), "install1ab.txt", dmp, FORCE);

		String txt = loadResource(getClass(), "install1ab.txt");
		Differ.diff(f.label(), txt, dmp).sdiff(true, 120).out();

		assertEquals(txt, dmp);
	}

	@Test
	void testInstallDot1ab() {
		Forest<String> f = TestUtil.buildForest1ab();
		String dot = ForestRender.toDot(f);
		writeResource(getClass(), "install1ab.md", dot, FORCE);

		String txt = loadResource(getClass(), "install1ab.md");
		Differ.diff(f.label(), txt, dot).sdiff(true, 120).out();

		assertEquals(txt, dot);
	}

	@Test
	void testInstallDump1a3d() {
		Forest<String> f = TestUtil.buildForest1a3d();
		String dmp = ForestRender.dump(f);
		writeResource(getClass(), "install1a3d.txt", dmp, FORCE);

		String txt = loadResource(getClass(), "install1a3d.txt");
		Differ.diff(f.label(), txt, dmp).sdiff(true, 120).out();

		assertEquals(txt, dmp);
	}

	@Test
	void testInstallDot1a3d() {
		Forest<String> f = TestUtil.buildForest1a3d();
		String dot = ForestRender.toDot(f);
		writeResource(getClass(), "install1a3d.md", dot, FORCE);

		String txt = loadResource(getClass(), "install1a3d.md");
		Differ.diff(f.label(), txt, dot).sdiff(true, 120).out();

		assertEquals(txt, dot);
	}

	@Test
	void testSize() {
		Forest<String> f = new Forest<>();
		f.install(TestUtil.V1a);
		assertEquals(6, f.size());
		f.install(TestUtil.V1b);
		assertEquals(10, f.size());
		f.install(TestUtil.V1c);
		assertEquals(12, f.size());
		f.install(TestUtil.V1d);
		assertEquals(12, f.size());
		f.install(TestUtil.V1e);
		assertEquals(12, f.size());

		f.install(TestUtil.V2a);
		assertEquals(14, f.size());
		f.install(TestUtil.V2b);
		assertEquals(16, f.size());
		f.install(TestUtil.V2c);
		assertEquals(16, f.size());
		f.install(TestUtil.V2d);
		assertEquals(17, f.size());

		f.install(TestUtil.V3a);
		assertEquals(23, f.size());
		f.install(TestUtil.V3b);
		assertEquals(24, f.size());
		f.install(TestUtil.V3c);
		assertEquals(24, f.size());
		f.install(TestUtil.V3d);
		assertEquals(25, f.size());
	}
}
