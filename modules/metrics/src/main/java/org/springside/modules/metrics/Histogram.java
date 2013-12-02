package org.springside.modules.metrics;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

public class Histogram {

	private LinkedList<Long> measurements = new LinkedList<Long>();
	private Double[] pcts;

	public Histogram(Double[] pcts) {
		this.pcts = pcts;
	}

	public void update(long value) {
		synchronized (measurements) {
			measurements.add(value);
		}
	}

	public HistogramMetric getMetric() {

		LinkedList<Long> snapshotList = measurements;
		measurements = new LinkedList();

		if (snapshotList.isEmpty()) {
			return createEmptyMetric();
		}

		Collections.sort(snapshotList);

		int count = snapshotList.size();

		HistogramMetric metric = new HistogramMetric();
		metric.min = snapshotList.get(0);
		metric.max = snapshotList.get(count - 1);

		double sum = 0;
		for (long value : snapshotList) {
			sum += value;
		}
		metric.mean = sum / count;

		for (Double pct : pcts) {

			metric.pcts.put(pct, getPercent(snapshotList, count, pct));
		}

		return metric;
	}

	public Long getPercent(List<Long> snapshotList, int count, double pct) {

		final double pos = pct * (count + 1);

		if (pos < 1) {
			return snapshotList.get(0);
		}

		if (pos >= count) {
			return snapshotList.get(count - 1);
		}

		return snapshotList.get((int) pos - 1);
	}

	private HistogramMetric createEmptyMetric() {
		HistogramMetric metric = new HistogramMetric();
		metric.min = 0;
		metric.max = 0;
		metric.mean = 0;
		for (Double pct : pcts) {
			metric.pcts.put(pct, 0L);
		}

		return metric;
	}
}
