package org.springside.modules.utils;

import static org.junit.Assert.*;

import java.lang.reflect.InvocationTargetException;

import org.junit.Test;

public class ReflectionsTest {

	@Test
	public void getAndSetFieldValue() {
		TestBean bean = new TestBean();
		//无需getter函数, 直接读取privateField
		assertEquals(1, Reflections.getFieldValue(bean, "privateField"));
		//绕过将publicField+1的getter函数,直接读取publicField的原始值
		assertEquals(1, Reflections.getFieldValue(bean, "publicField"));

		bean = new TestBean();
		//无需setter函数, 直接设置privateField
		Reflections.setFieldValue(bean, "privateField", 2);
		assertEquals(2, bean.inspectPrivateField());

		//绕过将publicField+1的setter函数,直接设置publicField的原始值
		Reflections.setFieldValue(bean, "publicField", 2);

		assertEquals(2, bean.inspectPublicField());

		try {
			Reflections.getFieldValue(bean, "notExist");
			fail("should throw exception here");
		} catch (IllegalArgumentException e) {

		}

		try {
			Reflections.setFieldValue(bean, "notExist", 2);
			fail("should throw exception here");
		} catch (IllegalArgumentException e) {

		}

	}

	@Test
	public void invokeGetterAndSetter() {
		TestBean bean = new TestBean();
		assertEquals(bean.inspectPublicField() + 1, Reflections.invokeGetter(bean, "publicField"));

		bean = new TestBean();
		//通过setter的函数将+1
		Reflections.invokeSetter(bean, "publicField", 10);
		assertEquals(10 + 1, bean.inspectPublicField());
	}

	@Test
	public void invokeMethod() {
		TestBean bean = new TestBean();
		//使用函数名+参数类型的匹配
		assertEquals("hello calvin", Reflections.invokeMethod(bean, "privateMethod", new Class[] { String.class },
				new Object[] { "calvin" }));

		//仅匹配函数名
		assertEquals("hello calvin", Reflections.invokeMethodByName(bean, "privateMethod", new Object[] { "calvin" }));

		//函数名错
		try {
			Reflections.invokeMethod(bean, "notExistMethod", new Class[] { String.class }, new Object[] { "calvin" });
			fail("should throw exception here");
		} catch (IllegalArgumentException e) {

		}

		//参数类型错
		try {
			Reflections.invokeMethod(bean, "privateMethod", new Class[] { Integer.class }, new Object[] { "calvin" });
			fail("should throw exception here");
		} catch (RuntimeException e) {

		}

		//函数名错
		try {
			Reflections.invokeMethodByName(bean, "notExistMethod", new Object[] { "calvin" });
			fail("should throw exception here");
		} catch (IllegalArgumentException e) {

		}

	}

	@Test
	public void getSuperClassGenricType() {
		//获取第1，2个泛型类型
		assertEquals(String.class, Reflections.getClassGenricType(TestBean.class));
		assertEquals(Long.class, Reflections.getClassGenricType(TestBean.class, 1));

		//定义父类时无泛型定义
		assertEquals(Object.class, Reflections.getClassGenricType(TestBean2.class));

		//无父类定义
		assertEquals(Object.class, Reflections.getClassGenricType(TestBean3.class));
	}

	@Test
	public void convertReflectionExceptionToUnchecked() {
		IllegalArgumentException iae = new IllegalArgumentException();
		//ReflectionException,normal
		RuntimeException e = Reflections.convertReflectionExceptionToUnchecked(iae);
		assertEquals(iae, e.getCause());

		//InvocationTargetException,extract it's target exception.
		Exception ex = new Exception();
		e = Reflections.convertReflectionExceptionToUnchecked(new InvocationTargetException(ex));
		assertEquals(ex, e.getCause());

		//UncheckedException, ignore it.
		RuntimeException re = new RuntimeException("abc");
		e = Reflections.convertReflectionExceptionToUnchecked(re);
		assertEquals("abc", e.getMessage());

		//Unexcepted Checked exception.
		e = Reflections.convertReflectionExceptionToUnchecked(ex);
		assertEquals("Unexpected Checked Exception.", e.getMessage());

	}

	public static class ParentBean<T, ID> {
	}

	public static class TestBean extends ParentBean<String, Long> {
		/** 没有getter/setter的field*/
		private int privateField = 1;
		/** 有getter/setter的field */
		private int publicField = 1;

		//通過getter函數會比屬性值+1
		public int getPublicField() {
			return publicField + 1;
		}

		//通過setter函數會被比輸入值加1
		public void setPublicField(int publicField) {
			this.publicField = publicField + 1;
		}

		public int inspectPrivateField() {
			return privateField;
		}

		public int inspectPublicField() {
			return publicField;
		}

		private String privateMethod(String text) {
			return "hello " + text;
		}
	}

	public static class TestBean2 extends ParentBean {
	}

	public static class TestBean3 {

		private int id;

		public int getId() {
			return id;
		}

		public void setId(int id) {
			this.id = id;
		}
	}
}
