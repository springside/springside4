package org.springside.modules.utils.text;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;

import org.apache.commons.lang3.StringEscapeUtils;

/**
 * 转义工具集.
 * 
 * 1.JDK提供的URL 转义，转义后的URL可作为URL中的参数
 * 
 * 2.Commons-Lang的xml/html 转义
 * 
 * 比如 "bread" & "butter" 转化为 &quot;bread&quot; &amp; &quot;butter&quot;
 * 
 * @author calvin
 */
public class EscapeUtil {

	/**
	 * URL 编码, Encode默认为UTF-8.
	 * 
	 * 转义后的URL可作为URL中的参数
	 */
	public static String urlEncode(String part) {
		try {
			return URLEncoder.encode(part, Charsets.UTF_8_NAME);
		} catch (UnsupportedEncodingException ignored) {
			return null;
		}
	}

	/**
	 * URL 解码, Encode默认为UTF-8. 转义后的URL可作为URL中的参数
	 */
	public static String urlDecode(String part) {
		try {
			return URLDecoder.decode(part, Charsets.UTF_8_NAME);
		} catch (UnsupportedEncodingException e) {
			return null;
		}
	}

	/**
	 * Xml转码，将字符串转码为符合XML1.1格式的字符串.
	 * 
	 * 比如 "bread" & "butter" 转化为 &quot;bread&quot; &amp; &quot;butter&quot;
	 */
	public static String escapeXml(String xml) {
		return StringEscapeUtils.escapeXml11(xml);
	}

	/**
	 * Xml转码，XML格式的字符串解码为普通字符串.
	 * 
	 * 比如 &quot;bread&quot; &amp; &quot;butter&quot; 转化为"bread" & "butter"
	 */
	public static String unescapeXml(String xml) {
		return StringEscapeUtils.unescapeXml(xml);
	}

	/**
	 * Html转码，将字符串转码为符合HTML4格式的字符串.
	 * 
	 * 比如 "bread" & "butter" 转化为 &quot;bread&quot; &amp; &quot;butter&quot;
	 */
	public static String escapeHtml(String html) {
		return StringEscapeUtils.escapeHtml4(html);
	}

	/**
	 * Html解码，将HTML4格式的字符串转码解码为普通字符串.
	 * 
	 * 比如 &quot;bread&quot; &amp; &quot;butter&quot;转化为"bread" & "butter"
	 */
	public static String unescapeHtml(String html) {
		return StringEscapeUtils.unescapeHtml4(html);
	}
}
