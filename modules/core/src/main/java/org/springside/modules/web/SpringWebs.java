package org.springside.modules.web;

import java.util.Map;

import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.servlet.HandlerMapping;

public class SpringWebs {

	public static String getPathVariable(NativeWebRequest request, String name) {
		Map<String, String> uriTemplateVars = (Map<String, String>) request.getAttribute(
				HandlerMapping.URI_TEMPLATE_VARIABLES_ATTRIBUTE, RequestAttributes.SCOPE_REQUEST);
		return uriTemplateVars.get(name);
	}

}
