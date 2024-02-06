package net.certiv.common.util;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.stream.Stream;

import net.certiv.common.ex.IllegalArgsEx;
import net.certiv.common.stores.Result;

public class Reflect {

	public static final Class<?>[] NoParams = null;
	public static final Object[] NoArgs = null;

	private static final String ERR_CHK = "Arguments check mismatch";

	private Reflect() {}

	@SuppressWarnings("unchecked")
	public static <T> Result<T> get(Object target, String fieldName) {
		try {
			Field f = classOf(target).getDeclaredField(fieldName);
			f.setAccessible(true);
			T value = (T) f.get(target);
			return Result.of(value);

		} catch (Exception | Error e) {
			return Result.of(e);
		}
	}

	@SuppressWarnings("unchecked")
	public static <T> Result<T> getSuper(Object target, String fieldName) {
		try {
			Field f = classOf(target).getSuperclass().getDeclaredField(fieldName);
			f.setAccessible(true);
			T value = (T) f.get(target);
			return Result.of(value);

		} catch (Exception | Error e) {
			return Result.of(e);
		}
	}

	@SuppressWarnings("unchecked")
	public static <T> Result<T> getSuper2(Object target, String fieldName) {
		try {
			Field f = classOf(target).getSuperclass().getSuperclass().getDeclaredField(fieldName);
			f.setAccessible(true);
			T value = (T) f.get(target);
			return Result.of(value);

		} catch (Exception | Error e) {
			return Result.of(e);
		}
	}

	public static Result<Boolean> set(Object target, String fieldName, Object value) {
		try {
			Field f = classOf(target).getDeclaredField(fieldName);
			f.setAccessible(true);
			f.set(target, value);
			return Result.OK;

		} catch (Exception | Error e) {
			return Result.of(e);
		}
	}

	public static Result<Boolean> setSuper(Object target, String fieldName, Object value) {
		try {
			Field f = classOf(target).getSuperclass().getDeclaredField(fieldName);
			f.setAccessible(true);
			f.set(target, value);
			return Result.OK;

		} catch (Exception | Error e) {
			return Result.of(e);
		}
	}

	/**
	 * Invokes the underlying no-argument method identified by the given method name.
	 *
	 * @param <T>        method return type
	 * @param target     class object or class containing the method
	 * @param methodName method to invoke
	 * @return {@link Result} containing the method return or invocation exception
	 */
	public static <T> Result<T> invoke(Object target, String methodName) {
		return invoke(target, methodName, NoParams, NoArgs);
	}

	/**
	 * Invokes the underlying method identified by the given method name with the given
	 * parameter arguments. Parameter types are auto-detected.
	 *
	 * @param <T>        method return type
	 * @param target     class object or class containing the method
	 * @param methodName method to invoke
	 * @param args       method arguments
	 * @return {@link Result} containing the method return or invocation exception
	 */
	public static <T> Result<T> invoke(Object target, String methodName, Object... args) {
		return invoke(target, methodName, params(args), args);
	}

	/**
	 * Invokes the underlying method identified by the given method name with the given
	 * parameter type and parameter arguments.
	 *
	 * @param <T>        method return type
	 * @param target     class object or class containing the method
	 * @param methodName method to invoke
	 * @param params     method parameter types
	 * @param args       method arguments
	 * @return {@link Result} containing the method return or invocation exception
	 */
	@SuppressWarnings("unchecked")
	public static <T> Result<T> invoke(Object target, String methodName, Class<?>[] params, Object[] args) {
		try {
			Method m = classOf(target).getDeclaredMethod(methodName, params);
			m.setAccessible(true);
			T value = (T) m.invoke(target, args);
			return Result.of(value);

		} catch (Exception | Error e) {
			return Result.of(e);
		}
	}

	@SuppressWarnings("unchecked")
	public static <T> Result<T> invokeSuperDeclared(Object target, String methodName, Class<?>[] params,
			Object[] args) {
		try {
			Method m = classOf(target).getSuperclass().getDeclaredMethod(methodName, params);
			m.setAccessible(true);
			T value = (T) m.invoke(target, args);
			return Result.of(value);

		} catch (Exception | Error e) {
			return Result.of(e);
		}
	}

	public static Result<Boolean> hasField(Object target, String name) {
		try {
			classOf(target).getDeclaredField(name);
			return Result.OK;

		} catch (Exception | Error e) {
			return Result.of(e);
		}
	}

	public static Result<Field> findField(Object target, String name) {
		Class<?> parent = classOf(target);
		while (parent != Object.class) {
			try {
				return Result.of(parent.getDeclaredField(name));
			} catch (Exception | Error e) {
				parent = parent.getSuperclass();
				continue;
			}
		}
		return Result.of(null);
	}

	/**
	 * Returns {@code true} if the field is found and successfully set. Searches for the
	 * field in the given target and ordered inheritance classes. Attempts to set the
	 * first found.
	 *
	 * @param target the object to search for the named field
	 * @param name   the field name
	 * @param value  the value to assign to the field
	 * @return a {@code Result} containing {@code true} on success, {@code false} on
	 *         failure to find, or {@code Throwable} on failure to set
	 */
	public static Result<Boolean> setField(Object target, String name, Object value) {
		Result<Field> res = findField(target, name);
		if (!res.validNonNull()) return Result.FAIL;
		try {
			res.get().setAccessible(true);
			res.get().set(target, value);
			return Result.OK;

		} catch (Exception | Error e) {
			return Result.of(e);
		}
	}

	/**
	 * Returns {@code true} if the given target, or subclass thereof, has a public method
	 * of the given name and parameter types.
	 *
	 * @param target     object to search
	 * @param methodName method name
	 * @param params     parameter types
	 * @return {@code true} if the given target has the requested method
	 */
	public static boolean hasMethod(Object target, String methodName, Class<?>[] params) {
		if (params == null) params = NoParams;
		try {
			classOf(target).getMethod(methodName, params);
			return true;

		} catch (Exception | Error e) {
			return false;
		}
	}

	/**
	 * Returns the class type of the first generic field parameter
	 *
	 * @param obj       the target object
	 * @param fieldname the name of the target contained field
	 * @return the class type of the first generic parameter
	 */
	public static <C> Result<Class<C>> typeOf(Object obj, String fieldname) {
		return typeOf(obj, fieldname, 0);
	}

	/**
	 * Returns the class type of the generic field parameter at the given index
	 *
	 * @param target    the target object
	 * @param fieldname the name of the target contained field
	 * @param idx       the position index of the generic parameter being queried
	 * @return the class type of the generic parameter at the given index
	 */
	public static <C> Result<Class<C>> typeOf(Object target, String fieldname, int idx) {
		try {
			Field decl = classOf(target).getDeclaredField(fieldname);
			Type[] types = ((ParameterizedType) decl.getGenericType()).getActualTypeArguments();
			if (idx < 0 || idx >= types.length) return Result.of(new IndexOutOfBoundsException(idx));
			return Result.of(cast((Class<?>) types[idx]));

		} catch (Exception | Error e) {
			return Result.of(e);
		}
	}

	/**
	 * Returns an initialized class instance corresponding to the given class name using
	 * the bootstrap class loader.
	 *
	 * @param name fully qualified name of the desired class
	 * @return {@code Result} object representing the desired class
	 */
	public static <C> Result<Class<C>> forName(String name) {
		return forName(name, ClassUtil.defaultClassLoader());
	}

	/**
	 * Returns an initialized class instance corresponding to the given class name using
	 * the given class loader.
	 *
	 * @param name   fully qualified name of the desired class
	 * @param loader the class loader to use to load the class
	 * @return {@code Result} object representing the desired class
	 */
	public static <C> Result<Class<C>> forName(String name, ClassLoader loader) {
		try {
			return Result.of(cast(Class.forName(name, true, loader)));

		} catch (Exception | Error e) {
			return Result.of(e);
		}
	}

	/**
	 * Returns an instantiated instance of the {@code Class} object identified by the
	 * given fully-qualified class name (in the same format returned by {@code getName}),
	 * using the bootstrap class loader and given constuctor parameter arguments.
	 * <p>
	 * Fails if the instantiated class cannot be cast to the intended class type.
	 *
	 * @param <C>       the intended class type
	 * @param classname fully-qualified class name
	 * @param args      constuctor parameter arguments required for instantiation
	 * @return {@code Result} containing the instantiated class or instantiation error
	 */
	public static <C> Result<C> make(String classname, Object... args) {
		try {
			ClassLoader cl = ClassUtil.defaultClassLoader();
			Class<C> cls = cast(Class.forName(classname, true, cl));
			return make(cls, args);

		} catch (Exception | Error e) {
			return Result.of(e);
		}
	}

	/**
	 * Returns an instantiated instance of the {@code Class} object identified by the
	 * given fully-qualified class name (in the same format returned by {@code getName}),
	 * using the bootstrap class loader and given constuctor parameter arguments.
	 * <p>
	 * Fails if the instantiated class cannot be cast to the intended class type.
	 *
	 * @param <C>       intended class type
	 * @param classname fully-qualified class name
	 * @param params    constuctor parameter types required for instantiation
	 * @param args      constuctor parameter arguments required for instantiation
	 * @return {@code Result} containing the instantiated class or instantiation error
	 */
	public static <C> Result<C> make(String classname, Class<?>[] params, Object[] args) {
		try {
			ClassLoader cl = ClassUtil.defaultClassLoader();
			Class<C> cls = cast(Class.forName(classname, true, cl));
			return make(cls, params, args);

		} catch (Exception | Error e) {
			return Result.of(e);
		}
	}

	/**
	 * Returns an instantiated instance of the {@code Class} object identified by the
	 * given fully-qualified class name (in the same format returned by {@code getName}),
	 * using the given class loader and constuctor parameter arguments.
	 * <p>
	 * Fails if the instantiated class cannot be cast to the intended class type.
	 *
	 * @param <C>       intended class type
	 * @param cl        class loader to use
	 * @param classname fully-qualified class name
	 * @param args      constuctor parameter arguments required for instantiation
	 * @return {@code Result} containing the instantiated class or instantiation error
	 */
	public static <C> Result<C> make(ClassLoader cl, String classname, Object... args) {
		try {
			Class<C> cls = cast(Class.forName(classname, true, cl));
			return make(cls, args);

		} catch (Exception | Error e) {
			return Result.of(e);
		}
	}

	/**
	 * Returns an instantiated instance of the given {@code Class} object using the
	 * no-argument constuctor.
	 *
	 * @param <C> intended class type
	 * @param cls class to instantiate
	 * @return {@code Result} containing the instantiated class or instantiation error
	 */
	public static <C> Result<C> make(Class<C> cls) {
		try {
			Constructor<?> ctor = cls.getConstructor();
			ctor.setAccessible(true);
			C inst = cast(ctor.newInstance());
			return Result.of(inst);

		} catch (InvocationTargetException e) {
			return Result.of(e.getTargetException());
		} catch (Exception | Error e) {
			return Result.of(e);
		}
	}

	/**
	 * Returns an instantiated instance of the given {@code Class} object using the given
	 * constuctor parameter arguments.
	 * <p>
	 * Note: primitive arguments will be boxed.
	 *
	 * @param <C>  intended class type
	 * @param cls  class to instantiate
	 * @param args constuctor parameter arguments required for instantiation
	 * @return {@code Result} containing the instantiated class or instantiation error
	 */
	public static <C> Result<C> make(Class<C> cls, Object... args) {
		try {
			return make(cls, params(args), args);

		} catch (Exception | Error e) {
			return Result.of(e);
		}
	}

	/**
	 * Returns an instantiated instance of the given {@code Class} object using the given
	 * constuctor parameter arguments.
	 * <p>
	 * Identify primitive types by, e.g., {@code int.class} or {@code Integer.TYPE}
	 *
	 * @param <C>    intended class type
	 * @param cls    class to instantiate
	 * @param params constuctor parameter types required for instantiation
	 * @param args   constuctor parameter arguments required for instantiation
	 * @return {@code Result} containing the instantiated class or instantiation error
	 */
	public static <C> Result<C> make(Class<C> cls, Class<?>[] params, Object[] args) {
		try {
			chkArgs(params, args);
			Constructor<?> ctor = cls.getConstructor(params);
			ctor.setAccessible(true);
			C inst = cast(ctor.newInstance(args));
			return Result.of(inst);

		} catch (InvocationTargetException e) {
			return Result.of(e.getTargetException());
		} catch (Exception | Error e) {
			return Result.of(e);
		}
	}

	/**
	 * Return a class array representing the types of the given arguments. Nominally used
	 * to auto-detect the type parameters for use in invoking an object constructor or
	 * method.
	 * <p>
	 * Note: primitive values will be boxed.
	 *
	 * @param args arguments
	 * @return class array
	 */
	public static Class<?>[] params(Object... args) {
		return Stream.of(args).map(a -> classOf(a)).toArray(Class<?>[]::new);
	}

	/**
	 * Return a formal Object array for the given arguments.
	 * <p>
	 * Note: primitive values will be boxed.
	 *
	 * @param args arguments
	 * @return object array
	 */
	public static Object[] args(Object... args) {
		return args;
	}

	/**
	 * Returns the class of the given object.
	 *
	 * @param obj class or value object
	 * @return class
	 */
	public static Class<?> classOf(Object obj) {
		return obj instanceof Class ? (Class<?>) obj : obj.getClass();
	}

	private static void chkArgs(Class<?>[] params, Object[] args) {
		if (params == null && args == null) return;
		if (params != null && args != null && params.length == args.length) return;
		throw IllegalArgsEx.of(ERR_CHK, params, args);
	}

	@SuppressWarnings("unchecked")
	static <C> Class<C> cast(Class<?> cls) {
		return (Class<C>) cls;
	}

	@SuppressWarnings("unchecked")
	static <T> T cast(Object obj) {
		return (T) obj;
	}
}
