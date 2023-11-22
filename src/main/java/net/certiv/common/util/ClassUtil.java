package net.certiv.common.util;

import java.io.IOException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.Arrays;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;

public class ClassUtil {

	public static final String JAR = ".jar";
	public static final String CLASS = ".class";

	public static final Comparator<URL> URLComp = new Comparator<>() {

		@Override
		public int compare(URL u1, URL u2) {
			return u1.toString().compareTo(u2.toString());
		}
	};
	private static final Comparator<? super Package> PkgComp = new Comparator<>() {

		@Override
		public int compare(Package p1, Package p2) {
			return p1.getName().compareTo(p2.getName());
		}
	};

	public static List<URL> dump() {
		List<URL> urls = new LinkedList<>();
		try (URLClassLoader cl = (URLClassLoader) Thread.currentThread().getContextClassLoader()) {
			urls.addAll(Arrays.asList(cl.getURLs()));
			urls.sort(URLComp);
		} catch (IOException ex) {}
		return urls;
	}

	// // ClassLoader cl = ClassLoader.getSystemClassLoader();
	// public static String dumpClasspath(URLClassLoader cl) {
	// return dumpURLs(cl.getURLs());
	// }
	//
	// public static String dumpURLs(URL[] urls) {
	// StringBuilder sb = new StringBuilder("URL dump:\n");
	// for (URL url : urls) {
	// sb.append(url.toString() + Strings.EOL);
	// }
	// return sb.toString();
	// }

	/**
	 * Dumps the loadable packages defined relative to the given class.
	 *
	 * @param cls a class defining the top-level classloader to examine
	 * @return printable list of the classloader packages
	 */
	public static String dump(Class<?> cls) {
		MsgBuilder mb = new MsgBuilder("ClassLoader Packages for %s", cls.getName());

		ClassLoader cl = cls.getClassLoader();
		if (cl instanceof URLClassLoader) {
			List<URL> urls = Arrays.asList(((URLClassLoader) cl).getURLs());
			urls.sort(URLComp);
			urls.stream().forEach(p -> mb.nl().indent(p.toString()));

		} else {
			List<Package> pkgs = Arrays.asList(cl.getDefinedPackages());
			pkgs.sort(PkgComp);
			pkgs.stream().forEach(p -> mb.nl().indent(p.getName()));
		}

		// for (ClassLoader cl = cls.getClassLoader(); cl != null; cl.getParent()) {
		// }
		return mb.toString();
	}

	public static ClassLoader defaultClassLoader() {
		try {
			ClassLoader cl = Thread.currentThread().getContextClassLoader();
			if (cl != null) return cl;

		} catch (Throwable t) {
			if (t instanceof OutOfMemoryError) throw t;
		}
		return ClassLoader.getSystemClassLoader();
	}
}
