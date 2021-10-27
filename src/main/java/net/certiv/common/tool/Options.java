package net.certiv.common.tool;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.apache.commons.text.TextStringBuilder;

import net.certiv.common.util.Strings;

public class Options {

	enum Flg {
		STR, DBL, INT, BOOL;
	}

	public static class Flag {

		public final String mark;
		public final String name;
		public final String desc;
		public final Flg flg;

		public static Flag str(Options opts, String mark, String name, String desc) {
			Flag flag = new Flag(opts, mark, name, desc, Flg.STR);
			return flag;
		}

		public static Flag num(Options opts, String mark, String name, String desc) {
			Flag flag = new Flag(opts, mark, name, desc, Flg.INT);
			return flag;
		}

		public static Flag dbl(Options opts, String mark, String name, String desc) {
			Flag flag = new Flag(opts, mark, name, desc, Flg.DBL);
			return flag;
		}

		public static Flag bool(Options opts, String mark, String name, String desc) {
			Flag flag = new Flag(opts, mark, name, desc, Flg.BOOL);
			return flag;
		}

		private Flag(Options opts, String mark, String name, String desc, Flg flg) {
			this.mark = chk(mark);
			this.name = name;
			this.desc = desc;
			this.flg = flg;

			opts.Flags.put(this.mark, this);
		}

		// flags must start with a dash; add if missing
		private String chk(String mark) {
			if (mark.startsWith(Strings.DASH)) return mark;
			return Strings.DASH + mark;
		}

		@Override
		public String toString() {
			return String.format("\t%s <%%s> '%s' %s", mark, name, desc);
		}
	}

	// set of defined flags: key=flag mark; value=flag
	private final Map<String, Flag> Flags = new LinkedHashMap<>();
	// parsed known flag/args: key=flag; value=parameter argument
	private final Map<Flag, String> opts = new LinkedHashMap<>();
	// remaining unknown flag/args
	private final List<String> rest = new ArrayList<>();
	// program name
	private final String name;

	/** Create an {@code Options} parser. */
	public Options(String name) {
		this.name = name;
	}

	public void parse(String[] args) {
		LinkedList<String> argList = new LinkedList<>(Arrays.asList(args));
		while (!argList.isEmpty()) {
			String arg = argList.poll();
			Flag flag = Flags.get(arg);

			if (flag != null) {
				String next = argList.peek();
				next = next != null && !next.startsWith(Strings.DASH) ? argList.poll() : null;
				opts.put(flag, next);

			} else {
				rest.add(arg);
			}
		}
	}

	public boolean has(Flag flag) {
		return opts.containsKey(flag);
	}

	/**
	 * Returns the argument parameter associated with the given flag, {@code EMPTY}
	 * if the flag has no argument parameter, or {@code null} is the flag was not
	 * defined in the {@code Options} arguments.
	 *
	 * @param flag the option flag
	 * @return the argument parameter associated with the flag
	 */
	public String str(Flag flag) {
		if (!has(flag)) return null;
		String value = opts.get(flag);
		return value != null ? value : Strings.EMPTY;
	}

	/**
	 * Returns the argument parameter associated with the given flag, or the given
	 * {@code defaultValue} if the flag has no argument parameter or was not defined
	 * in the {@code Options} arguments.
	 *
	 * @param flag the option flag
	 * @return the argument parameter associated with the flag
	 */
	public String str(Flag flag, String defaultValue) {
		String value = opts.get(flag);
		return value != null ? value : defaultValue;
	}

	/**
	 * Returns the argument parameter associated with the given flag, {@code TRUE}
	 * if the flag has no argument parameter, or {@code null} is the flag was not
	 * defined in the {@code Options} arguments.
	 *
	 * @param flag the option flag
	 * @return the argument parameter associated with the flag
	 */
	public Boolean bool(Flag flag) {
		if (!has(flag)) return null;
		String value = opts.get(flag);
		return value != null ? Boolean.valueOf(value) : true;
	}

	/**
	 * Returns the argument parameter associated with the given flag, or the given
	 * {@code defaultValue} if the flag has no argument parameter or was not defined
	 * in the {@code Options} arguments.
	 *
	 * @param flag the option flag
	 * @return the argument parameter associated with the flag
	 */
	public Boolean bool(Flag flag, Boolean defaultValue) {
		String value = opts.get(flag);
		return value != null ? Boolean.valueOf(value) : defaultValue;
	}

	/**
	 * Returns the argument parameter associated with the given flag, or {@code 0}
	 * if the flag is either not defined or has no argument parameter.
	 *
	 * @param flag the option flag
	 * @return the argument parameter associated with the flag
	 */
	public Integer num(Flag flag) {
		String value = opts.get(flag);
		return value != null ? Integer.valueOf(value) : 0;
	}

	/**
	 * Returns the argument parameter associated with the given flag, or the given
	 * {@code defaultValue} if the flag has no argument parameter or was not defined
	 * in the {@code Options} arguments.
	 *
	 * @param flag the option flag
	 * @return the argument parameter associated with the flag
	 */
	public Integer num(Flag flag, Integer defaultValue) {
		String value = opts.get(flag);
		return value != null ? Integer.valueOf(value) : defaultValue;
	}

	/**
	 * Returns the argument parameter associated with the given flag, or {@code 0}
	 * if the flag is either not defined or has no argument parameter.
	 *
	 * @param flag the option flag
	 * @return the argument parameter associated with the flag
	 */
	public Double dbl(Flag flag) {
		String value = opts.get(flag);
		return value != null ? Double.valueOf(value) : 0;
	}

	/**
	 * Returns the argument parameter associated with the given flag, or the given
	 * {@code defaultValue} if the flag has no argument parameter or was not defined
	 * in the {@code Options} arguments.
	 *
	 * @param flag the option flag
	 * @return the argument parameter associated with the flag
	 */
	public Double dbl(Flag flag, Double defaultValue) {
		String value = opts.get(flag);
		return value != null ? Double.valueOf(value) : defaultValue;
	}

	/**
	 * Returns the rest of the arguments.
	 *
	 * @return the rest of the arguments.
	 */
	public List<String> rest() {
		return rest;
	}

	public String help() {
		return help(null);
	}

	public String help(String version) {
		TextStringBuilder sb = new TextStringBuilder();
		if (!Strings.blank(version)) sb.appendln("%s (%s)", name, version);
		sb.appendln("Usage: %s [options] args...", name);
		sb.appendln("Options:");
		for (Flag flag : Flags.values()) {
			sb.appendln(String.format(flag.toString(), flag.flg));
		}
		return sb.toString();
	}

	public String dump() {
		TextStringBuilder sb = new TextStringBuilder();
		for (Flag flag : opts.keySet()) {
			Object val = null;
			switch (flag.flg) {
				case BOOL:
					val = bool(flag);
					break;
				case DBL:
					val = dbl(flag);
					break;
				case INT:
					val = num(flag);
					break;
				case STR:
					val = str(flag);
					break;
			}
			sb.appendln(String.format(flag.toString(), val));
		}
		if (!rest.isEmpty()) sb.appendln(rest());
		return sb.toString();
	}
}
