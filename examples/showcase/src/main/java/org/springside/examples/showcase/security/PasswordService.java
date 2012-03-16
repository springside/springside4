package org.springside.examples.showcase.security;

import org.apache.shiro.authc.credential.HashedCredentialsMatcher;
import org.springside.modules.security.utils.Digests;
import org.springside.modules.utils.Encodes;

public class PasswordService {

	private static final int INTERATIONS = 1024;
	private static final int SALT_SIZE = 8;
	private static final String ALGORITHM = "SHA-1";

	public HashPassword encrypt(String plainText) {
		HashPassword result = new HashPassword();
		byte[] salt = Digests.generateSalt(SALT_SIZE);
		result.salt = Encodes.encodeHex(salt);

		byte[] hashPassword = Digests.sha1(plainText, salt, INTERATIONS);
		result.password = Encodes.encodeHex(hashPassword);
		return result;

	}

	public HashedCredentialsMatcher getCredentialsMatcher() {
		HashedCredentialsMatcher matcher = new HashedCredentialsMatcher(ALGORITHM);
		matcher.setHashIterations(INTERATIONS);
		return matcher;
	}

	public static class HashPassword {
		public String salt;
		public String password;
	}
}
