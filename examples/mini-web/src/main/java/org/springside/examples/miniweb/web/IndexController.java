package org.springside.examples.miniweb.web;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class IndexController {

	@RequestMapping(value = "/")
	public String index() {
		return "redirect:/task";
	}

	@RequestMapping(value = "/healthcheck", produces = "plain/text")
	public @ResponseBody
	String healthCheck() {
		return "ok";
	}
}
