package net.certiv.common.util;

import java.io.IOException;
import java.net.URI;
import java.net.URL;
import java.nio.file.Path;
import java.text.DateFormat;
import java.time.Duration;
import java.time.Instant;

import com.google.gson.ExclusionStrategy;
import com.google.gson.FieldAttributes;
import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonParseException;
import com.google.gson.ToNumberStrategy;
import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import com.google.gson.stream.JsonWriter;
import com.google.gson.stream.MalformedJsonException;

import net.certiv.common.annotations.Exclude;
import net.certiv.common.stores.context.type.Hex;

public class GsonUtil {

	/**
	 * Serialize/deserialize for persistence; implements the
	 * {@code ExcludeAnnotationStrategy} to permit use of {@code Exclude} annotations.
	 */
	public static final Gson GSON = new GsonBuilder() //
			.registerTypeHierarchyAdapter(Class.class, ClassAdapter.safe())
			.registerTypeHierarchyAdapter(Path.class, PathAdapter.safe())
			.registerTypeAdapter(URI.class, UriAdapter.safe())
			.registerTypeAdapter(Instant.class, InstantAdapter.safe())
			.registerTypeAdapter(Duration.class, DurationAdapter.safe())
			.addSerializationExclusionStrategy(new ExcludeAnnotationStrategy())
			.enableComplexMapKeySerialization() //
			.disableHtmlEscaping() //
			.setFieldNamingPolicy(FieldNamingPolicy.IDENTITY) //
			.setDateFormat(DateFormat.LONG) //
			.setObjectToNumberStrategy(NumStrategy.safe()) //
			.create();

	/** Generalized, unconstrained serialize/deserialize operator. */
	public static final Gson SerDes = new GsonBuilder() //
			.registerTypeHierarchyAdapter(Class.class, ClassAdapter.safe())
			.registerTypeHierarchyAdapter(Path.class, PathAdapter.safe())
			.registerTypeAdapter(URI.class, UriAdapter.safe())
			.registerTypeAdapter(Instant.class, InstantAdapter.safe())
			.registerTypeAdapter(Duration.class, DurationAdapter.safe()) //
			.enableComplexMapKeySerialization() //
			.setObjectToNumberStrategy(NumStrategy.safe()) //
			.create();

	@SuppressWarnings("unchecked")
	@Deprecated
	public static final <T> T dup(T value) {
		return SerDes.fromJson(SerDes.toJson(value), (Class<T>) value.getClass());
	}

	public static final <T> String toJson(T value) {
		return GSON.toJson(value);
	}

	public static final <T> T fromJson(String json, Class<T> cls) {
		return GSON.fromJson(json, cls);
	}

	// ---- Strategies ----------------

	public static final class ExcludeAnnotationStrategy implements ExclusionStrategy {

		@Override
		public boolean shouldSkipClass(Class<?> cls) {
			return cls.getAnnotation(Exclude.class) != null;
		}

		@Override
		public boolean shouldSkipField(FieldAttributes f) {
			return f.getAnnotation(Exclude.class) != null;
		}
	}

	// ---- Type Adapters -------------

	public static final class ClassAdapter extends TypeAdapter<Class<?>> {

		private ClassAdapter() {}

		static TypeAdapter<Class<?>> safe() {
			return new ClassAdapter().nullSafe();
		}

		@Override
		public void write(final JsonWriter out, final Class<?> cls) throws IOException {
			out.value(cls.getName());
		}

		@Override
		public Class<?> read(final JsonReader in) throws IOException {
			try {
				return Class.forName(in.nextString());

			} catch (final ClassNotFoundException e) {
				throw new JsonParseException(e);
			}
		}
	}

	/** Handle filesystem dependent {@code Path}. */
	public static final class PathAdapter extends TypeAdapter<Path> {

		private PathAdapter() {}

		static TypeAdapter<Path> safe() {
			return new PathAdapter().nullSafe();
		}

		@Override
		public void write(final JsonWriter out, final Path path) throws IOException {
			out.value(path.toString()/* .replace(Chars.ESC, Chars.SLASH) */);
		}

		@Override
		public Path read(final JsonReader in) throws IOException {
			if (in.peek() == JsonToken.NULL) {
				in.nextNull();
				return null;
			}
			String str = in.nextString();
			return str != null ? Path.of(str) : null;
		}
	}

	public static final TypeAdapter<URL> URL = new TypeAdapter<>() {
		@Override
		public URL read(JsonReader in) throws IOException {
			if (in.peek() == JsonToken.NULL) {
				in.nextNull();
				return null;
			}
			String nextString = in.nextString();
			return "null".equals(nextString) ? null : new URL(nextString);
		}

		@Override
		public void write(JsonWriter out, URL value) throws IOException {
			out.value(value == null ? null : value.toExternalForm());
		}
	};

	public static final class UriAdapter extends TypeAdapter<URI> {

		private UriAdapter() {}

		static TypeAdapter<URI> safe() {
			return new UriAdapter().nullSafe();
		}

		@Override
		public void write(final JsonWriter out, final URI uri) throws IOException {
			out.value(uri.toString());
		}

		@Override
		public URI read(final JsonReader in) throws IOException {
			return URI.create(in.nextString());
		}
	}

	public static final class InstantAdapter extends TypeAdapter<Instant> {

		private InstantAdapter() {}

		static TypeAdapter<Instant> safe() {
			return new InstantAdapter().nullSafe();
		}

		@Override
		public void write(final JsonWriter out, final Instant instant) throws IOException {
			out.value(instant.toString());
		}

		@Override
		public Instant read(final JsonReader in) throws IOException {
			return Instant.parse(in.nextString());
		}
	}

	public static class DurationAdapter extends TypeAdapter<Duration> {

		private DurationAdapter() {}

		static TypeAdapter<Duration> safe() {
			return new DurationAdapter().nullSafe();
		}

		@Override
		public void write(final JsonWriter out, final Duration duration) throws IOException {
			out.value(duration.toString());
		}

		@Override
		public Duration read(final JsonReader in) throws IOException {
			return Duration.parse(in.nextString());
		}
	}

	public static final class NumStrategy implements ToNumberStrategy {
		private static final String ERR_NAN = "JSON forbids NaN and infinities: %s [%s]";
		private static final String ERR_UNK = "Unknown JSON %s [%s]";

		static NumStrategy safe() {
			return new NumStrategy();
		}

		@Override
		public Number readNumber(JsonReader in) throws IOException, JsonParseException {
			String value = in.nextString();
			if (value.contains(Strings.DOT)) {
				try {
					Double d = Double.valueOf(value);
					if ((d.isInfinite() || d.isNaN()) && !in.isLenient()) {
						String err = String.format(ERR_NAN, d, in.getPreviousPath());
						throw new MalformedJsonException(err);
					}
					return d;
				} catch (NumberFormatException e) {
					String err = String.format(ERR_UNK, value, in.getPreviousPath());
					throw new JsonParseException(err, e);
				}

			} else {
				try {
					return Integer.valueOf(value);
				} catch (NumberFormatException e) {
					try {
						return Long.parseLong(value);
					} catch (NumberFormatException x) {
						try {
							return Hex.parseString(value);
						} catch (NumberFormatException z) {
							String err = String.format(ERR_UNK, value, in.getPreviousPath());
							throw new JsonParseException(err, e);
						}
					}
				}
			}
		}
	}

	// --------------------------------

	// public static class NumberXAdapter extends TypeAdapter<Number> {
	//
	// private NumberXAdapter() {}
	//
	// static TypeAdapter<Number> safe() {
	// return new NumberXAdapter().nullSafe();
	// }
	//
	// @Override
	// public void write(final JsonWriter out, final Number num) throws IOException {
	// out.value(typeOf(num.getClass()) + Strings.COLON2 + num.toString());
	// }
	//
	// @Override
	// public Number read(final JsonReader in) throws IOException {
	// String[] part = in.nextString().split(Strings.COLON2);
	// return convert(part[0], part[1]);
	// }
	//
//		// @formatter:off
//		private	enum Type { I, L, F, D, B, H }
//		// @formatter:on
	//
	// private String typeOf(Class<? extends Number> type) {
	// if (type == Integer.class) return Type.I.name();
	// if (type == Long.class) return Type.L.name();
	// if (type == Float.class) return Type.F.name();
	// if (type == Double.class) return Type.D.name();
	// if (type == Byte.class) return Type.B.name();
	// if (type == Hex.class) return Type.H.name();
	// return "?";
	// }
	//
	// private Number convert(String type, String num) {
	// switch (Type.valueOf(type)) {
	// case I:
	// return Integer.valueOf(num);
	// case L:
	// return Long.valueOf(num);
	// case F:
	// return Float.valueOf(num);
	// case D:
	// return Double.valueOf(num);
	// case B:
	// return Long.valueOf(num);
	// case H:
	// return Hex.parseDouble(Double.valueOf(num));
	// default:
	// return null;
	// }
	// }
	// }
	//
	// public static class NumberSerDesAdapter implements JsonSerializer<Number>,
	// JsonDeserializer<Number> {
	//
	// static class TypedNumber {
	//
	// public final Number num;
	// public final Class<? extends Number> type;
	//
	// TypedNumber(Number num) {
	// this.num = num;
	// this.type = num != null ? num.getClass() : Number.class;
	// }
	//
	// public Number value() {
	// if (type == Integer.class) return Integer.valueOf(num.intValue());
	// if (type == Long.class) return Long.valueOf(num.longValue());
	// if (type == Float.class) return Float.valueOf(num.floatValue());
	// if (type == Double.class) return Double.valueOf(num.doubleValue());
	// if (type == Byte.class) return Long.valueOf(num.longValue());
	// if (type == Hex.class) return Hex.parseDouble(num.doubleValue());
	// return num;
	// }
	// }
	//
	// private NumberSerDesAdapter() {}
	//
	// static NumberSerDesAdapter safe() {
	// return new NumberSerDesAdapter(); // inherently safe
	// }
	//
	// @Override
	// public Number deserialize(JsonElement json, Type typeOfT,
	// JsonDeserializationContext ctx)
	// throws JsonParseException {
	//
	// try {
	// JsonObject obj = json.getAsJsonObject();
	// TypedNumber tn = ctx.deserialize(obj.get("typednum"), TypedNumber.class);
	// return tn.value();
	// } catch (Exception e) {
	// return null;
	// }
	// }
	//
	// @Override
	// public JsonElement serialize(Number num, Type typeOfSrc, JsonSerializationContext
	// ctx) {
	// if (num == null) return null;
	//
	// try {
	// JsonObject obj = new JsonObject();
	// obj.add("typednum", ctx.serialize(new TypedNumber(num), TypedNumber.class));
	// return obj;
	// } catch (Exception e) {
	// throw new RuntimeException(e);
	// }
	// }
	// }
	//
	// public static class IntegerAdapter extends TypeAdapter<Integer> {
	//
	// private IntegerAdapter() {}
	//
	// static TypeAdapter<Integer> safe() {
	// return new IntegerAdapter().nullSafe();
	// }
	//
	// @Override
	// public void write(final JsonWriter out, final Integer idx) throws IOException {
	// out.value(idx.toString());
	// }
	//
	// @Override
	// public Integer read(final JsonReader in) throws IOException {
	// return Integer.valueOf(in.nextString());
	// }
	// }

	// --------------------------------

	// public static final class HashBasedTableAdapter<R, C, V>
	// implements JsonSerializer<Table<R, C, V>>, JsonDeserializer<Table<R, C, V>> {
	//
	// private static final String BACKING_MAP = "backingMap";
	// private static final Type fieldType;
	// static {
	// try {
	// fieldType = HashBasedTable.class.getDeclaredField(BACKING_MAP).getGenericType();
	// } catch (NoSuchFieldException | SecurityException e) {
	// throw new AssertionError(e);
	// }
	// }
	//
	// @Override
	// public JsonElement serialize(Table<R, C, V> src, Type typeOfSrc,
	// JsonSerializationContext context) {
	// Result<Map<?, ?>> res = Reflect.get(src, BACKING_MAP);
	// if (res.err()) throw new IllegalArgumentException("No backing map.", res.err);
	// return context.serialize(res.result, asTableType(typeOfSrc));
	// }
	//
	// @Override
	// public Table<R, C, V> deserialize(JsonElement json, Type typeOfT,
	// JsonDeserializationContext context)
	// throws JsonParseException {
	//
	// Map<R, Map<C, V>> backingMap = context.deserialize(json, asTableType(typeOfT));
	// Table<R, C, V> table = HashBasedTable.create();
	// for (Entry<R, Map<C, V>> entry : backingMap.entrySet()) {
	// R row = entry.getKey();
	// for (Entry<C, V> value : entry.getValue().entrySet()) {
	// table.put(row, value.getKey(), value.getValue());
	// }
	// }
	// return table;
	// }
	//
	// private static Type asTableType(Type tableType) {
	// return TypeToken.of(tableType).resolveType(fieldType).getType();
	// }
	// }
	//
	// public static final class MultimapAdapter<K, V>
	// implements JsonSerializer<Multimap<K, V>>, JsonDeserializer<Multimap<K, V>> {
	//
	// private static final String AS_MAP = "asMap";
	// private static final Type retType;
	// static {
	// try {
	// retType = Multimap.class.getDeclaredMethod(AS_MAP).getGenericReturnType();
	// } catch (NoSuchMethodException e) {
	// throw new AssertionError(e);
	// }
	// }
	//
	// @Override
	// public JsonElement serialize(Multimap<K, V> src, Type typeOfSrc,
	// JsonSerializationContext context) {
	// return context.serialize(src.asMap(), asMapType(typeOfSrc));
	// }
	//
	// @Override
	// public Multimap<K, V> deserialize(JsonElement json, Type typeOfT,
	// JsonDeserializationContext context)
	// throws JsonParseException {
	//
	// Map<K, Collection<V>> asMap = context.deserialize(json, asMapType(typeOfT));
	// Multimap<K, V> multimap = ArrayListMultimap.create();
	// for (Map.Entry<K, Collection<V>> entry : asMap.entrySet()) {
	// multimap.putAll(entry.getKey(), entry.getValue());
	// }
	// return multimap;
	// }
	//
	// private static Type asMapType(Type multimapType) {
	// return TypeToken.of(multimapType).resolveType(retType).getType();
	// }
	// }
}
