package com.multi.maven.exception;

/**
 * 服务器发生错误，用户将无法判断发出的请求是否成功。
 */
public class InternalServerException extends RuntimeException {

	private static final long serialVersionUID = 6525461640799286507L;

	public InternalServerException(String message) {
		super(message);
	}

	public InternalServerException(String message, Throwable e) {
		super(message, e);
	}

}
