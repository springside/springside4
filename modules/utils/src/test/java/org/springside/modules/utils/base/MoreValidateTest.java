package org.springside.modules.utils.base;

import static org.assertj.core.api.Assertions.*;

import org.junit.Test;

public class MoreValidateTest {

	@Test
	public void test() {
		// int
		int a = MoreValidate.nonNegative("x", 0);
		assertThat(a).isEqualTo(0);
		a = MoreValidate.nonNegative("x", 1);
		assertThat(a).isEqualTo(1);

		a = MoreValidate.positive("x", 1);
		assertThat(a).isEqualTo(1);

		// Integer
		Integer c = MoreValidate.nonNegative("x", Integer.valueOf(0));
		assertThat(c).isEqualTo(0);
		c = MoreValidate.nonNegative("x", Integer.valueOf(21));
		assertThat(c).isEqualTo(21);

		c = MoreValidate.positive("x", Integer.valueOf(1));
		assertThat(c).isEqualTo(1);

		// long
		long b = MoreValidate.nonNegative("x", 0);
		assertThat(b).isEqualTo(0);

		b = MoreValidate.positive("x", 1);
		assertThat(b).isEqualTo(1);

		b = MoreValidate.nonNegative("x", 11);
		assertThat(b).isEqualTo(11);

		// Long
		Long d = MoreValidate.nonNegative("x", Long.valueOf(0));
		assertThat(d).isEqualTo(0);

		d = MoreValidate.positive("x", Long.valueOf(1));
		assertThat(d).isEqualTo(1);

		d = MoreValidate.nonNegative("x", Long.valueOf(11));
		assertThat(d).isEqualTo(11);

		try {
			MoreValidate.nonNegative("x", -1);
			fail("fail");
		} catch (Throwable t) {
			assertThat(t).isInstanceOf(IllegalArgumentException.class);
		}

		try {
			MoreValidate.nonNegative("x", -1l);
			fail("fail");
		} catch (Throwable t) {
			assertThat(t).isInstanceOf(IllegalArgumentException.class);
		}

		try {
			MoreValidate.nonNegative("x", Long.valueOf(-1));
			fail("fail");
		} catch (Throwable t) {
			assertThat(t).isInstanceOf(IllegalArgumentException.class);
		}

		try {
			MoreValidate.nonNegative("x", Integer.valueOf(-1));
			fail("fail");
		} catch (Throwable t) {
			assertThat(t).isInstanceOf(IllegalArgumentException.class);
		}

		//////

		try {
			MoreValidate.positive("x", -1);
			fail("fail");
		} catch (Throwable t) {
			assertThat(t).isInstanceOf(IllegalArgumentException.class);
		}

		try {
			MoreValidate.positive("x", 0);
			fail("fail");
		} catch (Throwable t) {
			assertThat(t).isInstanceOf(IllegalArgumentException.class);
		}

		try {
			MoreValidate.positive("x", -1l);
			fail("fail");
		} catch (Throwable t) {
			assertThat(t).isInstanceOf(IllegalArgumentException.class);
		}
		try {
			MoreValidate.positive("x", 0);
			fail("fail");
		} catch (Throwable t) {
			assertThat(t).isInstanceOf(IllegalArgumentException.class);
		}

		try {
			MoreValidate.positive("x", Integer.valueOf(-1));
			fail("fail");
		} catch (Throwable t) {
			assertThat(t).isInstanceOf(IllegalArgumentException.class);
		}

		try {
			MoreValidate.positive("x", Integer.valueOf(0));
			fail("fail");
		} catch (Throwable t) {
			assertThat(t).isInstanceOf(IllegalArgumentException.class);
		}

		try {
			MoreValidate.positive("x", Long.valueOf(-1));
			fail("fail");
		} catch (Throwable t) {
			assertThat(t).isInstanceOf(IllegalArgumentException.class);
		}

		try {
			MoreValidate.positive("x", Long.valueOf(0));
			fail("fail");
		} catch (Throwable t) {
			assertThat(t).isInstanceOf(IllegalArgumentException.class);
		}

	}

}
