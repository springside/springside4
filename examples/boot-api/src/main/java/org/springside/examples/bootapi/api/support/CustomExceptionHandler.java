package org.springside.examples.bootapi.api.support;

import javax.servlet.http.HttpServletRequest;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;
import org.springside.examples.bootapi.service.exception.ErrorCode;
import org.springside.examples.bootapi.service.exception.ServiceException;
import org.springside.modules.web.MediaTypes;

@ControllerAdvice(annotations = { RestController.class })
public class CustomExceptionHandler extends ResponseEntityExceptionHandler {

	@ExceptionHandler(value = { ServiceException.class })
	public final ResponseEntity<ErrorResult> handleServiceException(ServiceException ex, HttpServletRequest request) {
		// 注入servletRequest，用于出错时打印请求来源地址
		logger.error(ex.getMessage() + ", request from " + request.getRemoteAddr(), ex);

		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.parseMediaType(MediaTypes.JSON_UTF_8));
		ErrorResult result = new ErrorResult(ex.code.code, ex.getMessage());
		return new ResponseEntity<ErrorResult>(result, headers, HttpStatus.valueOf(ex.code.httpStatus));
	}

	@ExceptionHandler(value = { Exception.class })
	public final ResponseEntity<ErrorResult> handleGeneralException(Exception ex, HttpServletRequest request) {
		// 注入servletRequest，用于出错时打印请求来源地址
		logger.error(ex.getMessage() + ", request from " + request.getRemoteAddr(), ex);

		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.parseMediaType(MediaTypes.JSON_UTF_8));
		ErrorResult result = new ErrorResult(ErrorCode.INTERNAL_SERVER_ERROR.code, "Internal Server Error");
		return new ResponseEntity<ErrorResult>(result, headers, HttpStatus.INTERNAL_SERVER_ERROR);
	}

	public static class ErrorResult {

		public int code;
		public String message;

		public ErrorResult() {
		}

		public ErrorResult(int code, String message) {
			this.code = code;
			this.message = message;
		}
	}
}
