/*******************************************************************************
 * Copyright (c) 2016 - 2017 Certiv Analytics and others.
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
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.FileTime;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Random;
import java.util.stream.Stream;

public final class FsUtil {

	private static final int DEFAULT_READING_SIZE = 8192;
	private static final Random RANDOM = new Random(System.currentTimeMillis());
	private static final String TmpDir = System.getProperty("java.io.tmpdir");

	private static File sysTmp;

	private FsUtil() {}

	/** Returns the current filesystem time. */
	public static long now() {
		return Date.from(Instant.now()).getTime();
	}

	/**
	 * Returns a {@code long} represeting the last modified date/time of the file at
	 * the given path location.
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
	 * Returns the file exension of the given pathname or {@code EMPTY} if there is
	 * no extension.
	 *
	 * @param pathname a pathname string
	 * @return the pathname extension
	 */
	public static String getExt(String pathname) {
		if (pathname == null || pathname.isEmpty()) return Strings.EMPTY;
		Path path = Path.of(pathname);
		String name = path.getFileName().toString();
		int dot = name.lastIndexOf(Chars.DOT);
		if (dot > -1) return name.substring(dot + 1);
		return Strings.EMPTY;
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
		InputStreamReader reader;
		try {
			reader = new InputStreamReader(in, encoding);
			return new BufferedReader(reader);
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
	 * Returns a String from an InputStream that is aware of its encoding. If the
	 * encoding is {@code null} it sets the platform's default encoding
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

	public static BufferedWriter getWriter(File file) {
		try {
			return new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file), Strings.UTF_8));
		} catch (UnsupportedEncodingException | FileNotFoundException e) {
			throw new RuntimeException(e);
		}
	}

	public static BufferedWriter getWriter(OutputStream out) {
		OutputStreamWriter writer;
		try {
			writer = new OutputStreamWriter(out, Strings.UTF_8);
			return new BufferedWriter(writer);
		} catch (UnsupportedEncodingException e) {
			throw new RuntimeException(e);
		}
	}

	public static void write(File out, CharSequence page) {
		try (FileOutputStream fos = new FileOutputStream(out); BufferedWriter writer = getWriter(fos)) {
			writer.append(page);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

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
	 * Returns the given input stream's contents as a byte array. If a length is
	 * specified (i.e. if length != -1), only length bytes are returned. Otherwise
	 * all bytes in the stream are returned. Note this doesn't close the stream.
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
				int amountRequested = Math.max(stream.available(), DEFAULT_READING_SIZE);

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
	 * Returns the given input stream's contents as a character array. If a length
	 * is specified, i.e., length != -1, only length chars are returned. Otherwise
	 * all chars in the stream are returned.
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
				// We record first the read size. In this case len is the actual read size.
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

	public synchronized static File getSysTmp() {
		if (sysTmp == null) {
			sysTmp = new File(TmpDir);
			if (!sysTmp.exists()) sysTmp.mkdirs();
		}
		return sysTmp;
	}

	public static long nextRandom() {
		return Math.abs(RANDOM.nextLong());
	}

	public static int nextRandom(int bound) {
		return Math.abs(RANDOM.nextInt(bound));
	}

	public static void deleteTmpFolderOnExit(File dir) {
		Runtime.getRuntime().addShutdownHook(new Thread() {

			@Override
			public void run() {
				try {
					if (dir.isDirectory()) Files.walk(dir.toPath()) //
							.map(Path::toFile) //
							.sorted(Comparator.reverseOrder()) //
							.forEach(File::delete);
				} catch (Exception e) {}
			}
		});
	}

	public static File createTmpFolder(String path) throws IOException {
		return createTmpFolder(getSysTmp(), path);
	}

	public static File createTmpFolder(File root, String path) throws IOException {
		Assert.notNull(root, path);
		Assert.isLegal(root.isDirectory());

		File dir = new File(root, path);
		dir.mkdirs();
		return dir;
	}

	/**
	 * Create a new empty file in the given directory, using the given prefix and
	 * suffix strings to define the filename. If this method returns successfully
	 * then it is guaranteed that:
	 * <p>
	 * If the {@code prefix} argument is {@code null}, the prefix {@code "Tmp"} is
	 * used.
	 * <p>
	 * If the {@code suffix} argument is {@code null}, the suffix {@code ".tmp"} is
	 * used.
	 * <p>
	 * If the {@code dir} argument is {@code null}, the system-dependent default
	 * temporary-file directory is used.
	 *
	 * @param prefix a filename prefix
	 * @param suffix a filename suffix
	 * @param dir the directory that will contain the created file, or {@code null}
	 *            select the default system temporary-file directory
	 * @return the the newly-created file
	 * @throws IOException if a file could not be created
	 */
	public static File createTmpFile(String prefix, String suffix, File dir) throws IOException {
		dir = dir != null ? dir : new File(TmpDir);
		if (!dir.isDirectory()) {
			throw new IOException(String.format("Directory '%s' does not exist", dir));
		}

		prefix = prefix != null ? prefix : "Tmp";
		suffix = suffix != null ? suffix : ".tmp";

		String name = String.format("%s-%08d%s", prefix, FsUtil.nextRandom(99999999), suffix);
		return new File(dir, name);
	}

	public static void deleteFolder(File dir) throws IOException {
		if (dir == null || !dir.exists()) return;

		clearFolder(dir);
		if (dir.list().length == 0) {
			if (!dir.delete()) throw new IOException("Delete failed for " + dir);
		}
	}

	public static void clearFolder(File folder) {
		assert folder != null;
		assert folder.exists();
		assert folder.isDirectory();

		File[] files = folder.listFiles();
		for (File file : files) {
			if (file.isDirectory()) clearFolder(file);
			file.delete();
		}
	}
}
