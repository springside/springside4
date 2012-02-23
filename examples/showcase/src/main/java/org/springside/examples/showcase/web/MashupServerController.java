package org.springside.examples.showcase.web;

import java.util.Collections;
import java.util.Map;

import org.springframework.web.bind.annotation.RequestMapping;

/**
 * JSONP Mashup 服务端.
 * 
 * @author calvin
 */
@RequestMapping(value = "/mashup")
public class MashupServerController {

	public String execute() {
		//获取JQuery客户端动态生成的callback函数名.
		//String callbackName = Struts2Utils.getParameter("callback");

		//设置需要被格式化为JSON字符串的内容.
		Map<String, String> map = Collections.singletonMap("html", "<p>Hello World!</p>");

		//渲染返回结果.
		//	Struts2Utils.renderJsonp(callbackName, map);
		return null;
	}
}
