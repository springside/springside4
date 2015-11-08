package org.springside.examples.bootapi.api;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springside.examples.bootapi.api.support.RestException;
import org.springside.examples.bootapi.service.AccountService;
import org.springside.modules.web.MediaTypes;

// Spring Restful MVC Controller的标识, 直接输出内容，不调用template引擎.
@RestController
public class AccountEndPoint {

	private static Logger logger = LoggerFactory.getLogger(AccountEndPoint.class);

	@Autowired
	private AccountService accountServcie;

	@RequestMapping(value = "/api/accounts/login", produces = MediaTypes.TEXT_PLAIN_UTF_8)
	public String login(@RequestParam("email") String email, @RequestParam("password") String password) {

		if (StringUtils.isEmpty(email) || StringUtils.isEmpty(password)) {
			throw new RestException("User or password empty", HttpStatus.BAD_REQUEST);
		}

		return accountServcie.login(email, password);
	}

	@RequestMapping(value = "/api/accounts/logout")
	public void logout(@RequestParam(value = "token", required = false) String token) {
		accountServcie.logout(token);
	}

	@RequestMapping(value = "/api/accounts/register")
	public void register(@RequestParam("email") String email, @RequestParam("name") String name,
			@RequestParam("password") String password) {

		if (StringUtils.isEmpty(email) || StringUtils.isEmpty(name) || StringUtils.isEmpty(password)) {
			throw new RestException("User or name or password empty", HttpStatus.BAD_REQUEST);
		}

		accountServcie.register(email, name, password);
	}
}
