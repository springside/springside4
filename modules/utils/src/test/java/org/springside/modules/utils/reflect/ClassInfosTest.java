package org.springside.modules.utils.reflect;

import static org.assertj.core.api.Assertions.*;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import org.junit.Test;

public class ClassInfosTest {

	public interface AInterface {
	}

	@CAnnotation
	public interface BInterface extends AInterface {
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

	@DAnnotation
	public static class AClass implements DInterface {
	}

	@BAnnotation
	public static class BClass extends AClass implements CInterface, BInterface {

	}

	public static void main(String[] args) {
		System.out.println(ClasseInfos.getAllInterfaces(BClass.class));
		System.out.println(ClasseInfos.getAllSuperclasses(BClass.class));
		System.out.println(ClasseInfos.getAllAnnotations(BClass.class));
	}

	@Test
	public void getSuperClassGenricType() {
		// 获取第1，2个泛型类型
		assertThat(ClasseInfos.getClassGenricType(TestBean.class)).isEqualTo(String.class);
		assertThat(ClasseInfos.getClassGenricType(TestBean.class, 1)).isEqualTo(Long.class);

		// 定义父类时无泛型定义
		assertThat(ClasseInfos.getClassGenricType(TestBean2.class)).isEqualTo(Object.class);

		// 无父类定义
		assertThat(ClasseInfos.getClassGenricType(TestBean3.class)).isEqualTo(Object.class);
	}

	public static class ParentBean<T, ID> {
	}

	public static class TestBean extends ParentBean<String, Long> {

	}

	public static class TestBean2 extends ParentBean {
	}

	public static class TestBean3 {
	
	}

}
