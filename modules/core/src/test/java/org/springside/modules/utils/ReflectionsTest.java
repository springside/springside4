package org.springside.modules.utils;

import static org.junit.Assert.*;

import java.lang.reflect.InvocationTargetException;

import org.junit.Test;
import org.springside.modules.utils.Reflections;

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
	}

	@Test
	public void invokeGetterAndSetter() {
		TestBean bean = new TestBean();
		assertEquals(bean.inspectPublicField() + 1, Reflections.invokeGetter(bean, "publicField"));

		bean = new TestBean();
		Reflections.invokeSetter(bean, "publicField", 10, int.class);
		assertEquals(10 + 1, bean.inspectPublicField());
	}

	@Test
	public void invokeMethod() {
		TestBean bean = new TestBean();
		assertEquals("hello calvin", Reflections.invokeMethod(bean, "privateMethod", new Class[] { String.class },
				new Object[] { "calvin" }));
	}

	@Test
	public void getSuperClassGenricType() {
		//获取第1，2个泛型类型
		assertEquals(String.class, Reflections.getSuperClassGenricType(TestBean.class));
		assertEquals(Long.class, Reflections.getSuperClassGenricType(TestBean.class, 1));

		//定义父类时无泛型定义
		assertEquals(Object.class, Reflections.getSuperClassGenricType(TestBean2.class));

		//无父类定义
		assertEquals(Object.class, Reflections.getSuperClassGenricType(TestBean3.class));
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

		public int getPublicField() {
			return publicField + 1;
		}

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
