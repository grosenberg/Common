package net.certiv.common.stores;

import java.util.concurrent.atomic.AtomicLong;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.LongBinaryOperator;

/**
 * Atomic counter constrained to values between 0 and a maximum value. The maximum is
 * nominally Long.MAX_VALUE. Decrementing to below 0 will return 0. Incrementing to above
 * the maximum will reset the counter to 0.
 */
public class Counter extends AtomicLong {

	private long max;

	public Counter() {
		this(0, Long.MAX_VALUE);
	}

	public Counter(long initial) {
		this(initial, Long.MAX_VALUE);
	}

	public Counter(long initial, long max) {
		super(initial);
		this.max = max;
	}

	/**
	 * Adds a positive integer counter to a {@link Consumer} to create an indexing
	 * {@link BiConsumer}.
	 *
	 * <pre>
	 * Collection.forEach(Counter.on((idx, item) -> ...));
	 * </pre>
	 *
	 * @param <T>      consumer type
	 * @param consumer the consumer
	 * @return an indexing bi-consumer
	 */
	public static <T> Consumer<T> on(BiConsumer<Integer, T> consumer) {
		Counter counter = new Counter(0, Integer.MAX_VALUE);
		return item -> consumer.accept((int) counter.getAndIncrement(), item);
	}

	/**
	 * Increments and returns the value of the counter. Limited to positive {@code long}
	 * values.
	 */
	public final long inc() {
		return accumulateAndGet(1, new LongBinaryOperator() {

			@Override
			public long applyAsLong(long prev, long inc) {
				if (prev == max) prev = -1;
				return Math.max(prev + inc, 0);
			}
		});
	}

	/**
	 * Decrements and returns the value of the counter. Limited to positive {@code long}
	 * values.
	 */
	public final long dec() {
		return accumulateAndGet(-1, new LongBinaryOperator() {

			@Override
			public long applyAsLong(long prev, long inc) {
				return Math.max(prev + inc, 0);
			}
		});
	}
}
