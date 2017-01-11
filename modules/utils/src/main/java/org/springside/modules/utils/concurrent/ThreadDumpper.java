package org.springside.modules.utils.concurrent;

import java.lang.management.ManagementFactory;
import java.lang.management.ThreadInfo;
import java.lang.management.ThreadMXBean;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 由程序触发的ThreadDump，打印到日志中.
 * 
 * 因为ThreadDump本身会造成JVM停顿，所以加上了开关和最少间隔时间的选项(默认不限制)
 * 
 * 因为ThreadInfo的toString()最多只会打印8层的StackTrace，所以加上了最大打印层数的选项.(默认为8)
 * 
 * @author calvin
 */
public class ThreadDumpper {

	private static final int DEFAULT_MAX_STACK_LEVEL = 8;

	private static Logger logger = LoggerFactory.getLogger(ThreadDumpper.class);

	private boolean enable = true; // 快速关闭该功能
	private long leastIntervalMills = 0; // 每次打印ThreadDump的最小时间间隔，单位为毫秒
	private int maxStackLevel = DEFAULT_MAX_STACK_LEVEL; // 打印StackTrace的最大深度

	private ThreadMXBean threadMBean = ManagementFactory.getThreadMXBean();
	private volatile Long lastThreadDumpTime = 0L;

	public ThreadDumpper() {
	}

	public ThreadDumpper(long leastIntervalMills, int maxStackLevel) {
		this.leastIntervalMills = leastIntervalMills;
		this.maxStackLevel = maxStackLevel;
	}

	/**
	 * 符合条件则打印线程栈.
	 */
	public void threadDumpIfNeed() {
		threadDumpIfNeed(null);
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

		synchronized (this) {
			if (System.currentTimeMillis() - lastThreadDumpTime < leastIntervalMills) {
				return;
			} else {
				lastThreadDumpTime = System.currentTimeMillis();
			}
		}

		logger.info("Thread dump by ThreadDumpper" + reasonMsg != null ? (" for " + reasonMsg) : "");

		// 参数均为false, 避免输出lockedMonitors和lockedSynchronizers导致的JVM缓慢
		ThreadInfo[] threadInfos = threadMBean.dumpAllThreads(false, false);

		StringBuilder b = new StringBuilder(8192);
		b.append('[');
		for (int i = 0; i < threadInfos.length; i++) {
			b.append(dumpThreadInfo(threadInfos[i])).append(", ");
		}

		// 两条日志间的时间间隔，是VM被thread dump堵塞的时间.
		logger.info(b.toString());
	}

	/**
	 * 打印全部的stack，重新实现threadInfo的toString()函数，因为默认最多只打印8层的stack. 同时，不再打印lockedMonitors和lockedSynchronizers.
	 */
	private String dumpThreadInfo(ThreadInfo threadInfo) {
		StringBuilder sb = new StringBuilder(512);
		sb.append("\"").append(threadInfo.getThreadName()).append("\" Id=").append(threadInfo.getThreadId()).append(' ')
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
			sb.append("\tat ").append(ste.toString()).append('\n');
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

	/**
	 * 快速关闭打印
	 */
	public void setEnable(boolean enable) {
		this.enable = enable;
	}

	/**
	 * 打印ThreadDump的最小时间间隔，单位为秒，默认为0不限制.
	 */
	public void setLeastInterval(int leastIntervalSeconds) {
		synchronized (this) {
			this.leastIntervalMills = TimeUnit.SECONDS.toMillis(leastIntervalSeconds);
		}
	}

	/**
	 * 打印StackTrace的最大深度, 默认为8
	 */
	public void setMaxStackLevel(int maxStackLevel) {
		this.maxStackLevel = maxStackLevel;
	}
}
