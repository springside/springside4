package org.springside.examples.bootapi.service;

import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springside.examples.bootapi.api.support.RestException;
import org.springside.examples.bootapi.domain.Account;
import org.springside.examples.bootapi.repository.AccountDao;
import org.springside.modules.utils.Digests;
import org.springside.modules.utils.Encodes;
import org.springside.modules.utils.Identities;

import com.google.common.collect.Maps;

// Spring Bean的标识.
@Service
public class AccountService {

	private static Logger logger = LoggerFactory.getLogger(AccountService.class);
	private Map<String, Account> loginUsers = Maps.newConcurrentMap();

	@Autowired
	private AccountDao accountDao;

	@Transactional(readOnly = true)
	public String login(String email, String password) {
		Account account = accountDao.findByEmail(email);

		if (account == null) {
			throw new RestException("User not exist", HttpStatus.UNAUTHORIZED);
		}

		if (!account.hashPassword.equals(hashPassword(password))) {
			throw new RestException("Password wrong", HttpStatus.UNAUTHORIZED);
		}

		String token = Identities.uuid2();
		loginUsers.put(token, account);
		return token;
	}

	public void logout(String token) {
		Account account = loginUsers.remove(token);
		if (account == null) {
			logger.error("logout a alreay logout token:" + token);
		}
	}

	public Account getLoginUser(String token) {

		if (token == null) {
			throw new RestException("User doesn't login", HttpStatus.UNAUTHORIZED);
		}

		Account account = loginUsers.get(token);

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

	public static String hashPassword(String password) {
		return Encodes.encodeBase64(Digests.sha1(password));
	}

	public static void main(String[] args) throws Exception {
		System.out.println("hashPassword:" + hashPassword("springside"));
	}
}
