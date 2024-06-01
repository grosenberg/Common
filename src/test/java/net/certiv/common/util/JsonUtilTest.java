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
import net.certiv.common.util.json.ClassDeserializer;
import net.certiv.common.util.json.ClassSerializer;
import net.certiv.common.util.json.PathDeserializer;
import net.certiv.common.util.json.PathSerializer;
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

	private JsonUtil jsu;
	private KVStore store;

	@BeforeEach
	void setUp() throws Exception {
		jsu = JsonUtil.mapper() //
				.includeTypes(true) //
				.addSerDes(Path.class, new PathSerializer(), new PathDeserializer()) //
				.addSerDes(Class.class, new ClassSerializer(), new ClassDeserializer()) //
				.prettify(true) //
		;

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
		String typed = jsu.toJson(VStr);
		writeResource(getClass(), "StringTyped.json", typed, FORCE);

		jsu.includeTypes(false);

		String basic = jsu.toJson(VStr);
		writeResource(getClass(), "StringBasic.json", basic, FORCE);

		String array = jsu.toJson(VAry);
		writeResource(getClass(), "StringArray.json", array, FORCE);

		String exp = loadResource(getClass(), "StringBasic.json");
		Differ.diff("StringBasic", exp, basic).sdiff(true, 120).out();
		assertEquals(exp, basic);

		String str = jsu.fromJson(basic, String.class);
		assertEquals(VStr, str);
	}

	@Test
	void testPath() {
		String json = jsu.toJson(VPath);
		writeResource(getClass(), "Path.json", json, FORCE);

		String exp = loadResource(getClass(), "Path.json");
		Differ.diff("Path", exp, json).sdiff(true, 120).out();
		assertEquals(exp, json);

		Path path = jsu.fromJson(json, Path.class);
		assertEquals(VPath, path);
	}

	@Test
	void testPathBasic() {
		String json = jsu.includeTypes(false).toJson(VPath);
		writeResource(getClass(), "PathBasic.json", json, FORCE);

		String exp = loadResource(getClass(), "PathBasic.json");
		Differ.diff("PathBasic", exp, json).sdiff(true, 120).out();
		assertEquals(exp, json);

		Path path = jsu.fromJson(json, Path.class);
		assertEquals(VPath, path);
	}

	@Test
	void testClassType() {
		String json = jsu.toJson(VCls);
		writeResource(getClass(), "ClassType.json", json, FORCE);

		String exp = loadResource(getClass(), "ClassType.json");
		Differ.diff("Class", exp, json).sdiff(true, 120).out();
		assertEquals(exp, json);

		Class<?> cls = jsu.fromJson(json, Class.class);
		assertEquals(VCls, cls);
	}

	@Test
	void testClassInst() {
		AClass ac = new AClass(VStr, false);

		String json = jsu.includeTypes(false).toJson(ac);
		writeResource(getClass(), "ClassInst.json", json, FORCE);

		String exp = loadResource(getClass(), "ClassInst.json");
		Differ.diff("Class", exp, json).sdiff(true, 120).out();
		assertEquals(exp, json);

		AClass cls = jsu.fromJson(json, AClass.class);
		assertEquals(ac, cls);
	}

	@Test
	void testJavaTypedCollection() {
		LinkedHashList<String, AClass> list = new LinkedHashList<>();
		for (int idx = 0; idx < 5; idx++) {
			list.put("X", new AClass("X" + idx, false));
		}

		String json = jsu.toJson(list);
		writeResource(getClass(), "JavaTypedCollection.json", json, FORCE);

		String exp = loadResource(getClass(), "JavaTypedCollection.json");
		Differ.diff("JavaTypedCollection", exp, json).sdiff(true, 200).out();
		assertEquals(exp, json);

		JavaType type = jsu.typeOf(LinkedHashList.class, String.class, AClass.class);
		LinkedHashList<String, AClass> restored = jsu.fromJson(json, type);
		assertEquals(list, restored);
	}

	@Test
	void testRefTypedCollection() {
		LinkedHashMap<String, AClass> map = new LinkedHashMap<>();
		for (int idx = 0; idx < 5; idx++) {
			String key = "X" + idx;
			map.put(key, new AClass(key, false));
		}

		String json = jsu.toJson(map);
		writeResource(getClass(), "RefTypedCollection.json", json, FORCE);

		String exp = loadResource(getClass(), "RefTypedCollection.json");
		Differ.diff("RefTypedCollection", exp, json).sdiff(true, 200).out();
		assertEquals(exp, json);

		TypeReference<LinkedHashMap<String, AClass>> ref = JsonUtil.ref();
		LinkedHashMap<String, AClass> restored = jsu.fromJson(json, ref);
		assertEquals(map, restored);
	}

	@Test
	void testKVStore() {
		String json = jsu.toJson(store);
		writeResource(getClass(), "KVStore.json", json, FORCE);

		String exp = loadResource(getClass(), "KVStore.json");
		Differ.diff("KVStore", exp, json).sdiff(true, 200).out();
		assertEquals(exp, json);
	}

	@Test
	void testKVStoreDup() {
		KVStore dup = jsu.dup(store);
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
