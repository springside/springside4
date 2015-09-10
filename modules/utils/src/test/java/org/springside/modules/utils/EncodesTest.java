/*******************************************************************************
 * Copyright (c) 2005, 2014 springside.github.io
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 *******************************************************************************/
package org.springside.modules.utils;

import static org.assertj.core.api.Assertions.*;

import org.junit.Test;

public class EncodesTest {

	@Test
	public void hexEncode() {
		String input = "haha,i am a very long message";
		String result = Encodes.encodeHex(input.getBytes());
		assertThat(new String(Encodes.decodeHex(result))).isEqualTo(input);
	}

	@Test
	public void base64Encode() {
		String input = "haha,i am a very long message";
		String result = Encodes.encodeBase64(input.getBytes());
		assertThat(new String(Encodes.decodeBase64(result))).isEqualTo(input);
	}

	@Test
	public void base64UrlSafeEncode() {
		String input = "haha,i am a very long message";
		String result = Encodes.encodeUrlSafeBase64(input.getBytes());
		assertThat(new String(Encodes.decodeBase64(result))).isEqualTo(input);
	}

	@Test
	public void urlEncode() {
		String input = "http://locahost/?q=中文&t=1";
		String result = Encodes.urlEncode(input);
		System.out.println(result);

		assertThat(Encodes.urlDecode(result)).isEqualTo(input);
	}

	@Test
	public void xmlEncode() {
		String input = "1>2";
		String result = Encodes.escapeXml(input);
		assertThat(result).isEqualTo("1&gt;2");
		assertThat(Encodes.unescapeXml(result)).isEqualTo(input);
	}

	@Test
	public void html() {
		String input = "1>2";
		String result = Encodes.escapeHtml(input);
		assertThat(result).isEqualTo("1&gt;2");
		assertThat(Encodes.unescapeHtml(result)).isEqualTo(input);
	}
}
