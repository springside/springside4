package org.springside.examples.bootapi.service;

import java.util.concurrent.TimeUnit;

import javax.annotation.PostConstruct;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.actuate.metrics.CounterService;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springside.examples.bootapi.api.support.RestException;
import org.springside.examples.bootapi.domain.Account;
import org.springside.examples.bootapi.repository.AccountDao;
import org.springside.modules.utils.Digests;
import org.springside.modules.utils.Encodes;
import org.springside.modules.utils.Ids;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;

// Spring Bean的标识.
@Service
public class AccountService {

	private static Logger logger = LoggerFactory.getLogger(AccountService.class);

	@Autowired
	private AccountDao accountDao;

	// 注入配置值
	@Value("${app.loginTimeoutSecs}")
	private int loginTimeoutSecs;

	// codehale metrics
	@Autowired
	private CounterService counterService;

	// guava cache
	private Cache<String, Account> loginUsers;

	@PostConstruct
	public void init() {
		loginUsers = CacheBuilder.newBuilder().maximumSize(1000).expireAfterAccess(loginTimeoutSecs, TimeUnit.SECONDS)
				.build();
	}

	@Transactional(readOnly = true)
	public String login(String email, String password) {
		Account account = accountDao.findByEmail(email);

		if (account == null) {
			throw new RestException("User not exist", HttpStatus.UNAUTHORIZED);
		}

		if (!account.hashPassword.equals(hashPassword(password))) {
			throw new RestException("Password wrong", HttpStatus.UNAUTHORIZED);
		}

		String token = Ids.uuid2();
		loginUsers.put(token, account);
		counterService.increment("loginUser");
		return token;
	}

	public void logout(String token) {
		Account account = loginUsers.getIfPresent(token);
		if (account == null) {
			logger.warn("logout an alreay logout token:" + token);
		} else {
			loginUsers.invalidate(token);
			counterService.decrement("loginUser");
		}
	}

	public Account getLoginUser(String token) {

		if (token == null) {
			logger.error("no token");
			throw new RestException("User doesn't login", HttpStatus.UNAUTHORIZED);
		}

		Account account = loginUsers.getIfPresent(token);

		if (account == null) {
			throw new RestException("User doesn't login", HttpStatus.UNAUTHORIZED);
		}

		return account;
	}

	@Transactional
	public void register(String email, String name, String password) {
		Account account = new Account();
		account.email = email;
		account.name = name;
		account.hashPassword = hashPassword(password);
		accountDao.save(account);
	}

	protected static String hashPassword(String password) {
		return Encodes.encodeBase64(Digests.sha1(password));
	}
}
