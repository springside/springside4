package org.springside.modules.metrics.metric;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;

import org.springside.modules.metrics.utils.Clock;

public abstract class CachedGauge<T extends Number> extends Gauge<T>{

	public static Clock clock = Clock.DEFAULT;
	
	private AtomicLong reloadAt = new AtomicLong(0);
	private long timeoutNS = 0;

	private volatile T value;

	public CachedGauge() {
	}

	public CachedGauge(long timeout, TimeUnit timeoutUnit) {
		this.reloadAt = new AtomicLong(0);
		this.timeoutNS = timeoutUnit.toMillis(timeout);
	}

	@Override
	public T getValue() {
		if (shouldLoad()) {
			this.value = loadValue();
		}
		return value;
	}

	private boolean shouldLoad() {
		for (;;) {
			final long time = clock.getCurrentTime();
			final long current = reloadAt.get();
			if (current > time) {
				return false;
			}
			if (reloadAt.compareAndSet(current, time + timeoutNS)) {
				return true;
			}
		}
	}

	//子类中实现
	protected abstract T loadValue();

}
