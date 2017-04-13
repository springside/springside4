package org.springside.examples.showcase.demos.utilities.event.guava;


import com.google.common.eventbus.Subscribe;

/**
 * 事件监听器.
 * 
 * @author hzl7652
 */
public class EventListener {

	private String message;

	public String getMessage() {
		return message;
	}

	/**
	 * 在指定的方法上加上@Subscribe注解，监听事件.
	 */
	@Subscribe
	public void listen(TestEvent event) {
		message = event.getMessage();
	}
}
