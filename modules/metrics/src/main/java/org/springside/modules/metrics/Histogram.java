package org.springside.modules.metrics;

import java.util.concurrent.ConcurrentSkipListMap;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;

import org.springside.modules.metrics.utils.Clock;

public class Histogram {

	// allow for this many duplicate ticks before overwriting measurements
	private static final int COLLISION_BUFFER = 256;
	// only trim on updating once every N
	private static final int TRIM_THRESHOLD = 256;

	private final Clock clock;
	private final ConcurrentSkipListMap<Long, Long> measurements;
	private final long window;
	private final AtomicLong lastTick;
	private final AtomicLong count;

	public Histogram(long window, TimeUnit windowUnit) {
		this(window, windowUnit, Clock.DEFAULT);
	}

	public Histogram(long window, TimeUnit windowUnit, Clock clock) {
		this.clock = clock;
		this.measurements = new ConcurrentSkipListMap<Long, Long>();
		this.window = windowUnit.toMillis(window) * COLLISION_BUFFER;
		this.lastTick = new AtomicLong();
		this.count = new AtomicLong();
	}

	public void update(long value) {
		if ((count.incrementAndGet() % TRIM_THRESHOLD) == 0) {
			trim();
		}
		measurements.put(getTick(), value);
	}

	public HistogramSnapshot getSnapshot() {
		trim();
		return new HistogramSnapshot(measurements.values());
	}

	/**
	 * TODO:??
	 */
	private long getTick() {
		for (;;) {
			final long oldTick = lastTick.get();
			final long tick = clock.getCurrentTime() * COLLISION_BUFFER;

			final long newTick = tick > oldTick ? tick : oldTick + 1;
			if (lastTick.compareAndSet(oldTick, newTick)) {
				return newTick;
			}
		}
	}

	private void trim() {
		measurements.headMap(getTick() - window).clear();
	}

}
