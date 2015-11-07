package org.springside.examples.bootapi.api.support;

import org.springframework.http.HttpStatus;

public class RestException extends RuntimeException {

	private static final long serialVersionUID = -8634700792767837033L;

	public HttpStatus status;

	public RestException(String message, HttpStatus status) {
		super(message);
		this.status = status;
	}
}
