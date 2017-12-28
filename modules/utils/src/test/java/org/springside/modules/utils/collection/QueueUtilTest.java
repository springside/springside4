package org.springside.modules.utils.collection;

import static org.assertj.core.api.Assertions.*;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.LinkedList;
import java.util.Queue;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.LinkedBlockingQueue;

import org.junit.Test;

public class QueueUtilTest {

	@Test
	public void guavaBuildSet() {
		ArrayDeque<String> queue1 = QueueUtil.newArrayDeque(16);
		LinkedList<String> queue2 = QueueUtil.newLinkedDeque();

		ConcurrentLinkedQueue<String> queue3 = QueueUtil.newConcurrentNonBlockingQueue();
		Deque<String> queue7 = QueueUtil.newConcurrentNonBlockingDeque();

		LinkedBlockingQueue<String> queue4 = QueueUtil.newBlockingUnlimitQueue();
		LinkedBlockingDeque<String> queue8 = QueueUtil.newBlockingUnlimitDeque();

		LinkedBlockingQueue<String> queue5 = QueueUtil.newLinkedBlockingQeque(100);
		ArrayBlockingQueue<String> queue6 = QueueUtil.newArrayBlockingQueue(100);
		LinkedBlockingDeque<String> queue9 = QueueUtil.newBlockingDeque(100);
	}

	@Test
	public void stack() {

		Queue<String> stack = ExQueues.createStack(10);
		Queue<String> stack2 = ExQueues.createConcurrentStack();

		stack.offer("1");
		stack.offer("2");
		
		assertThat(stack.poll()).isEqualTo("2");
		assertThat(stack.poll()).isEqualTo("1");
		
		stack2.offer("1");
		stack2.offer("2");
		
		assertThat(stack2.poll()).isEqualTo("2");
		assertThat(stack2.poll()).isEqualTo("1");
	}

}
