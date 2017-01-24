package org.springside.modules.utils.text;

import java.nio.charset.Charset;

/**
 * JDK7可直接使用java.nio.charset.StandardCharsets.
 * 
 * 也可以直接使用Guava Charsets.
 * 
 * 尽量使用Charsets.UTF8而不是"UTF-8"，减少JDK里的Charset查找消耗.
 * 
 * @author calvin
 */
public class Charsets {
	public static final String UTF_8_NAME = "UTF-8";

	public static final Charset UTF_8 = Charset.forName("UTF-8");
	public static final Charset US_ASCII = Charset.forName("US-ASCII");
	public static final Charset ISO_8859_1 = Charset.forName("ISO-8859-1");
}
