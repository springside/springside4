package org.springside.examples.bootapi.service;

import org.springside.examples.bootapi.service.exception.ErrorCode;

public class ServiceException extends RuntimeException {

	private static final long serialVersionUID = -8634700792767837033L;

	public ErrorCode code;

	public ServiceException(String message, ErrorCode code) {
		super(message);
		this.code = code;
	}
}
