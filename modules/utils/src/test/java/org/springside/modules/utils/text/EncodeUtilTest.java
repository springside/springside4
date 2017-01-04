/*******************************************************************************
 * Copyright (c) 2005, 2014 springside.github.io
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 *******************************************************************************/
package org.springside.modules.utils.text;

import static org.assertj.core.api.Assertions.*;

import org.junit.Test;

public class EncodeUtilTest {

	@Test
	public void hexEncode() {
		String input = "haha,i am a very long message";
		String result = EncodeUtil.encodeHex(input.getBytes());
		assertThat(new String(EncodeUtil.decodeHex(result))).isEqualTo(input);
	}

	@Test
	public void base64Encode() {
		String input = "haha,i am a very long message";
		String result = EncodeUtil.encodeBase64(input.getBytes());
		assertThat(new String(EncodeUtil.decodeBase64(result))).isEqualTo(input);
	}

	@Test
	public void base64UrlSafeEncode() {
		String input = "haha,i am a very long message";
		String result = EncodeUtil.encodeUrlSafeBase64(input.getBytes());
		assertThat(new String(EncodeUtil.decodeBase64(result))).isEqualTo(input);
	}
}
