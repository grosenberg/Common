package net.certiv.common.util;

import java.io.IOException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.Arrays;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

public class ClassUtil {

	public static final String JAR = ".jar";
	public static final String CLASS = ".class";

	public static final Comparator<URL> URLComp = new Comparator<>() {

		@Override
		public int compare(URL u1, URL u2) {
			return u1.toString().compareToIgnoreCase(u1.toString());
		}
	};

	public static ClassLoader defaultClassLoader() {
		ClassLoader cl = null;
		try {
			cl = Thread.currentThread().getContextClassLoader();
		} catch (Throwable ex) {
			System.err.println("No context classLoader: " + ex.getMessage());
		}
		if (cl == null) {
			cl = ClassUtil.class.getClassLoader();
			if (cl == null) {
				System.err.println("No 'ClassUtil.class' classLoader");
				try {
					cl = ClassLoader.getSystemClassLoader();
				} catch (Throwable ex) {
					System.err.println("No system classLoader: " + ex.getMessage());
				}
			}
		}
		System.err.println("ClassLoader: " + cl != null ? cl.getName() : null);
		return cl;
	}

	public static List<URL> dump() {
		List<URL> urls = new LinkedList<>();
		try (URLClassLoader cl = (URLClassLoader) Thread.currentThread().getContextClassLoader()) {
			urls.addAll(Arrays.asList(cl.getURLs()));
			urls.sort(URLComp);
		} catch (IOException ex) {}
		return urls;
	}

	// ClassLoader cl = ClassLoader.getSystemClassLoader();
	public static String dumpClasspath(URLClassLoader cl) {
		return dumpURLs(cl.getURLs());
	}

	public static String dumpURLs(URL[] urls) {
		StringBuilder sb = new StringBuilder("URL dump:\n");
		for (URL url : urls) {
			sb.append(url.toString() + Strings.EOL);
		}
		return sb.toString();
	}

	/** Dumps he packages defined in this class loader. */
	public static String dump(ClassLoader loader) {

		StringBuilder sb = new StringBuilder();

		if (loader instanceof URLClassLoader) {
			URLClassLoader urlLoader = (URLClassLoader) loader;
			for (URL url : urlLoader.getURLs()) {
				sb.append(url.toString() + Strings.SEMI);
			}

		} else {
			Class<?> cls = loader.getClass();
			while (cls != ClassLoader.class) {
				cls = cls.getSuperclass();
			}

			LinkedHashMap<String, Package> packages = new LinkedHashMap<>();
			try {
				Map<?, ?> pkgmap = (Map<?, ?>) Reflect.get(cls, "packages");
				for (Entry<?, ?> entry : pkgmap.entrySet()) {
					String name = (String) entry.getKey();
					Package pkg = (Package) entry.getValue();
					packages.put(name, pkg);
				}
			} catch (Exception e) {
				return "Error: " + e.getMessage();
			}

			for (String pkg : packages.keySet()) {
				sb.append(pkg + Strings.SEMI);
			}
		}

		return sb.toString();
	}
	//
	// /** Returns the class name of the given object. */
	// @Deprecated
	// public static String name(Object obj) {
	// if (obj == null) return null;
	// return obj.getClass().getName();
	// }
	//
	// /** Returns the class simple name of the given object. */
	// @Deprecated
	// public static String simple(Object obj) {
	// if (obj == null) return null;
	// return obj.getClass().getSimpleName();
	// }
}
