package org.springside.modules.utils.base;

import java.lang.management.ManagementFactory;
import java.lang.management.RuntimeMXBean;

import org.apache.commons.lang3.StringUtils;

/**
 * 各种运行时信息的工具类
 * 
 * 1.取得当前进程PID
 * 
 * 2.通过StackTrace 获得当前方法的类名方法名，调用者的类名方法名(获取StackTrace有消耗，不要滥用)
 */
public class RuntimeUtil {

	private static RuntimeMXBean mxBean;

	/**
	 * 获得当前进程的PID
	 * 
	 * 当失败时返回-1
	 */
	public static int getPid() {
		if (mxBean == null) {
			mxBean = ManagementFactory.getRuntimeMXBean();
		}

		// format: "pid@hostname"
		String name = mxBean.getName();
		String[] split = name.split("@");
		if (split.length != 2) {
			return -1;
		}

		try {
			return Integer.parseInt(split[0]);
		} catch (Exception e) { // NOSONAR
			return -1;
		}
	}

	//////// 通过StackTrace 获得当前方法的调用者 ////
	/**
	 * 通过StackTrace，获得调用者的类名.
	 * 
	 * 获取StackTrace有消耗，不要滥用
	 */
	public static String getCallerClass() {
		return getClassFromStackTrace(4);
	}

	/**
	 * 通过StackTrace，获得调用者的"类名.方法名()"
	 * 
	 * 获取StackTrace有消耗，不要滥用
	 */
	public static String getCallerMethod() {
		return getMethodFromStackTrace(4);
	}

	/**
	 * 通过StackTrace，获得当前方法的类名.
	 * 
	 * 获取StackTrace有消耗，不要滥用
	 */
	public static String getCurrentClass() {
		return getClassFromStackTrace(3);
	}

	/**
	 * 通过StackTrace，获得当前方法的"类名.方法名()"
	 * 
	 * 获取StackTrace有消耗，不要滥用
	 */
	public static String getCurrentMethod() {
		return getMethodFromStackTrace(3);
	}

	private static String getClassFromStackTrace(int level) {
		StackTraceElement[] stacktrace = Thread.currentThread().getStackTrace();
		if (stacktrace.length >= level) {
			StackTraceElement element = stacktrace[level - 1];
			return element.getClassName();
		} else {
			return StringUtils.EMPTY;
		}
	}

	private static String getMethodFromStackTrace(int level) {
		StackTraceElement[] stacktrace = Thread.currentThread().getStackTrace();
		if (stacktrace.length >= level) {
			StackTraceElement element = stacktrace[level - 1];
			return element.getClassName() + '.' + element.getMethodName() + "()";
		} else {
			return StringUtils.EMPTY;
		}
	}

}
