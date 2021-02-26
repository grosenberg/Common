package net.certiv.tools.util;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;

import net.certiv.tools.log.Log;

public class ClassUtil {

	public static final String JAR = ".jar";
	public static final String CLASS = ".class";

	public static final Comparator<URL> URLComp = new Comparator<URL>() {

		@Override
		public int compare(URL u1, URL u2) {
			return u1.toString().compareToIgnoreCase(u1.toString());
		}
	};

	/**
	 * Return a file corresponding to the given class.
	 * <p>
	 * If the given class is located on the filesystem, returns the parent directory
	 * file.
	 * <p>
	 * If the given class is located within a jar, returns the jar file.
	 */
	public static URI locate(Class<?> cls) {
		if (cls != null) {
			try {
				URI uri = cls.getProtectionDomain().getCodeSource().getLocation().toURI();
				System.err.println("URI: " + uri.toString());
				if (uri != null) {
					if (uri.getScheme().equalsIgnoreCase("file")) return uri;
					if (uri.getScheme().equalsIgnoreCase("rsrc")) return null;
				}
			} catch (URISyntaxException e) {}

			try {
				URI uri = cls.getResource(cls.getSimpleName() + CLASS).toURI();
				System.err.println("Class URI: " + uri.toString());

				if (uri.getScheme().equalsIgnoreCase("jar")) {
					System.err.println("Class URI jar path1: " + uri.getPath());
					Path pathname = Paths.get(uri.getPath());
					System.err.println("Class URI jar path2: " + pathname);
					return pathname.toUri();
				}

			} catch (Exception e) {
				Log.error(ClassUtil.class, "Error identifying base URI for %s.", cls.getName());
			}
		}
		return null;

	}

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
}
