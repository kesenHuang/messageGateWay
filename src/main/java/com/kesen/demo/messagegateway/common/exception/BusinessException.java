package com.kesen.demo.messagegateway.common.exception;

/**
 * @Auther: kesen
 * @Date: 2021/3/14 17:44
 * @Description:
 **/
public class BusinessException extends BaseException {
	private static final long serialVersionUID = 8905503531165516209L;


	public BusinessException(int code, String msg, Object... args) {
		super(code, msg, args);
	}

	public BusinessException(int code, String msg, Throwable cause, Object... args) {
		super(code, msg, cause, args);
	}
}
