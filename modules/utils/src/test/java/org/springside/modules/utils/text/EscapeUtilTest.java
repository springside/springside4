package org.springside.modules.utils.text;

import static org.assertj.core.api.Assertions.*;

import org.junit.Test;

public class EscapeUtilTest {

	@Test
	public void urlEncode() {
		String input = "http://locahost/?q=中文&t=1";
		String result = EscapeUtil.urlEncode(input);
		System.out.println(result);

		assertThat(EscapeUtil.urlDecode(result)).isEqualTo(input);
	}

	@Test
	public void xmlEncode() {
		String input = "1>2";
		String result = EscapeUtil.escapeXml(input);
		assertThat(result).isEqualTo("1&gt;2");
		assertThat(EscapeUtil.unescapeXml(result)).isEqualTo(input);
	}

	@Test
	public void html() {
		String input = "1>2";
		String result = EscapeUtil.escapeHtml(input);
		assertThat(result).isEqualTo("1&gt;2");
		assertThat(EscapeUtil.unescapeHtml(result)).isEqualTo(input);
	}

}
