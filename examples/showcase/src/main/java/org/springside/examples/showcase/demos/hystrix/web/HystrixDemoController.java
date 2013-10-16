package org.springside.examples.showcase.demos.hystrix.web;

import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springside.examples.showcase.demos.hystrix.dependency.DependencyResourceController;
import org.springside.examples.showcase.demos.hystrix.service.UserService;
import org.springside.examples.showcase.webservice.rest.RestException;
import org.springside.examples.showcase.webservice.rest.UserDTO;

import com.google.common.collect.Maps;

@Controller
public class HystrixDemoController {

	private static Logger logger = LoggerFactory.getLogger(HystrixDemoController.class);

	private static Map<String, String> allStatus = Maps.newHashMap();

	static {
		allStatus.put("normal", "正常");
		allStatus.put("timeout", "超时");
		allStatus.put("server-error", "服务器错误");
		allStatus.put("bad-request", "请求错误");
	}

	@Autowired
	private UserService userService;

	@RequestMapping(value = "/hystrix", method = RequestMethod.GET)
	public String index(Model model) {

		model.addAttribute("allStatus", allStatus);
		model.addAttribute("statusHolder", new StatusHolder(DependencyResourceController.status));
		return "story/hystrix";
	}

	@RequestMapping(value = "/hystrix/user/{id}", method = RequestMethod.GET)
	@ResponseBody
	public UserDTO getUser(@PathVariable("id") Long id) {
		try {
			return userService.getUser(id);
		} catch (Exception e) {
			// 化简异常处理，正式项目应区分错误类型.
			logger.error(e.getMessage(), e);
			throw new RestException();
		}
	}

	/**
	 * 设定Resource的状态.
	 */
	@RequestMapping(value = "/hystrix/status")
	public String updateStatus(@RequestParam("status") String newStatus) {
		DependencyResourceController.status = newStatus;
		return "redirect:/hystrix";
	}

	public static class StatusHolder {
		public String status;

		public StatusHolder(String status) {
			this.status = status;
		}

		public String getStatus() {
			return status;
		}
	}
}
