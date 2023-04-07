package net.certiv.common.log;

import java.lang.StackWalker.Option;
import java.lang.StackWalker.StackFrame;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.stream.Stream;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.core.LoggerContext;
import org.apache.logging.log4j.core.appender.AbstractAppender;
import org.apache.logging.log4j.core.appender.ConsoleAppender;
import org.apache.logging.log4j.core.appender.ConsoleAppender.Target;
import org.apache.logging.log4j.core.config.AbstractConfiguration;
import org.apache.logging.log4j.core.config.Configurator;
import org.apache.logging.log4j.core.layout.PatternLayout;
import org.apache.logging.log4j.message.StringFormattedMessage;
import org.apache.logging.log4j.spi.ExtendedLogger;

public class Log {

	// @Deprecated
	// public static void trace(Object source, String message) {
	// log(Level.TRACE, message, null);
	// }
	//
	// @Deprecated
	// public static void trace(Object source, String format, Object... args) {
	// log(Level.TRACE, String.format(format, args), null);
	// }
	//
	// @Deprecated
	// public static void trace(Object source, String message, Throwable e) {
	// log(Level.TRACE, message, e);
	// }
	//
	// @Deprecated
	// public static void debug(Object source, String message) {
	// log(Level.DEBUG, message, null);
	// }
	//
	// @Deprecated
	// public static void debug(Object source, String format, Object... args) {
	// log(Level.DEBUG, String.format(format, args), null);
	// }
	//
	// @Deprecated
	// public static void debug(Object source, String message, Throwable e) {
	// log(Level.DEBUG, message, e);
	// }
	//
	// @Deprecated
	// public static void info(Object source, String message) {
	// log(Level.INFO, message, null);
	// }
	//
	// @Deprecated
	// public static void info(Object source, String format, Object... args) {
	// log(Level.INFO, String.format(format, args), null);
	// }
	//
	// @Deprecated
	// public static void info(Object source, String message, Throwable e) {
	// log(Level.INFO, message, e);
	// }
	//
	// @Deprecated
	// public static void warn(Object source, String message) {
	// log(Level.WARN, message, null);
	// }
	//
	// @Deprecated
	// public static void warn(Object source, String format, Object... args) {
	// log(Level.WARN, String.format(format, args), null);
	// }
	//
	// @Deprecated
	// public static void warn(Object source, String message, Throwable e) {
	// log(Level.WARN, message, e);
	// }
	//
	// @Deprecated
	// public static void error(Object source, String message) {
	// log(Level.ERROR, message, null);
	// }
	//
	// @Deprecated
	// public static void error(Object source, String format, Object... args) {
	// log(Level.ERROR, String.format(format, args), null);
	// }
	//
	// @Deprecated
	// public static void error(Object source, Throwable e, String format, Object... args)
	// {
	// log(Level.ERROR, String.format(format, args), e);
	// }
	//
	// @Deprecated
	// public static void error(Object source, String message, Throwable e) {
	// log(Level.ERROR, message, e);
	// }
	//
	// @Deprecated
	// public static void fatal(Object source, String message) {
	// log(Level.FATAL, message, null);
	// }
	//
	// @Deprecated
	// public static void fatal(Object source, String message, Throwable e) {
	// log(Level.FATAL, message, e);
	// }
	//
	// @Deprecated
	// public static void printf(Object source, Level level, String format, Instant time,
	// String logger,
	// String message) {
	// printf(level, message);
	// }
	//
	// @Deprecated
	// public static void log(Object source, Level level, String msg, Throwable e) {
	// log(level, msg, e);
	// }

	// --------------------------------------------------------------

	public static void trace(String message) {
		log(Level.TRACE, message, null);
	}

	public static void trace(String format, Object... args) {
		log(Level.TRACE, String.format(format, args), null);
	}

	public static void trace(String message, Throwable e) {
		log(Level.TRACE, message, e);
	}

	public static void debug(String message) {
		log(Level.DEBUG, message, null);
	}

	public static void debug(String format, Object... args) {
		log(Level.DEBUG, String.format(format, args), null);
	}

	public static void debug(String message, Throwable e) {
		log(Level.DEBUG, message, e);
	}

	public static void info(String message) {
		log(Level.INFO, message, null);
	}

	public static void info(String format, Object... args) {
		log(Level.INFO, String.format(format, args), null);
	}

	public static void info(String message, Throwable e) {
		log(Level.INFO, message, e);
	}

	public static void warn(String message) {
		log(Level.WARN, message, null);
	}

	public static void warn(String format, Object... args) {
		log(Level.WARN, String.format(format, args), null);
	}

	public static void warn(String message, Throwable e) {
		log(Level.WARN, message, e);
	}

	public static void error(String message) {
		log(Level.ERROR, message, null);
	}

	public static void error(String format, Object... args) {
		log(Level.ERROR, String.format(format, args), null);
	}

	public static void error(Throwable e, String format, Object... args) {
		log(Level.ERROR, String.format(format, args), e);
	}

	public static void error(String message, Throwable e) {
		log(Level.ERROR, message, e);
	}

	public static void fatal(String message) {
		log(Level.FATAL, message, null);
	}

	public static void fatal(String message, Throwable e) {
		log(Level.FATAL, message, e);
	}

	public static void printf(Level level, String msg) {
		_printf(level, new StringFormattedMessage(msg));
	}

	public static void printf(Level level, String fmt, Object... args) {
		_printf(level, new StringFormattedMessage(fmt, args));
	}

	public static void _printf(Level level, StringFormattedMessage msg) {
		Class<?> origin = caller();
		if (loggable(origin, level)) {
			ExtendedLogger logger = LogManager.getLogger(origin, ctx_);
			logger.logMessage(FQCN, level, null, msg, null);
		}
	}

	public static void log(Level level, String msg, Throwable e) {
		Class<?> origin = caller();
		if (loggable(origin, level)) {
			ExtendedLogger log = LogManager.getLogger(origin, ctx_);
			log.logIfEnabled(FQCN, level, null, msg, e);
		}
	}

	// ==========================================

	/** @return the the class that called Log2, or {@code Log2.class} */
	private static Class<?> caller() {
		StackFrame frame = StackWalker.getInstance(OPTIONS).walk(Log::caller);
		return frame != null ? frame.getDeclaringClass() : Log.class;
	}

	// find the stack frame for the class that called Log
	private static StackFrame caller(Stream<StackFrame> frames) {
		return frames //
				.filter(f -> !f.getClassName().equals(FQCN)) //
				.findFirst() //
				.orElse(null);
	}

	// /////////////////////////////////////////////////////////////////////////

	private static final String DEF_LVLSET = "Default logging level set to [%s]";
	private static final String ERR_LVLSET = "Logging level set for %s[%s]";
	private static final String ERR_CONFIG = "Configuration failed @%s[%s:%s]";

	private static final String FQCN = Log.class.getName();
	private static final int LogId = Log.class.hashCode();

	private static final String CONSOLE = "Console";
	private static final EnumSet<Option> OPTIONS = EnumSet.of(StackWalker.Option.SHOW_HIDDEN_FRAMES,
			StackWalker.Option.RETAIN_CLASS_REFERENCE);
	private static final Target OUTPUT = Target.SYSTEM_OUT;

	private static Class<?> refCls_ = Log.class;

	private static final HashMap<Integer, Level> Levels = new HashMap<>();

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
	 * The first call to use the logger will force initialization. The initialization will
	 * use the currently set log name and location to create a log file. If no log name or
	 * location is set, defaults are used. If the name or location is set, or reset, after
	 * any initialization, the logger is forced to reinitialize.
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
	 * @param logname  the log base filename ('.log' will be appended)
	 * @param location if relative, a path fragment specifiying the offset from the
	 *                 effective class location; if absolute, the absolute location of the
	 *                 directory that will contain the log file
	 */
	public static void setName(String logname, String location) {
		logname_ = logname;
		location_ = location;
		initd_ = false;
	}

	/**
	 * Sets a log pathname.
	 *
	 * @param cls     a class defining the effective location of the log file
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
	 * @param cls      a class defining the effective location of the log file
	 * @param logname  the log base filename ('.log' will be appended)
	 * @param location if relative, a path fragment specifiying the offset from the
	 *                 effective class location; if absolute, the absolute location of the
	 *                 directory that will contain the log file
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
	 * Sets a log level as a default for a class type. In the absence of a class specific
	 * level, the global default is applied. For a log message to be printed, the log
	 * level must be GTE the log level set for the source class.
	 */
	public static void defLevel(Level level) {
		setLevel(Log.class, level != null ? level : defaultLevel());
	}

	public static void defLevel(String level) {
		defLevel(level != null ? Level.valueOf(level) : defaultLevel());
	}

	public static void setLevel(Level level) {
		setLevel(caller(), level != null ? level : defaultLevel());
	}

	public static void setLevel(String level) {
		setLevel(level != null ? Level.valueOf(level) : defaultLevel());
	}

	private static void setLevel(Class<?> cls, Level level) {
		Levels.put(cls.hashCode(), level);
		if (cls == Log.class) {
			log(Level.TRACE, String.format(DEF_LVLSET, level), null);
		} else {
			log(Level.TRACE, String.format(ERR_LVLSET, cls.getName(), level), null);
		}
	}

	private static boolean loggable(Class<?> source, Level level) {
		chkInit();
		Level srcLevel = levelOf(source);
		return level.isMoreSpecificThan(srcLevel);
	}

	private static Level levelOf(Class<?> source) {
		Level level = Levels.get(source.hashCode());
		return level != null ? level : defaultLevel();
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
				defLevel(ctx_.getConfiguration().getRootLogger().getLevel());

			} else {
				defLevel(Level.TRACE);
				log(Level.ERROR, String.format(ERR_CONFIG, logname_, location_, layout_), null);
			}
		}
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
