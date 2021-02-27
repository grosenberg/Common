package net.certiv.tools.util;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

public class Reflect {

	public static final Class<?>[] NoParams = null;
	public static final Object[] NoArgs = null;

	private Reflect() {}

	public static boolean set(Object target, String fieldName, Object value) {
		try {
			Field f = target.getClass().getDeclaredField(fieldName);
			f.setAccessible(true);
			f.set(target, value);
			return true;
		} catch (Exception e) {
			return false;
		}
	}

	public static boolean setSuper(Object target, String fieldName, Object value) {
		try {
			Field f = target.getClass().getSuperclass().getDeclaredField(fieldName);
			f.setAccessible(true);
			f.set(target, value);
			return true;
		} catch (Exception e) {
			return false;
		}
	}

	public static Object get(Object target, String fieldName) {
		return get(target, fieldName, false);
	}

	public static Object get(Object target, String fieldName, boolean quiet) {
		try {
			Field f = target.getClass().getDeclaredField(fieldName);
			f.setAccessible(true);
			return f.get(target);
		} catch (Exception e) {
			if (!quiet) e.printStackTrace();
		}
		return null;
	}

	// public static Object getSuper(Object target, String fieldName) {
	// try {
	// Field f = target.getClass().getSuperclass().getDeclaredField(fieldName);
	// f.setAccessible(true);
	// return f.get(target);
	// } catch (Exception e) {}
	// return null;
	// }

	@SuppressWarnings("unchecked")
	public static <T> T getSuper(Object target, String fieldName) {
		try {
			Field f = target.getClass().getSuperclass().getDeclaredField(fieldName);
			f.setAccessible(true);
			return (T) f.get(target);
		} catch (Exception e) {}
		return null;
	}

	public static Object getSuper2(Object target, String fieldName) {
		try {
			Field f = target.getClass().getSuperclass().getSuperclass().getDeclaredField(fieldName);
			f.setAccessible(true);
			return f.get(target);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	public static boolean hasMethod(Object target, String methodName, Class<?>[] params) {
		if (params == null) params = NoParams;
		try {
			Method m = target.getClass().getMethod(methodName, params);
			if (m != null) return true;
		} catch (Exception e) {}
		return false;
	}

	public static Object invoke(Object target, String methodName) {
		return invoke(target, methodName, NoParams, NoArgs);
	}

	public static Object invoke(Object target, String methodName, Class<?>[] params, Object[] args) {
		try {
			Method m = target.getClass().getMethod(methodName, params);
			m.setAccessible(true);
			return m.invoke(target, args);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	public static Object invokeSuperDeclared(Object target, String methodName, Class<?>[] params,
			Object[] args) {
		try {
			Method m = target.getClass().getSuperclass().getDeclaredMethod(methodName, params);
			m.setAccessible(true);
			return m.invoke(target, args);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * Returns an initialized class instance corresponding to the given
	 * {@code classname}, using the bootstrap class loader.
	 *
	 * @param loader class loader from which the class must be loaded
	 * @param classname fully qualified name of the desired class
	 * @return class object representing the desired class
	 * @throws ClassNotFoundException if the class is not found by the loader
	 */
	public static Class<?> classOf(String classname) throws ClassNotFoundException {
		return Class.forName(classname, true, null);
	}

	/**
	 * Returns an initialized class instance corresponding to the given
	 * {@code classname}, using the given class loader.
	 *
	 * @param loader class loader from which the class must be loaded
	 * @param classname fully qualified name of the desired class
	 * @return class object representing the desired class
	 * @throws ClassNotFoundException if the class is not found by the loader
	 */
	public static Class<?> classOf(ClassLoader loader, String classname) throws ClassNotFoundException {
		return Class.forName(classname, true, loader);
	}

	public static <C> C make(ClassLoader loader, String className, Object... args)
			throws ReflectiveOperationException {
		Class<?> cls = Class.forName(className, true, loader);
		return make(cls, args);
	}

	public static <C> C make(Class<?> cls, Object... args) throws ReflectiveOperationException {
		Class<?>[] types = Stream.of(args).map(Object::getClass).toArray(Class<?>[]::new);
		@SuppressWarnings("unchecked")
		Constructor<C> ctor = (Constructor<C>) cls.getConstructor(types);
		ctor.setAccessible(true);
		return ctor.newInstance(args);
	}

	@SafeVarargs
	public static <T> List<T> makeList(T... values) {
		List<T> list = new ArrayList<>();
		for (T value : values) {
			list.add(value);
		}
		return list;
	}

	public static Class<?> typeOfField(Object obj, String field, int argN) {
		try {
			Field decl = obj.getClass().getDeclaredField(field);
			Type[] types = ((ParameterizedType) decl.getGenericType()).getActualTypeArguments();
			if (argN >= 0 && types.length > argN) {
				return (Class<?>) types[argN];
			}
		} catch (Exception e) {}
		return null;
	}

	public static boolean hasField(Object target, String name) {
		try {
			target.getClass().getDeclaredField(name);
			return true;
		} catch (Exception e) {
			return false;
		}
	}
}
