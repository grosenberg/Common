package net.certiv.tools.log;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.spi.ExtendedLogger;
import org.apache.logging.log4j.util.StackLocatorUtil;

public class LogManager {

	private static final String ERR_NO_CLASS = "No class provided, and an appropriate one cannot be found.";

	/**
	 * Returns a Logger with the name of the calling class.
	 *
	 * @return The Logger for the calling class.
	 * @throws UnsupportedOperationException if the calling class cannot be determined.
	 */
	public static Logger getLogger() {
		return getLogger(StackLocatorUtil.getCallerClass(2));
	}

	/**
	 * Returns a Logger using the fully qualified class name of the value as the Logger
	 * name.
	 *
	 * @param value The value whose class name should be used as the Logger name. If null
	 *            the name of the calling class will be used as the logger name.
	 * @return The Logger.
	 * @throws UnsupportedOperationException if {@code value} is {@code null} and the
	 *             calling class cannot be determined.
	 */
	public static Logger getLogger(final Object value) {
		return getLogger(value != null ? value.getClass() : StackLocatorUtil.getCallerClass(2));
	}

	/**
	 * Returns a Logger using the fully qualified name of the Class as the Logger name.
	 *
	 * @param origin The Class whose name should be used as the Logger name. If null it
	 *            will default to the calling class.
	 * @return The Logger.
	 * @throws UnsupportedOperationException if {@code clazz} is {@code null} and the
	 *             calling class cannot be determined.
	 */
	public static ExtendedLogger getLogger(final Class<?> origin) {
		final Class<?> cls = callerClass(origin);
		ExtendedLogger logger = org.apache.logging.log4j.LogManager.getContext(cls.getClassLoader(), false)
				.getLogger(toLoggerName(cls));
		return new LoggerExt(logger, logger.getName());
	}

	private static Class<?> callerClass(final Class<?> cls) {
		if (cls != null) return cls;

		final Class<?> caller = StackLocatorUtil.getCallerClass(3);
		if (caller == null) throw new UnsupportedOperationException(ERR_NO_CLASS);
		return caller;
	}

	private static String toLoggerName(final Class<?> cls) {
		final String canonical = cls.getCanonicalName();
		return canonical != null ? canonical : cls.getName();
	}
}
