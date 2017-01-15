package org.springside.modules.utils.text;

/**
 * 参考Netty的InternalThreadLocalMap 与 BigDecimal, 放在threadLocal中重用的StringBuilder, 节约StringBuilder内部的char[]
 * 
 * 参考文章：《StringBuilder在高性能场景下的正确用法》http://calvin1978.blogcn.com/articles/stringbuilder.html
 * 
 * 不过仅在String对象较大时才有明显效果，否则抵不上访问ThreadLocal的消耗.
 * 
 * 在Netty环境中，使用Netty提供的基于FastThreadLocal的版本。
 *
 */
public class StringBuilderHolder {

	private static ThreadLocal<StringBuilder> stringBuilder = new ThreadLocal<StringBuilder>() {
		@Override
		protected StringBuilder initialValue() {
			return new StringBuilder(1024);
		}
	};

	/**
	 * 重置StringBuilder内部的writerIndex, 而char[]保留不动.
	 */
	public static StringBuilder get() {
		StringBuilder sb = stringBuilder.get();
		sb.setLength(0);
		return sb;
	}
}