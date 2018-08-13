package com.multi.maven.exception;

import com.multi.maven.enums.RetCodeEnum;

/**
 * 业务异常
 * Created by James on 2016/7/27
 */
public class BusinessException extends BaseException {

	private static final long serialVersionUID = 7760867055452954659L;
	
    private final RetCodeEnum retCode;

    public BusinessException() {
        this(null,null, null);
    }
    public BusinessException(Throwable cause) {
        this(null, null, cause);
    }

    public BusinessException(String message) {
        this(null,message, null);
    }

    public BusinessException(String message,Throwable cause) {
        this(null,message, cause);
    }

    public BusinessException(RetCodeEnum retCode) {
        this(retCode,null, null);
    }

    public BusinessException(RetCodeEnum retCode,String msg) {
        this(retCode,msg, null);
    }

    public BusinessException(RetCodeEnum retCode,Throwable cause) {
        this(retCode,null, cause);
    }

    public BusinessException(RetCodeEnum retCode,String msg, Throwable cause) {
        super(msg, cause);
        this.retCode = retCode;
    }


    public RetCodeEnum getRetCode() {
        return retCode;
    }

}
