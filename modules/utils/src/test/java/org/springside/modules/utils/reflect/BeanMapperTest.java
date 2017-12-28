package org.springside.modules.utils.reflect;

import static org.assertj.core.api.Assertions.*;

import java.util.List;

import org.junit.Test;
import org.springside.modules.utils.collection.ListUtil;

import ma.glasnost.orika.metadata.Type;

public class BeanMapperTest {

	@Test
	public void copySingleObject() {
		Student student = new Student("zhang3", 20, new Teacher("li4"), ListUtil.newArrayList("chinese", "english"));

		StudentVO studentVo = BeanMapper.map(student, StudentVO.class);

		assertThat(studentVo.name).isEqualTo("zhang3");
		assertThat(studentVo.getAge()).isEqualTo(20);
		assertThat(studentVo.getTeacher().getName()).isEqualTo("li4");
		assertThat(studentVo.getCourse()).containsExactly("chinese", "english");

		//////////
		Type<Student> studentType = BeanMapper.getType(Student.class);
		Type<StudentVO> studentVoType = BeanMapper.getType(StudentVO.class);
		studentVo = BeanMapper.map(student, studentType, studentVoType);

		assertThat(studentVo.name).isEqualTo("zhang3");
		assertThat(studentVo.getAge()).isEqualTo(20);
		assertThat(studentVo.getTeacher().getName()).isEqualTo("li4");
		assertThat(studentVo.getCourse()).containsExactly("chinese", "english");
	}

	@Test
	public void copyListObject() {
		Student student1 = new Student("zhang3", 20, new Teacher("li4"), ListUtil.newArrayList("chinese", "english"));
		Student student2 = new Student("zhang4", 30, new Teacher("li5"), ListUtil.newArrayList("chinese2", "english4"));
		Student student3 = new Student("zhang5", 40, new Teacher("li6"), ListUtil.newArrayList("chinese3", "english5"));
		List<Student> studentList = ListUtil.newArrayList(student1, student2, student3);

		List<StudentVO> studentVoList = BeanMapper.mapList(studentList, Student.class, StudentVO.class);
		assertThat(studentVoList).hasSize(3);
		StudentVO studentVo = studentVoList.get(0);

		assertThat(studentVo.name).isEqualTo("zhang3");
		assertThat(studentVo.getAge()).isEqualTo(20);
		assertThat(studentVo.getTeacher().getName()).isEqualTo("li4");
		assertThat(studentVo.getCourse()).containsExactly("chinese", "english");

		/////////
		Type<Student> studentType = BeanMapper.getType(Student.class);
		Type<StudentVO> studentVoType = BeanMapper.getType(StudentVO.class);
		studentVoList = BeanMapper.mapList(studentList, studentType, studentVoType);

		studentVoList = BeanMapper.mapList(studentList, Student.class, StudentVO.class);
		assertThat(studentVoList).hasSize(3);
		studentVo = studentVoList.get(0);

		assertThat(studentVo.name).isEqualTo("zhang3");
		assertThat(studentVo.getAge()).isEqualTo(20);
		assertThat(studentVo.getTeacher().getName()).isEqualTo("li4");
		assertThat(studentVo.getCourse()).containsExactly("chinese", "english");
	}

	@Test
	public void copyArrayObject() {
		Student student1 = new Student("zhang3", 20, new Teacher("li4"), ListUtil.newArrayList("chinese", "english"));
		Student student2 = new Student("zhang4", 30, new Teacher("li5"), ListUtil.newArrayList("chinese2", "english4"));
		Student student3 = new Student("zhang5", 40, new Teacher("li6"), ListUtil.newArrayList("chinese3", "english5"));
		Student[] studentList = new Student[] { student1, student2, student3 };
		StudentVO[] studentVoList = new StudentVO[studentList.length];
		BeanMapper.mapArray(studentVoList, studentList, StudentVO.class);
		assertThat(studentVoList).hasSize(3);
		StudentVO studentVo = studentVoList[0];

		assertThat(studentVo.name).isEqualTo("zhang3");
		assertThat(studentVo.getAge()).isEqualTo(20);
		assertThat(studentVo.getTeacher().getName()).isEqualTo("li4");
		assertThat(studentVo.getCourse()).containsExactly("chinese", "english");

		/////////
		Type<Student> studentType = BeanMapper.getType(Student.class);
		Type<StudentVO> studentVoType = BeanMapper.getType(StudentVO.class);

		StudentVO[] studentVoList2 = new StudentVO[studentList.length];
		BeanMapper.mapArray(studentVoList2, studentList, studentType, studentVoType);
		assertThat(studentVoList).hasSize(3);
		studentVo = studentVoList2[0];

		assertThat(studentVo.name).isEqualTo("zhang3");
		assertThat(studentVo.getAge()).isEqualTo(20);
		assertThat(studentVo.getTeacher().getName()).isEqualTo("li4");
		assertThat(studentVo.getCourse()).containsExactly("chinese", "english");
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

	public static class StudentVO {
		public String name;
		private int age;
		private TeacherVO teacher;
		private List<String> course = ListUtil.newArrayList();

		public StudentVO(String name, int age, TeacherVO teacher, List<String> course) {
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

		public TeacherVO getTeacher() {
			return teacher;
		}

		public void setTeacher(TeacherVO teacher) {
			this.teacher = teacher;
		}

	}

	public static class TeacherVO {
		private String name;

		public TeacherVO(String name) {
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
