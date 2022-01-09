package com.kesen.demo.messagegateway.common.enums;

/**
 * @Auther: kesen
 * @Date: 2021/3/14 15:51
 * @Description: 错误码枚举类
 **/
public enum GlobalErrorCode implements IErrorCode {
	/**
	 * 成功
	 */
	SUCCESS(200, "success"),

	/**
	 * 参数校验失败
	 */
	REQUEST_VALIDATION_FAILED(400, "参数参数错误"),

	/**
	 * 登录
	 */
	UNAUTHORIZED(401, "用户未登录"),
	LOGINED(421, "用户已登录,请不要重复登录"),

	RESIGIGERED(402, "用户已注册"),
	UN_RESIGIGERED(412, "用户未注册"),
	/**
	 * 权限
	 */
	FORBIDDEN(403, "会话ID不合法"),

	RESOURCE_NOT_FOUND(404, "未找到该资源"),

	METHOD_NOT_ALLOWED (405, "请求方法不正确"),

	LOCKED (423, "请求失败，请稍后重试"),

	USER_NOT_FOUND(425, "用户未注册"),

	TOO_MANY_REQUESTS(429, "请求过于频繁，请稍后重试"),
	/** 系统异常 */
	FAILED(500, "内部服务器错误"),

	// ========== 全局通用异常 1001000000 ==========
	PREDEFINED_CANNOT_OPERATE(1001000000, "内置数据不允许操作"),
	FUNC_DISABLED_CANNOT_OPERATE(1001000001, "功能禁用期间不允许操作"),
	;

	private final Integer code;
	private final String message;

	GlobalErrorCode(int code, String message) {
		this.code = code;
		this.message = message;
	}

	@Override
	public Integer getCode() {
		return code;
	}

	@Override
	public String getMessage() {
		return message;
	}
}
