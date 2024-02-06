package net.certiv.common.log;

import org.apache.commons.lang3.ClassUtils;

import net.certiv.common.util.ExceptUtil;
import net.certiv.common.util.MsgBuilder;

/**
 * Log Descriptor bundles logging information to enable transport.
 */
public class LogDesc {

	/**
	 * Creates a non-localized message logging descriptor.
	 *
	 * <pre>{@code
	 * LogDesc desc = LogDesc.of(level, msg);
	 * Log.printf(desc);
	 * }</pre>
	 *
	 * @param level log level
	 * @param msg   log message
	 * @return log descriptor
	 */
	public static LogDesc of(Level level, String msg) {
		return of(null, level, null, msg);
	}

	/**
	 * Creates a non-localized message logging descriptor.
	 *
	 * <pre>{@code
	 * LogDesc desc = LogDesc.of(level, fmt, args);
	 * Log.printf(desc);
	 * }</pre>
	 *
	 * @param level log level
	 * @param fmt   format string
	 * @param args  format arguments
	 * @return log descriptor
	 */
	public static LogDesc of(Level level, String fmt, Object... args) {
		return of(null, level, null, String.format(fmt, args));
	}

	/**
	 * Creates a logging descriptor localized to the given call locator object. The call
	 * locator object can be a {@link StackTraceElement}, class instance, or class.
	 *
	 * <pre>{@code
	 * LogDesc desc = LogDesc.of(this, level, msg);
	 * Log.printf(desc);
	 * }</pre>
	 *
	 * @param loc   call locator
	 * @param level log level
	 * @param msg   log message
	 * @return log descriptor
	 */
	public static LogDesc of(Object loc, Level level, String msg) {
		return of(loc, level, null, msg);
	}

	/**
	 * Creates a logging descriptor localized to the given call locator object. The call
	 * locator object can be a {@link StackTraceElement}, class instance, or class.
	 *
	 * <pre>{@code
	 * LogDesc desc = LogDesc.of(this, level, fmt, args);
	 * Log.printf(desc);
	 * }</pre>
	 *
	 * @param loc   call locator
	 * @param level log level
	 * @param fmt   format string
	 * @param args  format arguments
	 * @return log descriptor
	 */
	public static LogDesc of(Object loc, Level level, String fmt, Object... args) {
		return of(loc, level, null, String.format(fmt, args));
	}

	/**
	 * Creates a logging descriptor localized to the given call locator object. The call
	 * locator object can be a {@link StackTraceElement}, class instance, or class.
	 *
	 * <pre>{@code
	 * LogDesc desc = LogDesc.of(this, level, e, msg);
	 * Log.printf(desc);
	 * }</pre>
	 *
	 * @param loc   call locator
	 * @param level log level
	 * @param e     exception
	 * @param msg   log message
	 * @return log descriptor
	 */
	public static LogDesc of(Object loc, Level level, Throwable e, String msg) {
		return new LogDesc(elem(loc), level, msg, e);
	}

	/**
	 * Creates a logging descriptor localized to the given call locator object. The call
	 * locator object can be a {@link StackTraceElement}, class instance, or class.
	 *
	 * <pre>{@code
	 * LogDesc desc = LogDesc.of(this, level, e, fmt, args);
	 * Log.printf(desc);
	 * }</pre>
	 *
	 * @param loc   call locator
	 * @param level log level
	 * @param e     exception
	 * @param fmt   format string
	 * @param args  format arguments
	 * @return log descriptor
	 */
	public static LogDesc of(Object loc, Level level, Throwable e, String fmt, Object... args) {
		return new LogDesc(elem(loc), level, String.format(fmt, args), e);
	}

	private static StackTraceElement elem(Object loc) {
		if (loc == null) return null;
		if (loc instanceof StackTraceElement) return (StackTraceElement) loc;
		return Log.callerLocation(loc);
	}

	// --------------------------------

	private final StackTraceElement elem;
	private final Level level;
	private final String msg;
	private final Throwable e;

	private LogDesc(StackTraceElement elem, Level level, String msg, Throwable e) {
		this.elem = elem;
		this.level = level;
		this.msg = msg;
		this.e = e;
	}

	public Level level() {
		return level;
	}

	public String msg() {
		return msg;
	}

	public StackTraceElement location() {
		return elem;
	}

	public String fileName() {
		return elem.getFileName();
	}

	public String className() {
		return elem.getClassName();
	}

	public String methodName() {
		return elem.getMethodName();
	}

	public int lineNumber() {
		return elem.getLineNumber();
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
		if (elem != null) mb.append("%-50s ", ClassUtils.getAbbreviatedName(elem.getClassName(), 50));
		if (elem != null) mb.append(" : %-4s ", elem.getLineNumber());
		if (msg != null) mb.append("-- %s ", msg);
		if (e != null) mb.append("[%s]", e.getMessage());
		return mb.toString().trim();
	}
}
