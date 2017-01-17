package org.springside.modules.utils.reflect;

import static org.assertj.core.api.Assertions.*;

import java.lang.reflect.InvocationTargetException;
import java.util.List;

import org.junit.Test;
import org.springside.modules.utils.collection.ListUtil;

import net.sf.cglib.reflect.FastMethod;

public class FastMethodInvokerTest {

	@Test
	public void test() throws InvocationTargetException {
		FastMethod fastGetAge = FastMethodInvoker.getFastMethod(Student.class, "getAge");
		Student student1 = new Student("zhang3", 20, new Teacher("li4"), ListUtil.newArrayList("chinese", "english"));
		Student student2 = new Student("zhang4", 30, new Teacher("li5"), ListUtil.newArrayList("chinese2", "english4"));

		int age = FastMethodInvoker.invoke(fastGetAge, student1);
		assertThat(age).isEqualTo(20);

		age = FastMethodInvoker.invoke(fastGetAge, student2);
		assertThat(age).isEqualTo(30);

		FastMethod fastSetAge = FastMethodInvoker.getFastMethod(Student.class, "setAge", int.class);
		FastMethodInvoker.invoke(fastSetAge, student1, 1);

		assertThat(student1.getAge()).isEqualTo(1);
	}

	public static class Student {
		public String name;
		private int age;
		private Teacher teacher;
		private List<String> course = ListUtil.newArrayList();

		public Student(String name, int age, Teacher teacher, List<String> course) {
			this.name = name;
			this.age = age;
			this.teacher = teacher;
			this.course = course;
		}

		public List<String> getCourse() {
			return course;
		}

		public void setCourse(List<String> course) {
			this.course = course;
		}

		public int getAge() {
			return age;
		}

		public void setAge(int age) {
			this.age = age;
		}

		public Teacher getTeacher() {
			return teacher;
		}

		public void setTeacher(Teacher teacher) {
			this.teacher = teacher;
		}

	}

	public static class Teacher {
		private String name;

		public Teacher(String name) {
			super();
			this.name = name;
		}

		public String getName() {
			return name;
		}

		public void setName(String name) {
			this.name = name;
		}

	}
}
