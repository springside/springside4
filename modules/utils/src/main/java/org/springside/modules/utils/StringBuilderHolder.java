package org.springside.modules.utils;

/**
 * 参考BigDecimal, 可重用的StringBuilder, 节约StringBuilder内部的char[]
 * 
 * 参考下面的示例代码将其保存为ThreadLocal.
 * 
 * <pre>
 * private static final ThreadLocal<StringBuilderHelper> threadLocalStringBuilderHolder = new ThreadLocal<StringBuilderHelper>() {
 * 	&#64;Override
 * 	protected StringBuilderHelper initialValue() {
 * 		return new StringBuilderHelper(256);
 * 	}
 * };
 * 
 * StringBuilder sb = threadLocalStringBuilderHolder.get().resetAndGetStringBuilder();
 * 
 * </pre>
 * 
 * @author calvin
 *
 */
public class StringBuilderHolder {

	private final StringBuilder sb;

	public StringBuilderHolder(int capacity) {
		sb = new StringBuilder(capacity);
	}

	/**
	 * 重置StringBuilder内部的writerIndex, 而char[]保留不动.
	 */
	public StringBuilder resetAndGetStringBuilder() {
		sb.setLength(0);
		return sb;
	}
}