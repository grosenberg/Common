package net.certiv.common.util.bits;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Collections;
import java.util.IdentityHashMap;
import java.util.Locale;
import java.util.Map;

public class BitUtil {

	public static final int MAX_ARRAY_LENGTH = Integer.MAX_VALUE - 16;

	/** True iff running on a 64bit JVM */
	public static final boolean JRE_IS_64BIT = is64Bit();

	/** Kilobyte size in bytes. */
	public static final long KB = 1024;

	/** Megabyte size in bytes. */
	public static final long MB = KB * KB;

	/** Gigabyte size in bytes. */
	public static final long GB = KB * MB;

	/** Sizes of primitive classes. */
	public static final Map<Class<?>, Integer> PrimitiveSizes;

	static {
		Map<Class<?>, Integer> map = new IdentityHashMap<>();
		map.put(boolean.class, 1);
		map.put(byte.class, 1);
		map.put(char.class, Character.BYTES);
		map.put(short.class, Short.BYTES);
		map.put(int.class, Integer.BYTES);
		map.put(float.class, Float.BYTES);
		map.put(long.class, Long.BYTES);
		map.put(double.class, Double.BYTES);

		PrimitiveSizes = Collections.unmodifiableMap(map);
	}

	private static boolean is64Bit() {
		String model = System.getProperty("sun.arch.data.model");
		if (model == null) model = System.getProperty("os.arch");
		return model != null && model.contains("64");
	}

	public static long[] grow(long[] array, int minSize) {
		assert minSize >= 0 : "size must be positive (got " + minSize + "): likely integer overflow?";
		if (array.length >= minSize) return array;

		long[] newArray = new long[oversize(minSize, sizeofLong())];
		System.arraycopy(array, 0, newArray, 0, array.length);
		return newArray;
	}

	/**
	 * Returns an array size >= minTargetSize, generally over-allocating exponentially to
	 * achieve amortized linear-time cost as the array grows.
	 * <p>
	 * NOTE: originally borrowed from Python 2.4.2 listobject.c sources (attribution in
	 * LICENSE.txt), but has now been substantially changed based on discussions from
	 * java-dev thread with subject "Dynamic array reallocation algorithms", started on
	 * Jan 12 2010.
	 *
	 * @param minTargetSize   minimum required value to be returned
	 * @param bytesPerElement bytes used by each element of the array
	 */
	private static int oversize(int minTargetSize, int bytesPerElement) {
		if (minTargetSize < 0) {
			// catch usage that accidentally overflows int
			throw new IllegalArgumentException("invalid array size " + minTargetSize);
		}

		if (minTargetSize == 0) {
			// wait until at least one element is requested
			return 0;
		}

		if (minTargetSize > MAX_ARRAY_LENGTH) {
			throw new IllegalArgumentException("requested array size " + minTargetSize
					+ " exceeds maximum array in java (" + MAX_ARRAY_LENGTH + ")");
		}

		// asymptotic exponential growth by 1/8th, favors spending a bit more CPU to not
		// tie up too much wasted RAM:
		int extra = minTargetSize >> 3;

		if (extra < 3) {
			// for very small arrays, where constant overhead of realloc
			// is presumably relatively high, we grow faster
			extra = 3;
		}

		int newSize = minTargetSize + extra;

		// add 7 to allow for worst case byte alignment addition below:
		if (newSize + 7 < 0 || newSize + 7 > MAX_ARRAY_LENGTH) {
			// int overflowed, or we exceeded the maximum array length
			return MAX_ARRAY_LENGTH;
		}

		if (JRE_IS_64BIT) {
			// round up to 8 byte alignment in 64bit env
			switch (bytesPerElement) {
				case 4:
					// round up to multiple of 2
					return (newSize + 1) & 0x7ffffffe;
				case 2:
					// round up to multiple of 4
					return (newSize + 3) & 0x7ffffffc;
				case 1:
					// round up to multiple of 8
					return (newSize + 7) & 0x7ffffff8;
				case 8:
					// no rounding
				default:
					// odd (invalid?) size
					return newSize;
			}
		} else {
			// round up to 4 byte alignment in 64bit env
			switch (bytesPerElement) {
				case 2:
					// round up to multiple of 2
					return (newSize + 1) & 0x7ffffffe;
				case 1:
					// round up to multiple of 4
					return (newSize + 3) & 0x7ffffffc;
				case 4:
				case 8:
					// no rounding
				default:
					// odd (invalid?) size
					return newSize;
			}
		}
	}

	/** Returns the number of set bits in an array of longs. */
	public static long pop_array(long[] arr, int wordOffset, int numWords) {
		long popCount = 0;
		for (int idx = wordOffset, end = wordOffset + numWords; idx < end; ++idx) {
			popCount += Long.bitCount(arr[idx]);
		}
		return popCount;
	}

	/** Returns the primitive size of an integer in bytes. */
	public static int sizeOfInt() {
		return PrimitiveSizes.get(int.class);
	}

	/** Returns the primitive size of a long in bytes. */
	public static int sizeofLong() {
		return PrimitiveSizes.get(long.class);
	}

	/**
	 * Returns the size of a primitive class in bytes. Returns {@code -1} if the primitive
	 * class is unknown.
	 */
	public static int sizeof(Class<?> primitive) {
		return PrimitiveSizes.getOrDefault(primitive, -1);
	}

	/** Returns {@code size} in human-readable units (GB, MB, KB or bytes). */
	public static String toHumanUnits(long bytes) {
		return toHumanUnits(bytes, new DecimalFormat("0.#", DecimalFormatSymbols.getInstance(Locale.ROOT)));
	}

	/** Returns {@code size} in human-readable units (GB, MB, KB or bytes). */
	public static String toHumanUnits(long bytes, DecimalFormat df) {
		if (bytes / GB > 0) {
			return df.format((float) bytes / GB) + " GB";
		} else if (bytes / MB > 0) {
			return df.format((float) bytes / MB) + " MB";
		} else if (bytes / KB > 0) {
			return df.format((float) bytes / KB) + " KB";
		} else {
			return bytes + " bytes";
		}
	}
}
