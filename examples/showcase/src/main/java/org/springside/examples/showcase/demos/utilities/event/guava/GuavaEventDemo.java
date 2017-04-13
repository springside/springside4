package org.springside.examples.showcase.demos.utilities.event.guava;

import static junit.framework.Assert.*;

import org.junit.Test;

import com.google.common.eventbus.EventBus;

/**
 * guava EventBus 演示, 主要用来简化处理生产/消费者编程模型.
 * 
 * @author hzl7652
 */
public class GuavaEventDemo {

	@Test
	public void receiveEventTest() {
		//事件总线对象
		EventBus eventBus = new EventBus("test");
		//监听器
		EventListener listener = new EventListener();
		//注册监听器
		eventBus.register(listener);
		//发送事件
		eventBus.post(new TestEvent("event message"));
		//触发事件
		assertEquals("event message", listener.getMessage());

	}

	/**
	 * 监听多事件演示.
	 */
	@Test
	public void receiveMultipleEvents() {

		EventBus eventBus = new EventBus("test");
		MultipleListener multiListener = new MultipleListener();

		eventBus.register(multiListener);

		eventBus.post(new Integer(100));
		eventBus.post(new Long(800));

		assertEquals(new Integer(100), multiListener.getLastInteger());
		assertEquals(new Long(800), multiListener.getLastLong());
	}
}
