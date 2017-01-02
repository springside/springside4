package org.springside.modules.utils.reflect;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

public class ClassesTest {

	public interface AInterface {
	}
	
	@CAnnotation
	public interface BInterface extends AInterface{
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
	public static class AClass implements DInterface{
	}
	
	@BAnnotation
	public static class BClass extends AClass implements CInterface,BInterface{
		
	}
	
	public static void main(String[] args) {
		System.out.println(Classes.getAllInterfaces(BClass.class));
		System.out.println(Classes.getAllSuperclasses(BClass.class));
		
		System.out.println(Classes.getAllAnnotations(BClass.class));
		
	}

}
