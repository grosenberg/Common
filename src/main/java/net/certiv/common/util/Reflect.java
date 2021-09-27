package net.certiv.common.util;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.stream.Stream;

import net.certiv.common.stores.Result;

public class Reflect {

	public static final Class<?>[] NoParams = null;
	public static final Object[] NoArgs = null;

	private Reflect() {}

	@SuppressWarnings("unchecked")
	public static <T> Result<T> get(Object target, String fieldName) {
		try {
			Field f = target.getClass().getDeclaredField(fieldName);
			f.setAccessible(true);
			T value = (T) f.get(target);
			return Result.of(value);
		} catch (Exception e) {
			return Result.of(e);
		}
	}

	@SuppressWarnings("unchecked")
	public static <T> Result<T> getSuper(Object target, String fieldName) {
		try {
			Field f = target.getClass().getSuperclass().getDeclaredField(fieldName);
			f.setAccessible(true);
			T value = (T) f.get(target);
			return Result.of(value);
		} catch (Exception e) {
			return Result.of(e);
		}
	}

	@SuppressWarnings("unchecked")
	public static <T> Result<T> getSuper2(Object target, String fieldName) {
		try {
			Field f = target.getClass().getSuperclass().getSuperclass().getDeclaredField(fieldName);
			f.setAccessible(true);
			T value = (T) f.get(target);
			return Result.of(value);
		} catch (Exception e) {
			return Result.of(e);
		}
	}

	public static Result<Boolean> set(Object target, String fieldName, Object value) {
		try {
			Field f = target.getClass().getDeclaredField(fieldName);
			f.setAccessible(true);
			f.set(target, value);
			return Result.of(true);
		} catch (Exception e) {
			return Result.of(e);
		}
	}

	public static Result<Boolean> setSuper(Object target, String fieldName, Object value) {
		try {
			Field f = target.getClass().getSuperclass().getDeclaredField(fieldName);
			f.setAccessible(true);
			f.set(target, value);
			return Result.of(true);
		} catch (Exception e) {
			return Result.of(e);
		}
	}

	public static <T> Result<T> invoke(Object target, String methodName) {
		return invoke(target, methodName, NoParams, NoArgs);
	}

	@SuppressWarnings("unchecked")
	public static <T> Result<T> invoke(Object target, String methodName, Class<?>[] params, Object[] args) {
		try {
			Method m = target.getClass().getMethod(methodName, params);
			m.setAccessible(true);
			T value = (T) m.invoke(target, args);
			return Result.of(value);
		} catch (Exception e) {
			return Result.of(e);
		}
	}

	@SuppressWarnings("unchecked")
	public static <T> Result<T> invokeSuperDeclared(Object target, String methodName, Class<?>[] params,
			Object[] args) {
		try {
			Method m = target.getClass().getSuperclass().getDeclaredMethod(methodName, params);
			m.setAccessible(true);
			T value = (T) m.invoke(target, args);
			return Result.of(value);
		} catch (Exception e) {
			return Result.of(e);
		}
	}

	public static Result<Boolean> hasField(Object target, String name) {
		try {
			target.getClass().getDeclaredField(name);
			return Result.of(true);
		} catch (Exception e) {
			return Result.of(e);
		}
	}

	public static Result<Boolean> hasMethod(Object target, String methodName, Class<?>[] params) {
		if (params == null) params = NoParams;
		try {
			target.getClass().getMethod(methodName, params);
			return Result.of(true);
		} catch (Exception e) {
			return Result.of(e);
		}
	}

	/**
	 * Returns the class type of the first generic field parameter
	 *
	 * @param target the target object
	 * @param fieldname the name of the target contained field
	 * @return the class type of the first generic parameter
	 */
	public static Result<Class<?>> typeOf(Object obj, String fieldname) {
		return typeOf(obj, fieldname, 0);
	}

	/**
	 * Returns the class type of the generic field parameter at the given index
	 *
	 * @param target the target object
	 * @param fieldname the name of the target contained field
	 * @param idx the position index of the generic parameter being queried
	 * @return the class type of the generic parameter at the given index
	 */
	public static Result<Class<?>> typeOf(Object target, String fieldname, int idx) {
		try {
			Field decl = target.getClass().getDeclaredField(fieldname);
			Type[] types = ((ParameterizedType) decl.getGenericType()).getActualTypeArguments();
			if (idx < 0 || idx >= types.length) return Result.of(new IndexOutOfBoundsException(idx));
			return Result.of((Class<?>) types[idx]);
		} catch (Exception e) {
			return Result.of(e);
		}
	}

	/**
	 * Returns an initialized class instance corresponding to the given class name
	 * using the bootstrap class loader.
	 *
	 * @param name fully qualified name of the desired class
	 * @return {@code Result} object representing the desired class
	 */
	public static Result<Class<?>> forName(String name) {
		return forName(name, null);
	}

	/**
	 * Returns an initialized class instance corresponding to the given class name
	 * using the given class loader.
	 *
	 * @param loader the class loader to use to load the class
	 * @param name fully qualified name of the desired class
	 * @return {@code Result} object representing the desired class
	 */
	public static Result<Class<?>> forName(String name, ClassLoader loader) {
		try {
			return Result.of(Class.forName(name, true, loader));
		} catch (Exception e) {
			return Result.of(e);
		}
	}

	/**
	 * Returns an instantiated instance of the {@code Class} object identified by
	 * the given fully-qualified class name (in the same format returned by
	 * {@code getName}), using the bootstrap class loader and given constuctor
	 * parameter arguments.
	 * <p>
	 * Fails if the instantiated class cannot be cast to the intended class type.
	 *
	 * @param <C> the intended class type
	 * @param classname fully-qualified class name
	 * @param args constuctor parameter arguments required for instantiation
	 * @return {@code Result} containing the instantiated class or instantiation
	 *             error
	 */
	public static <C> Result<C> make(String classname, Object... args) {
		try {
			@SuppressWarnings("unchecked")
			Class<C> cls = (Class<C>) Class.forName(classname, true, null);
			return make(cls, args);

		} catch (Exception e) {
			return Result.of(e);
		}
	}

	/**
	 * Returns an instantiated instance of the {@code Class} object identified by
	 * the given fully-qualified class name (in the same format returned by
	 * {@code getName}), using the given class loader and constuctor parameter
	 * arguments.
	 * <p>
	 * Fails if the instantiated class cannot be cast to the intended class type.
	 *
	 * @param <C> the intended class type
	 * @param loader the class loader to use
	 * @param classname fully-qualified class name
	 * @param args constuctor parameter arguments required for instantiation
	 * @return {@code Result} containing the instantiated class or instantiation
	 *             error
	 */
	public static <C> Result<C> make(ClassLoader loader, String classname, Object... args) {
		try {
			@SuppressWarnings("unchecked")
			Class<C> cls = (Class<C>) Class.forName(classname, true, loader);
			return make(cls, args);

		} catch (Exception e) {
			return Result.of(e);
		}
	}

	/**
	 * Returns an instantiated instance of the given {@code Class} object using the
	 * given constuctor parameter arguments.
	 * <p>
	 * Fails if the instantiated class cannot be cast to the intended class type.
	 *
	 * @param <C> the intended class type
	 * @param cls the class to instantiate
	 * @param args constuctor parameter arguments required for instantiation
	 * @return {@code Result} containing the instantiated class or instantiation
	 *             error
	 */
	public static <C> Result<C> make(Class<C> cls, Object... args) {
		try {
			Class<?>[] types = Stream.of(args).map(Object::getClass).toArray(Class<?>[]::new);
			Constructor<?> ctor = cls.getConstructor(types);
			ctor.setAccessible(true);
			@SuppressWarnings("unchecked")
			C inst = (C) ctor.newInstance(args);
			return Result.of(inst);

		} catch (Exception e) {
			return Result.of(e);
		}
	}
}
