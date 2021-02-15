/*******************************************************************************
 * Copyright (c) 2017, 2018 Certiv Analytics. All rights reserved.
 * Use of this file is governed by the Eclipse Public License v1.0
 * that can be found in the LICENSE.txt file in the project root,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package net.certiv.tools.util;

import java.math.MathContext;
import java.util.Collection;

public class Maths {

	public static final MathContext Scale2 = new MathContext(2);

	/** The natural logarithm of 2. */
	public static double log2 = Math.log(2);

	/** The small deviation allowed in double comparisons */
	public static double SMALL = 1e-6;

	public static int delta(int a, int b) {
		return Math.abs(a - b);
	}

	/**
	 * Computes the sum of the elements of an array of integers.
	 *
	 * @param ints the array of integers
	 * @return the sum of the elements
	 */
	public static int sum(int[] ints) {
		int sum = 0;
		for (int j : ints) {
			sum += j;
		}
		return sum;
	}

	/** Returns the sum of the given values. */
	public static double sum(double[] vals) {
		double sum = 0;
		for (double val : vals) {
			sum += val;
		}
		return sum;
	}

	public static int sum(Collection<Integer> values) {
		int sum = 0;
		for (int val : values) {
			sum += val;
		}
		return sum;
	}

	/** Returns the normalized sum of the given values. */
	public static double nsum(double[] vals) {
		double maxVal = max(vals);
		double minVal = min(vals);
		if (maxVal == minVal) return vals.length;

		double sum = 0;
		for (double val : vals) {
			sum += (val - minVal) / (maxVal - minVal);
		}
		return sum;
	}

	/** Returns the average of the given values, rounded to an int. */
	public static int ave(double... vals) {
		return round(sum(vals) / vals.length);
	}

	/**
	 * Computes the mean for an array of doubles.
	 *
	 * @param vector the array
	 * @return the mean
	 */
	public static double mean(double[] vector) {
		double sum = 0;
		if (vector.length == 0) return 0;

		for (double element : vector) {
			sum += element;
		}
		return sum / vector.length;
	}

	/**
	 * Computes the median for an array of doubles.
	 *
	 * @param vector the array
	 * @return the median
	 */
	public static double median(double[] vector) {
		if (vector.length == 0) return 0;

		int mid = vector.length / 2;
		if (vector.length % 2 != 0) return vector[mid];
		return (vector[mid - 1] + vector[mid]) / 2;
	}

	/**
	 * Computes the standard deviation for an array of doubles.
	 *
	 * @param vector the array
	 * @return the variance
	 */
	public static double stdDeviation(double[] vector) {
		return Math.sqrt(variance(vector));
	}

	/**
	 * Computes the variance for an array of doubles.
	 *
	 * @param vector the array
	 * @return the variance
	 */
	public static double variance(double[] vector) {
		double sum = 0, sumSquared = 0;
		if (vector.length <= 1) return 0;
		for (double element : vector) {
			sum += element;
			sumSquared += (element * element);
		}
		double result = (sumSquared - (sum * sum / vector.length)) / (vector.length - 1);
		return Math.max(0, result);
	}

	/**
	 * Normalizes the doubles in the array by their sum.
	 *
	 * @param doubles the array of double
	 * @exception IllegalArgumentException if sum is Zero or NaN
	 */
	public static void normalize(double[] doubles) {
		double sum = 0;
		for (double d : doubles) {
			sum += d;
		}
		normalize(doubles, sum);
	}

	/**
	 * Normalizes the doubles in the array using the given value.
	 *
	 * @param doubles the array of double
	 * @param sum the value by which the doubles are to be normalized
	 * @exception IllegalArgumentException if sum is zero or NaN
	 */
	public static void normalize(double[] doubles, double sum) {
		if (Double.isNaN(sum)) {
			throw new IllegalArgumentException("Can't normalize array. Sum is NaN.");
		}
		if (sum == 0) {
			// Maybe this should just be a return.
			throw new IllegalArgumentException("Can't normalize array. Sum is zero.");
		}
		for (int i = 0; i < doubles.length; i++) {
			doubles[i] /= sum;
		}
	}

	/**
	 * Returns the correlation coefficient of two double vectors.
	 *
	 * @param y1 double vector 1
	 * @param y2 double vector 2
	 * @param n the length of two double vectors
	 * @return the correlation coefficient
	 */
	public static double correlation(double y1[], double y2[], int n) {
		int i;
		double av1 = 0.0, av2 = 0.0, y11 = 0.0, y22 = 0.0, y12 = 0.0, c;

		if (n <= 1) return 1.0;
		for (i = 0; i < n; i++) {
			av1 += y1[i];
			av2 += y2[i];
		}
		av1 /= n;
		av2 /= n;
		for (i = 0; i < n; i++) {
			y11 += (y1[i] - av1) * (y1[i] - av1);
			y22 += (y2[i] - av2) * (y2[i] - av2);
			y12 += (y1[i] - av1) * (y2[i] - av2);
		}
		if (y11 * y22 == 0.0) {
			c = 1.0;
		} else {
			c = y12 / Math.sqrt(Math.abs(y11 * y22));
		}
		return c;
	}

	/**
	 * Computes entropy for an array of integers.
	 *
	 * @param counts array of counts
	 * @return - a log2 a - b log2 b - c log2 c + (a+b+c) log2 (a+b+c) when given
	 *             array [a b c]
	 */
	public static double entropy(int counts[]) {
		int total = 0;
		double x = 0;
		for (int count : counts) {
			x -= xlogx(count);
			total += count;
		}
		return x + xlogx(total);
	}

	/**
	 * Returns c*log2(c) for a given integer value c.
	 *
	 * @param c an integer value
	 * @return c*log2(c) (but is careful to return 0 if c is 0)
	 */
	public static double xlogx(int c) {
		if (c == 0) return 0.0;
		return c * log2(c);
	}

	/**
	 * Returns the logarithm of a for base 2.
	 *
	 * @param a a double
	 * @return the logarithm for base 2
	 */
	public static double log2(double a) {
		return Math.log(a) / log2;
	}

	/**
	 * Returns the given value constrained to the range defined by the given min and
	 * max values.
	 */
	public static int constrain(int value, int min, int max) {
		value = Math.max(min, value);
		value = Math.min(value, max);
		return value;
	}

	/**
	 * Returns the given value constrained to the range defined by the given min and
	 * max values.
	 */
	public static double constrain(double value, double min, double max) {
		value = Math.max(value, min);
		value = Math.min(value, max);
		return value;
	}

	/**
	 * Computes the max for an array of doubles.
	 *
	 * @param vector the array
	 * @return the maximum
	 */
	public static double max(double[] vals) {
		if (vals == null) throw new IllegalArgumentException("Argument cannot be null");

		double max = Double.MIN_VALUE;
		for (double val : vals) {
			if (!Double.isNaN(val)) {
				max = (max > val) ? max : val;
			}
		}
		return max;
	}

	/**
	 * Computes the minimum for an array of doubles.
	 *
	 * @param vector the array
	 * @return the minimum value
	 */
	public static double min(double[] vals) {
		if (vals == null) throw new IllegalArgumentException("Argument cannot be null");

		double min = Double.MAX_VALUE;
		for (double val : vals) {
			if (!Double.isNaN(val)) {
				min = (min > val) ? val : min;
			}
		}
		return min;
	}

	/**
	 * Rounds a double to the next nearest integer value. The JDK version of it
	 * doesn't work properly.
	 *
	 * @param value the double value
	 * @return the resulting integer value
	 */
	public static int round(double value) {
		int roundedValue = value > 0 ? (int) (value + 0.5) : -(int) (Math.abs(value) + 0.5);
		return roundedValue;
	}

	/**
	 * Rounds a double to the given number of decimal places.
	 *
	 * @param value the double value
	 * @param precision the number of digits after the decimal point
	 * @return the double rounded to the given precision
	 */
	public static double round(double value, int precision) {
		double mask = Math.pow(10.0, precision);
		return (Math.round(value * mask)) / mask;
	}
}
