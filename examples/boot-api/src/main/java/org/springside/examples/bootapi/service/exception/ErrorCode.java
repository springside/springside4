package org.springside.examples.bootapi.service.exception;

public enum ErrorCode {

	BAD_REQUEST(1000, 400), UNAUTHORIZED(1001, 401), FORBIDDEN(1002, 403),

	BOOK_STATUS_WRONG(1100, 400), BOOK_OWNERSHIP_WRONG(1101, 403), NO_TOKEN(1102, 401);

	public int code;
	public int httpStatus;

	ErrorCode(int code, int httpStatus) {
		this.code = code;
		this.httpStatus = httpStatus;
	}

}
