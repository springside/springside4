package org.springside.examples.showcase.demos.hystrix.web;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;
import org.springside.modules.web.MediaTypes;

import com.netflix.hystrix.exception.HystrixBadRequestException;
import com.netflix.hystrix.exception.HystrixRuntimeException;
import com.netflix.hystrix.exception.HystrixRuntimeException.FailureType;

/**
 * 自定义ExceptionHandler，专门处理Hystrix异常.
 * 
 * @author calvin
 */
@ControllerAdvice
public class HystrixExceptionHandler extends ResponseEntityExceptionHandler {
	/**
	 * 处理Hystrix Runtime异常.
	 */
	@ExceptionHandler(value = { HystrixRuntimeException.class })
	public final ResponseEntity<?> handleException(HystrixRuntimeException e, WebRequest request) {
		HttpStatus status = HttpStatus.SERVICE_UNAVAILABLE;
		String message = e.getMessage();

		FailureType type = e.getFailureType();

		// 对命令抛出的异常进行特殊处理
		if (type.equals(FailureType.COMMAND_EXCEPTION)) {
			status = HttpStatus.INTERNAL_SERVER_ERROR;
			message = "HystrixRuntimeException:" + e.getCause().getMessage();
		}

		logger.error(message, e);

		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.parseMediaType(MediaTypes.TEXT_PLAIN_UTF_8));
		return handleExceptionInternal(e, message, headers, status, request);
	}

	/**
	 * 处理Hystrix ClientException异常.
	 */
	@ExceptionHandler(value = { HystrixBadRequestException.class })
	public final ResponseEntity<?> handleException(HystrixBadRequestException e, WebRequest request) {
		Exception realException = (Exception) e.getCause();

		logger.error("HystrixClientException:" + realException.getMessage(), realException);

		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.parseMediaType(MediaTypes.TEXT_PLAIN_UTF_8));
		return handleExceptionInternal(realException, realException.getMessage(), headers, HttpStatus.BAD_REQUEST,
				request);
	}
}
