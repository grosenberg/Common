package net.certiv.common.util;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Path;

import com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.PrettyPrinter;
import com.fasterxml.jackson.core.json.JsonReadFeature;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.core.util.DefaultPrettyPrinter;
import com.fasterxml.jackson.core.util.MinimalPrettyPrinter;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.fasterxml.jackson.databind.cfg.ConstructorDetector;
import com.fasterxml.jackson.databind.json.JsonMapper;
import com.fasterxml.jackson.databind.jsontype.impl.LaissezFaireSubTypeValidator;
import com.fasterxml.jackson.databind.module.SimpleModule;

import net.certiv.common.ex.JsonException;
import net.certiv.common.util.json.ClassDeserializer;
import net.certiv.common.util.json.ClassSerializer;
import net.certiv.common.util.json.PathDeserializer;
import net.certiv.common.util.json.PathSerializer;

public class JsonUtil {

	// ---- Public API ----------------

	/**
	 * Duplicate (deep clone) the given value by Json serialization/deserialization.
	 *
	 * @param <T>   value type
	 * @param value source value
	 * @return duplicated value
	 * @throws {@link JsonException} on any duplication processing failure
	 */
	public static final <T> T dup(T value) {
		return Inst.serdes(value);
	}

	/**
	 * Serialize the given value. If {@code typed}, include field classes to ensure
	 * correct deserialization. If {@code prettify}, pretty-print the Json output.
	 *
	 * @param <T>      value type
	 * @param value    source value
	 * @param typed    {@code true} to include field typing information
	 * @param prettify {@code true} to pretty-print the Json output
	 * @return a Json string
	 * @throws {@link JsonException} on any serialization processing failure
	 */
	public static final <T> String toJson(T value, boolean typed, boolean prettify) {
		return Inst.serialize(value, typed, prettify);
	}

	/**
	 * Serialize the given value to the given output stream. If {@code typed}, include
	 * field classes to ensure correct deserialization. If {@code prettify}, pretty-print
	 * the Json output.
	 *
	 * @param <T>      value type
	 * @param value    source value
	 * @param typed    {@code true} to include field typing information
	 * @param prettify {@code true} to pretty-print the Json output
	 * @param os       output stream
	 * @throws {@link JsonException} on any serialization processing failure
	 */
	public static final <T> void toJson(T value, boolean typed, boolean prettify, OutputStream os) {
		Inst.serialize(value, typed, prettify, os);
	}

	/**
	 * Deserialize the given Json string to an instance of the given class.
	 *
	 * @param <T>   value type
	 * @param json  Json string
	 * @param typed {@code true} if json includes field typing information
	 * @param cls   target class
	 * @return target class instance
	 * @throws {@link JsonException} on any deserialization processing failure
	 */
	public static final <T> T fromJson(String json, boolean typed, Class<T> cls) {
		return Inst.deserialize(json, typed, cls);
	}

	/**
	 * Deserialize the given Json string to a class instance of the given Java type.
	 *
	 * @param <T>   value type
	 * @param json  Json string
	 * @param typed {@code true} if json includes field typing information
	 * @param type  Java type of target class
	 * @return target class instance
	 * @throws {@link JsonException} on any deserialization processing failure
	 */
	public static <T> T fromJson(String json, boolean typed, JavaType type) {
		return Inst.deserialize(json, typed, type);
	}

	/**
	 * Deserialize the given Json string to a class instance of the given reference type.
	 *
	 * @param <T>   value type
	 * @param json  Json string
	 * @param typed {@code true} if json includes field typing information
	 * @param ref   target class type referrence
	 * @return target class instance
	 * @throws {@link JsonException} on any deserialization processing failure
	 */
	public static final <T> T fromJson(String json, boolean typed, TypeReference<T> ref) {
		return Inst.deserialize(json, typed, ref);
	}

	/**
	 * Deserialize a Json string, as obtained from the given input stream, to an instance
	 * of the given class.
	 *
	 * @param <T>   value type
	 * @param is    input stream
	 * @param typed {@code true} if json includes field typing information
	 * @param cls   target class
	 * @return target class instance
	 * @throws {@link JsonException} on any deserialization processing failure
	 */
	public static final <T> T fromJson(InputStream is, boolean typed, Class<T> cls) {
		return Inst.deserialize(is, typed, cls);
	}

	/**
	 * Deserialize a Json string, as obtained from the given input stream, to a class
	 * instance of the given Java type.
	 *
	 * @param <T>   value type
	 * @param is    input stream
	 * @param typed {@code true} if json includes field typing information
	 * @param type  Java type of target class
	 * @return target class instance
	 * @throws {@link JsonException} on any deserialization processing failure
	 */
	public static final <T> T fromJson(InputStream is, boolean typed, JavaType type) {
		return Inst.deserialize(is, typed, type);
	}

	/**
	 * Deserialize a Json string, as obtained from the given input stream, to a class
	 * instance of the given reference type.
	 *
	 * @param <T>   value type
	 * @param is    input stream
	 * @param typed {@code true} if json includes field typing information
	 * @param ref   target class type referrence
	 * @return target class instance
	 * @throws {@link JsonException} on any deserialization processing failure
	 */
	public static final <T> T fromJson(InputStream is, boolean typed, TypeReference<T> ref) {
		return Inst.deserialize(is, typed, ref);
	}

	/**
	 * Constructs a {@link JavaType} that represents a parameterized type, such as a
	 * generic collection.
	 *
	 * <pre>{@code
	 * JavaType type = JsonUtil.typeOf(LinkedHashList.class, String.class, AClass.class);
	 * LinkedHashList<String, AClass> restored = JsonUtil.fromJson(json, type);
	 * }</pre>
	 *
	 * @param base   base generic class
	 * @param params parameterization classes
	 * @return JavaType
	 */
	public static final JavaType typeOf(Class<?> base, Class<?>... params) {
		return Inst.constructParametricType(base, params);
	}

	/**
	 * Constructs a {@link TypeReference} that represents a parameterized type, such as a
	 * generic collection.
	 *
	 * <pre>{@code
	 * TypeReference<HashMap<String, AClass> ref = JsonUtil.ref();
	 * HashMap<String, AClass> restored = JsonUtil.fromJson(json, ref);
	 * }</pre>
	 *
	 * @param <T> parameterized type
	 * @return TypeReference
	 */
	public static final <T> TypeReference<T> ref() {
		return new TypeReference<>() {};
	}

	// --------------------------------

	private static final DefaultPrettyPrinter DefPP = new DefaultPrettyPrinter();
	private static final MinimalPrettyPrinter MinPP = new MinimalPrettyPrinter();

	private static final JsonUtil Inst = new JsonUtil();

	private ObjectMapper basicMapper;
	private ObjectMapper typedMapper;

	private JsonUtil() {
		// basic json object mapper
		basicMapper = createMapper();
		// basic with included type information for correct deserialization
		typedMapper = createMapper().activateDefaultTyping(new LaissezFaireSubTypeValidator(),
				ObjectMapper.DefaultTyping.EVERYTHING);

	}

	@SuppressWarnings("unchecked")
	private <T> T serdes(T value) {
		return deserialize(serialize(value, true, false), true, (Class<T>) value.getClass());
	}

	private <T> String serialize(T value, boolean typed, boolean prettify) {
		try {
			return writer(typed, prettify).writeValueAsString(value);
		} catch (JsonProcessingException e) {
			throw new JsonException(e);
		}
	}

	private <T> void serialize(T value, boolean typed, boolean prettify, OutputStream os) {
		try {
			writer(typed, prettify).writeValue(os, value);
			os.flush();
		} catch (IOException e) {
			throw new JsonException(e);
		}
	}

	private <T> T deserialize(String value, boolean typed, Class<T> cls) {
		try {
			String json = sanitize(value);
			return typed ? typedMapper.readValue(json, cls) : basicMapper.readValue(json, cls);
		} catch (JsonProcessingException e) {
			throw new JsonException(e);
		}
	}

	private <T> T deserialize(String value, boolean typed, JavaType type) {
		try {
			String json = sanitize(value);
			return typed ? typedMapper.readValue(json, type) : basicMapper.readValue(json, type);
		} catch (JsonProcessingException e) {
			throw new JsonException(e);
		}
	}

	private <T> T deserialize(String value, boolean typed, TypeReference<T> ref) {
		try {
			String json = sanitize(value);
			return typed ? typedMapper.readValue(json, ref) : basicMapper.readValue(json, ref);
		} catch (JsonProcessingException e) {
			throw new JsonException(e);
		}
	}

	private <T> T deserialize(InputStream is, boolean typed, Class<T> cls) {
		try {
			return typed ? typedMapper.readValue(is, cls) : basicMapper.readValue(is, cls);
		} catch (IOException e) {
			throw new JsonException(e);
		}
	}

	private <T> T deserialize(InputStream is, boolean typed, JavaType type) {
		try {
			return typed ? typedMapper.readValue(is, type) : basicMapper.readValue(is, type);
		} catch (IOException e) {
			throw new JsonException(e);
		}
	}

	private <T> T deserialize(InputStream is, boolean typed, TypeReference<T> ref) {
		try {
			return typed ? typedMapper.readValue(is, ref) : basicMapper.readValue(is, ref);
		} catch (IOException e) {
			throw new JsonException(e);
		}
	}

	private JavaType constructParametricType(Class<?> parametrized, Class<?>... params) {
		return basicMapper.getTypeFactory().constructParametricType(parametrized, params);
	}

	// --------------------------------

	private ObjectMapper createMapper() {
		ObjectMapper mapper = JsonMapper.builder() //
				.constructorDetector(ConstructorDetector.DEFAULT) //
				.enable(JsonReadFeature.ALLOW_UNESCAPED_CONTROL_CHARS)
				.enable(JsonParser.Feature.ALLOW_SINGLE_QUOTES)
				.enable(MapperFeature.ACCEPT_CASE_INSENSITIVE_ENUMS) //
				.findAndAddModules() //
				.build();

		// serialize only non-null values
		mapper.setSerializationInclusion(Include.NON_NULL);

		// serialize all non-transient fields without relying on getters/setters
		mapper.setVisibility( //
				mapper.getSerializationConfig().getDefaultVisibilityChecker()
						.withFieldVisibility(Visibility.ANY) //
						.withGetterVisibility(Visibility.NONE) //
						.withIsGetterVisibility(Visibility.NONE) //
		);

		SimpleModule mod = new SimpleModule();
		mod.addSerializer(Path.class, new PathSerializer());
		mod.addDeserializer(Path.class, new PathDeserializer());
		mod.addSerializer(Class.class, new ClassSerializer());
		mod.addDeserializer(Class.class, new ClassDeserializer());
		mapper.registerModule(mod);

		return mapper;
	}

	private String sanitize(String txt) {
		if (txt.startsWith("[“") || txt.startsWith("{“")) {
			return txt.replace("“", "\"").replace("”", "\"");
		}
		return txt;
	}

	private ObjectWriter writer(boolean typed, boolean prettify) {
		ObjectMapper mapper = typed ? typedMapper : basicMapper;
		PrettyPrinter pp = prettify ? DefPP : MinPP;
		return mapper.writer(pp);
	}
}
