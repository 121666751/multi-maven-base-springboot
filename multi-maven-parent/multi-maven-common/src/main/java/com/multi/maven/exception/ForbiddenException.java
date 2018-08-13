package com.multi.maven.exception;

/**
 * 表示用户没有权限（令牌、用户名、密码错误）。
 */
public class ForbiddenException extends RuntimeException {

	private static final long serialVersionUID = 6525461640799286507L;

	public ForbiddenException(String message) {
		super(message);
	}

	public ForbiddenException(String message, Throwable e) {
		super(message, e);
	}
}
