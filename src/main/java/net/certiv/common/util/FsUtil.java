/*******************************************************************************
 * Copyright (c) 2016 - 2023 Certiv Analytics and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package net.certiv.common.util;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.Closeable;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.FileTime;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOUtils;

import net.certiv.common.check.Assert;
import net.certiv.common.ex.IOEx;
import net.certiv.common.ex.IllegalArgsEx;
import net.certiv.common.log.Log;
import net.certiv.common.stores.Result;

public final class FsUtil {

	private static final int BufSize = 8192;
	private static final String TmpDir = System.getProperty("java.io.tmpdir");

	private static final String WIN_PREFIX = "/\\w+\\:.*";
	private static final Pattern FROM = Pattern.compile("(src/(main|test)/\\w+|target/(test-)?classes)");
	private static final String TEST_RES = "src/test/resources";

	private static final String ERR_LOC = "Error identifying location URI: %s";
	private static final String ERR_LOC_CLASS = "Indeterminable location for Class '%s'.";
	private static final String ERR_LOC_JAR = "Cannot determime jar-in-jar location for Class '%s'.";

	private static final String ERR_NOT_WRITABLE = "File not writable: %s";
	private static final String ERR_NO_FOLDER = "Folder does not exist: %s";

	private static File sysTmp;

	private FsUtil() {}

	/** Returns the current filesystem time. */
	public static long now() {
		return Date.from(Instant.now()).getTime();
	}

	/**
	 * Returns a {@code long} represeting the last modified date/time of the file at the
	 * given path location.
	 */
	public static long getLastModified(Path path) {
		try {
			FileTime mod = Files.getLastModifiedTime(path);
			return Date.from(mod.toInstant()).getTime();
		} catch (IOException e) {
			return 0;
		}
	}

	/**
	 * Replaces each DOT in the given source string with a forward slash.
	 *
	 * @param source an original string
	 * @return the modified source
	 */
	public static String slashify(String source) {
		return slashify(Strings.DOT, source);
	}

	/**
	 * Replaces each target sequence in the given source string with a forward slash.
	 *
	 * @param target a sequence of char values to be replaced
	 * @param source an original string
	 * @return the modified source
	 */
	public static String slashify(CharSequence target, String source) {
		return source.replace(target, Strings.SLASH);
	}

	public static String mkPathname(Package pkg, String name) {
		String pkgname = slashify(pkg.getName());
		return String.join(Strings.SLASH, pkgname, name);
	}

	/**
	 * Returns the container location for the given class, or {@code null} if the class
	 * does not have a recognizable location.
	 * <p>
	 * For a class file located on the filesystem, the URI is directory containing the
	 * class file.
	 * <p>
	 * For a class file located within a jar, returns the the URI of the jar file
	 * containing the class file.
	 *
	 * @param cls the class to locate
	 * @return {@code URI} identifying the class container
	 * @see #location(Class)
	 */
	public static URI locate(Class<?> cls) {
		Result<URI> res = location(cls);
		return res.get();
	}

	/**
	 * Locates the test resources container of the given class, or {@code null} if the
	 * class does not have a recognizable location.
	 *
	 * @param cls the class used to define the resource location
	 * @return {@code URI} identifying the resource container
	 */
	public static URI locateTest(Class<?> cls) {
		URI uri = locate(cls);
		if (uri == null) return null;

		String path = uri.toString();
		Matcher m = FROM.matcher(path);
		if (m.find()) {
			path = m.reset().replaceFirst(TEST_RES);
		} else {
			path = append(path, TEST_RES).toString();
		}

		if (path.endsWith(Strings.SLASH)) {
			path = path.substring(0, path.length() - 1);
		}
		String pkg = slashify(cls.getPackageName());
		path = String.join(Strings.SLASH, path, pkg);

		return URI.create(path);
	}

	/**
	 * Returns a {@code Result} containing the container location URI for the given class,
	 * or an {@link IOException} if the class does not have a recognizable location.
	 * <p>
	 * For a class file located on the filesystem, the URI is directory containing the
	 * class file.
	 * <p>
	 * For a class file located within a jar, returns the the URI of the jar file
	 * containing the class file.
	 *
	 * @param cls the class to locate
	 * @return {@code Result} containing a class location URI, or {@link IOException} if
	 *         not determinable
	 */
	public static Result<URI> location(Class<?> cls) {
		try {
			URI uri = cls.getProtectionDomain().getCodeSource().getLocation().toURI();
			return loc(uri);

		} catch (Exception e) {
			try {
				URI uri = cls.getResource(cls.getSimpleName() + ClassUtil.CLASS).toURI();
				return loc(uri);
			} catch (Exception ex) {}
			return Result.of(IOEx.of(ERR_LOC_CLASS, cls));
		}
	}

	private static Result<URI> loc(URI uri) {
		try {
			switch (uri.getScheme().toLowerCase()) {
				case "file":
					return Result.of(sanitize(uri));

				case "rsrc":
					return Result.of(IOEx.of(ERR_LOC_JAR, uri));

				case "jar":
				default:
					String part = uri.getSchemeSpecificPart();
					int dot = part.lastIndexOf(ClassUtil.JAR_SEP);
					if (dot > -1) {
						part = part.substring(0, dot);
					}
					return Result.of(sanitize(URI.create(part)));

			}
		} catch (Exception ex) {}
		return Result.of(IOEx.of(ERR_LOC_CLASS, uri));
	}

	/**
	 * Sanitize a URL to ensure correct encoding of spaces and other special characters.
	 * Standardizes on use of Unix-style file path separators.
	 *
	 * @param url source URL
	 * @return santitized URL
	 * @throws IllegalArgumentException     if this URL is not absolute
	 * @throws MalformedURLException        if a URL protocol handler error occurred
	 * @throws UnsupportedEncodingException if this URL is not UTF-8 or equivalent
	 * @throws URISyntaxException           if this URL cannot be converted to a URI
	 */
	public static URL sanitize(URL url)
			throws MalformedURLException, UnsupportedEncodingException, URISyntaxException {
		return sanitize(url.toURI()).toURL();
	}

	/**
	 * Sanitize a URI to ensure correct encoding of spaces and other special characters.
	 * Standardizes on use of Unix-style file path separators.
	 *
	 * @param uri source URI
	 * @return santitized URI
	 * @throws IllegalArgumentException     if this URI is not absolute
	 * @throws UnsupportedEncodingException if this URI is not UTF-8 or equivalent
	 */
	public static URI sanitize(URI uri) throws UnsupportedEncodingException {
		String loc = Path.of(uri).toString();
		loc = FilenameUtils.separatorsToUnix(loc);
		Path path = Path.of(URLDecoder.decode(loc, Strings.UTF_8));
		return path.toUri();
	}

	/**
	 * Returns the file exension of the given pathname or {@code EMPTY} if there is no
	 * extension.
	 *
	 * @param pathname a pathname string
	 * @return the pathname extension
	 */
	public static String getExt(String pathname) {
		if (pathname == null) return Strings.EMPTY;
		return getExt(Path.of(pathname));
	}

	/**
	 * Returns the file exension of the given path or {@code EMPTY} if there is no
	 * extension.
	 *
	 * @param path a pathname
	 * @return the pathname extension
	 */
	public static String getExt(Path path) {
		if (path == null) return Strings.EMPTY;
		String name = path.getFileName().toString();
		int dot = name.lastIndexOf(Chars.DOT);
		if (dot > -1) return name.substring(dot + 1);
		return Strings.EMPTY;
	}

	/**
	 * Returns the given pathname with the filename modified to have the given extension.
	 *
	 * @param pathname a pathname
	 * @param ext      the new extension
	 * @return the modified pathname
	 */
	public static String replaceExt(String pathname, String ext) {
		Path path = replaceExt(Path.of(pathname), ext);
		return path.toString().replace("\\", "/");
	}

	/**
	 * Returns the given pathname with the filename modified to have the given extension.
	 *
	 * @param pathname a pathname
	 * @param ext      the new extension
	 * @return the modified pathname
	 */
	public static Path replaceExt(Path pathname, String ext) {
		if (pathname == null) return pathname;

		String name = pathname.getFileName().toString();
		if (name.matches("[.|..]")) return pathname;

		Path base = pathname.getParent();
		ext = ext.startsWith(Strings.DOT) ? ext : Strings.DOT + ext;

		int dot = name.lastIndexOf(Chars.DOT);
		if (dot > -1) {
			name = name.substring(0, dot);
		}
		return base != null ? base.resolve(name + ext) : Path.of(name + ext);
	}

	public static BufferedReader getReader(File file) {
		try (FileInputStream fis = new FileInputStream(file)) {
			return getReader(fis);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	public static BufferedReader getReader(InputStream in) {
		return getReader(in, Strings.UTF_8);
	}

	public static BufferedReader getReader(InputStream in, String encoding) {
		try {
			return new BufferedReader(new InputStreamReader(in, encoding));
		} catch (UnsupportedEncodingException e) {
			throw new RuntimeException(e);
		}
	}

	public static String readToString(File file) throws IOException {
		try (FileInputStream is = new FileInputStream(file)) {
			return readFromStream(is);
		}
	}

	public static String[] readToLines(File file) throws IOException {
		List<String> lines = new ArrayList<>();
		try (BufferedReader in = new BufferedReader(new FileReader(file))) {
			for (String str = in.readLine(); str != null; str = in.readLine()) {
				lines.add(str);
			}
		}
		return lines.toArray(new String[lines.size()]);
	}

	/**
	 * Returns a String from an InputStream using the platform's default encoding
	 * ({@code ResourcesPlugin.getEncoding()}).
	 */
	public static String readFromStream(InputStream stream) throws IOException {
		return readFromStream(stream, null);
	}

	/**
	 * Returns a String from an InputStream that is aware of its encoding. If the encoding
	 * is {@code null} it sets the platform's default encoding
	 * ({@code ResourcesPlugin.getEncoding()}).
	 */
	public static String readFromStream(InputStream stream, String encoding) throws IOException {
		if (encoding == null) encoding = Strings.UTF_8;
		StringBuilder sb = new StringBuilder(2048);
		try (BufferedInputStream is = new BufferedInputStream(stream);
				InputStreamReader in = new InputStreamReader(is, encoding);) {
			char[] readBuffer = new char[2048];
			int n = in.read(readBuffer);
			while (n > 0) {
				sb.append(readBuffer, 0, n);
				n = in.read(readBuffer);
			}
		}
		return sb.toString();
	}

	@Deprecated
	public static BufferedWriter getWriter(OutputStream out) {
		try (OutputStreamWriter writer = new OutputStreamWriter(out, Strings.UTF_8)) {
			return new BufferedWriter(writer);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	@Deprecated
	public static BufferedWriter getWriter(File file) {
		try (FileOutputStream fos = new FileOutputStream(file); //
				OutputStreamWriter writer = new OutputStreamWriter(fos, Strings.UTF_8)) {
			return new BufferedWriter(writer);

		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	public static void write(File file, CharSequence txt) {
		try (FileOutputStream fos = new FileOutputStream(file); //
				OutputStreamWriter osw = new OutputStreamWriter(fos, Strings.UTF_8);
				BufferedWriter writer = new BufferedWriter(osw)) {

			writer.append(txt);
			writer.flush();

		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	@Deprecated
	public static void close(Closeable io) {
		if (io == null) return;
		try {
			io.close();
		} catch (IOException e) {
			if (e.getMessage() != null && e.getMessage().contains("Closed")) {
				return;
			}
			e.printStackTrace();
		}
	}

	public static void delete(File file) {
		if (!file.exists()) return;
		if (file.delete()) return;
		System.gc();
		if (file.delete()) return;

		try {
			Thread.sleep(50L);
		} catch (InterruptedException interruptedexception) {}
		file.delete();
		if (!file.exists()) return;

		String msg = String.format("Could not delete '%s'", file.getAbsolutePath());
		throw new RuntimeException(new IOException(msg));
	}

	public static StringBuilder getContent(File file) throws IOException {
		StringBuilder sb = new StringBuilder();
		Path path = Paths.get(file.getAbsolutePath());
		try (Stream<String> stream = Files.lines(path, StandardCharsets.UTF_8)) {
			stream.forEach(s -> sb.append(s).append(Strings.EOL));
		}
		return sb;
	}

	public static Path save(StringBuilder sb, File file) throws IOException {
		Assert.notNull(sb, file);
		return Files.writeString(file.toPath(), sb, StandardCharsets.UTF_8);
	}

	/**
	 * Returns the given input stream's contents as a byte array. If a length is specified
	 * (i.e. if length != -1), only length bytes are returned. Otherwise all bytes in the
	 * stream are returned. Note this doesn't close the stream.
	 *
	 * @throws IOException if a problem occured reading the stream.
	 */
	public static byte[] getInputStreamAsByteArray(InputStream stream, int length) throws IOException {
		byte[] contents;
		if (length == -1) {
			contents = new byte[0];
			int contentsLength = 0;
			int amountRead = -1;
			do {
				int amountRequested = Math.max(stream.available(), BufSize);

				// resize contents if needed
				if (contentsLength + amountRequested > contents.length) {
					System.arraycopy(contents, 0, contents = new byte[contentsLength + amountRequested], 0,
							contentsLength);
				}

				// read as many bytes as possible
				amountRead = stream.read(contents, contentsLength, amountRequested);

				if (amountRead > 0) {
					// remember length of contents
					contentsLength += amountRead;
				}
			} while (amountRead != -1);

			// resize contents if necessary
			if (contentsLength < contents.length) {
				System.arraycopy(contents, 0, contents = new byte[contentsLength], 0, contentsLength);
			}
		} else {
			contents = new byte[length];
			int len = 0;
			int readSize = 0;
			while ((readSize != -1) && (len != length)) {
				len += readSize;
				readSize = stream.read(contents, len, length - len);
			}
		}

		return contents;
	}

	/**
	 * Returns the given input stream's contents as a character array. If a length is
	 * specified, i.e., length != -1, only length chars are returned. Otherwise all chars
	 * in the stream are returned.
	 *
	 * @throws IOException if a problem occured reading the stream.
	 */
	public static char[] getInputStreamAsCharArray(InputStream stream, int length, String encoding)
			throws IOException {
		InputStreamReader reader = encoding == null ? new InputStreamReader(stream)
				: new InputStreamReader(stream, encoding);
		char[] contents;
		if (length == -1) {
			contents = new char[0];
			int contentsLength = 0;
			int charsRead = -1;
			do {
				int available = stream.available();
				// resize contents if needed
				if (contentsLength + available > contents.length) {
					System.arraycopy(contents, 0, contents = new char[contentsLength + available], 0,
							contentsLength);
				}
				// read as many chars as possible
				charsRead = reader.read(contents, contentsLength, available);
				if (charsRead > 0) {
					// remember length of contents
					contentsLength += charsRead;
				}
			} while (charsRead > 0);
			// resize contents if necessary
			if (contentsLength < contents.length) {
				System.arraycopy(contents, 0, contents = new char[contentsLength], 0, contentsLength);
			}

		} else {
			contents = new char[length];
			int len = 0;
			int readSize = 0;
			while (readSize != -1 && len != length) {
				// See PR 1FMS89U
				// We record first the read size. In this case len is the actual read
				// size.
				len += readSize;
				readSize = reader.read(contents, len, length - len);
			}
			// See PR 1FMS89U
			// Now we need to resize in case the default encoding used more
			// than one byte for each character
			if (len != length) {
				System.arraycopy(contents, 0, (contents = new char[len]), 0, len);
			}
		}
		reader.close();
		return contents;
	}

	/**
	 * Checks for the existence of a named resource file having a non-zero file size.
	 * Attempts to read the contents of the resource file having the given name in the
	 * package folder, defined by the package of the given class, under the project
	 * {@code <resource>} directory.
	 * <p>
	 * For a {@code <resource>} directory of {@code <project>/src/test/resources},
	 * reference class of {@code a.b.c.D.class}, and name {@code y/Z.txt}, data will be
	 * read from {@code <project>/src/test/resources/a/b/c/y/Z.txt}.
	 *
	 * @param cls  a resource classloader relative class
	 * @param name the resource filename
	 * @return {@code true} if the resource exists with a non-zero size
	 */
	public static boolean checkResource(Class<?> cls, String name) {
		try (InputStream rs = cls.getClassLoader().getResourceAsStream(name)) {
			int b = rs.read();
			return b > -1;

		} catch (Exception e) {
			String pkg = slashify(cls.getPackageName());
			String res = String.join(Strings.SLASH, pkg, name);
			try (InputStream rs = cls.getClassLoader().getResourceAsStream(res)) {
				int b = rs.read();
				return b > -1;

			} catch (Exception ex) {
				return false;
			}
		}
	}

	/**
	 * Writes, or overwrites, the given data to a resource file of the given name in the
	 * package folder, defined by the package of the given class, under the project
	 * {@code test/resources} directory.
	 * <p>
	 * For a {@code test/resources} directory of {@code <project>/src/test/resources},
	 * reference class of {@code a.b.c.D.class}, and name {@code y/Z.txt}, the data will
	 * be written to {@code <project>/src/test/resources/a/b/c/y/Z.txt}.
	 *
	 * @param cls  reference class defining the resource package folder
	 * @param name the resource filename
	 * @param data the data to write
	 * @return a {@link Result} providing a success flag or failure exception
	 */
	public static Result<Boolean> writeResource(Class<?> cls, String name, String data) {
		try {
			URI folder = locateTest(cls);
			String scheme = folder.getScheme();
			if (folder == null || scheme == null || !scheme.equalsIgnoreCase("file")) {
				throw IllegalArgsEx.of(ERR_LOC, cls.getName());
			}

			URI pathname = append(folder, name);
			File dst = new File(pathname);

			File parent = dst.getParentFile();
			if (!parent.exists()) {
				try {
					parent.mkdirs();
				} catch (Exception e) {}
			}

			boolean exists = parent.exists();
			boolean writable = parent.canWrite();

			if (!exists || !writable) {
				String fmt = !exists ? ERR_NO_FOLDER : ERR_NOT_WRITABLE;
				Log.error(fmt, dst.toString());
				throw IllegalArgsEx.of(fmt, dst);
			}

			write(dst, data);
			return Result.OK;

		} catch (Exception e) {
			return Result.of(e);
		}
	}

	/**
	 * Reads the contents of the resource file having the given name to a UTF-8 encoded
	 * String. The resource file is located in the package folder, as defined by the
	 * package of the given class, under the project {@code <resource>} directory.
	 * <p>
	 * For a {@code <resource>} directory of {@code <project>/src/test/resources},
	 * reference class of {@code a.b.c.D.class}, and name {@code y/Z.txt}, data will be
	 * read from {@code <project>/src/test/resources/a/b/c/y/Z.txt}.
	 *
	 * @param cls  class defining the resource classloader
	 * @param name resource filename
	 * @return {@link Result} containing the resource content, or a failure exception
	 */
	public static Result<String> loadResource(Class<?> cls, String name) {
		try (InputStream rs = cls.getClassLoader().getResourceAsStream(name)) {
			return Result.of(IOUtils.toString(rs, Strings.UTF_8));

		} catch (Exception e) {
			String pkg = slashify(cls.getPackageName());
			String res = String.join(Strings.SLASH, pkg, name);
			try (InputStream rs = cls.getClassLoader().getResourceAsStream(res)) {
				return Result.of(IOUtils.toString(rs, Strings.UTF_8));

			} catch (Exception ex) {
				return Result.of(IOEx.of(ex, ex.getMessage()));
			}
		}
	}

	/**
	 * Create a URI from the given URI extended by the given name.
	 *
	 * @param uri  a base URI
	 * @param name nominally a file name
	 * @return the extended URI
	 */
	public static URI append(URI uri, String name) {
		String basepath = uri.toString();
		return append(basepath, name);
	}

	/**
	 * Create a URI from the given basepath and name.
	 *
	 * @param basepath a base path
	 * @param name     nominally a file name
	 * @return the extended URI
	 */
	public static URI append(String basepath, String name) {
		if (basepath.matches(WIN_PREFIX)) {
			basepath = "file:/" + basepath;
		}
		if (basepath.endsWith(Strings.SLASH)) {
			basepath = basepath.substring(0, basepath.length() - 1);
		}
		basepath = String.join(Strings.SLASH, basepath, name);
		return URI.create(basepath);
	}

	/**
	 * Returns the content of the given named resource as a {@code byte[]}.
	 *
	 * @param cls  a resource classloader relative class
	 * @param name the resource name
	 * @return the resource content as a {@code String}
	 * @throws IOException on load IO exception
	 */
	public static byte[] loadByteResource(Class<?> cls, String name) throws IOException {
		try (InputStream rs = cls.getClassLoader().getResourceAsStream(name)) {
			return rs.readAllBytes();

		} catch (Exception e) {
			String pkg = slashify(cls.getPackageName());
			String res = String.join(Strings.SLASH, pkg, name);
			try (InputStream rs = cls.getClassLoader().getResourceAsStream(res)) {
				return rs.readAllBytes();

			} catch (Exception ex) {
				throw IOEx.of(ex, ex.getMessage());
			}
		}
	}

	/**
	 * Copies a resource file with the given pathname, located relative to the bundle root
	 * of the bundle identified by the given ref, to the given destination directory.
	 *
	 * @param ref      object reference that identifies a bundle
	 * @param pathname bundle root relative resource name
	 * @param dst      filesystem destination directory
	 * @return the copied out File
	 */
	public static Result<File> fromBundle(Object ref, String pathname, File dst) {
		Class<?> cls = ref instanceof Class ? (Class<?>) ref : ref.getClass();
		try {
			URL url = cls.getResource(pathname);
			if (url == null) {
				return Result.of(new NoSuchFileException(String.format("'%s:%s' not found.", ref, pathname)));
			}
			dst = new File(dst, new File(pathname).getName());
			dst.getParentFile().mkdirs();
			FileUtils.copyURLToFile(url, dst);
			return Result.of(dst);

		} catch (Exception e) {
			return Result.of(e);
		}
	}

	/**
	 * Copies a resource file at the given resource pathname to the given destination
	 * pathname. If the given resource pathname is absolute, the resource is located
	 * relative to the root of the bundle containing the reference class. Otherwise, the
	 * resource is located relative to the given reference class.
	 *
	 * @param ref      object reference that identifies a bundle
	 * @param resource pathname identifying the resource
	 * @param dst      filesystem destination pathname
	 * @return {@link Result} containing the destination {@link File}, or an
	 *         {@link IOException} identifying the resource read or write failure
	 */
	public static Result<File> fromResource(Object ref, String resource, Path dst) {
		Class<?> cls = ref instanceof Class ? (Class<?>) ref : ref.getClass();
		try (InputStream is = cls.getResourceAsStream(resource)) {
			File file = dst.toFile();
			FileUtils.copyInputStreamToFile(is, file);
			return Result.of(file);

		} catch (Exception e) {
			return Result.of(e);
		}
	}

	// ---- Filesystem ----------------

	public static List<String> getContainedFilenames(Path dir, String ext) throws IOException {
		return getContainedPathnames(dir, ext) //
				.stream() //
				.map(Path::toString) //
				.collect(Collectors.toList());
	}

	public static List<Path> getContainedPathnames(Path dir, String ext) throws IOException {
		return getContainedPathnames(dir, Integer.MAX_VALUE, Strings.EMPTY, ext);
	}

	public static List<Path> getContainedPathnames(Path dir, int depth, String prefix, String ext)
			throws IOException {
		return Files.walk(dir, depth) //
				.filter(Files::isRegularFile) //
				.filter(p -> p.getFileName().toString().startsWith(prefix)) //
				.filter(p -> p.getFileName().toString().endsWith(Strings.DOT + ext)) //
				.collect(Collectors.toList());
	}

	// --------------------------------

	/**
	 * Returns the canonical system temporary directory. Symbolic links are resolved to
	 * the real directory.
	 * <p>
	 * On Windows, {@code System.getProperty("java.io.tmpdir")} returns the real directory
	 * {@code C:\Users\<user>\AppData\Local\Temp}
	 * <p>
	 * On {@code *nix} systems {@code System.getProperty("java.io.tmpdir")} returns a
	 * directory {@code '/var/...} symlinked to the real temporary directory
	 * {@code '/private/var/...}.
	 *
	 * @return canonical system temporary directory
	 * @throws {@link RuntimeException} if filesystem access error occurs on resolving the
	 *                canonical pathname
	 */
	public synchronized static File getSysTmp() {
		if (sysTmp == null) {
			try {
				sysTmp = new File(TmpDir).getCanonicalFile();
			} catch (IOException e) {
				throw new RuntimeException(e);
			}
		}
		return sysTmp;
	}

	/**
	 * Create a new empty file in the given directory, using the given prefix and suffix
	 * strings to define the filename. If this method returns successfully then it is
	 * guaranteed that:
	 * <p>
	 * If the {@code prefix} argument is {@code null}, the prefix {@code "Tmp"} is used.
	 * <p>
	 * If the {@code suffix} argument is {@code null}, the suffix {@code ".tmp"} is used.
	 * <p>
	 * If the {@code dir} argument is {@code null}, the system-dependent default
	 * temporary-file directory is used.
	 *
	 * @param prefix a filename prefix
	 * @param suffix a filename suffix
	 * @param dir    the directory that will contain the created file, or {@code null} to
	 *               select the default system temporary-file directory
	 * @return the the newly-created file
	 * @throws IOException if a file could not be created
	 */
	public static File createTmpFile(String prefix, String suffix, File dir) throws IOException {
		prefix = prefix != null ? prefix : "Tmp";
		suffix = suffix != null ? suffix : ".tmp";
		dir = dir != null ? dir : getSysTmp();
		if (!dir.isDirectory()) {
			try {
				dir.mkdirs();
			} catch (Exception e) {
				throw IOEx.of("Directory '%s' does not exist/cannot be created.", dir);
			}
		}

		String name = String.format("%s-%08d%s", prefix, Maths.nextRandom(99999999), suffix);
		return new File(dir, name);
	}

	/**
	 * Create a filesystem folder at the given path offset under the system temporary
	 * directory.
	 *
	 * @param offset path offset; may be {@code null} or empty
	 * @return constructed filesystem folder
	 * @throws IOException if the folder cannot be created
	 */
	public static File createTmpFolder(String offset) throws IOException {
		return createFolder(getSysTmp(), offset);
	}

	/**
	 * Create a filesystem folder at the given path offset under the given root directory.
	 *
	 * @param dir    base directory
	 * @param offset path offset; may be {@code null} or empty
	 * @return constructed filesystem folder
	 * @throws IOException if the folder cannot be created
	 */
	public static File createFolder(File dir, String offset) throws IOException {
		if (dir == null || !dir.isDirectory()) {
			throw IOEx.of("Directory '%s' does not exist", dir);
		}
		if (offset == null) offset = Strings.EMPTY;

		File tgt = new File(dir, offset);
		tgt.mkdirs();
		return tgt;
	}

	/**
	 * Deletes the given directory, including all contained files and subdirectories.
	 * Returns silently if the given directory is {@code null} or is not a directory.
	 * <p>
	 * Function differs from {@link FileUtils#deleteDirectory(dir)} in that this method
	 * returns silently if the given directory is {@code null} or is not a directory.
	 *
	 * @param dir directory to delete
	 * @throws IOException where deletion is unsuccessful
	 */
	public static void deleteFolder(File dir) throws IOException {
		if (dir != null && dir.isDirectory()) {
			try {
				clearFolder(dir);
				dir.delete();
			} catch (IOException e) {
				throw IOEx.of("Delete folder failed for '%s' [%s]", dir, e.getMessage());
			}
		}
	}

	/**
	 * Deletes the given directory, including all contained files and subdirectories, on
	 * JVM runtime shutdown.
	 *
	 * @param dir directory to delete
	 */
	public static void deleteFolderOnExit(File dir) {
		if (dir != null && dir.isDirectory()) {
			Runtime.getRuntime().addShutdownHook(new Thread() {

				@Override
				public void run() {
					try {
						clearFolder(dir);
						dir.delete();
					} catch (Exception e) {}
				}
			});
		}
	}

	/**
	 * Clears the given directory by deleting all contained files and subdirectories.
	 * Returns silently if the given directory is {@code null} or is not a directory.
	 *
	 * @param dir directory to clear
	 * @throws IOException on failure to delete a contained file or subdirectory
	 */
	public static void clearFolder(File dir) throws IOException {
		if (dir != null && dir.isDirectory()) {
			Files.walk(dir.toPath()) //
					.map(Path::toFile) //
					.sorted(Comparator.reverseOrder()) //
					.forEach(File::delete);
		}
	}
}
