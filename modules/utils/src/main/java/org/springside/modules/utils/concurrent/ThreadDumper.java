package org.springside.modules.utils.concurrent;

import java.lang.management.ManagementFactory;
import java.lang.management.ThreadInfo;
import java.lang.management.ThreadMXBean;
import java.util.concurrent.atomic.AtomicLong;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 打印线程栈.
 * 
 * 为避免打印过于频繁, 需要设置最小时间间隔, 默认一小时.
 * 
 * 可设置StackTrace的深度，默认为8.
 */
public class ThreadDumper {

	private static final int DEFAULT_LEAST_INTERVAL = 3600000; // 1小时
	private static final int DEFAULT_MAX_STACK_LEVEL = 8;
	private static final String DEFAULT_DUMP_REASON = "unknown reason";

	private static Logger logger = LoggerFactory.getLogger(ThreadDumper.class);

	private boolean enable = true; // 快速关闭该功能
	private int leastInterval = DEFAULT_LEAST_INTERVAL; // 每次打印ThreadDump的最小时间间隔
	private int maxStackLevel = DEFAULT_MAX_STACK_LEVEL; // 打印StackTrace的最大深度

	private ThreadMXBean threadMBean = ManagementFactory.getThreadMXBean();
	private AtomicLong lastThreadDumpTime = new AtomicLong(0);

	/**
	 * 符合条件则打印线程栈.
	 */
	public void threadDumpIfNeed() {
		threadDumpIfNeed(DEFAULT_DUMP_REASON);
	}

	/**
	 * 符合条件则打印线程栈.
	 * 
	 * @param reasonMsg 发生ThreadDump的原因
	 */
	public void threadDumpIfNeed(String reasonMsg) {
		if (!enable) {
			return;
		}

		synchronized (lastThreadDumpTime) {
			if (System.currentTimeMillis() - lastThreadDumpTime.get() < leastInterval) {
				return;
			} else {
				lastThreadDumpTime.set(System.currentTimeMillis());
			}
		}

		// 参数均为false, 避免输出lockedMonitors和lockedSynchronizers导致的JVM缓慢
		ThreadInfo[] threadInfos = threadMBean.dumpAllThreads(false, false);

		StringBuilder b = new StringBuilder(8192);
		b.append('[');
		for (int i = 0; i < threadInfos.length; i++) {
			b.append(dumpThreadInfo(threadInfos[i])).append(", ");
		}
		logger.info("Dump thread info for {}", reasonMsg);
		logger.info(b.toString());
	}

	/**
	 * 打印全部的stack，重新实现threadInfo的toString()函数，因为默认最多只打印8层的stack. 同时，不再打印lockedMonitors和lockedSynchronizers.
	 */
	public String dumpThreadInfo(ThreadInfo threadInfo) {
		StringBuilder sb = new StringBuilder(512);
		sb.append("\"").append(threadInfo.getThreadName()).append("\" Id=").append(threadInfo.getThreadId()).append(" ")
				.append(threadInfo.getThreadState());
		if (threadInfo.getLockName() != null) {
			sb.append(" on ").append(threadInfo.getLockName());
		}
		if (threadInfo.getLockOwnerName() != null) {
			sb.append(" owned by \"").append(threadInfo.getLockOwnerName()).append("\" Id=")
					.append(threadInfo.getLockOwnerId());
		}
		if (threadInfo.isSuspended()) {
			sb.append(" (suspended)");
		}
		if (threadInfo.isInNative()) {
			sb.append(" (in native)");
		}
		sb.append('\n');
		int i = 0;
		StackTraceElement[] stackTrace = threadInfo.getStackTrace();
		for (; i < Math.min(maxStackLevel, stackTrace.length); i++) {
			StackTraceElement ste = stackTrace[i];
			sb.append("\tat " + ste.toString()).append('\n');
			if (i == 0 && threadInfo.getLockInfo() != null) {
				Thread.State ts = threadInfo.getThreadState();
				switch (ts) {
				case BLOCKED:
					sb.append("\t-  blocked on ").append(threadInfo.getLockInfo()).append('\n');
					break;
				case WAITING:
					sb.append("\t-  waiting on ").append(threadInfo.getLockInfo()).append('\n');
					break;
				case TIMED_WAITING:
					sb.append("\t-  time waiting on ").append(threadInfo.getLockInfo()).append('\n');
					break;
				default:
				}
			}

		}
		if (i < stackTrace.length) {
			sb.append("\t...").append('\n');
		}

		sb.append('\n');
		return sb.toString();
	}

	public void setEnable(boolean enable) {
		this.enable = enable;
	}

	public void setLeastInterval(int leastInterval) {
		this.leastInterval = leastInterval;
	}

	public void setMaxStackLevel(int maxStackLevel) {
		this.maxStackLevel = maxStackLevel;
	}
}
