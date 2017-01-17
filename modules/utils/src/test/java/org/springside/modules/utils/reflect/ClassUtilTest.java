package org.springside.modules.utils.reflect;

import static org.assertj.core.api.Assertions.*;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import org.junit.Test;

public class ClassUtilTest {

	@Test
	public void getMessage() {
		assertThat(ClassUtil.getShortClassName(ClassUtilTest.class)).isEqualTo("ClassUtilTest");
		assertThat(ClassUtil.getShortClassName(BClass.class)).isEqualTo("ClassUtilTest.BClass");

		assertThat(ClassUtil.getShortClassName(ClassUtilTest.class.getName())).isEqualTo("ClassUtilTest");
		assertThat(ClassUtil.getShortClassName(BClass.class.getName())).isEqualTo("ClassUtilTest.BClass");

		assertThat(ClassUtil.getPackageName(ClassUtilTest.class)).isEqualTo("org.springside.modules.utils.reflect");
		assertThat(ClassUtil.getPackageName(BClass.class)).isEqualTo("org.springside.modules.utils.reflect");
		assertThat(ClassUtil.getPackageName(ClassUtilTest.class.getName()))
				.isEqualTo("org.springside.modules.utils.reflect");
		assertThat(ClassUtil.getPackageName(BClass.class.getName())).isEqualTo("org.springside.modules.utils.reflect");

	}

	@Test
	public void getAllClass() {

		assertThat(ClassUtil.getAllInterfaces(BClass.class)).hasSize(4).contains(AInterface.class, BInterface.class,
				CInterface.class, DInterface.class);

		assertThat(ClassUtil.getAllSuperclasses(BClass.class)).hasSize(2).contains(AClass.class, Object.class);

		assertThat(ClassUtil.getAllAnnotations(BClass.class)).hasSize(4);

		assertThat(ClassUtil.getPublicFieldsAnnotatedWith(BClass.class, AAnnotation.class)).hasSize(2).contains(
				ClassUtil.getAccessibleField(BClass.class, "sfield"),
				ClassUtil.getAccessibleField(BClass.class, "tfield"));

		assertThat(ClassUtil.getFieldsAnnotatedWith(BClass.class, EAnnotation.class)).hasSize(3).contains(
				ClassUtil.getAccessibleField(BClass.class, "bfield"),
				ClassUtil.getAccessibleField(BClass.class, "efield"),
				ClassUtil.getAccessibleField(AClass.class, "afield"));

		assertThat(ClassUtil.getFieldsAnnotatedWith(BClass.class, FAnnotation.class)).hasSize(1)
				.contains(ClassUtil.getAccessibleField(AClass.class, "dfield"));
	}

	@Test
	public void getSuperClassGenricType() {
		// 获取第1，2个泛型类型
		assertThat(ClassUtil.getClassGenricType(TestBean.class)).isEqualTo(String.class);
		assertThat(ClassUtil.getClassGenricType(TestBean.class, 1)).isEqualTo(Long.class);

		// 定义父类时无泛型定义
		assertThat(ClassUtil.getClassGenricType(TestBean2.class)).isEqualTo(Object.class);

		// 无父类定义
		assertThat(ClassUtil.getClassGenricType(TestBean3.class)).isEqualTo(Object.class);
	}

	public static class ParentBean<T, ID> {
	}

	public static class TestBean extends ParentBean<String, Long> {

	}

	public static class TestBean2 extends ParentBean {
	}

	public static class TestBean3 {

	}

	public interface AInterface {
	}

	@CAnnotation
	public interface BInterface extends AInterface {
		@FAnnotation
		void hello();
	}

	public interface CInterface {
	}

	public interface DInterface {
	}

	@Retention(RetentionPolicy.RUNTIME)
	public @interface AAnnotation {
	}

	@Retention(RetentionPolicy.RUNTIME)
	@AAnnotation
	public @interface BAnnotation {
	}

	@Retention(RetentionPolicy.RUNTIME)
	public @interface CAnnotation {
	}

	@Retention(RetentionPolicy.RUNTIME)
	public @interface DAnnotation {
	}

	@Retention(RetentionPolicy.RUNTIME)
	public @interface EAnnotation {
	}

	@Retention(RetentionPolicy.RUNTIME)
	public @interface FAnnotation {
	}

	@DAnnotation
	public static class AClass implements DInterface {

		@EAnnotation
		private int afield;

		private int cfield;

		@FAnnotation
		private int dfield;

		@AAnnotation
		public int tfield;

		@AAnnotation
		protected int vfield;

		public void hello2(int i) {

		}
	}

	@BAnnotation
	public static class BClass extends AClass implements CInterface, BInterface {

		@EAnnotation
		private int bfield;

		@EAnnotation
		private int efield;

		@AAnnotation
		public int sfield;

		@AAnnotation
		protected int ufield;

		@Override
		@EAnnotation
		public void hello() {
			// TODO Auto-generated method stub

		}

		public void hello2(int i) {

		}

	}

}
