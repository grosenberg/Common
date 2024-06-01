package net.certiv.common.util;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.PrettyPrinter;
import com.fasterxml.jackson.core.json.JsonReadFeature;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.core.util.DefaultPrettyPrinter;
import com.fasterxml.jackson.core.util.MinimalPrettyPrinter;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.cfg.ConstructorDetector;
import com.fasterxml.jackson.databind.introspect.VisibilityChecker;
import com.fasterxml.jackson.databind.json.JsonMapper;
import com.fasterxml.jackson.databind.jsontype.impl.LaissezFaireSubTypeValidator;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.module.paramnames.ParameterNamesModule;

import net.certiv.common.ex.JsonException;

public class JsonUtil {

	/**
	 * Create an instance.
	 *
	 * @return json mapping instance
	 */
	public static JsonUtil mapper() {
		return new JsonUtil();
	}

	// ---- Builder API ---------------

	/**
	 * Include element typing information. Must be used consistently for both
	 * serialization and deserialization operations.
	 *
	 * @param enable {@code true} to include typing information
	 * @return updated json mapping instance
	 */
	public JsonUtil includeTypes(boolean enable) {
		if (enable) {
			mapper.activateDefaultTyping(new LaissezFaireSubTypeValidator(),
					ObjectMapper.DefaultTyping.EVERYTHING);
		} else {
			mapper.deactivateDefaultTyping();
		}
		return this;
	}

	/**
	 * @see VisibilityChecker#withFieldVisibility(Visibility)
	 */
	public JsonUtil withFieldVisibility(Visibility vis) {
		mapper.setVisibility( //
				mapper.getSerializationConfig() //
						.getDefaultVisibilityChecker() //
						.withFieldVisibility(vis) //
		);
		return this;
	}

	/**
	 * @see VisibilityChecker#withGetterVisibility(Visibility)
	 */
	public JsonUtil withGetterVisibility(Visibility vis) {
		mapper.setVisibility( //
				mapper.getSerializationConfig() //
						.getDefaultVisibilityChecker() //
						.withGetterVisibility(vis) //
		);
		return this;
	}

	/**
	 * @see VisibilityChecker#withIsGetterVisibility(Visibility)
	 */
	public JsonUtil withIsGetterVisibility(Visibility vis) {
		mapper.setVisibility( //
				mapper.getSerializationConfig() //
						.getDefaultVisibilityChecker() //
						.withIsGetterVisibility(vis) //
		);
		return this;
	}

	/**
	 * Add a custom serializer/deserializer module for handling instances of the given
	 * class.
	 *
	 * @param <T> class type
	 * @param cls class
	 * @param ser serializer for instances of type T
	 * @param des deserializer for instances of type T
	 * @return updated json mapping instance
	 */
	public <T> JsonUtil addSerDes(Class<T> cls, JsonSerializer<T> ser, JsonDeserializer<T> des) {
		SimpleModule mod = new SimpleModule();
		mod.addSerializer(cls, ser);
		mod.addDeserializer(cls, des);
		mapper.registerModule(mod);
		return this;
	}

	/**
	 * Specify whether to emit json output using either a prettified or minimized json
	 * presentation format.
	 *
	 * @param enable {@code true} to select prettified json format; otherwise minimized
	 * @return updated json mapping instance
	 */
	public JsonUtil prettify(boolean enable) {
		pp = enable ? DefPP : MinPP;
		return this;
	}

	// --------------------------------
	// ---- Operation API -------------

	/**
	 * Serialize the given value.
	 *
	 * @param <T>   value type
	 * @param value source value
	 * @return a Json string
	 * @throws {@link JsonException} on any serialization processing failure
	 */
	public <T> String toJson(T value) {
		try {
			return mapper.writer(pp).writeValueAsString(value);
		} catch (JsonProcessingException e) {
			throw new JsonException(e);
		}
	}

	/**
	 * Serialize the given value to the given output stream.
	 *
	 * @param <T>   value type
	 * @param value source value
	 * @param os    output stream
	 * @throws {@link JsonException} on any serialization processing failure
	 */
	public <T> void toJson(T value, OutputStream os) {
		try {
			mapper.writer(pp).writeValue(os, value);
			os.flush();
		} catch (IOException e) {
			throw new JsonException(e);
		}
	}

	/**
	 * Deserialize the given Json string to an instance of the given class.
	 *
	 * @param <T>  value type
	 * @param json Json string
	 * @param cls  target class
	 * @return target class instance
	 * @throws {@link JsonException} on any deserialization processing failure
	 */
	public <T> T fromJson(String json, Class<T> cls) {
		try {
			return mapper.readValue(sanitize(json), cls);
		} catch (JsonProcessingException e) {
			throw new JsonException(e);
		}
	}

	/**
	 * Deserialize the given Json string to a class instance of the given Java type.
	 *
	 * @param <T>  value type
	 * @param json Json string
	 * @param type Java type of target class
	 * @return target class instance
	 * @throws {@link JsonException} on any deserialization processing failure
	 */
	public <T> T fromJson(String json, JavaType type) {
		try {
			return mapper.readValue(sanitize(json), type);
		} catch (JsonProcessingException e) {
			throw new JsonException(e);
		}
	}

	/**
	 * Deserialize the given Json string to a class instance of the given reference type.
	 *
	 * @param <T>  value type
	 * @param json Json string
	 * @param ref  target class type referrence
	 * @return target class instance
	 * @throws {@link JsonException} on any deserialization processing failure
	 */
	public <T> T fromJson(String json, TypeReference<T> ref) {
		try {
			return mapper.readValue(sanitize(json), ref);
		} catch (JsonProcessingException e) {
			throw new JsonException(e);
		}
	}

	/**
	 * Deserialize a Json string, as obtained from the given input stream, to an instance
	 * of the given class.
	 *
	 * @param <T> value type
	 * @param is  input stream
	 * @param cls target class
	 * @return target class instance
	 * @throws {@link JsonException} on any deserialization processing failure
	 */
	public <T> T fromJson(InputStream is, Class<T> cls) {
		try {
			return mapper.readValue(is, cls);
		} catch (IOException e) {
			throw new JsonException(e);
		}
	}

	/**
	 * Deserialize a Json string, as obtained from the given input stream, to a class
	 * instance of the given Java type.
	 *
	 * @param <T>  value type
	 * @param is   input stream
	 * @param type Java type of target class
	 * @return target class instance
	 * @throws {@link JsonException} on any deserialization processing failure
	 */
	public <T> T fromJson(InputStream is, JavaType type) {
		try {
			return mapper.readValue(is, type);
		} catch (IOException e) {
			throw new JsonException(e);
		}
	}

	/**
	 * Deserialize a Json string, as obtained from the given input stream, to a class
	 * instance of the given reference type.
	 *
	 * @param <T> value type
	 * @param is  input stream
	 * @param ref target class type referrence
	 * @return target class instance
	 * @throws {@link JsonException} on any deserialization processing failure
	 */
	public <T> T fromJson(InputStream is, TypeReference<T> ref) {
		try {
			return mapper.readValue(is, ref);
		} catch (IOException e) {
			throw new JsonException(e);
		}
	}

	/**
	 * Duplicate (deep clone) the given value by Json serialization/deserialization.
	 *
	 * @param <T>   value type
	 * @param value source value
	 * @return duplicated value
	 * @throws {@link JsonException} on any duplication processing failure
	 */
	@SuppressWarnings("unchecked")
	public <T> T dup(T value) {
		try {
			return fromJson(toJson(value), (Class<T>) value.getClass());
		} catch (Exception e) {
			throw new JsonException(e);
		}
	}

	// --------------------------------
	// ---- Helpers -------------------

	/**
	 * Constructs a {@link JavaType} that represents a parameterized type, such as a
	 * generic collection.
	 *
	 * <pre>{@code
	 * JsonUtil jsu = ... ;
	 * JavaType type = jsu.typeOf(LinkedHashList.class, String.class, AClass.class);
	 * LinkedHashList<String, AClass> restored = jsu.fromJson(json, type);
	 * }</pre>
	 *
	 * @param base   base generic class
	 * @param params parameterization classes
	 * @return JavaType
	 */
	public JavaType typeOf(Class<?> base, Class<?>... params) {
		return mapper.getTypeFactory().constructParametricType(base, params);
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

	private final ObjectMapper mapper;
	private PrettyPrinter pp = MinPP;

	private JsonUtil() {
		mapper = JsonMapper.builder() //
				.constructorDetector(ConstructorDetector.DEFAULT) //
				.enable(JsonReadFeature.ALLOW_UNESCAPED_CONTROL_CHARS)
				.enable(JsonParser.Feature.ALLOW_SINGLE_QUOTES)
				.enable(MapperFeature.ACCEPT_CASE_INSENSITIVE_ENUMS) //
				.findAndAddModules() //
				.build();

		// default: serialize only non-null values
		mapper.setSerializationInclusion(Include.NON_NULL);

		// default: serialize all non-transient fields without relying on getters/setters
		mapper.setVisibility( //
				mapper.getSerializationConfig().getDefaultVisibilityChecker()
						.withFieldVisibility(Visibility.ANY) //
						.withGetterVisibility(Visibility.NONE) //
						.withIsGetterVisibility(Visibility.NONE) //
		);

		// match constructor parameter names with field names
		mapper.registerModule(new ParameterNamesModule(JsonCreator.Mode.PROPERTIES));
	}

	private String sanitize(String txt) {
		if (txt.startsWith("[“") || txt.startsWith("{“")) {
			return txt.replace("“", "\"").replace("”", "\"");
		}
		return txt;
	}
}
