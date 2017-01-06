package org.springside.modules.utils.collection;

import java.util.ArrayDeque;
import java.util.LinkedList;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.LinkedBlockingQueue;

public class QueueUtil {

	public static <E> ArrayDeque<E> newArrayQueue() {
		return new ArrayDeque<E>();
	}

	public static <E> LinkedList<E> newLinkedQueue() {
		return new LinkedList<E>();
	}

	public static <E> ConcurrentLinkedQueue<E> newConcurrentNonBlockingQueue() {
		return new ConcurrentLinkedQueue<E>();
	}

	public static <E> ArrayBlockingQueue<E> newArrayBlockingQueue(int capacity) {
		return new ArrayBlockingQueue<E>(capacity);
	}

	public static <E> LinkedBlockingQueue<E> newLinkedBlockingQeque(int capacity) {
		return new LinkedBlockingQueue<E>(capacity);
	}

	public static <E> LinkedBlockingQueue<E> newLinkedBlockingQueueWithoutLimit() {
		return new LinkedBlockingQueue<E>();
	}

	/**
	 * 支持后进先出的栈，用ArrayDeque实现.
	 */
	public static <E> ArrayDeque<E> newStack() {
		return new ArrayDeque<E>();
	}

	/**
	 * 支持后进先出的并发栈，用LinkedBlockingDeque实现.
	 */
	public static <E> LinkedBlockingDeque<E> newConcurrentStack() {
		return new LinkedBlockingDeque<E>();
	}
}
