package net.certiv.common.util;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.Collection;
import java.util.Comparator;
import java.util.Enumeration;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

import net.certiv.common.stores.Result;

public class ClassUtil {

	public static final String CLASS = ".class";
	public static final String JAR = ".jar";
	public static final String JAR_SEP = "!/";

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

	// --------------------------------

	/**
	 * Loads the class with the given <a href="#binary-name">binary</a> classname from the
	 * jar located at the absoluate path specified by the given jarname.
	 *
	 * @param jarname   absolute pathname of a jar
	 * @param classname <a href="#binary-name">binary name</a> of the class
	 * @return {@code Result} containig the resulting {@code Class} object or
	 *         {@link MalformedURLException} if {@code jarname} cannot be resolved to a
	 *         valid URL or {@link ClassNotFoundException} if the class is not found
	 * @see ClassLoader#loadClass(String)
	 */
	public static Result<Class<?>> loadClass(String jarname, String classname) {
		try {
			URLClassLoader cl = createClassLoader(jarname, false);
			return Result.of(cl.loadClass(classname));
		} catch (Exception e) {
			return Result.of(e);
		}
	}

	/**
	 * Creates a new instance of URLClassLoader for the single jar located at the
	 * absoluate path specified by the given jarname. Includes the default class loader as
	 * the parent class loader.
	 * <p>
	 * If {@code install}, the classloader is set as the current context class loader.
	 * When installed, use {@link ClassLoader#getParent()} to recover the original
	 * classloader.
	 *
	 * @param jarname absolute pathname of a jar
	 * @param install {@code true} to install as the current context class loader
	 * @return the resulting class loader
	 * @throws MalformedURLException if {@code jarname} cannot be resolved to a valid URL
	 */
	public static URLClassLoader createClassLoader(String jarname, boolean install)
			throws MalformedURLException {
		URL url = Path.of(jarname).toUri().toURL();
		return createClassLoader(url, install);
	}

	/**
	 * Creates a new instance of URLClassLoader for the given URL. Includes the default
	 * class loader as the parent class loader.
	 * <p>
	 * If {@code install}, the classloader is set as the current context class loader.
	 * When installed, use {@link ClassLoader#getParent()} to recover the original
	 * classloader.
	 *
	 * @param url     URL of a jar
	 * @param install {@code true} to install as the current context class loader
	 * @return the resulting class loader
	 * @throws NullPointerException if {@code url} is {@code null}
	 */
	public static URLClassLoader createClassLoader(URL url, boolean install) {
		return createClassLoader(new URL[] { url }, install);
	}

	/**
	 * Creates a new instance of URLClassLoader for the given URLs. Includes the default
	 * class loader as the parent class loader.
	 * <p>
	 * If {@code install}, the classloader is set as the current context class loader.
	 * When installed, use {@link ClassLoader#getParent()} to recover the original
	 * classloader.
	 *
	 * @param urls    collection of jar URLs
	 * @param install {@code true} to install as the current context class loader
	 * @return the resulting class loader
	 * @throws NullPointerException if {@code urls} is {@code null}
	 */
	public static URLClassLoader createClassLoader(Collection<URL> urls, boolean install) {
		return createClassLoader(urls.toArray(new URL[urls.size()]), install);
	}

	/**
	 * Creates a new instance of URLClassLoader for the given URLs. Includes the default
	 * class loader as the parent class loader.
	 * <p>
	 * If {@code install}, the classloader is set as the current context class loader.
	 * When installed, use {@link ClassLoader#getParent()} to recover the original
	 * classloader.
	 *
	 * @param urls    array of jar URLs
	 * @param install {@code true} to install as the current context class loader
	 * @return the resulting class loader
	 * @throws NullPointerException if {@code urls} is {@code null}
	 */
	public static URLClassLoader createClassLoader(URL[] urls, boolean install) {
		ClassLoader parent = Thread.currentThread().getContextClassLoader();
		URLClassLoader cl = URLClassLoader.newInstance(urls, parent);
		if (install) Thread.currentThread().setContextClassLoader(cl);
		return cl;
	}

	/**
	 * Returns the default class loader. This will be the current thread context class
	 * loader or, if {@code null}, the system class loader.
	 *
	 * @return class loader
	 * @throws OutOfMemoryError if thread access is memory constrained
	 */
	public static ClassLoader defaultClassLoader() {
		try {
			ClassLoader cl = Thread.currentThread().getContextClassLoader();
			if (cl != null) return cl;

		} catch (Throwable t) {
			if (t instanceof OutOfMemoryError) throw t;
		}
		return ClassLoader.getSystemClassLoader();
	}

	/**
	 * Returns the URLs discoverable from the given class loader in the form of a Java
	 * classpath string.
	 *
	 * @param cl URL classloader
	 * @return classpath
	 */
	public static String toClasspath(URLClassLoader cl) {
		if (cl == null) return Strings.EMPTY;
		return Arrays.stream(cl.getURLs()) //
				.map(u -> {
					try {
						return FsUtil.sanitize(u);
					} catch (Exception e) {
						return null;
					}
				}) //
				.filter(u -> u != null) //
				.map(u -> new File(u.getPath()).toString()) //
				.collect(Collectors.joining(File.pathSeparator));
	}

	/**
	 * Dumps a listing of the packages and resources discoverable from the default
	 * classloader.
	 *
	 * @return formatted list of packages and resources
	 */
	public static String dump() {
		return dump(defaultClassLoader());
	}

	/**
	 * Dumps a listing of the packages and resources discoverable from the classloader of
	 * given class.
	 *
	 * @param cls a class defining the top-level classloader to examine
	 * @return formatted list of packages and resources
	 */
	public static String dump(Class<?> cls) {
		return dump(cls.getClassLoader());
	}

	/**
	 * Dumps a listing of the packages and resources discoverable from the given class
	 * loader.
	 *
	 * @param cl classloader to examine
	 * @return formatted list of packages and resources
	 */
	public static String dump(ClassLoader cl) {
		MsgBuilder mb = new MsgBuilder();
		mb.nl().append("Classloader dump");
		mb.nl().append("Packages...");
		if (cl instanceof URLClassLoader) {
			List<URL> urls = Arrays.asList(((URLClassLoader) cl).getURLs());
			urls.sort(URLComp);
			urls.stream().forEach(p -> mb.nl().indent(p.toString()));

		} else {
			List<Package> pkgs = Arrays.asList(cl.getDefinedPackages());
			pkgs.sort(PkgComp);
			pkgs.stream().forEach(p -> mb.nl().indent(p.getName()));
		}

		mb.nl().append("Resources...");
		try {
			Enumeration<URL> urls = cl.getResources(Strings.DOT);
			List<URL> list = new LinkedList<>();
			while (urls.hasMoreElements()) {
				list.add(urls.nextElement());
			}
			list.sort(URLComp);
			list.stream().forEach(p -> mb.nl().indent(p.toString()));

		} catch (IOException e) {}

		return mb.toString();
	}
}
