package org.springside.examples.showcase.demos.hystrix.web;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class HystrixController {

	public static String status = "normal";

	@RequestMapping(value = "/story/hystrix/{status}")
	public String updateStatus(@PathVariable("status") String status) {
		HystrixController.status = status;
		return "story/hystrix";
	}
}
