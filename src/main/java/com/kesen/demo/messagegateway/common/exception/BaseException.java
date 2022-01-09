package com.kesen.demo.messagegateway.common.exception;

/**
 * @Auther: kesen
 * @Date: 2021/3/14 16:14
 * @Description:
 **/
public class BaseException extends RuntimeException {
	private static final long serialVersionUID = 1L;
	private int code = -1;
	/**
	 * 异常级别
	 */
	private int level = 5;

	private Object[] args;

	public BaseException() {
		super();
	}

	public BaseException(String msg) {
		super(msg);
	}


	protected BaseException(String msg, Throwable cause) {
		super(msg, cause);
	}

	public BaseException(int code) {
		super();
		this.code = code;
	}

	public BaseException(int code, String msg) {
		super(msg);
		this.code = code;
	}

	public BaseException(int code, String msg, Object[] args) {
		super(msg);
		this.code = code;
		this.args = args;
	}

	public BaseException(int code, String msg, Throwable cause) {
		super(msg, cause);
		this.code = code;
	}

	public BaseException(int code, String msg, Throwable cause, Object[] args) {
		super(msg, cause);
		this.code = code;
		this.args = args;
	}

	public int getCode() {
		return code;
	}

	public void setCode(int code) {
		this.code = code;
	}

	public int getLevel() {
		return level;
	}

	public void setLevel(int level) {
		this.level = level;
	}

	public Object[] getArgs() {
		return args;
	}

	public void setArgs(Object[] args) {
		this.args = args;
	}

	@Override
	public String toString() {
		return "[" + code + "]" + getMessage();
	}
}

