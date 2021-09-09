package net.certiv.common.log;

import java.time.Instant;
import java.util.HashMap;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.core.LoggerContext;
import org.apache.logging.log4j.core.appender.AbstractAppender;
import org.apache.logging.log4j.core.appender.ConsoleAppender;
import org.apache.logging.log4j.core.appender.ConsoleAppender.Target;
import org.apache.logging.log4j.core.config.AbstractConfiguration;
import org.apache.logging.log4j.core.config.Configurator;
import org.apache.logging.log4j.core.layout.PatternLayout;
import org.apache.logging.log4j.spi.AbstractLogger;
import org.apache.logging.log4j.spi.ExtendedLogger;

import net.certiv.common.util.Chars;

public class Log {

	public static void trace(Object source, String message) {
		log(source, Level.TRACE, message, null);
	}

	public static void trace(Object source, String format, Object... args) {
		log(source, Level.TRACE, String.format(format, args), null);
	}

	public static void trace(Object source, String message, Throwable e) {
		log(source, Level.TRACE, message, e);
	}

	public static void debug(Object source, String message) {
		log(source, Level.DEBUG, message, null);
	}

	public static void debug(Object source, String format, Object... args) {
		log(source, Level.DEBUG, String.format(format, args), null);
	}

	public static void debug(Object source, String message, Throwable e) {
		log(source, Level.DEBUG, message, e);
	}

	public static void info(Object source, String message) {
		log(source, Level.INFO, message, null);
	}

	public static void info(Object source, String format, Object... args) {
		log(source, Level.INFO, String.format(format, args), null);
	}

	public static void info(Object source, String message, Throwable e) {
		log(source, Level.INFO, message, e);
	}

	public static void warn(Object source, String message) {
		log(source, Level.WARN, message, null);
	}

	public static void warn(Object source, String format, Object... args) {
		log(source, Level.WARN, String.format(format, args), null);
	}

	public static void warn(Object source, String message, Throwable e) {
		log(source, Level.WARN, message, e);
	}

	public static void error(Object source, String message) {
		log(source, Level.ERROR, message, null);
	}

	public static void error(Object source, String format, Object... args) {
		log(source, Level.ERROR, String.format(format, args), null);
	}

	public static void error(Object source, Throwable e, String format, Object... args) {
		log(source, Level.ERROR, String.format(format, args), e);
	}

	public static void error(Object source, String message, Throwable e) {
		log(source, Level.ERROR, message, e);
	}

	public static void fatal(Object source, String message) {
		log(source, Level.FATAL, message, null);
	}

	public static void fatal(Object source, String message, Throwable e) {
		log(source, Level.FATAL, message, e);
	}

	public static void printf(Object source, Level level, String format, Instant time, String logger,
			String message) {
		Class<?> origin = source != null ? source.getClass() : Log.class;
		if (loggable(source, level)) {
			AbstractLogger log = (AbstractLogger) LogManager.getLogger(origin);
			log.printf(level, format, time, logger, message);
		}
	}

	public static void log(Object source, Level level, String msg, Throwable e) {
		Class<?> origin = source != null ? source.getClass() : Log.class;
		if (loggable(source, level)) {
			ExtendedLogger log = LogManager.getLogger(origin, ctx_);
			log.logIfEnabled(FQCN, level, null, msg, e);
		}
	}

	// /////////////////////////////////////////////////////////////////////////

	private static final String FQCN = Log.class.getName();
	private static final String CONSOLE = "Console";
	private static final Target OUTPUT = Target.SYSTEM_OUT;

	private static final int LogId = Log.class.hashCode();
	private static final HashMap<Integer, Level> Levels = new HashMap<>();

	private static Class<?> refCls_ = Log.class;

	private static boolean initd_;
	private static boolean testMode_;

	private static LoggerContext ctx_;

	private static String layout_ = LogConfig.LAYOUT;
	private static String logname_ = LogConfig.NAME;
	private static String location_ = LogConfig.OFFSET;

	private static AbstractAppender stdAppender_;
	private static ConsoleAppender testAppender_;

	/**
	 * Returns {@code true} if the logger has been initialized.
	 * <p>
	 * The first call to use the logger will force initialization. The
	 * initialization will use the currently set log name and location to create a
	 * log file. If no log name or location is set, defaults are used. If the name
	 * or location is set, or reset, after any initialization, the logger is forced
	 * to reinitialize.
	 *
	 * @return the current initialization state
	 */
	public static boolean isInitalized() {
		chkInit();
		return initd_;
	}

	/**
	 * Sets a log pathname.
	 *
	 * @param logname the log base filename ('.log' will be appended)
	 */
	public static void setName(String logname) {
		logname_ = logname;
		initd_ = false;
	}

	/**
	 * Sets a log pathname.
	 *
	 * @param logname the log base filename ('.log' will be appended)
	 * @param location if relative, a path fragment specifiying the offset from the
	 *            effective class location; if absolute, the absolute location of
	 *            the directory that will contain the log file
	 */
	public static void setName(String logname, String location) {
		logname_ = logname;
		location_ = location;
		initd_ = false;
	}

	/**
	 * Sets a log pathname.
	 *
	 * @param cls a class defining the effective location of the log file
	 * @param logname the log base filename ('.log' will be appended)
	 */
	public static void setName(Class<?> cls, String logname) {
		refCls_ = cls;
		logname_ = logname;
		initd_ = false;
	}

	/**
	 * Sets a log pathname.
	 *
	 * @param cls a class defining the effective location of the log file
	 * @param logname the log base filename ('.log' will be appended)
	 * @param location if relative, a path fragment specifiying the offset from the
	 *            effective class location; if absolute, the absolute location of
	 *            the directory that will contain the log file
	 */
	public static void setName(Class<?> cls, String logname, String location) {
		refCls_ = cls;
		logname_ = logname;
		location_ = location;
		initd_ = false;
	}

	public static void setLayout(String layout) {
		layout_ = layout;
		initd_ = false;
	}

	/**
	 * Sets a log level as a default for a class type. In the absence of a class
	 * specific level, the global default is applied. For a log message to be
	 * printed, the log level must be GTE the log level set for the source class.
	 */
	public static void defLevel(Level level) {
		setLevel(null, level);
	}

	public static void defLevel(String level) {
		setLevel(null, level);
	}

	public static void setLevel(Object source, String level) {
		if (level == null) return;
		setLevel(source, Level.valueOf(level));
	}

	public static void setLevel(Object source, Level level) {
		if (source == null && level == null) return;
		if (source == null) source = LogId;
		if (level == null) level = defaultLevel();

		int id = source.hashCode();
		String name = objNameOf(source);

		Levels.put(id, level);
		if (id == LogId) {
			trace(Log.class, "Default logging level set [level=" + level.toString() + "]");
		} else {
			trace(Log.class, "Class Logging level set [class=" + name + ", level=" + level.toString() + "]");
		}
	}

	private static boolean loggable(Object source, Level level) {
		chkInit();
		Level srcLevel = levelOf(source);
		return level.isMoreSpecificThan(srcLevel);
	}

	private static Level levelOf(Object source) {
		if (source == null) return defaultLevel();
		Level level = Levels.get(source.hashCode());
		if (level == null) return defaultLevel();
		return level;
	}

	private static Level defaultLevel() {
		chkInit();
		return Levels.get(LogId);
	}

	private static void chkInit() {
		if (!initd_) {
			initd_ = true;
			ctx_ = Configurator.initialize(LogConfig.getConfiguration(refCls_, logname_, location_, layout_));
			if (ctx_ != null) {
				Level level = ctx_.getConfiguration().getRootLogger().getLevel();
				setLevel(LogId, level);

			} else {
				setLevel(LogId, Level.TRACE);
				error(LogId, "Configuration failed @%s[%s:%s]", logname_, location_, layout_);
			}
		}
	}

	private static String objNameOf(Object source) {
		String fqn = source.getClass().getName();
		int dot = fqn.lastIndexOf(Chars.DOT);
		return dot > -1 ? fqn.substring(dot + 1) : fqn;
	}

	public static void setTestMode(boolean testMode) {
		testMode_ = testMode;

		try (LoggerContext ctx = (LoggerContext) org.apache.logging.log4j.LogManager.getContext()) {
			AbstractConfiguration cfg = (AbstractConfiguration) ctx.getConfiguration();
			if (stdAppender_ == null) {
				stdAppender_ = (AbstractAppender) cfg.getAppender(CONSOLE);
			}
			if (testAppender_ == null) {
				PatternLayout testLayout = PatternLayout.createDefaultLayout();
				testAppender_ = ConsoleAppender.newBuilder().setLayout(testLayout).setTarget(OUTPUT)
						.setName(CONSOLE).setIgnoreExceptions(true).build();
			}

			if (testMode_) {
				cfg.removeAppender(CONSOLE);
				cfg.addAppender(testAppender_);
			} else {
				cfg.removeAppender(CONSOLE);
				cfg.addAppender(stdAppender_);
			}
			ctx.updateLoggers();
		}
	}
}
