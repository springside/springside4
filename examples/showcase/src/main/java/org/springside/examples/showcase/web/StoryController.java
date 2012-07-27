package org.springside.examples.showcase.web;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class StoryController {

	@RequestMapping(value = "/story/{page}")
	public String story(@PathVariable("page") String page) {
		return "story/" + page;
	}
}
