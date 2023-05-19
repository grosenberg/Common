package net.certiv.common.log;

import java.io.File;
import java.net.URI;
import java.nio.file.Path;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.core.Filter;
import org.apache.logging.log4j.core.LoggerContext;
import org.apache.logging.log4j.core.appender.ConsoleAppender;
import org.apache.logging.log4j.core.config.Configuration;
import org.apache.logging.log4j.core.config.builder.api.AppenderComponentBuilder;
import org.apache.logging.log4j.core.config.builder.api.ComponentBuilder;
import org.apache.logging.log4j.core.config.builder.api.ConfigurationBuilder;
import org.apache.logging.log4j.core.config.builder.api.RootLoggerComponentBuilder;
import org.apache.logging.log4j.core.config.builder.impl.BuiltConfiguration;
import org.apache.logging.log4j.core.config.builder.impl.DefaultConfigurationBuilder;

import net.certiv.common.util.ClassUtil;
import net.certiv.common.util.FsUtil;
import net.certiv.common.util.Strings;

public class LogConfig {

	/** Default log entry layout. */
	public static final String LAYOUT = "%d{HH:mm:ss} [%-5p] %-40.90C{-3} : %-4L - %m%n";
	/** Relative log location offset. */
	public static final String OFFSET = "logs";
	/** Default log name. */
	public static final String NAME = "Default";

	public static Configuration getConfiguration() {
		return getConfiguration(Log.class, NAME, OFFSET, LAYOUT);
	}

	public static Configuration getConfiguration(final String name) {
		return getConfiguration(Log.class, name, OFFSET, LAYOUT);
	}

	public static Configuration getConfiguration(final Class<?> cls, final String name, final String location,
			final String layout) {

		ConfigurationBuilder<BuiltConfiguration> builder = new DefaultConfigurationBuilder<>();
		return create(name, builder, getLocation(cls, name, location), layout);
	}

	public static String getLocation(Class<?> cls, String name, String location) {
		Path loc = Path.of(location != null ? location : Strings.DOT);
		if (location.startsWith(Strings.SLASH) || location.startsWith("\\")) {
			loc = loc.toAbsolutePath();
		}

		if (loc.isAbsolute()) {
			Path path = loc.resolve(name + ".log");
			System.err.println("Logpath: " + path.toString());
			return new File(path.toUri()).getPath();
		}

		// location is an offset relative to the class file
		URI uri = FsUtil.locate(cls);
		if (uri != null) {
			Path path = Path.of(uri);
			if (path.getFileName().toString().endsWith(ClassUtil.JAR)) {
				uri = path.getParent().toUri();
			}

		} else {
			uri = new File(Strings.DOT).toURI();
		}

		Path path = Path.of(uri).resolve(location).resolve(name + ".log");
		System.err.println("Logpath: " + path.toString());
		return new File(path.toUri()).getPath();
	}

	private static Configuration create(String logname, ConfigurationBuilder<BuiltConfiguration> builder,
			String pathname, String layout) {

		builder.setConfigurationName(logname);
		builder.setLoggerContext(new LoggerContext("common.log"));
		builder.setStatusLevel(Level.DEBUG);
		builder.setVerbosity("disable");

		// threshold filter
		builder.add(builder.newFilter("ThresholdFilter", Filter.Result.ACCEPT, Filter.Result.NEUTRAL)
				.addAttribute("level", Level.DEBUG) //
		);

		// console appender
		AppenderComponentBuilder consoleAppender = builder.newAppender("Stdout", "CONSOLE") //
				.addAttribute("target", ConsoleAppender.Target.SYSTEM_OUT);
		consoleAppender.add(builder.newLayout("PatternLayout").addAttribute("pattern", layout));
		builder.add(consoleAppender);

		// rolling file appender
		ComponentBuilder<?> triggerPolicy = builder.newComponent("Policies")
				.addComponent(builder.newComponent("OnStartupTriggeringPolicy"))
				.addComponent(builder.newComponent("SizeBasedTriggeringPolicy").addAttribute("size", "5 MB"));

		AppenderComponentBuilder rollingAppender = builder.newAppender("Rolling", "RollingFile") //
				.addAttribute("fileName", pathname) //
				.addAttribute("filePattern", pathname.replace(".log", "-%i.log")) //
				.addAttribute("immediateFlush", true) //
				.add(builder.newLayout("PatternLayout").addAttribute("pattern", layout)) //
				.addComponent(builder.newComponent("DefaultRolloverStrategy").addAttribute("max", "2")) //
				.addComponent(triggerPolicy) //
		;
		builder.add(rollingAppender);

		// console logger
		builder.add(builder.newLogger(logname, Level.DEBUG, true) //
				.add(builder.newAppenderRef("Stdout")) //
				.addAttribute("additivity", false) //
		);

		// rolling file logger
		builder.add(builder.newLogger(logname, Level.DEBUG, true) //
				.add(builder.newAppenderRef("Rolling")) //
				.addAttribute("additivity", false) //
		);

		// root logger
		RootLoggerComponentBuilder root = builder.newRootLogger(Level.DEBUG);
		root.add(builder.newAppenderRef("Stdout"));
		root.add(builder.newAppenderRef("Rolling"));
		builder.add(root);

		return builder.build();
	}
}
