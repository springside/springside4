package org.springside.modules.metrics;

import static java.lang.Math.*;

import java.util.Arrays;
import java.util.Collection;

public class HistogramSnapshot {

	private final long[] values;

	/**
	 * Create a new {@link HistogramSnapshot} with the given values.
	 * 
	 * @param values an unordered set of values in the reservoir
	 */
	public HistogramSnapshot(Collection<Long> values) {
		final Object[] copy = values.toArray();
		this.values = new long[copy.length];
		for (int i = 0; i < copy.length; i++) {
			this.values[i] = (Long) copy[i];
		}
		Arrays.sort(this.values);
	}

	/**
	 * Create a new {@link HistogramSnapshot} with the given values.
	 * 
	 * @param values an unordered set of values in the reservoir
	 */
	public HistogramSnapshot(long[] values) {
		this.values = Arrays.copyOf(values, values.length);
		Arrays.sort(this.values);
	}

	/**
	 * Returns the value at the given quantile.
	 * 
	 * @param quantile a given quantile, in {@code [0..1]}
	 * @return the value in the distribution at {@code quantile}
	 */
	public double getValue(double quantile) {
		if ((quantile < 0.0) || (quantile > 1.0)) {
			throw new IllegalArgumentException(quantile + " is not in [0..1]");
		}

		if (values.length == 0) {
			return 0.0;
		}

		final double pos = quantile * (values.length + 1);

		if (pos < 1) {
			return values[0];
		}

		if (pos >= values.length) {
			return values[values.length - 1];
		}

		final double lower = values[(int) pos - 1];
		final double upper = values[(int) pos];
		return lower + ((pos - floor(pos)) * (upper - lower));
	}

	/**
	 * Returns the value at the 99th percentile in the distribution.
	 * 
	 * @return the value at the 99th percentile
	 */
	public double get90th() {
		return getValue(0.9);
	}

	/**
	 * Returns the highest value in the snapshot.
	 * 
	 * @return the highest value
	 */
	public long getMax() {
		if (values.length == 0) {
			return 0;
		}
		return values[values.length - 1];
	}

	/**
	 * Returns the lowest value in the snapshot.
	 * 
	 * @return the lowest value
	 */
	public long getMin() {
		if (values.length == 0) {
			return 0;
		}
		return values[0];
	}

	/**
	 * Returns the arithmetic mean of the values in the snapshot.
	 * 
	 * @return the arithmetic mean
	 */
	public double getMean() {
		if (values.length == 0) {
			return 0;
		}

		double sum = 0;
		for (long value : values) {
			sum += value;
		}
		return sum / values.length;
	}
}
