package org.springside.modules.utils.text;

import static org.assertj.core.api.Assertions.*;

import org.junit.Test;

public class EscapesTest {

	@Test
	public void urlEncode() {
		String input = "http://locahost/?q=中文&t=1";
		String result = Escapes.urlEncode(input);
		System.out.println(result);

		assertThat(Escapes.urlDecode(result)).isEqualTo(input);
	}

	@Test
	public void xmlEncode() {
		String input = "1>2";
		String result = Escapes.escapeXml(input);
		assertThat(result).isEqualTo("1&gt;2");
		assertThat(Escapes.unescapeXml(result)).isEqualTo(input);
	}

	@Test
	public void html() {
		String input = "1>2";
		String result = Escapes.escapeHtml(input);
		assertThat(result).isEqualTo("1&gt;2");
		assertThat(Escapes.unescapeHtml(result)).isEqualTo(input);
	}

}
