package net.certiv.tools.log;

import java.util.HashMap;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.core.LoggerContext;
import org.apache.logging.log4j.core.appender.AbstractAppender;
import org.apache.logging.log4j.core.appender.ConsoleAppender;
import org.apache.logging.log4j.core.appender.ConsoleAppender.Target;
import org.apache.logging.log4j.core.config.AbstractConfiguration;
import org.apache.logging.log4j.core.config.Configurator;
import org.apache.logging.log4j.core.layout.PatternLayout;
import org.apache.logging.log4j.spi.ExtendedLogger;

import net.certiv.tools.util.Chars;

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

	private static void log(Object source, Level level, String msg, Throwable e) {
		Class<?> origin = source != null ? source.getClass() : Log.class;
		if (loggable(source, level)) {
			ExtendedLogger log = LogManager.getLogger(origin);
			log.logIfEnabled(FQCN, level, null, msg, e);
		}
	}

	// /////////////////////////////////////////////////////////////////////////

	private static final String FQCN = Log.class.getName();
	private static final String CONSOLE = "Console";
	private static final Target OUTPUT = Target.SYSTEM_OUT;

	private static final int LogId = Log.class.hashCode();
	private static final HashMap<Integer, Level> Levels = new HashMap<>();

	private static boolean Initd;
	private static Class<?> Cls = Log.class;
	private static String Layout = LogConfig.LAYOUT;
	private static String Logname = LogConfig.NAME;
	private static String Offset = LogConfig.OFFSET;

	private static boolean TestMode;

	private static AbstractAppender stdAppender;
	private static ConsoleAppender testAppender;

	public static void setName(String logname) {
		Logname = logname;
		Initd = false;
	}

	public static void setName(String logname, String offset) {
		Logname = logname;
		Offset = offset;
		Initd = false;
	}

	public static void setName(Class<?> cls, String logname, String offset) {
		Cls = cls;
		Logname = logname;
		Offset = offset;
		Initd = false;
	}

	public static void setLayout(String layout) {
		Layout = layout;
		Initd = false;
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
		if (!Initd) {
			Configurator.initialize(LogConfig.getConfiguration(Cls, Logname, Offset, Layout));
			if (Levels.get(LogId) == null) setLevel(LogId, Level.WARN);
			Initd = true;
		}
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
		return Levels.get(LogId);
	}

	private static String objNameOf(Object source) {
		String fqn = source.getClass().getName();
		int dot = fqn.lastIndexOf(Chars.DOT);
		return dot > -1 ? fqn.substring(dot + 1) : fqn;
	}

	public static void setTestMode(boolean testMode) {
		TestMode = testMode;

		try (LoggerContext ctx = (LoggerContext) org.apache.logging.log4j.LogManager.getContext()) {
			AbstractConfiguration cfg = (AbstractConfiguration) ctx.getConfiguration();
			if (stdAppender == null) {
				stdAppender = (AbstractAppender) cfg.getAppender(CONSOLE);
			}
			if (testAppender == null) {
				PatternLayout testLayout = PatternLayout.createDefaultLayout();
				testAppender = ConsoleAppender.newBuilder().setLayout(testLayout).setTarget(OUTPUT)
						.setName(CONSOLE).setIgnoreExceptions(true).build();
			}

			if (TestMode) {
				cfg.removeAppender(CONSOLE);
				cfg.addAppender(testAppender);
			} else {
				cfg.removeAppender(CONSOLE);
				cfg.addAppender(stdAppender);
			}
			ctx.updateLoggers();
		}
	}
}
