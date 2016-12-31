package org.springside.modules.utils.text;

/**
 * 参考BigDecimal, threadLocal重用的StringBuilder, 节约StringBuilder内部的char[]
 * 
 * 考虑从ThreadLocal中国年hu
 * 
 * <pre>
 * private static final ThreadLocal<StringBuilderHelper> threadLocalStringBuilderHolder = new ThreadLocal<StringBuilderHelper>() {
 * 	&#64;Override
 * 	protected StringBuilderHelper initialValue() {
 * 		return new StringBuilderHelper(256);
 * 	}
 * };
 * 
 * StringBuilder sb = threadLocalStringBuilderHolder.get().getStringBuilder();
 * 
 * </pre>
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
	public StringBuilder getStringBuilder() {
		sb.setLength(0);
		return sb;
	}
}