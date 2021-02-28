package net.certiv.common.util;

/**
 * Simple Stopwatch utility to time code execution.
 * <p>
 * Simple replacement for, and removes a minor dependency on,
 * org.apache.commons.lang.time.StopWatch.
 */
public final class Stopwatch {

	private long timeStart;
	private long timeStop;
	private boolean isRunning;
	private boolean hasRun;

	public Stopwatch() {
		super();
		timeStart = 0;
		timeStop = 0;
		isRunning = false;
		hasRun = false;
	}

	/**
	 * Start the Stopwatch.
	 * 
	 * @throws IllegalStateException if the Stopwatch is running.
	 */
	public void start() {
		if (isRunning) {
			throw new IllegalStateException("Must stop before calling start again.");
		}
		// reset both start and stop
		timeStart = System.currentTimeMillis();
		isRunning = true;
		hasRun = true;
	}

	/**
	 * Stop the Stopwatch.
	 * 
	 * @throws IllegalStateException if the Stopwatch is not running.
	 */
	public void stop() {
		if (!isRunning) {
			throw new IllegalStateException("Cannot stop if not currently isRunning.");
		}
		timeStop = System.currentTimeMillis();
		isRunning = false;
	}

	/**
	 * Read the Stopwatch value.
	 * 
	 * @throws IllegalStateException if the Stopwatch has never been started is still isRunning.
	 */
	@Override
	public String toString() {
		validateIsReadable();
		StringBuffer result = new StringBuffer();
		result.append(timeStop - timeStart);
		result.append(" ms");
		return result.toString();
	}

	/**
	 * Read the Stopwatch value.
	 * 
	 * @throws IllegalStateException if the Stopwatch has never been started is still isRunning.
	 */
	public long toValue() {
		validateIsReadable();
		return timeStop - timeStart;
	}

	/**
	 * @throws IllegalStateException if the Stopwatch has never been started is still isRunning.
	 */
	private void validateIsReadable() {
		if (isRunning) {
			String message = "Cannot read a Stopwatch which is still isRunning.";
			throw new IllegalStateException(message);
		}
		if (!hasRun) {
			String message = "Cannot read a Stopwatch which has never been started.";
			throw new IllegalStateException(message);
		}
	}
}
