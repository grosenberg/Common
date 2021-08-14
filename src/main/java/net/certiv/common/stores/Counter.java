package net.certiv.common.stores;

import java.util.concurrent.atomic.AtomicLong;
import java.util.function.LongBinaryOperator;

/** Atomic counter constrained to values between 0 and Long.MAX_VALUE. */
public class Counter extends AtomicLong {

	/**
	 * Increments and returns the value of the counter. Limited to positive
	 * {@code long} values.
	 */
	public final long inc() {
		return accumulateAndGet(1, new LongBinaryOperator() {

			@Override
			public long applyAsLong(long prev, long inc) {
				return Math.max(prev + inc, 0);
			}
		});
	}

	/**
	 * Decrements and returns the value of the counter. Limited to positive
	 * {@code long} values.
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
