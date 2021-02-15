package net.certiv.tools.log;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.Marker;
import org.apache.logging.log4j.message.Message;
import org.apache.logging.log4j.spi.ExtendedLogger;
import org.apache.logging.log4j.spi.ExtendedLoggerWrapper;
import org.apache.logging.log4j.spi.LocationAwareLogger;

public class LoggerExt extends ExtendedLoggerWrapper {

	public LoggerExt(ExtendedLogger logger, String name) {
		super(logger, name, logger.getMessageFactory());
	}

	@Override
	public void logMessage(String fqcn, Level level, Marker marker, Message message, Throwable t) {
		if (logger instanceof LocationAwareLogger && requiresLocation()) {
			StackTraceElement element = calcLocation(fqcn);
			((LocationAwareLogger) logger).logMessage(level, marker, fqcn, element, message, t);
		}
		logger.logMessage(fqcn, level, marker, message, t);
	}

	public StackTraceElement calcLocation(final String fqcn) {
		if (fqcn == null) return null;

		final StackTraceElement[] stack = new Throwable().getStackTrace();
		boolean found = false;
		for (int idx = 0; idx < stack.length; idx++) {
			final String clsName = stack[idx].getClassName();
			if (fqcn.equals(clsName)) {
				found = true;
				continue;
			}
			if (found && !fqcn.equals(clsName)) return stack[idx];
		}
		return null;
	}
}
