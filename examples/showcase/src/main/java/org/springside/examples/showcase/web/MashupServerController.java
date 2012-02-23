package org.springside.examples.showcase.web;

import java.util.Collections;
import java.util.Map;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springside.modules.mapper.JsonMapper;

/**
 * JSONP Mashup 服务端.
 * 
 * @author calvin
 */
@Controller
public class MashupServerController {

	private JsonMapper mapper = JsonMapper.buildNormalMapper();

	@RequestMapping("/web/mashup")
	@ResponseBody
	public String execute(@RequestParam("callback") String callbackName) {

		//设置需要被格式化为JSON字符串的内容.
		Map<String, String> map = Collections.singletonMap("html", "<p>Hello World!</p>");

		//渲染返回结果.
		//	Struts2Utils.renderJsonp(callbackName, map);
		return mapper.toJsonP(callbackName, map);
	}
}
