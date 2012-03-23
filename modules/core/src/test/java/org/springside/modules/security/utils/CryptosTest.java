package org.springside.modules.security.utils;

import static org.junit.Assert.*;

import org.junit.Test;
import org.springside.modules.utils.Encodes;

public class CryptosTest {
	@Test
	public void mac() {
		String input = "foo message";

		//key可为任意字符串
		//byte[] key = "a foo key".getBytes();
		byte[] key = Cryptos.generateHmacSha1Key();
		assertEquals(20, key.length);

		byte[] macResult = Cryptos.hmacSha1(input.getBytes(), key);
		System.out.println("hmac-sha1 key in hex      :" + Encodes.encodeHex(key));
		System.out.println("hmac-sha1 in hex result   :" + Encodes.encodeHex(macResult));

		assertTrue(Cryptos.isMacValid(macResult, input.getBytes(), key));
	}

	@Test
	public void aes() {
		byte[] key = Cryptos.generateAesKey();
		assertEquals(16, key.length);
		String input = "foo message";

		byte[] encryptResult = Cryptos.aesEncrypt(input.getBytes(), key);
		String descryptResult = Cryptos.aesDecrypt(encryptResult, key);

		System.out.println("aes key in hex            :" + Encodes.encodeHex(key));
		System.out.println("aes encrypt in hex result :" + Encodes.encodeHex(encryptResult));
		assertEquals(input, descryptResult);
	}

	@Test
	public void aesWithIV() {
		byte[] key = Cryptos.generateAesKey();
		byte[] iv = Cryptos.generateIV();
		assertEquals(16, key.length);
		assertEquals(16, iv.length);
		String input = "foo message";

		byte[] encryptResult = Cryptos.aesEncrypt(input.getBytes(), key, iv);
		String descryptResult = Cryptos.aesDecrypt(encryptResult, key, iv);

		System.out.println("aes key in hex            :" + Encodes.encodeHex(key));
		System.out.println("iv in hex                 :" + Encodes.encodeHex(iv));
		System.out.println("aes encrypt in hex result :" + Encodes.encodeHex(encryptResult));
		assertEquals(input, descryptResult);
	}
}
