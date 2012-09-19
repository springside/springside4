package org.springside.examples.showcase.demos.utilities.event.guava;

import com.google.common.eventbus.Subscribe;

/**
 * 监听多种事件.
 * @author hzl7652
 */
public class MultipleListener {

	public Integer lastInteger;
	public Long lastLong;

	@Subscribe
	public void listenInteger(Integer event) {
		lastInteger = event;
	}

	@Subscribe
	public void listenLong(Long event) {
		lastLong = event;
	}

	public Integer getLastInteger() {
		return lastInteger;
	}

	public Long getLastLong() {
		return lastLong;
	}
}