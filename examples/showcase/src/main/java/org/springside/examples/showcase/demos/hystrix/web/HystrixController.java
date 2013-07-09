package org.springside.examples.showcase.demos.hystrix.web;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class HystrixController {

	public static String status = "normal";
	public static String fallback = "exception";

	@RequestMapping(value = "/story/hystrix/status/{status}")
	public String updateStatus(@PathVariable("status") String newStatus) {
		HystrixController.status = newStatus;
		return "redirect:/story/hystrix";
	}

	@RequestMapping(value = "/story/hystrix/fallback/{fallback}")
	public String updateFallback(@PathVariable("fallback") String newFallback) {
		HystrixController.fallback = newFallback;
		return "redirect:/story/hystrix";
	}
}
