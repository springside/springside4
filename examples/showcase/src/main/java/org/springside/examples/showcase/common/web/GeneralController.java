package org.springside.examples.showcase.common.web;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class GeneralController {

	@RequestMapping(value = "/index")
	public String index() {
		return "index";
	}

	@RequestMapping(value = "/story/{page}")
	public String story(@PathVariable("page") String page) {
		return "story/" + page;
	}

	@RequestMapping(value = "/web/mashup-client")
	public String mashupClient() {
		return "web/mashup-client";
	}
}
