package com.multi.maven.exception;

/**
 * 表示用户得到授权（与401错误相对），但是访问是被禁止的。
 */
public class UnauthorizedException extends RuntimeException {

	private static final long serialVersionUID = 6525461640799286507L;

	public UnauthorizedException(String message) {
		super(message);
	}

	public UnauthorizedException(String message, Throwable e) {
		super(message, e);
	}
}
