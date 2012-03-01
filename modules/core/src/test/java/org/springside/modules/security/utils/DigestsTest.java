package org.springside.modules.security.utils;

import java.io.IOException;

import org.junit.Test;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springside.modules.security.utils.Digests;

public class DigestsTest {

	@Test
	public void digestString() {
		String input = "foo message";

		System.out.println("sha1 in hex result              :" + Digests.sha1Hex(input));
		System.out.println("sha1 in base64 result           :" + Digests.sha1Base64(input));
		System.out.println("sha1 in base64 url result       :" + Digests.sha1Base64UrlSafe(input));
	}

	@Test
	public void digestFile() throws IOException {
		Resource resource = new ClassPathResource("/log4j.properties");

		System.out.println("md5: " + Digests.md5Hex(resource.getInputStream()));
		System.out.println("sha1:" + Digests.sha1Hex(resource.getInputStream()));
	}
}
