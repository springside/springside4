package org.springside.modules.utils.text;

import static org.assertj.core.api.Assertions.*;

import org.junit.Test;

public class WildcardMatcherTest {

	@Test
	public void matchString() {
		assertThat(WildcardMatcher.match("abc", "*")).isTrue();
		assertThat(WildcardMatcher.match("abc", "*c")).isTrue();
		assertThat(WildcardMatcher.match("abc", "a*")).isTrue();
		assertThat(WildcardMatcher.match("abc", "a*c")).isTrue();

		assertThat(WildcardMatcher.match("abc", "a?c")).isTrue();
		assertThat(WildcardMatcher.match("abcd", "a?c?")).isTrue();
		assertThat(WildcardMatcher.match("abcd", "a??d")).isTrue();

		assertThat(WildcardMatcher.match("abcde", "a*d?")).isTrue();

		assertThat(WildcardMatcher.match("abcde", "a*d")).isFalse();
		assertThat(WildcardMatcher.match("abcde", "a*x")).isFalse();
		assertThat(WildcardMatcher.match("abcde", "a*df")).isFalse();

		assertThat(WildcardMatcher.match("abcde", "?abcd")).isFalse();

		assertThat(WildcardMatcher.match("ab\\\\*cde", "ab\\\\*c*")).isTrue();
		assertThat(WildcardMatcher.match("ab\\\\*cde", "ab\\\\*?de")).isTrue();
	}

	@Test
	public void matchPath() {
		assertThat(WildcardMatcher.matchPath("/a/b/dd", "**")).isTrue();
		
		assertThat(WildcardMatcher.matchPath("/a/b/dd", "**/dd")).isTrue();
		assertThat(WildcardMatcher.matchPath("/a/b/c/dd", "/a/**/dd")).isTrue();
		assertThat(WildcardMatcher.matchPath("/a/b/dd", "/a/*/dd")).isTrue();
		assertThat(WildcardMatcher.matchPath("/a/b/dd", "/a/*/d?")).isTrue();
		assertThat(WildcardMatcher.matchPath("/a/b/ddxxa", "/a/*/dd*")).isTrue();
		assertThat(WildcardMatcher.matchPath("/a/b/ddxxa", "/a/?/dd*")).isTrue();
		assertThat(WildcardMatcher.matchPath("a/b/ddxxa", "a/?/dd*")).isTrue();
		assertThat(WildcardMatcher.matchPath("a/b/dd", "**/dd")).isTrue();
		
		
		assertThat(WildcardMatcher.matchPath("/a/b/c/dd", "/a/*/dd")).isFalse();
	}
}
