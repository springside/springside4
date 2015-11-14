package org.springside.examples.bootapi.api.support;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.web.ErrorController;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springside.modules.mapper.JsonMapper;
import org.springside.modules.web.MediaTypes;

import com.google.common.collect.Maps;

/**
 * 重载替换Spring Boot默认的BasicErrorController
 * 
 * @author calvin
 *
 */
@Controller
public class ErrorPageController implements ErrorController {

	private Logger logger = LoggerFactory.getLogger(ErrorPageController.class);

	private JsonMapper jsonMapper = new JsonMapper();

	@Value("${error.path:/error}")
	private String errorPath;

	@Override
	public String getErrorPath() {
		return this.errorPath;
	}

	@RequestMapping(value = "${error.path:/error}", produces = MediaTypes.JSON_UTF_8)
	@ResponseBody
	public ErrorResult handle(HttpServletRequest request) {
		ErrorResult result = new ErrorResult();
		result.code = getStatus(request).value();
		result.message = (String) request.getAttribute("javax.servlet.error.message");

		logError(result, request);

		return result;
	}

	private HttpStatus getStatus(HttpServletRequest request) {
		Integer statusCode = (Integer) request.getAttribute("javax.servlet.error.status_code");
		if (statusCode != null) {
			try {
				return HttpStatus.valueOf(statusCode);
			} catch (Exception ex) {
			}
		}
		return HttpStatus.INTERNAL_SERVER_ERROR;
	}

	private void logError(ErrorResult result, HttpServletRequest request) {
		Map<String, Object> map = Maps.newHashMap();
		map.put("code", result.code);
		map.put("message", result.message);
		map.put("from", request.getRemoteAddr());
		String queryString = request.getQueryString();
		map.put("uri", queryString != null ? (request.getRequestURI() + "?" + queryString) : request.getRequestURI());

		logger.error(jsonMapper.toJson(map));
	}
}
