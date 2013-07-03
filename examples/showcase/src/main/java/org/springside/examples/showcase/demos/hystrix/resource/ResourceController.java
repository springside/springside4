package org.springside.examples.showcase.demos.hystrix.resource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springside.examples.showcase.demos.hystrix.web.HystrixController;
import org.springside.examples.showcase.entity.User;
import org.springside.examples.showcase.service.AccountEffectiveService;
import org.springside.examples.showcase.webservice.rest.UserDTO;
import org.springside.modules.mapper.BeanMapper;
import org.springside.modules.utils.Threads;

/**
 * 被Service所依赖的Resource.
 */
@Controller
public class ResourceController {
	public static final int TIMEOUT = 20000;

	@Autowired
	private AccountEffectiveService accountService;

	/**
	 * 根据控制器中的状态而演示不同的行为，如正常返回，20秒无返回或直接报错
	 */
	@RequestMapping(value = "/hystrix/resource/{id}", method = RequestMethod.GET)
	@ResponseBody
	public UserDTO getUser(@PathVariable("id") Long id) {
		if ("normal".equals(HystrixController.status)) {
			return handleRequest(id);
		}

		if ("timeout".equals(HystrixController.status)) {
			Threads.sleep(TIMEOUT);
			return handleRequest(id);
		}

		if ("fail".equals(HystrixController.status)) {
			throw new RuntimeException("Server Exception");
		}

		return null;
	}

	private UserDTO handleRequest(Long id) {
		User user = accountService.getUser(id);
		UserDTO dto = BeanMapper.map(user, UserDTO.class);
		dto.setTeamId(user.getTeam().getId());
		return dto;
	}
}
