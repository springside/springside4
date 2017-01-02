package org.springside.modules.utils.time;

import java.util.concurrent.atomic.AtomicReference;

import org.apache.commons.lang3.time.FastDateFormat;

/**
 * DateFormat.format()消耗较大，如果时间戳是递增的，而且同一毫秒内有多次format()，使用用本类减少重复调用.
 * 
 * From Log4j2 DatePatternConverter
 * 
 * @author calvin
 */
public class CachingDateFormatter {
	private FastDateFormat fastDateFormat;
	private AtomicReference<CachedTime> cachedTime;

	public CachingDateFormatter(String pattern) {
		fastDateFormat = FastDateFormat.getInstance(pattern);
		cachedTime = new AtomicReference<CachedTime>(new CachedTime(System.currentTimeMillis()));
	}

	public CachingDateFormatter(FastDateFormat fastDateFormat) {
		this.fastDateFormat = fastDateFormat;
		this.cachedTime = new AtomicReference<CachedTime>(new CachedTime(System.currentTimeMillis()));
	}

	public String format(final long timestampMillis) {
		CachedTime cached = cachedTime.get();
		if (timestampMillis != cached.timestampMillis) {
			final CachedTime newTime = new CachedTime(timestampMillis);
			if (cachedTime.compareAndSet(cached, newTime)) {
				cached = newTime;
			} else {
				cached = cachedTime.get();
			}
		}
		return cached.formatted;
	}

	final class CachedTime {
		public long timestampMillis;
		public String formatted;

		public CachedTime(final long timestampMillis) {
			this.timestampMillis = timestampMillis;
			this.formatted = fastDateFormat.format(this.timestampMillis);
		}
	}
}
