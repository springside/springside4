package org.springside.examples.showcase.demos.utilities.event.guava;

/**
 * 测试事件对象.
 * 
 * @author hzl7652
 */
public class TestEvent {

	private String message;

	public TestEvent() {
	}

	public TestEvent(String message) {
		this.message = message;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

}
