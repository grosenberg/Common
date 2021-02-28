package net.certiv.common.util;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.attribute.BasicFileAttributes;
import java.nio.file.attribute.FileTime;
import java.text.SimpleDateFormat;
import java.util.Properties;
import java.util.jar.Attributes;
import java.util.jar.JarFile;
import java.util.jar.Manifest;

import net.certiv.common.log.Log;
import net.certiv.common.stores.Pair;

public class Version {

	public static String pkgVersion(Class<?> cls) {
		String ver = cls.getPackage().getImplementationVersion();
		if (ver != null) return ver;
		return Strings.UNKNOWN;
	}

	/** Returns the version identifier. */
	public static String propertiesVersion(Class<?> cls) {
		ClassLoader cl = cls.getClassLoader();
		try (InputStream in = cl.getResourceAsStream("ver.properties")) {
			Properties prop = new Properties();
			prop.load(in);
			return (String) prop.get("version");

		} catch (IOException e) {
			Log.error(Version.class, "Failed reading version property: %s", e.getMessage());
			return Strings.UNKNOWN;
		}
	}

	public static Pair<String, String> manifestVersion(Class<?> cls) {
		try {
			File file = new File(cls.getProtectionDomain().getCodeSource().getLocation().toURI());
			if (file.isFile()) {
				try (JarFile jar = new JarFile(file)) {
					Manifest manifest = jar.getManifest();
					Attributes attributes = manifest.getMainAttributes();
					String ver = attributes.getValue("Implementation-Version");
					return Pair.of(ver, creationDate(file));
				}
			}
		} catch (Exception e) {
			Log.error(Version.class, "Failed reading manifest version: %s", e.getMessage());
		}
		return Pair.of(Strings.UNKNOWN, Strings.UNKNOWN);
	}

	public static String creationDate(File file) {
		try {
			BasicFileAttributes attr = Files.readAttributes(file.toPath(), BasicFileAttributes.class);
			FileTime date = attr.creationTime();
			SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
			return df.format(date.toMillis());

		} catch (IOException e) {
			Log.error(Version.class, "Failed reading file creation date: %s", e.getMessage());
			return Strings.UNKNOWN;
		}
	}
}
