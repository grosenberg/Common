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
import java.util.ArrayList;
import java.util.List;

import net.certiv.common.log.Log;
import net.certiv.common.util.Strings;

public class Cmd {

	/**
	 * Execute a command in a subprocess. Sanitizes the I/O to UTF-8.
	 *
	 * @param cmd  command line argument array defining the command and options. The
	 *             command must execute as a standard filter: stdIn to stdOut.
	 * @param base the base directory for the command or {@code null} if the command is
	 *             absolute
	 * @param data an input data string
	 * @return output data
	 */
	public static synchronized String process(String[] cmd, String base, String data) {
		final StringBuilder sb = new StringBuilder();
		final ProcessBuilder pb = new ProcessBuilder(cmd);
		try {
			if (base != null) pb.directory(new File(base));
			pb.redirectErrorStream(true);
			Process process = pb.start();

			// prep for output from the process
			try (InputStreamReader in = new InputStreamReader(process.getInputStream(), Strings.UTF_8);
					BufferedReader br = new BufferedReader(in)) {

				// prep and feed input to the process
				OutputStreamWriter out = new OutputStreamWriter(process.getOutputStream(), in.getEncoding());
				BufferedWriter bw = new BufferedWriter(out);

				bw.write(toUTF8(data));
				bw.close();

				// read output from the process
				String line;
				while ((line = br.readLine()) != null) {
					sb.append(line + Strings.EOL);
				}
				return sb.toString();
			}

		} catch (IOException e) {
			Log.error("Cmd execution error: %s", e.getMessage());
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
		char ac[] = command.toCharArray();
		for (char c : ac) {
			if (quoted) {
				qStr.append(c);
				if (c == '"') {
					quoted = false;
				}
			} else if (Character.isWhitespace(c)) {
				if (qStr.length() != 0) {
					args.add(qStr.toString());
					qStr = new StringBuilder();
				}
			} else {
				qStr.append(c);
				if (c == '"') {
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
