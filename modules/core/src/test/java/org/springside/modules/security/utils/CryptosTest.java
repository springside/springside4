package org.springside.modules.security.utils;

import static org.junit.Assert.*;

import org.junit.Test;
import org.springside.modules.security.utils.Cryptos;
import org.springside.modules.utils.Encodes;

public class CryptosTest {
	@Test
	public void mac() {
		String input = "foo message";

		//key可为任意字符串
		//byte[] key = "a foo key".getBytes();
		byte[] key = Cryptos.generateMacSha1Key();
		assertEquals(20, key.length);

		String macHexResult = Cryptos.hmacSha1ToHex(input, key);
		String macBase64Result = Cryptos.hmacSha1ToBase64(input, key);
		String macBase64UrlResult = Cryptos.hmacSha1ToBase64UrlSafe(input, key);
		System.out.println("hmac-sha1 key in hex            :" + Encodes.encodeHex(key));
		System.out.println("hmac-sha1 in hex result         :" + macHexResult);
		System.out.print("hmac-sha1 in base64 result      :" + macBase64Result);
		System.out.println("hmac-sha1 in base64 url result  :" + macBase64UrlResult);

		assertTrue(Cryptos.isHexMacValid(macHexResult, input, key));
		assertTrue(Cryptos.isBase64MacValid(macBase64Result, input, key));
	}

	@Test
	public void des() {
		byte[] key = Cryptos.generateDesKey();
		assertEquals(8, key.length);
		String input = "foo message";

		String encryptHexResult = Cryptos.desEncryptToHex(input, key);
		String descryptHexResult = Cryptos.desDecryptFromHex(encryptHexResult, key);

		String encryptBase64Result = Cryptos.desEncryptToBase64(input, key);
		String descryptBase64Result = Cryptos.desDecryptFromBase64(encryptBase64Result, key);

		System.out.println("des key in hex                  :" + Encodes.encodeHex(key));
		System.out.println("des encrypt in hex result       :" + encryptHexResult);
		System.out.print("des encrypt in base64 result    :" + encryptBase64Result);

		assertEquals(input, descryptHexResult);
		assertEquals(input, descryptBase64Result);
	}

	@Test
	public void aes() {
		byte[] key = Cryptos.generateAesKey();
		assertEquals(16, key.length);
		String input = "foo message";

		String encryptHexResult = Cryptos.aesEncryptToHex(input, key);
		String descryptHexResult = Cryptos.aesDecryptFromHex(encryptHexResult, key);

		String encryptBase64Result = Cryptos.aesEncryptToBase64(input, key);
		String descryptBase64Result = Cryptos.aesDecryptFromBase64(encryptBase64Result, key);

		System.out.println("aes key in hex                  :" + Encodes.encodeHex(key));
		System.out.println("aes encrypt in hex result       :" + encryptHexResult);
		System.out.print("aes encrypt in base64 result    :" + encryptBase64Result);

		assertEquals(input, descryptHexResult);
		assertEquals(input, descryptBase64Result);
	}
}
