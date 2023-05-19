package net.certiv.common.log;

public enum Level {
	/** Indicates that no event will be logged. */
	OFF,
	/** Identifies a program termination event. */
	FATAL,
	/** Identifies a possibly recoverable program error event. */
	ERROR,
	/** Identifies a warning event. */
	WARN,
	/** Identifies an informational event. */
	INFO,
	/** Identifies a debugging event. */
	DEBUG,
	/** Identifies a fine-grained debugging event. */
	TRACE;

	/**
	 * Determines whether this level is in the range, inclusive, defined by the given
	 * levels.
	 *
	 * @param min range minimum level
	 * @param max range maximum level
	 * @return {@code true} if this level is in range, inclusive
	 */
	public boolean isInRange(Level min, Level max) {
		return ordinal() >= min.ordinal() && ordinal() <= max.ordinal();
	}

	/**
	 * Returns {@code true} if this level is more or equally specific relative to the
	 * given level.
	 * <p>
	 * The specificity order is defined as the enum order. Thus, {@code ERROR} is the most
	 * specific, while {@code TRACE} is the least specific.
	 *
	 * @param level the level to test
	 * @return {@code true} if this level is more or equally specific as the given level
	 */
	public boolean isMoreSpecificThan(Level level) {
		return ordinal() <= level.ordinal();
	}

	/**
	 * Returns {@code true} if this level is less or equally specific relative to the
	 * given level.
	 * <p>
	 * The specificity order is defined as the enum order. Thus, {@code ERROR} is the most
	 * specific, while {@code TRACE} is the least specific.
	 *
	 * @param level the level to test
	 * @return {@code true} if this level is less or equally specific as the given level
	 */
	public boolean isLessSpecificThan(Level level) {
		return ordinal() >= level.ordinal();
	}

	/**
	 * Converts the given level name, if valid, to a corresponding level. If invalid,
	 * converts to {@code Level#DEBUG}.
	 *
	 * @param name a level name
	 * @return the corresponding level
	 */
	public static Level toLevel(String name) {
		return toLevel(name, Level.DEBUG);
	}

	/**
	 * Converts the given level name, if valid, to a corresponding level. If invalid,
	 * converts to the given default level. A {@code null} default level is treated
	 * equivalent to {@code Level#DEBUG}.
	 *
	 * @param name a level name
	 * @param def  a default level
	 * @return the corresponding level
	 */
	public static Level toLevel(String name, Level def) {
		if (def == null) def = Level.DEBUG;
		if (name == null) return def;

		try {
			return Level.valueOf(name.trim().toUpperCase());
		} catch (Exception e) {
			return def;
		}
	}
}
