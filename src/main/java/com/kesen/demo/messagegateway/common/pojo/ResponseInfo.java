package com.kesen.demo.messagegateway.common.pojo;



import com.fasterxml.jackson.annotation.JsonIgnore;
import com.kesen.demo.messagegateway.common.enums.GlobalErrorCode;
import com.kesen.demo.messagegateway.common.enums.IErrorCode;
import com.kesen.demo.messagegateway.common.exception.BusinessException;
import lombok.Data;
import org.springframework.util.Assert;

import java.io.Serializable;
import java.util.Objects;

/**
 * 通用返回
 *
 * @param
 */
@Data
public class ResponseInfo implements Serializable {

    /**
     * 错误码
     *
     * @see IErrorCode#getCode()
     */
    private Integer code;
    /**
     * 错误提示，用户可阅读
     *
     * @see IErrorCode#getMessage()
     */
    private String message;

    /**
     * 将传入的 result 对象，转换成另外一个泛型结果的对象
     * <p>
     * 因为 A 方法返回的 CommonResult 对象，不满足调用其的 B 方法的返回，所以需要进行转换。
     *
     * @param result 传入的 result 对象
     * @return 新的 CommonResult 对象
     */
    public static  ResponseInfo error(ResponseInfo result) {
        return error(result.getCode(), result.getMessage());
    }

    public static  ResponseInfo error(Integer code, String message) {
        Assert.isTrue(!GlobalErrorCode.SUCCESS.getCode().equals(code), "code 必须是错误的！");
        ResponseInfo result = new ResponseInfo();
        result.code = code;
        result.message = message;
        return result;
    }

    public static  ResponseInfo error(IErrorCode errorCode) {
        return error(errorCode.getCode(), errorCode.getMessage());
    }

    public static  ResponseInfo success() {
        ResponseInfo result = new ResponseInfo();
        result.code = GlobalErrorCode.SUCCESS.getCode();
        result.message = GlobalErrorCode.SUCCESS.getMessage();
        return result;
    }

    public static boolean isSuccess(Integer code) {
        return Objects.equals(code, GlobalErrorCode.SUCCESS.getCode());
    }

    @JsonIgnore // 避免 jackson 序列化
    public boolean isSuccess() {
        return isSuccess(code);
    }

    @JsonIgnore // 避免 jackson 序列化
    public boolean isError() {
        return !isSuccess();
    }

    // ========= 和 Exception 异常体系集成 =========

    /**
     * 判断是否有异常。如果有，则抛出 {@link BusinessException} 异常
     */
    public void checkError() throws BusinessException {
        if (isSuccess()) {
            return;
        }
        // 业务异常
        throw new BusinessException(code, message);
    }

    public static  ResponseInfo error(BusinessException businessException) {
        return error(businessException.getCode(), businessException.getMessage());
    }

}
