package org.springside.examples.showcase.utilities.validate;

import static org.junit.Assert.*;

import java.util.List;

import org.apache.commons.lang3.Validate;
import org.junit.Assert;
import org.junit.Test;

import com.google.common.collect.Lists;

/**
 * 演示用Apache Commons3的Validate，在代码中进行防御性校验.
 * 
 * @author calvin
 *
 */
public class ValidateDemo {

	@Test
	public void asserts() {

		//not null Object
		try {
			String parameter = "abc";

			//not null后返回值到等式左边
			String result = Validate.notNull(parameter);
			assertEquals("abc", result);

			//检验not null，用默认出错信息.
			Validate.notNull(null);
			Assert.fail();
		} catch (NullPointerException e) {
			assertEquals("The validated object is null", e.getMessage());
		}

		//notBlank String
		try {
			String parameter = "abc";
			String result = Validate.notBlank(parameter);
			assertEquals("abc", result);

			//检验not null，用自定义出错信息.
			Validate.notBlank("", "The name must not be blank");
			Assert.fail();

		} catch (IllegalArgumentException e) {
			assertEquals("The name must not be blank", e.getMessage());
		}

		//notEmpty Collection
		try {
			List parameter = Lists.newArrayList();
			List result = Validate.notEmpty(parameter);
			Assert.fail();
		} catch (IllegalArgumentException e) {
			assertEquals("The validated collection is empty", e.getMessage());
		}

		//is true
		try {
			//出錯信息可格式化參數
			Validate.isTrue(1 == 3, "Message %s", "foo");
			Assert.fail();
		} catch (IllegalArgumentException e) {
			assertEquals("Message foo", e.getMessage());
		}
	}
}
