package org.springside.modules.utils.text;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;

import org.apache.commons.lang3.StringEscapeUtils;
import org.springside.modules.utils.base.Platforms;

/**
 * 转义工具集.
 * 
 * 1.Commons-Lang的json/xml/html 转义
 * 
 * 2.JDK提供的URL 转义
 * 
 * 比如 "bread" & "butter" 转化为 &quot;bread&quot; &amp; &quot;butter&quot;
 * 
 * @author calvin
 */
public class EscapeUtil {
	/**
	 * Json转码，将字符串转码为符合JSON格式的字符串.
	 * 
	 * 比如 "Stop!" 转化为\"Stop!\"
	 */
	public static String escapeJson(String json) {
		return StringEscapeUtils.escapeJson(json);
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

	/**
	 * URL 编码, Encode默认为UTF-8.
	 */
	public static String urlEncode(String part) {
		try {
			return URLEncoder.encode(part, Platforms.UTF_8);
		} catch (UnsupportedEncodingException ignored) {
			return null;
		}
	}

	/**
	 * URL 解码, Encode默认为UTF-8.
	 */
	public static String urlDecode(String part) {
		try {
			return URLDecoder.decode(part, Platforms.UTF_8);
		} catch (UnsupportedEncodingException e) {
			return null;
		}
	}
}
