package org.springside.modules.utils.log;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

import org.slf4j.Logger;
import org.slf4j.Marker;
import org.springside.modules.utils.time.ClockUtil;

/**
 * 带限流功能的Logger，该logger在配置的时间间隔内只输出一条日志。
 * 
 * 比如大量重复的出错日志，不希望输出太多时可用此logger进行限流.
 * 
 * From facebook
 * https://github.com/facebook/jcommon/blob/master/logging-util/src/main/java/com/facebook/logging/util/TimeSamplingSLF4JLogger.java
 * 
 * @author calvin
 */
public class ThrottledSlf4jLogger implements Logger {

	private final Logger delegate;
	private final long windowSizeMillis;
	private final AtomicBoolean logToggle = new AtomicBoolean(false);

	private volatile long lastLoggedMillis = 0;

	public ThrottledSlf4jLogger(Logger logger, long time, TimeUnit timeUnit) {
		this.delegate = logger;
		windowSizeMillis = timeUnit.toMillis(time);
	}

	private boolean shouldLog() {
		if (ClockUtil.currentTimeMillis() - lastLoggedMillis >= windowSizeMillis
				&& logToggle.compareAndSet(false, true)) {
			try {
				lastLoggedMillis = ClockUtil.currentTimeMillis();

				return true;
			} finally {
				logToggle.set(false);
			}
		}

		return false;
	}

	@Override
	public boolean isDebugEnabled() {
		return delegate.isDebugEnabled();
	}

	@Override
	public void debug(String msg) {
		if (shouldLog()) {
			delegate.debug(msg);
		}
	}

	@Override
	public void debug(String format, Object arg) {
		if (shouldLog()) {
			delegate.debug(format, arg);
		}
	}

	@Override
	public void debug(String format, Object arg1, Object arg2) {
		if (shouldLog()) {
			delegate.debug(format, arg1, arg2);
		}
	}

	@Override
	public void debug(String format, Object... args) {
		if (shouldLog()) {
			delegate.debug(format, args);
		}
	}

	@Override
	public void debug(String message, Throwable throwable) {
		if (shouldLog()) {
			delegate.debug(message, throwable);
		}
	}

	@Override
	public boolean isDebugEnabled(Marker marker) {
		return delegate.isDebugEnabled(marker);
	}

	@Override
	public void debug(Marker marker, String msg) {
		if (shouldLog()) {
			delegate.debug(marker, msg);
		}
	}

	@Override
	public void debug(Marker marker, String format, Object arg) {
		if (shouldLog()) {
			delegate.debug(marker, format, arg);
		}
	}

	@Override
	public void debug(Marker marker, String format, Object arg1, Object arg2) {
		if (shouldLog()) {
			delegate.debug(marker, format, arg1, arg2);
		}
	}

	@Override
	public void debug(Marker marker, String format, Object... arguments) {
		if (shouldLog()) {
			delegate.debug(marker, format, arguments);
		}
	}

	@Override
	public void debug(Marker marker, String msg, Throwable t) {
		if (shouldLog()) {
			delegate.debug(marker, msg, t);
		}
	}

	@Override
	public boolean isInfoEnabled() {
		return delegate.isInfoEnabled();
	}

	@Override
	public void info(String msg) {
		if (shouldLog()) {
			delegate.debug(msg);
		}
	}

	@Override
	public void info(String format, Object arg) {
		if (shouldLog()) {
			delegate.info(format, arg);
		}
	}

	@Override
	public void info(String format, Object arg1, Object arg2) {
		if (shouldLog()) {
			delegate.info(format, arg1, arg2);
		}
	}

	@Override
	public boolean isWarnEnabled() {
		return delegate.isWarnEnabled();
	}

	@Override
	public void warn(String msg) {
		if (shouldLog()) {
			delegate.warn(msg);
		}
	}

	@Override
	public void warn(String format, Object arg) {
		if (shouldLog()) {
			delegate.warn(format, arg);
		}
	}

	@Override
	public boolean isErrorEnabled() {
		return delegate.isErrorEnabled();
	}

	@Override
	public void error(String msg) {
		if (shouldLog()) {
			delegate.error(msg);
		}
	}

	@Override
	public void error(String format, Object arg) {
		if (shouldLog()) {
			delegate.error(format, arg);
		}
	}

	@Override
	public void error(String format, Object arg1, Object arg2) {
		if (shouldLog()) {
			delegate.error(format, arg1, arg2);
		}
	}

	@Override
	public void info(String format, Object... args) {
		if (shouldLog()) {
			delegate.info(format, args);
		}
	}

	@Override
	public void info(String message, Throwable throwable) {
		if (shouldLog()) {
			delegate.info(message, throwable);
		}
	}

	@Override
	public boolean isInfoEnabled(Marker marker) {
		return delegate.isInfoEnabled(marker);
	}

	@Override
	public void info(Marker marker, String msg) {
		if (shouldLog()) {
			delegate.info(marker, msg);
		}
	}

	@Override
	public void info(Marker marker, String format, Object arg) {
		if (shouldLog()) {
			delegate.info(marker, format, arg);
		}
	}

	@Override
	public void info(Marker marker, String format, Object arg1, Object arg2) {
		if (shouldLog()) {
			delegate.info(marker, format, arg1, arg2);
		}
	}

	@Override
	public void info(Marker marker, String format, Object... arguments) {
		if (shouldLog()) {
			delegate.info(marker, format, arguments);
		}
	}

	@Override
	public void info(Marker marker, String msg, Throwable t) {
		if (shouldLog()) {
			delegate.info(marker, msg, t);
		}
	}

	@Override
	public void warn(String format, Object... args) {
		if (shouldLog()) {
			delegate.warn(format, args);
		}
	}

	@Override
	public void warn(String format, Object arg1, Object arg2) {
		if (shouldLog()) {
			delegate.warn(format, arg1, arg2);
		}
	}

	@Override
	public void warn(String message, Throwable throwable) {
		if (shouldLog()) {
			delegate.warn(message, throwable);
		}
	}

	@Override
	public boolean isWarnEnabled(Marker marker) {
		return delegate.isWarnEnabled(marker);
	}

	@Override
	public void warn(Marker marker, String msg) {
		if (shouldLog()) {
			delegate.warn(marker, msg);
		}
	}

	@Override
	public void warn(Marker marker, String format, Object arg) {
		if (shouldLog()) {
			delegate.warn(marker, format, arg);
		}
	}

	@Override
	public void warn(Marker marker, String format, Object arg1, Object arg2) {
		if (shouldLog()) {
			delegate.warn(marker, format, arg1, arg2);
		}
	}

	@Override
	public void warn(Marker marker, String format, Object... arguments) {
		if (shouldLog()) {
			delegate.warn(marker, format, arguments);
		}
	}

	@Override
	public void warn(Marker marker, String msg, Throwable t) {
		if (shouldLog()) {
			delegate.warn(marker, msg, t);
		}
	}

	@Override
	public void error(String format, Object... args) {
		if (shouldLog()) {
			delegate.error(format, args);
		}
	}

	@Override
	public void error(String message, Throwable throwable) {
		if (shouldLog()) {
			delegate.error(message, throwable);
		}
	}

	@Override
	public boolean isErrorEnabled(Marker marker) {
		return delegate.isErrorEnabled(marker);
	}

	@Override
	public void error(Marker marker, String msg) {
		if (shouldLog()) {
			delegate.error(marker, msg);
		}
	}

	@Override
	public void error(Marker marker, String format, Object arg) {
		if (shouldLog()) {
			delegate.error(marker, format, arg);
		}
	}

	@Override
	public void error(Marker marker, String format, Object arg1, Object arg2) {
		if (shouldLog()) {
			delegate.error(marker, format, arg1, arg2);
		}
	}

	@Override
	public void error(Marker marker, String format, Object... arguments) {
		if (shouldLog()) {
			delegate.error(marker, format, arguments);
		}
	}

	@Override
	public void error(Marker marker, String msg, Throwable t) {
		if (shouldLog()) {
			delegate.error(marker, msg, t);
		}
	}

	@Override
	public String getName() {
		return delegate.getName();
	}

	@Override
	public boolean isTraceEnabled() {
		return delegate.isTraceEnabled();
	}

	@Override
	public void trace(String msg) {
		if (shouldLog()) {
			delegate.trace(msg);
		}
	}

	@Override
	public void trace(String format, Object arg) {
		if (shouldLog()) {
			delegate.trace(format, arg);
		}
	}

	@Override
	public void trace(String format, Object arg1, Object arg2) {
		if (shouldLog()) {
			delegate.trace(format, arg1, arg2);
		}
	}

	@Override
	public void trace(String format, Object... arguments) {
		if (shouldLog()) {
			delegate.trace(format, arguments);
		}
	}

	@Override
	public void trace(String msg, Throwable t) {
		if (shouldLog()) {
			delegate.trace(msg, t);
		}
	}

	@Override
	public boolean isTraceEnabled(Marker marker) {
		return delegate.isTraceEnabled(marker);
	}

	@Override
	public void trace(Marker marker, String msg) {
		if (shouldLog()) {
			delegate.trace(marker, msg);
		}
	}

	@Override
	public void trace(Marker marker, String format, Object arg) {
		if (shouldLog()) {
			delegate.trace(marker, format, arg);
		}
	}

	@Override
	public void trace(Marker marker, String format, Object arg1, Object arg2) {
		if (shouldLog()) {
			delegate.trace(marker, format, arg1, arg2);
		}
	}

	@Override
	public void trace(Marker marker, String format, Object... argArray) {
		if (shouldLog()) {
			delegate.trace(marker, format, argArray);
		}
	}

	@Override
	public void trace(Marker marker, String msg, Throwable t) {
		if (shouldLog()) {
			delegate.trace(marker, msg, t);
		}
	}
}
