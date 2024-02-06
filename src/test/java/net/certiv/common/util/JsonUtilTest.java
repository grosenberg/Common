package net.certiv.common.util;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.net.URI;
import java.net.URL;
import java.nio.file.Path;
import java.time.Duration;
import java.time.Instant;
import java.util.LinkedHashMap;
import java.util.Objects;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JavaType;

import net.certiv.common.diff.Differ;
import net.certiv.common.log.Level;
import net.certiv.common.log.Log;
import net.certiv.common.stores.LinkedHashList;
import net.certiv.common.stores.context.KVStore;
import net.certiv.common.stores.context.Key;
import net.certiv.common.util.test.CommonTestBase;

class JsonUtilTest extends CommonTestBase {

	static final boolean FORCE = false;

	private static final Key<String> KStr = Key.of("str");
	private static final Key<String[]> KAry = Key.of("ary");
	private static final Key<Class<?>> KCls = Key.of("class");
	private static final Key<Duration> KDur = Key.of("key.duration");
	private static final Key<Instant> KInst = Key.of("key.instant");
	private static final Key<Level> KLvl = Key.of("key.level");
	private static final Key<Path> KPath = Key.of("key.path");
	private static final Key<URI> KUri = Key.of("key.uri");
	private static final Key<URL> KUrl = Key.of("key.url");

	private static final String VStr = "one off";
	private static final String[] VAry = { "one", "two", "three" };
	private static final Class<?> VCls = Log.class;
	private static final Duration VDur = Duration.ofSeconds(10);
	private static final Instant VInst = Instant.ofEpochSecond(10000);
	private static final Level VLvl = Level.INFO;
	private static final Path VPath = Path.of("test", "path");
	private static final URI VUri;
	private static final URL VUrl;

	static {
		try {
			VUri = Path.of("test", "uri").toUri();
			VUrl = Path.of("test", "url").toUri().toURL();
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	private KVStore store;

	@BeforeEach
	void setUp() throws Exception {
		// Log.setTestMode(true);

		store = new KVStore();
		store.put(KStr, VStr);
		store.put(KAry, VAry);
		store.put(KCls, VCls);
		store.put(KDur, VDur);
		store.put(KInst, VInst);
		store.put(KLvl, VLvl);
		store.put(KPath, VPath);
		store.put(KUri, VUri);
		store.put(KUrl, VUrl);
	}

	@AfterEach
	void tearDown() throws Exception {
		store.clear();
		store = null;
	}

	@Test
	void testString() {
		String typed = JsonUtil.toJson(VStr, true, true);
		writeResource(getClass(), "StringTyped.json", typed, FORCE);

		String basic = JsonUtil.toJson(VStr, false, true);
		writeResource(getClass(), "StringBasic.json", basic, FORCE);

		String array = JsonUtil.toJson(VAry, false, true);
		writeResource(getClass(), "StringArray.json", array, FORCE);

		String exp = loadResource(getClass(), "StringBasic.json");
		Differ.diff("StringBasic", exp, basic).sdiff(true, 120).out();
		assertEquals(exp, basic);

		String str = JsonUtil.fromJson(basic, false, String.class);
		assertEquals(VStr, str);
	}

	@Test
	void testPath() {
		String json = JsonUtil.toJson(VPath, true, true);
		writeResource(getClass(), "Path.json", json, FORCE);

		String exp = loadResource(getClass(), "Path.json");
		Differ.diff("Path", exp, json).sdiff(true, 120).out();
		assertEquals(exp, json);

		Path path = JsonUtil.fromJson(json, true, Path.class);
		assertEquals(VPath, path);
	}

	@Test
	void testPathBasic() {
		String json = JsonUtil.toJson(VPath, false, true);
		writeResource(getClass(), "PathBasic.json", json, FORCE);

		String exp = loadResource(getClass(), "PathBasic.json");
		Differ.diff("PathBasic", exp, json).sdiff(true, 120).out();
		assertEquals(exp, json);

		Path path = JsonUtil.fromJson(json, false, Path.class);
		assertEquals(VPath, path);
	}

	@Test
	void testClassType() {
		String json = JsonUtil.toJson(VCls, true, true);
		writeResource(getClass(), "ClassType.json", json, FORCE);

		String exp = loadResource(getClass(), "ClassType.json");
		Differ.diff("Class", exp, json).sdiff(true, 120).out();
		assertEquals(exp, json);

		Class<?> cls = JsonUtil.fromJson(json, true, Class.class);
		assertEquals(VCls, cls);
	}

	@Test
	void testClassInst() {
		AClass ac = new AClass(VStr, false);

		String json = JsonUtil.toJson(ac, false, true);
		writeResource(getClass(), "ClassInst.json", json, FORCE);

		String exp = loadResource(getClass(), "ClassInst.json");
		Differ.diff("Class", exp, json).sdiff(true, 120).out();
		assertEquals(exp, json);

		AClass cls = JsonUtil.fromJson(json, false, AClass.class);
		assertEquals(ac, cls);
	}

	@Test
	void testJavaTypedCollection() {
		LinkedHashList<String, AClass> list = new LinkedHashList<>();
		for (int idx = 0; idx < 5; idx++) {
			list.put("X", new AClass("X" + idx, false));
		}

		String json = JsonUtil.toJson(list, true, true);
		writeResource(getClass(), "JavaTypedCollection.json", json, FORCE);

		String exp = loadResource(getClass(), "JavaTypedCollection.json");
		Differ.diff("JavaTypedCollection", exp, json).sdiff(true, 200).out();
		assertEquals(exp, json);

		JavaType type = JsonUtil.typeOf(LinkedHashList.class, String.class, AClass.class);
		LinkedHashList<String, AClass> restored = JsonUtil.fromJson(json, true, type);
		assertEquals(list, restored);
	}

	@Test
	void testRefTypedCollection() {
		LinkedHashMap<String, AClass> map = new LinkedHashMap<>();
		for (int idx = 0; idx < 5; idx++) {
			String key = "X" + idx;
			map.put(key, new AClass(key, false));
		}

		String json = JsonUtil.toJson(map, true, true);
		writeResource(getClass(), "RefTypedCollection.json", json, FORCE);

		String exp = loadResource(getClass(), "RefTypedCollection.json");
		Differ.diff("RefTypedCollection", exp, json).sdiff(true, 200).out();
		assertEquals(exp, json);

		TypeReference<LinkedHashMap<String, AClass>> ref = JsonUtil.ref();
		LinkedHashMap<String, AClass> restored = JsonUtil.fromJson(json, true, ref);
		assertEquals(map, restored);
	}

	@Test
	void testKVStore() {
		String json = JsonUtil.toJson(store, true, true);
		writeResource(getClass(), "KVStore.json", json, FORCE);

		String exp = loadResource(getClass(), "KVStore.json");
		Differ.diff("KVStore", exp, json).sdiff(true, 200).out();
		assertEquals(exp, json);
	}

	@Test
	void testKVStoreDup() {
		KVStore dup = JsonUtil.dup(store);
		assertEquals(store, dup);
	}

	static class AClass {
		public String mode;
		public boolean enable;

		public AClass(String mode, boolean enable) {
			this.mode = mode;
			this.enable = enable;
		}

		@Override
		public int hashCode() {
			return Objects.hash(enable, mode);
		}

		@Override
		public boolean equals(Object obj) {
			if (this == obj) return true;
			if (!(obj instanceof AClass)) return false;
			AClass a = (AClass) obj;
			return enable == a.enable && Objects.equals(mode, a.mode);
		}
	}
}
