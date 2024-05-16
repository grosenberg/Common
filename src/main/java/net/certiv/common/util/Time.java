package net.certiv.common.util;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Formatter;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import net.certiv.common.stores.Pair;
import net.certiv.common.stores.context.Value;

/**
 * Time related utilities.
 */
public class Time {

	public static final String DT_DEF = "MM/dd/yyyy hh:mm a";
	public static final String DT_MDY = "MM/dd/yyyy";

	public static final DateTimeFormatter UTC_FORMATTER = DateTimeFormatter.ISO_LOCAL_DATE_TIME
			.withZone(ZoneId.of(Strings.UTC));

	/**
	 * The time given in the argument is scoped to a local (mgr default) time zone. The
	 * value is adjusted to UTC time zone.
	 */
	public static String toUtcDateTimeString(long msSinceEpoch) {
		if (msSinceEpoch == 0L) return null;
		return UTC_FORMATTER.format(Instant.ofEpochMilli(msSinceEpoch)) + 'Z';
	}

	/** Sleep for the given count of seconds. */
	public static InterruptedException sleep(int seconds) {
		try {
			TimeUnit.SECONDS.sleep(seconds);
			return null;
		} catch (InterruptedException e) {
			return e;
		}
	}

	/**
	 * Sleep for the given count of units, where the given unit is convertable to
	 * {@link TimeUnit}.
	 */
	public static Exception sleep(int cnt, String unit) {
		try {
			return sleep(cnt, TimeUnit.valueOf(unit.toUpperCase()));
		} catch (Exception e) {
			return e;
		}
	}

	/** Sleep for the given count of time units. */
	public static InterruptedException sleep(int cnt, TimeUnit unit) {
		try {
			unit.sleep(cnt);
			return null;
		} catch (InterruptedException e) {
			return e;
		}
	}

	/** Sleep for the given value defined number and type of time units. */
	public static void sleep(Value<Integer> delay) {
		Time.sleep(delay.value(), delay.unit());
	}

	public static final String formatDate(LocalDateTime date) {
		return formatDate(date, DT_DEF);
	}

	public static final String formatDate(LocalDateTime date, String pattern) {
		return date.format(DateTimeFormatter.ofPattern(pattern));
	}

	// ---- Timer ---------------------

	/** key=identifier; value=start/stop times. */
	private static final Map<Enum<?>, Pair<Long, Long>> TIMERS = new LinkedHashMap<>();

	/** Nanoseconds (1e-9) per millisecond (1e-3) */
	private static final double NANOS_PER_MILLI = 1e6; // 1000000

	/**
	 * Start measuring time for the given id.
	 *
	 * @param id enum-type timer identifer
	 * @deprecated use {@link Timer}
	 */
	@Deprecated
	public static <E extends Enum<E>> void start(E id) {
		long start = System.nanoTime();
		TIMERS.put(id, Pair.of(start, start));
	}

	/**
	 * Stops measuring time for the given id. Returns the elapsed time in milliseconds
	 * with a decimal precision of 2.
	 *
	 * @param id enum-type timer identifer
	 * @return elapsed time in milliseconds
	 * @deprecated use {@link Timer}
	 */
	@Deprecated
	public static <E extends Enum<E>> double stop(E id) {
		Pair<Long, Long> time = TIMERS.get(id);
		if (time != null) TIMERS.put(id, Pair.of(time.left, System.nanoTime()));
		return elapsed(id);
	}

	/**
	 * Stops measuring time for the given id. Returns the elapsed time in milliseconds
	 * with a decimal precision of 2 subject to the given format.
	 *
	 * @param id  enum-type timer identifer
	 * @param fmt {@link Formatter} syntax format string
	 * @return formatted elapsed time
	 * @deprecated use {@link Timer}
	 */
	@Deprecated
	public static <E extends Enum<E>> String stop(E id, String fmt) {
		return String.format(fmt, stop(id));
	}

	/**
	 * Returns the elapsed time for the given id in milliseconds with a decimal precision
	 * of 2.
	 *
	 * @param id enum-type timer identifer
	 * @deprecated use {@link Timer}
	 */
	@Deprecated
	public static <E extends Enum<E>> double elapsed(E id) {
		double millis = 0;
		Pair<Long, Long> time = TIMERS.get(id);
		if (time != null) {
			long now = System.nanoTime();
			long stop = time.right > time.left ? time.right : now;
			millis = Math.max(stop - time.left, 0) / NANOS_PER_MILLI;
		}
		return Maths.round(millis, 2);
	}

	/**
	 * Returns the elapsed time for the given id in milliseconds with a decimal precision
	 * of 2 subject to the given format.
	 *
	 * @param id  enum-type timer identifer
	 * @param fmt {@link Formatter} syntax format string
	 * @return formatted elapsed time
	 * @deprecated use {@link Timer}
	 */
	@Deprecated
	public static <E extends Enum<E>> String elapsed(E id, String fmt) {
		return String.format(fmt, elapsed(id));
	}

	/**
	 * Clears the given timer id.
	 *
	 * @param id enum-type timer identifer
	 * @deprecated use {@link Timer}
	 */
	@Deprecated
	public static <E extends Enum<E>> void clear(E id) {
		TIMERS.remove(id);
	}

	/**
	 * Clears all timer ids.
	 *
	 * @deprecated use {@link Timer}
	 */
	@Deprecated
	public static void clear() {
		TIMERS.clear();
	}
}
