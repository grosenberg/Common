package net.certiv.common.util;

import java.util.Formatter;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map.Entry;
import java.util.TreeMap;

import net.certiv.common.check.Assert;
import net.certiv.common.ex.IllegalArgsEx;
import net.certiv.common.stores.LinkedHashList;

/**
 * Utility for keeping running measurements of time.
 *
 * @param <N> timer name type
 */
public class Timer<N> {

	private static final String ERR_TIMER_ID = "Timer id cannot be 'null'.";
	/** Nanoseconds (1e-9) per millisecond (1e-3) */
	private static final double NANOS_PER_MILLI = 1e6; // 1,000,000
	private static final String TIMER = "Timer";

	public enum Op {
		START,
		STOP,
		ELAPSED,
		CLEAR;
	}

	private static class Intv {
		long beg;
		long end;

		Intv(long beg) {
			this.beg = beg;
			this.end = Long.MIN_VALUE;
		}

		Intv(long beg, long end) {
			this.beg = beg;
			this.end = end;
		}

		/**
		 * Returns whether this interval has an end value.
		 *
		 * @return {@code true} if this interval is open
		 */
		boolean open() {
			return end == Long.MIN_VALUE;
		}

		double delta() {
			long stop = !open() ? end : System.nanoTime();
			return (stop - beg) / NANOS_PER_MILLI;
		}

		@Override
		public String toString() {
			return String.format("%s", delta());
		}
	}

	/** Timer instance name. */
	private final String name;
	/** key=identifier; value=list of start/stop times. */
	private final LinkedHashList<N, Intv> map = new LinkedHashList<>();

	/** Anonymous timer instance constructor. */
	public Timer() {
		this(TIMER);
	}

	/** Named timer instance constructor. */
	public Timer(String name) {
		this.name = name;
	}

	private void chk(N id) {
		if (id == null) throw IllegalArgsEx.of(ERR_TIMER_ID);
	}

	private long now() {
		return System.nanoTime();
	}

	/**
	 * Start measuring time for the given id.
	 *
	 * @param id timer identifer
	 */
	public void start(N id) {
		chk(id);
		if (!map.containsKey(id)) {
			map.put(id, new Intv(now()));
		}

		Intv last = map.get(id).peekLast();
		if (!last.open()) {
			map.put(id, new Intv(now()));
		}
	}

	/**
	 * Stops measuring time for the given id. Returns the elapsed time in milliseconds
	 * with a decimal precision of 2.
	 *
	 * @param id timer identifer
	 * @return elapsed time in milliseconds
	 */
	public double stop(N id) {
		chk(id);
		if (!map.containsKey(id)) {
			long now = now();
			map.put(id, new Intv(now, now));
		}

		Intv last = map.get(id).peekLast();
		if (last.open()) {
			last.end = now();
		}

		return lastElapsed(id);
	}

	/**
	 * Stops measuring time for the given id. Returns the elapsed time in milliseconds
	 * with a decimal precision of 2 subject to the given format.
	 *
	 * @param id  timer identifer
	 * @param fmt {@link Formatter} syntax format string
	 * @return formatted elapsed time
	 */
	public String stop(N id, String fmt) {
		Assert.notNull(fmt);
		return String.format(fmt, stop(id));
	}

	/**
	 * Return the name of this timer instance.
	 *
	 * @return timer name
	 */
	public String name() {
		return name;
	}

	/**
	 * Get all timer ids sorted ascending by initial interval start time.
	 *
	 * @return sorted timer id list
	 */
	public List<N> timerNames() {
		TreeMap<Long, N> tmp = new TreeMap<>();
		for (Entry<N, LinkedList<Intv>> entry : map.entrySet()) {
			Intv first = entry.getValue().peekFirst();
			if (first != null) tmp.put(first.beg, entry.getKey());
		}
		return List.copyOf(tmp.values());
	}

	/**
	 * Returns the total elapsed time of this timer instance as measured from the start of
	 * the first timer started to the stop of the last timer stopped, or {@code now} if
	 * any timer has not yet been stopped.
	 * <p>
	 * The returned time value is given in milliseconds with a decimal precision of 2.
	 * Does not alter the timer.
	 */
	public double totalElapsed() {
		long beg = firstStart();
		long end = lastStop();

		double millis = (end - beg) / NANOS_PER_MILLI;
		return Maths.round(millis, 2);
	}

	private long firstStart() {
		long min = Long.MAX_VALUE;
		for (LinkedList<Intv> seq : map.values()) {
			min = Math.min(min, seq.getFirst().beg);
		}
		return min;
	}

	private long lastStop() {
		long max = 0;
		for (LinkedList<Intv> seq : map.values()) {
			Intv last = seq.getLast();
			if (last.open()) return now();
			max = Math.max(max, last.end);
		}
		return max;
	}

	/**
	 * Returns the aggregate elapsed time for the given id. The returned time value is
	 * given in milliseconds with a decimal precision of 2. Does not alter the timer.
	 *
	 * @param id timer identifer
	 */
	public double totalElapsed(N id) {
		chk(id);
		double millis = map.get(id).stream() //
				.mapToDouble(r -> r.delta()) //
				.sum();
		return Maths.round(millis, 2);
	}

	/**
	 * Returns the aggregate elapsed time for the given id. The returned time value is
	 * given in milliseconds with a decimal precision of 2 subject to the given format.
	 * Does not stop the timer.
	 *
	 * @param id  timer identifer
	 * @param fmt {@link Formatter} syntax format string
	 * @return formatted elapsed time
	 */
	public String totalElapsed(N id, String fmt) {
		chk(id);
		return String.format(fmt, totalElapsed(id));
	}

	/**
	 * Returns all elapsed times by id sorted ascending by initial start time.
	 *
	 * @return sorted id and total elapsed times
	 */
	public LinkedHashMap<N, Double> allElapsed() {
		LinkedHashMap<N, Double> tmp = new LinkedHashMap<>();
		for (N id : timerNames()) {
			tmp.put(id, totalElapsed(id));
		}
		return tmp;
	}

	/**
	 * Returns the elapsed time for the last interval for the given id. The returned time
	 * value is given in milliseconds with a decimal precision of 2. Does not stop the
	 * timer.
	 *
	 * @param id timer identifer
	 */
	public double lastElapsed(N id) {
		chk(id);
		double millis = 0;

		Intv last = map.get(id).peekLast();
		if (last != null) {
			millis = last.delta();
		}
		return Maths.round(millis, 2);
	}

	/**
	 * Clears the given timer id.
	 *
	 * @param id timer identifer
	 */
	public void clear(N id) {
		chk(id);
		map.remove(id);
	}

	/**
	 * Clears all timer ids.
	 */
	public void clear() {
		map.clear();
	}

	@Override
	public String toString() {
		MsgBuilder mb = new MsgBuilder();
		mb.nl().append("**** %s ****", name);
		for (Entry<N, Double> e : allElapsed().entrySet()) {
			mb.nl().append("%-16s :: %6.1f ms.", e.getKey(), e.getValue());
		}
		mb.nl().append("** Total elapsed :: %6.1f ms.", totalElapsed());
		return mb.toString();
	}
}
