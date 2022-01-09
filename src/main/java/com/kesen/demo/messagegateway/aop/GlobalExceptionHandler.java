package com.kesen.demo.messagegateway.aop;


import com.kesen.demo.messagegateway.common.enums.GlobalErrorCode;
import com.kesen.demo.messagegateway.common.exception.BusinessException;
import com.kesen.demo.messagegateway.common.pojo.ResponseInfo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.BindException;
import org.springframework.validation.FieldError;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.NoHandlerFoundException;

import javax.servlet.http.HttpServletRequest;
import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import java.sql.SQLException;


/**
 * 全局异常处理器，将 Exception 翻译成 ResponseInfo + 对应的异常编号
 *
 * @author kesen
 */
@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {


    /**
     * 处理所有异常，主要是提供给 Filter 使用
     * 因为 Filter 不走 SpringMVC 的流程，但是我们又需要兜底处理异常，所以这里提供一个全量的异常处理过程，保持逻辑统一。
     *
     * @param request 请求
     * @param ex      异常
     * @return 通用返回
     */
    public ResponseInfo allExceptionHandler(HttpServletRequest request, Throwable ex) {
        if (ex instanceof MissingServletRequestParameterException) {
            return missingServletRequestParameterExceptionHandler((MissingServletRequestParameterException) ex);
        }
        if (ex instanceof MethodArgumentTypeMismatchException) {
            return methodArgumentTypeMismatchExceptionHandler((MethodArgumentTypeMismatchException) ex);
        }
        if (ex instanceof MethodArgumentNotValidException) {
            return methodArgumentNotValidExceptionExceptionHandler((MethodArgumentNotValidException) ex);
        }
        if (ex instanceof BindException) {
            return bindExceptionHandler((BindException) ex);
        }
        if (ex instanceof ConstraintViolationException) {
            return constraintViolationExceptionHandler((ConstraintViolationException) ex);
        }
        if (ex instanceof NoHandlerFoundException) {
            return noHandlerFoundExceptionHandler((NoHandlerFoundException) ex);
        }
        if (ex instanceof HttpRequestMethodNotSupportedException) {
            return httpRequestMethodNotSupportedExceptionHandler((HttpRequestMethodNotSupportedException) ex);
        }

        if (ex instanceof BusinessException) {
            return serviceExceptionHandler((BusinessException) ex);
        }
    /*    if (ex instanceof AccessDeniedException) {
            return accessDeniedExceptionHandler(request, (AccessDeniedException) ex);
        }
        if (ex.getCause() instanceof SQLException) {
            return SQLExceptionHandler((SQLException) ex.getCause());
        }
        if  (ex instanceof UsernameNotFoundException) {
            return UsernameNotFoundExceptionHandler( (UsernameNotFoundException) ex);
        }*/
        return defaultExceptionHandler(request, ex);
    }

    /**
     * 处理 SpringMVC 请求参数缺失
     * <p>
     * 例如说，接口上设置了 @RequestParam("xx") 参数，结果并未传递 xx 参数
     */
    @ExceptionHandler(value = MissingServletRequestParameterException.class)
    public ResponseInfo missingServletRequestParameterExceptionHandler(MissingServletRequestParameterException ex) {
        log.warn("[missingServletRequestParameterExceptionHandler]", ex);
        return ResponseInfo.error(GlobalErrorCode.REQUEST_VALIDATION_FAILED.getCode(), String.format("请求参数缺失:%s", ex.getParameterName()));
    }

    /**
     * 处理 SpringMVC 请求参数类型错误
     * <p>
     * 例如说，接口上设置了 @RequestParam("xx") 参数为 Integer，结果传递 xx 参数类型为 String
     */
    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseInfo methodArgumentTypeMismatchExceptionHandler(MethodArgumentTypeMismatchException ex) {
        log.warn("[missingServletRequestParameterExceptionHandler]", ex);
        return ResponseInfo.error(GlobalErrorCode.REQUEST_VALIDATION_FAILED.getCode(), String.format("请求参数类型错误:%s", ex.getMessage()));
    }

    /**
     * 处理 SpringMVC 参数校验不正确
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseInfo methodArgumentNotValidExceptionExceptionHandler(MethodArgumentNotValidException ex) {
        log.warn("[methodArgumentNotValidExceptionExceptionHandler]", ex);
        FieldError fieldError = ex.getBindingResult().getFieldError();
        // 断言，避免告警
        assert fieldError != null;
        return ResponseInfo.error(GlobalErrorCode.REQUEST_VALIDATION_FAILED.getCode(), String.format("请求参数不正确:%s", fieldError.getDefaultMessage()));
    }

    /**
     * 处理 SpringMVC 参数绑定不正确，本质上也是通过 Validator 校验
     */
    @ExceptionHandler(BindException.class)
    public ResponseInfo bindExceptionHandler(BindException ex) {
        log.warn("[handleBindException]", ex);
        FieldError fieldError = ex.getFieldError();
        // 断言，避免告警
        assert fieldError != null;
        return ResponseInfo.error(GlobalErrorCode.REQUEST_VALIDATION_FAILED.getCode(), String.format("请求参数不正确:%s", fieldError.getDefaultMessage()));
    }

    /**
     * 处理 Validator 校验不通过产生的异常
     */
    @ExceptionHandler(value = ConstraintViolationException.class)
    public ResponseInfo constraintViolationExceptionHandler(ConstraintViolationException ex) {
        log.warn("[constraintViolationExceptionHandler]", ex);
        ConstraintViolation constraintViolation = ex.getConstraintViolations().iterator().next();
        return ResponseInfo.error(GlobalErrorCode.REQUEST_VALIDATION_FAILED.getCode(), String.format("请求参数不正确:%s", constraintViolation.getMessage()));
    }


    /**
     * 处理 SpringMVC 请求地址不存在
     * <p>
     * 注意，它需要设置如下两个配置项：
     * 1. spring.mvc.throw-exception-if-no-handler-found 为 true
     * 2. spring.mvc.static-path-pattern 为 /statics/**
     */
    @ExceptionHandler(NoHandlerFoundException.class)
    public ResponseInfo noHandlerFoundExceptionHandler(NoHandlerFoundException ex) {
        log.warn("[noHandlerFoundExceptionHandler]", ex);
        return ResponseInfo.error(GlobalErrorCode.RESOURCE_NOT_FOUND.getCode(), String.format("请求地址不存在:%s", ex.getRequestURL()));
    }

    /**
     * 处理 SpringMVC 请求方法不正确
     * <p>
     * 例如说，A 接口的方法为 GET 方式，结果请求方法为 POST 方式，导致不匹配
     */
    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public ResponseInfo httpRequestMethodNotSupportedExceptionHandler(HttpRequestMethodNotSupportedException ex) {
        log.warn("[httpRequestMethodNotSupportedExceptionHandler]", ex);
        return ResponseInfo.error(GlobalErrorCode.METHOD_NOT_ALLOWED.getCode(), String.format("请求方法不正确:%s", ex.getMessage()));
    }


/*
    */
/**
     * 处理 Spring Security 权限不足的异常
     * <p>
     * 来源是，使用 @PreAuthorize 注解，AOP 进行权限拦截
     *//*

    @ExceptionHandler(value = AccessDeniedException.class)
    public ResponseInfo accessDeniedExceptionHandler(HttpServletRequest req, AccessDeniedException ex) {
        log.warn("[accessDeniedExceptionHandler][userId({}) 无法访问 url({})]", WebFrameworkUtils.getLoginUserId(req),
                req.getRequestURL(), ex);
        return ResponseInfo.error(GlobalErrorCode.FORBIDDEN.getCode(), GlobalErrorCode.FORBIDDEN.getMessage());
    }
*/

    /**
     * 处理业务异常 ServiceException
     * <p>
     * 例如说，商品库存不足，用户手机号已存在。
     */
    @ExceptionHandler(value = BusinessException.class)
    public ResponseInfo serviceExceptionHandler(BusinessException ex) {
        //log.info("[businessServiceExceptionHandler]", ex);
        return ResponseInfo.error(ex.getCode(), ex.getMessage());
    }


    /**
     * 处理业务异常 ServiceException
     * <p>
     * 例如说，商品库存不足，用户手机号已存在。
     */
    @ExceptionHandler(value = SQLException.class)
    public ResponseInfo SQLExceptionHandler(SQLException ex) {
        log.info("[sqlExceptionHandler]", ex);
        String msg = ex.getMessage();
        int index = msg.toUpperCase().indexOf("ORA-");
        if (index != -1) {
            int index1 = msg.indexOf(":");
            if (index1 != 1) {
                msg = msg.substring(index1 + 1, msg.length() - 1);
            }
        }
        return ResponseInfo.error(ex.getErrorCode(), msg);
    }


    /**
     * 处理系统异常，兜底处理所有的一切
     */
    @ExceptionHandler(value = Exception.class)
    public ResponseInfo defaultExceptionHandler(HttpServletRequest req, Throwable ex) {
        log.error("[defaultExceptionHandler]", ex);
        if (ex.getCause() instanceof  SQLException) {
            return  this.SQLExceptionHandler((SQLException) ex.getCause());
        }
        return ResponseInfo.error(GlobalErrorCode.FAILED.getCode(), GlobalErrorCode.FAILED.getMessage());
    }


}
