package org.springside.modules.web.springmvc;

import org.springframework.core.MethodParameter;
import org.springframework.web.bind.annotation.ValueConstants;
import org.springframework.web.servlet.mvc.method.annotation.PathVariableMethodArgumentResolver;

/**
 * 用于标注不一定存在的PathVarible. 主要用于无@RequestMapping定义, 只有方法级@ModelAttribute标注的函数的参数。
 * 
 * @author calvin
 */
public class OptionalPathVariableMethodArgumentResolver extends PathVariableMethodArgumentResolver {
	@Override
	public boolean supportsParameter(MethodParameter parameter) {
		return parameter.hasParameterAnnotation(OptionalPathVariable.class);
	}

	@Override
	protected NamedValueInfo createNamedValueInfo(MethodParameter parameter) {
		OptionalPathVariable annotation = parameter.getParameterAnnotation(OptionalPathVariable.class);
		return new PathVariableNamedValueInfo(annotation);
	}

	private static class PathVariableNamedValueInfo extends NamedValueInfo {
		private PathVariableNamedValueInfo(OptionalPathVariable annotation) {
			//设定required为false
			super(annotation.value(), false, ValueConstants.DEFAULT_NONE);
		}
	}
}
