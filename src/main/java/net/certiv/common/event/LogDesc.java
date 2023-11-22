package net.certiv.common.event;

import net.certiv.common.log.Level;
import net.certiv.common.util.ExceptUtil;
import net.certiv.common.util.MsgBuilder;

public class LogDesc {

	public static LogDesc of(Level level, String msg) {
		return new LogDesc(level, msg, null, null);
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
		mb.append(level != null, "[%s] ", level);
		mb.append(loc != null, "%50s ", loc.getClassName());
		mb.append(loc != null, ": %s ", loc.getLineNumber());
		mb.append(msg != null, "-- %s ", msg);
		mb.append(e != null, "[%s]", e.getMessage());
		return mb.toString();
	}
}
