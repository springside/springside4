package org.springside.examples.showcase.webservice.rest;

import org.springframework.http.HttpStatus;

public class RestException extends RuntimeException {

	public HttpStatus status;

	public RestException() {
	}

	public RestException(HttpStatus status) {
		this.status = status;
	}

	public RestException(HttpStatus status, String message) {
		super(message);
		this.status = status;
	}
}
