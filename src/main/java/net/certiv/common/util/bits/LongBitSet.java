/**
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package net.certiv.common.util.bits;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import net.certiv.common.ex.BoundsEx;
import net.certiv.common.ex.IllegalArgsEx;
import net.certiv.common.util.Strings;

/**
 * Provides a bit per position representation of a zero-referenced vector space with a
 * defined span. Bit set indexing is by non-negative long values, <em>i.e.<em>, from
 * {@code 0} to {@link Long#MAX_VALUE}.
 * <p>
 * Each position of the bit set has an assigned {@code boolean} value. Initially, all
 * positions have a {@code false} value.
 * <p>
 * Derived from {@code lucene-4.7.2/src/org/apache/lucene/util/LongBitSet.java}.
 */
public final class LongBitSet {

	private static final String ERR_INVALID1 = "Bit index invalid: %d in [0:%d].";
	private static final String ERR_INVALID2 = "Bit index invalid: (%d:%d) in [0:%d].";
	private static final String ERR_EXCEEDS1 = "Bit index exceeds set size: %d in [0:%d].";
	private static final String ERR_EXCEEDS2 = "Bit index exceeds set size: (%d:%d) in [0:%d].";
	private static final String ERR_SIZING = "Bit word sizes mismatch: this %d > other %d";

	private final long[] bits;
	private final long numBits;
	private final int numWords;

	public LongBitSet(long numBits) {
		this.numBits = numBits;
		bits = new long[bits2words(numBits)];
		numWords = bits.length;
	}

	protected LongBitSet(long[] storedBits, long numBits) {
		this.numWords = bits2words(numBits);
		if (numWords > storedBits.length) {
			throw IllegalArgsEx.of("The given long array is too small to hold %d bits.", numBits);
		}
		this.numBits = numBits;
		this.bits = storedBits;
	}

	/** returns the number of 64 bit words it would take to hold numBits */
	protected static int bits2words(long numBits) {
		int numLong = (int) (numBits >>> 6);
		if ((numBits & 63) != 0) {
			numLong++;
		}
		return numLong;
	}

	/**
	 * If the given {@link LongBitSet} is large enough to hold {@code numBits}, returns
	 * the given bits, otherwise returns a new {@link LongBitSet} which can hold the
	 * requested number of bits.
	 * <p>
	 * <b>NOTE:</b> the returned bitset reuses the underlying {@code long[]} of the given
	 * {@code bits} if possible. Also, calling {@link #length()} on the returned bits may
	 * return a value greater than {@code numBits}.
	 */
	protected static LongBitSet ensureCapacity(LongBitSet bits, long numBits) {
		if (numBits < bits.length()) return bits;

		int numWords = bits2words(numBits);
		long[] arr = bits.getBits();
		if (numWords >= arr.length) {
			arr = BitUtil.grow(arr, numWords + 1);
		}
		return new LongBitSet(arr, arr.length << 6);
	}

	protected final void chk(long idx) {
		if (idx < 0) throw BoundsEx.of(ERR_INVALID1, idx, numBits);
		if (idx > numBits) throw BoundsEx.of(ERR_EXCEEDS1, idx, numBits);
	}

	protected final boolean chk(long begIdx, long endIdx) {
		if (begIdx < 0) throw BoundsEx.of(ERR_INVALID2, begIdx, endIdx, numBits);
		if (begIdx > numBits || endIdx > numBits + 1)
			throw BoundsEx.of(ERR_EXCEEDS2, begIdx, endIdx, numBits);
		return endIdx > begIdx;
	}

	protected final void chk(LongBitSet other) {
		if (numWords > other.numWords) throw BoundsEx.of(ERR_SIZING, numWords, other.numWords);
	}

	/** Returns the number of bits stored in this bitset. */
	public long length() {
		return numBits;
	}

	/**
	 * Returns number of set bits. NOTE: this visits every long in the backing bits array,
	 * and the result is not internally cached!
	 */
	public long cardinality() {
		return BitUtil.pop_array(bits, 0, bits.length);
	}

	public boolean get(long idx) {
		chk(idx);
		int wordNum = (int) (idx >> 6);			// div 64
		// signed shift will keep a negative index and force an
		// array-index-out-of-bounds-exception, removing the need for an explicit check.
		int bit = (int) (idx & 0x3f);           // mod 64
		long bitmask = 1L << bit;
		return (bits[wordNum] & bitmask) != 0;
	}

	public boolean getAndSet(long idx) {
		chk(idx);
		int wordNum = (int) (idx >> 6);		// div 64
		int bit = (int) (idx & 0x3f);		// mod 64
		long bitmask = 1L << bit;
		boolean val = (bits[wordNum] & bitmask) != 0;
		bits[wordNum] |= bitmask;
		return val;
	}

	public boolean getAndClear(long idx) {
		chk(idx);
		int wordNum = (int) (idx >> 6);		// div 64
		int bit = (int) (idx & 0x3f);		// mod 64
		long bitmask = 1L << bit;
		boolean val = (bits[wordNum] & bitmask) != 0;
		bits[wordNum] &= ~bitmask;
		return val;
	}

	public void set(long idx) {
		chk(idx);
		int wordNum = (int) (idx >> 6);		// div 64
		int bit = (int) (idx & 0x3f);		// mod 64
		long bitmask = 1L << bit;
		bits[wordNum] |= bitmask;
	}

	/**
	 * Sets a range of bits
	 *
	 * @param begIdx begin index, inclusive (0..n)
	 * @param endIdx end index, exclusive (1..n+1)
	 */
	public void set(long begIdx, long endIdx) {
		if (chk(begIdx, endIdx)) {
			int begWord = (int) (begIdx >> 6);
			int endWord = (int) ((endIdx - 1) >> 6);

			long begMask = -1L << begIdx;
			// 64-(endIdx&0x3f) is the same as -endIdx due to wrap
			long endMask = -1L >>> -endIdx;

			if (begWord == endWord) {
				bits[begWord] |= (begMask & endMask);

			} else {
				bits[begWord] |= begMask;
				Arrays.fill(bits, begWord + 1, endWord, -1L);
				bits[endWord] |= endMask;
			}
		}
	}

	/**
	 * Returns the index of the first set bit starting at the given index, inclusive.
	 * Returns {@code -1} if there are no more set bits.
	 *
	 * @param idx starting index, inclusive
	 * @return next set bit index or {@code -1} if none exists
	 */
	public long nextSetBit(long idx) {
		chk(idx);
		int wordNum = (int) (idx >> 6);
		int subIndex = (int) (idx & 0x3f);		// index within the word
		long word = bits[wordNum] >> subIndex;	// skip all bits right of index

		if (word != 0) {
			return idx + Long.numberOfTrailingZeros(word);
		}

		while (++wordNum < numWords) {
			word = bits[wordNum];
			if (word != 0) {
				return (wordNum << 6) + Long.numberOfTrailingZeros(word);
			}
		}

		return -1;
	}

	/**
	 * Returns the index of the last set bit at or before the given index, inclusive.
	 * Returns {@code -1} if there are no more set bits.
	 *
	 * @param idx starting index, inclusive
	 * @return previous set bit index or {@code -1} if none exists
	 */
	public long prevSetBit(long idx) {
		chk(idx);
		int wordNum = (int) (idx >> 6);
		int subIndex = (int) (idx & 0x3f);				// index within the word
		long word = (bits[wordNum] << (63 - subIndex));	// skip all bits left of index

		if (word != 0) {
			return (wordNum << 6) + subIndex - Long.numberOfLeadingZeros(word);
		}

		while (--wordNum >= 0) {
			word = bits[wordNum];
			if (word != 0) {
				return (wordNum << 6) + 63 - Long.numberOfLeadingZeros(word);
			}
		}

		return -1;
	}

	public void clear() {
		clear(length());
	}

	public void clear(long idx) {
		chk(idx);
		int wordNum = (int) (idx >> 6);
		long bitmask = 1L << idx;
		bits[wordNum] &= ~bitmask;
	}

	/**
	 * Clears a range of bits.
	 *
	 * @param begIdx begin index, inclusive (0..n)
	 * @param endIdx end index, exclusive (1..n+1)
	 */
	public void clear(long begIdx, long endIdx) {
		if (chk(begIdx, endIdx)) {
			int begWord = (int) (begIdx >> 6);
			int endWord = (int) ((endIdx - 1) >> 6);

			long begMask = -1L << begIdx;
			// 64-(endIdx&0x3f) is the same as -endIdx due to wrap
			long endMask = -1L >>> -endIdx;

			// invert masks since we are clearing
			begMask = ~begMask;
			endMask = ~endMask;

			if (begWord == endWord) {
				bits[begWord] &= (begMask | endMask);

			} else {
				bits[begWord] &= begMask;
				Arrays.fill(bits, begWord + 1, endWord, 0L);
				bits[endWord] &= endMask;
			}
		}
	}

	/** this = this OR other */
	public void or(LongBitSet other) {
		chk(other);
		int pos = Math.min(numWords, other.numWords);
		while (--pos >= 0) {
			bits[pos] |= other.bits[pos];
		}
	}

	/** this = this XOR other */
	public void xor(LongBitSet other) {
		chk(other);
		int pos = Math.min(numWords, other.numWords);
		while (--pos >= 0) {
			bits[pos] ^= other.bits[pos];
		}
	}

	/** returns true if the sets have any elements in common */
	public boolean intersects(LongBitSet other) {
		int pos = Math.min(numWords, other.numWords);
		while (--pos >= 0) {
			if ((bits[pos] & other.bits[pos]) != 0) return true;
		}
		return false;
	}

	/** this = this AND other */
	public void and(LongBitSet other) {
		int pos = Math.min(numWords, other.numWords);
		while (--pos >= 0) {
			bits[pos] &= other.bits[pos];
		}
		if (numWords > other.numWords) {
			Arrays.fill(bits, other.numWords, numWords, 0L);
		}
	}

	/** this = this AND NOT other */
	public void andNot(LongBitSet other) {
		int pos = Math.min(numWords, other.bits.length);
		while (--pos >= 0) {
			bits[pos] &= ~other.bits[pos];
		}
	}

	/**
	 * Flips a range of bits
	 *
	 * @param begIdx begin index, inclusive (0..n)
	 * @param endIdx end index, exclusive (1..n+1)
	 */
	public void flip(long begIdx, long endIdx) {
		if (chk(begIdx, endIdx)) {
			int begWord = (int) (begIdx >> 6);
			int endWord = (int) ((endIdx - 1) >> 6);

			long begMask = -1L << begIdx;
			// 64-(endIdx & 0x3f) is the same as -endIdx due to wrap
			long endMask = -1L >>> -endIdx;

			if (begWord == endWord) {
				bits[begWord] ^= (begMask & endMask);

			} else {
				bits[begWord] ^= begMask;
				for (int i = begWord + 1; i < endWord; i++) {
					bits[i] = ~bits[i];
				}
				bits[endWord] ^= endMask;
			}
		}
	}

	protected long[] getBits() {
		return bits;
	}

	/**
	 * Return the bit positions.
	 *
	 * @return position list
	 */
	public List<Long> positions() {
		List<Long> pos = new LinkedList<>();
		if (bits != null) {
			long idx = nextSetBit(0);
			while (idx > -1) {
				pos.add(idx);
				idx = nextSetBit(idx + 1);
			}
		}
		return pos;
	}

	@Override
	public LongBitSet clone() {
		long[] bits = new long[this.bits.length];
		System.arraycopy(this.bits, 0, bits, 0, bits.length);
		return new LongBitSet(bits, numBits);
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (!(o instanceof LongBitSet other)) return false;
		if (numBits != other.length()) return false;
		return Arrays.equals(bits, other.bits);
	}

	@Override
	public int hashCode() {
		long h = 0;
		for (int i = numWords; --i >= 0;) {
			h ^= bits[i];
			h = (h << 1) | (h >>> 63); // rotate left
		}
		// fold leftmost bits into right and add a constant to prevent
		// empty sets from returning 0, which is too common.
		return (int) ((h >> 32) ^ h) + 0x98761234;
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder("{");
		sb.append(Strings.join(positions()));
		sb.append("}");
		return sb.toString();
	}
}
