package net.certiv.common.event;

import net.certiv.common.annotations.VisibleForTesting;
import net.certiv.common.log.Level;
import net.certiv.common.util.Chars;
import net.certiv.common.util.ExceptUtil;
import net.certiv.common.util.MsgBuilder;
import net.certiv.common.util.Strings;

public class LogDesc {

	public static LogDesc of(Level level, String msg) {
		return new LogDesc(level, msg, null, null);
	}

	public static LogDesc of(Level level, String msg, StackTraceElement loc) {
		return new LogDesc(level, msg, loc, null);
	}

	public static LogDesc of(Level level, String msg, StackTraceElement loc, Throwable e) {
		return new LogDesc(level, msg, loc, e);
	}

	// --------------------------------

	private final Level level;
	private final String msg;
	private final StackTraceElement loc;
	private final Throwable e;

	private LogDesc(Level level, String msg, StackTraceElement loc, Throwable e) {
		this.level = level;
		this.msg = msg;
		this.loc = loc;
		this.e = e;
	}

	public Level level() {
		return level;
	}

	public String msg() {
		return msg;
	}

	public StackTraceElement location() {
		return loc;
	}

	public String fileName() {
		return loc.getFileName();
	}

	public String className() {
		return loc.getClassName();
	}

	public String methodName() {
		return loc.getMethodName();
	}

	public int lineNumber() {
		return loc.getLineNumber();
	}

	public Throwable throwable() {
		return e;
	}

	public String printThrowable() {
		return ExceptUtil.stacktrace(e);
	}

	@Override
	public String toString() {
		MsgBuilder mb = new MsgBuilder();
		if (level != null) mb.append("[%-5s] ", level);
		if (loc != null) mb.append("%-50s ", adj(loc.getClassName()));
		if (loc != null) mb.append(" : %-4s ", loc.getLineNumber());
		if (msg != null) mb.append("-- %s ", msg);
		if (e != null) mb.append("[%s]", e.getMessage());
		return mb.toString();
	}

	@VisibleForTesting
	String adj(String cn) {
		if (cn.length() <= 50) return cn;
		if (!cn.contains("$")) return abbr(cn);

		String[] parts = cn.split("\\$", 2);
		String outer = abbr(parts[0]);
		String inner = parts[1];
		return outer + "$" + inner;
	}

	@VisibleForTesting
	String abbr(String cn) {
		if (!cn.contains(Strings.DOT)) return cn;

		StringBuilder sb = new StringBuilder();
		String[] parts = cn.split("\\.");
		for (int idx = 0; idx < parts.length - 1; idx++) {
			String part = parts[idx];
			if (!part.isEmpty()) {
				sb.append(String.valueOf(part.charAt(0)) + Chars.DOT);
			}
		}
		sb.append(parts[parts.length - 1]);
		return sb.toString();
	}
}
