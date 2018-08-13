package com.multi.maven.exception;



public class BaseException extends RuntimeException {

	//
	private static final long serialVersionUID = 9214703048266215153L;

	
	/**
	 * 
	 */
	public BaseException() {
		super();
	}
	
	public BaseException(String message) {
		super(message, null);
	}
	
	public BaseException(String message, Throwable cause) {
		super(message, cause);
	}
	
	public BaseException(Throwable cause) {
		super(cause);
	}
	
}