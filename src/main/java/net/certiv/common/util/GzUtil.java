package net.certiv.common.util;

import static java.nio.file.StandardCopyOption.REPLACE_EXISTING;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.jar.JarFile;
import java.util.stream.Collectors;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import org.apache.commons.compress.archivers.ArchiveEntry;
import org.apache.commons.compress.archivers.tar.TarArchiveInputStream;
import org.apache.commons.compress.compressors.gzip.GzipCompressorInputStream;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOUtils;

import net.certiv.common.check.Assert;
import net.certiv.common.ex.IOEx;
import net.certiv.common.ex.IllegalArgsEx;
import net.certiv.common.stores.Result;

public class GzUtil {

	private static final String ERR_UNKNOWN_ARCHIVE = "Unknown archive type [%s]: %s";
	private static final String ERR_DIR = "Unable to create directory '%s' from '%s'.";

	public static final String JSON = ".json";
	public static final String GZ = ".gz";
	public static final String JSON_GZ = JSON + GZ;

	private GzUtil() {}

	public static boolean isJsonName(Path pathname) {
		if (pathname == null) return false;

		String name = pathname.getFileName().toString();
		return name.endsWith(JSON) || name.endsWith(JSON_GZ);
	}

	// ---- Text ----------------------

	/**
	 * Returns {@code String} text read from a file specified by the given pathname
	 * corresponding to an absolute location within the local filesystem or offset
	 * relative to the root of the Core jar.
	 *
	 * @param pathname filesystem absolute or Core jar root-relative pathname
	 * @return a text string
	 */
	public static Result<String> loadText(Path pathname) {
		return loadText(pathname, GzUtil.class.getClassLoader());
	}

	/**
	 * Returns {@code String} text read from a file specified by the given pathname
	 * corresponding to an absolute location within the local filesystem or offset
	 * relative to the root of the Jar containing the given class.
	 *
	 * @param pathname a pathname: filesystem absolute or Jar root-relative
	 * @param cl       the relevant {@code Classloader}
	 * @return a text string
	 * @throws IOException on bad pathname or read failure
	 */
	public static Result<String> loadText(Path pathname, ClassLoader cl) {
		if (pathname == null) return Result.of(IllegalArgsEx.of("Path parameter is 'null'."));
		if (cl == null) return Result.of(IllegalArgsEx.of("ClassLoader parameter is 'null'."));

		try (InputStream is = toStream(pathname, cl)) {
			if (is == null) return Result.of(IllegalArgsEx.of("Could not read from '%s'", pathname));
			return Result.of(IOUtils.toString(is, Strings.UTF_8));

		} catch (IOException e) {
			return Result.of(e);
		}
	}

	/**
	 * Saves the given plain text to a file at the given pathname. The file will be GZip'd
	 * dependent on the pathname extension. Parent directories will be created as needed.
	 * Overwrites any existing file.
	 *
	 * @param txt      text content
	 * @param pathname local filesystem absolute pathname
	 */
	public static Result<Boolean> saveText(String txt, Path pathname) {
		if (txt == null) return Result.of(IllegalArgsEx.of("Text parameter is 'null'."));
		if (pathname == null) return Result.of(IllegalArgsEx.of("Path parameter is 'null'."));

		pathname.getParent().toFile().mkdirs();
		try (OutputStream os = Files.newOutputStream(pathname)) {
			if (pathname.toString().endsWith(GZ)) {
				try (GZIPOutputStream writer = new GZIPOutputStream(os)) {
					writer.write(txt.getBytes());
				}

			} else {
				os.write(txt.getBytes());
			}
			return Result.OK;

		} catch (IOException e) {
			return Result.of(e);
		}
	}

	/**
	 * Returns an input stream for reading a file from the given pathname within the
	 * standard filesystem or relative to the root of the Jar containing the given class.
	 *
	 * @param pathname file pathname
	 * @param cl       jar relative classloader
	 * @return input stream or {@code null} if not found
	 * @throws IOException on read error
	 */
	public static InputStream toStream(Path pathname, ClassLoader cl) throws IOException {
		InputStream is = null;
		if (Files.isRegularFile(pathname)) {
			is = Files.newInputStream(pathname);

		} else if (cl != null) {
			String name = pathname.getFileName().toString();
			is = cl.getResourceAsStream(name);
		}

		if (is != null && pathname.toString().endsWith(GZ)) {
			is = new GZIPInputStream(is);
		}

		return is;
	}

	// ---- Archives ------------------

	/**
	 * Copies the content from the given archive to the given destination directory. The
	 * hierarchical structure of the content is maintained.
	 * <p>
	 * Handles {@code '.gz'}, {@code '.tgz'}, {@code '.tar.gz'}, {@code '.jar'}, and
	 * {@code '.zip'} archives.
	 *
	 * @param archive archive file on the filesystem
	 * @param dir     filesystem destination directory
	 * @return {@link Result} containing the filesystem destination file for single file
	 *         archives ({@code '.gz'}), base extraction directory for collection archives
	 *         ({@code '.tgz'}, {@code '.tar.gz'}, {@code '.jar'}, and {@code '.zip'}), or
	 *         an {@link IOException} identifying whatever read/write failure occurred
	 */
	public static Result<File> extractArchive(File archive, File dir) {
		try {
			Objects.requireNonNull(archive);
			Objects.requireNonNull(dir);

			String name = archive.getName();
			String ext = decodeExtension(name);
			switch (ext) {
				case "gz":
					return extractGz(archive, dir);
				case "tgz":
					return extractTar(archive, dir, true);
				case "tar":
					return extractTar(archive, dir, false);
				case "jar":
					return extractZip(archive, dir, true);
				case "zip":
					return extractZip(archive, dir, false);
				default:
					throw IOEx.of(ERR_UNKNOWN_ARCHIVE, ext, name);
			}

		} catch (Exception e) {
			return Result.of(e);
		}
	}

	private static String decodeExtension(String name) {
		String ext = FilenameUtils.getExtension(name);
		if (ext.equals("gz") && name.endsWith(".tar.gz")) return "tgz";
		return ext;
	}

	private static Result<File> extractGz(File archive, File dir) {
		try {
			Assert.notNull(archive, dir);
			Assert.isTrue(archive.isFile());
			if (!dir.exists()) dir.mkdirs();
			Assert.isTrue(dir.isDirectory());

			try (BufferedInputStream in = new BufferedInputStream(new FileInputStream(archive));
					GzipCompressorInputStream is = new GzipCompressorInputStream(in)) {

				String name = is.getMetaData().getFileName();
				Path dst = dir.toPath().resolve(name);
				Files.copy(is, dst, REPLACE_EXISTING);
				return Result.of(dst.toFile());
			}

		} catch (Exception e) {
			return Result.of(e);
		}
	}

	private static Result<File> extractTar(File archive, File dir, boolean gzip) {
		try {
			Assert.notNull(archive, dir);
			Assert.isTrue(archive.isFile());
			if (!dir.exists()) dir.mkdirs();
			Assert.isTrue(dir.isDirectory());

			try (BufferedInputStream in = new BufferedInputStream(new FileInputStream(archive));
					TarArchiveInputStream is = new TarArchiveInputStream(
							gzip ? new GzipCompressorInputStream(in) : in)) {

				ArchiveEntry entry;
				while ((entry = is.getNextEntry()) != null) {
					File dst = new File(dir, entry.getName());
					if (entry.isDirectory()) {
						dst.mkdirs();
						if (!dst.isDirectory()) {
							throw IOEx.of(ERR_DIR, dst.getCanonicalPath(), archive.getName());
						}
					} else {
						Files.copy(is, dst.toPath(), REPLACE_EXISTING);
					}
				}
				return Result.of(dir);
			}

		} catch (Exception e) {
			return Result.of(e);
		}
	}

	private static Result<File> extractZip(File archive, File dir, boolean jar) {
		try {
			Assert.notNull(archive, dir);
			Assert.isTrue(archive.isFile());
			if (!dir.exists()) dir.mkdirs();
			Assert.isTrue(dir.isDirectory());

			try (ZipFile zip = jar ? new JarFile(archive) : new ZipFile(archive)) {
				// sort to create folders first (?)
				List<? extends ZipEntry> entries = zip.stream() //
						.sorted(Comparator.comparing(ZipEntry::getName)) //
						.collect(Collectors.toList());

				for (ZipEntry entry : entries) {
					File dst = new File(dir, entry.getName());
					if (entry.isDirectory()) {
						dst.mkdirs();
						if (!dst.isDirectory()) {
							throw IOEx.of(ERR_DIR, dst.getCanonicalPath(), archive.getName());
						}
					} else {
						try (InputStream is = zip.getInputStream(entry)) {
							Files.copy(is, dst.toPath(), REPLACE_EXISTING);
						}
					}
				}
			}
			return Result.of(dir);

		} catch (Exception e) {
			return Result.of(e);
		}
	}

}
