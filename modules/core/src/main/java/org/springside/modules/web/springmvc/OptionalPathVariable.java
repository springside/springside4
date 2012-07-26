package org.springside.modules.web.springmvc;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 用于标注不一定存在的PathVarible. 主要用于无@RequestMapping定义, 只有方法级@ModelAttribute标注的函数的参数。
 * 
 * @author calvin
 */
@Target(ElementType.PARAMETER)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface OptionalPathVariable {

	/** The URI template variable to bind to. */
	String value() default "";
}
