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

	public static final Comparator<? super Package> PkgComp = new Comparator<>() {

		@Override
		public int compare(Package p1, Package p2) {
			return p1.getName().compareTo(p2.getName());
		}
	};

	String adj(String cn) {
		if (!cn.contains("$")) return abbr(cn);

		String[] parts = cn.split("\\$", 2);
		String outer = abbr(parts[0]);
		String inner = parts[1];
		return outer + "$" + inner;
	}

	String abbr(String cn) {
		if (!cn.contains(Strings.DOT)) return cn;

		StringBuilder sb = new StringBuilder();
		String[] parts = cn.split("\\.");
		for (int idx = 0; idx < parts.length - 1; idx++) {
			String part = parts[idx];
			if (!part.isEmpty()) {
				sb.append(part.charAt(0) + Chars.DOT);
			}
		}
		sb.append(parts[parts.length - 1]);
		return sb.toString();
	}

	public static List<URL> dump() {
		List<URL> urls = new LinkedList<>();
		try (URLClassLoader cl = (URLClassLoader) Thread.currentThread().getContextClassLoader()) {
			urls.addAll(Arrays.asList(cl.getURLs()));
			urls.sort(URLComp);
		} catch (IOException ex) {}
		return urls;
	}

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
