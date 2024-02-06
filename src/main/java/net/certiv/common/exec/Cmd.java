/*******************************************************************************
 * Copyright (c) 2016 - 2017 Certiv Analytics and others. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package net.certiv.common.exec;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.List;

import net.certiv.common.check.Assert;
import net.certiv.common.log.Log;
import net.certiv.common.stores.Result;
import net.certiv.common.util.Chars;
import net.certiv.common.util.ClassUtil;
import net.certiv.common.util.FsUtil;
import net.certiv.common.util.Strings;

public class Cmd {

	/**
	 * Execute a Java application in a separate background process.
	 *
	 * @param cls     class, located within a jar, containing a main method
	 * @param args    main method arguments; may be {@code null}
	 * @param working working directory; may be {@code null}
	 * @param data    program input data; may be {@code null}
	 * @return program output data
	 */
	public static synchronized String javaw(Class<?> cls, List<String> args, File working, String data) {
		Assert.notNull(cls);
		Result<URI> res = FsUtil.location(cls);
		Assert.isTrue(res.validNonNull(), String.format("Cannot locate jar containing '%s'", cls));

		List<String> cmd = new ArrayList<>();
		cmd.add("javaw");
		cmd.add("-jar");
		cmd.add(new File(res.get()).toString());
		if (args != null) cmd.addAll(args);

		ProcessBuilder pb = new ProcessBuilder(cmd);
		if (working != null && working.isDirectory()) pb.directory(working);
		return exec(pb, data);
	}

	/**
	 * Execute a Java class in the context of the given classloader in a separate
	 * background process.
	 *
	 * @param cls     class to execute
	 * @param cl      class loader defining a class path environment
	 * @param working working directory; may be {@code null}
	 * @param data    program input data; may be {@code null}
	 * @return program output data
	 */
	public static synchronized String javaw(Class<?> cls, URLClassLoader cl, File working, String data) {
		Assert.notNull(cls, cl);
		String program = cls.getCanonicalName();
		String cp = ClassUtil.toClasspath(cl);
		ProcessBuilder pb = new ProcessBuilder("javaw", "-cp", cp, program);
		if (working != null && working.isDirectory()) pb.directory(working);
		return exec(pb, data);
	}

	/**
	 * Execute a command in a subprocess. Sanitizes the I/O to UTF-8.
	 *
	 * @param cmd     command line argument array defining the command and options. The
	 *                command must execute as a standard filter: stdIn to stdOut.
	 * @param working base directory for the command or {@code null} if the command is
	 *                absolute
	 * @param data    input data string
	 * @return output data
	 */
	public static synchronized String process(String[] cmd, String working, String data) {
		ProcessBuilder pb = new ProcessBuilder(cmd);
		if (working != null) pb.directory(new File(working));
		return exec(pb, data);
	}

	private static String exec(ProcessBuilder pb, String data) {
		try {
			pb.redirectErrorStream(true);
			Process p = pb.start();

			// prep for output from the process
			try (InputStreamReader in = new InputStreamReader(p.getInputStream(), Strings.UTF_8);
					BufferedReader br = new BufferedReader(in)) {

				if (data != null) {
					// prep and feed input into the process
					OutputStreamWriter out = new OutputStreamWriter(p.getOutputStream(), in.getEncoding());
					BufferedWriter bw = new BufferedWriter(out);

					bw.write(toUTF8(data));
					bw.close();
				}

				// read output from the process
				StringBuilder sb = new StringBuilder();
				String line;
				while ((line = br.readLine()) != null) {
					sb.append(line + Strings.EOL);
				}
				return sb.toString();
			}

		} catch (IOException e) {
			Log.error("Process error[%s] '%s' ", e.getMessage(), pb.command());
			return Strings.EMPTY;
		}
	}

	/** Returns a UTF-8 encoded copy of the given string. */
	public static String toUTF8(String data) throws UnsupportedEncodingException {
		return new String(data.getBytes(Strings.UTF_8), Strings.UTF_8);
	}

	/** Parse a string into an array of command line arguments. */
	public static String[] parse(String command) {
		List<String> args = new ArrayList<>();
		StringBuilder qStr = new StringBuilder();
		boolean quoted = false;
		char[] ac = command.toCharArray();
		for (char c : ac) {
			if (quoted) {
				qStr.append(c);
				if (c == Chars.QUOTE) {
					quoted = false;
				}
			} else if (Character.isWhitespace(c)) {
				if (qStr.length() != 0) {
					args.add(qStr.toString());
					qStr = new StringBuilder();
				}
			} else {
				qStr.append(c);
				if (c == Chars.QUOTE) {
					quoted = true;
				}
			}
		}

		if (qStr.length() != 0) args.add(qStr.toString());
		if (System.getProperty("os.name").startsWith("Windows")) {
			ArrayList<String> cmd = new ArrayList<>(args.size() + 2);
			cmd.add("cmd.exe");
			cmd.add("/C");
			cmd.addAll(args);
			args = cmd;
		}
		return args.toArray(new String[args.size()]);
	}
}
