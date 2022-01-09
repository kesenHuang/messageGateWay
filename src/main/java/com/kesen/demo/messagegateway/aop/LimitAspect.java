package com.kesen.demo.messagegateway.aop;

import com.kesen.demo.messagegateway.aop.annotation.DistriLimiter;
import com.kesen.demo.messagegateway.common.exception.BusinessException;
import com.kesen.demo.messagegateway.redis.Constants;
import com.kesen.demo.messagegateway.redis.DistributedLimiter;
import com.sun.org.apache.bcel.internal.generic.SWITCH;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;

/**
 * @program: messageGateWay
 * @ClassName LimitAspect
 * @description:
 * @author: kesen
 * @create: 2022-01-08 18:12
 * @Version 1.0
 **/
@Slf4j
@Aspect
@Component
@EnableAspectJAutoProxy(proxyTargetClass = true)
public class LimitAspect {
    @Autowired
    DistributedLimiter distributedLimiter;

    @Pointcut("@annotation(com.kesen.demo.messagegateway.aop.annotation.DistriLimiter)")
    public void limit() {
    }


    @Before("limit()")
    public void beforeLimit(JoinPoint joinPoint) throws Exception {
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Method method = signature.getMethod();
        DistriLimiter distriLimiter = method.getAnnotation(DistriLimiter.class);
        String key = distriLimiter.limitKey();
        int limit = distriLimiter.limit();


        MethodSignature methodSignature = (MethodSignature) signature;
        // 方法参数名
        String[] paramsNames = methodSignature.getParameterNames();

        // 参数获取
        Object[] params = joinPoint.getArgs();

        String sessionId = "";
        String userName = "";
        String qos = "";
        String tels = "";
        for (int i = 0; i < paramsNames.length; i++) {
            if (paramsNames[i].equals("sessionId")) {
                sessionId = (String) params[i];
            }
            if (paramsNames[i].equals("userName")) {
                userName = (String) params[i];
            }
            if (paramsNames[i].equals("qos")) {
                qos = (String) params[i];
            }
            if (paramsNames[i].equals("tels")) {
                tels = (String) params[i];
            }
        }
        boolean acquired = true;
        switch (qos) {
            case "1":
                acquired = distributedLimiter.distributedLimit(getKey(key), String.valueOf(limit), "0", "0");
            case "2":
                acquired = distributedLimiter.distributedLimit(getKey(key), String.valueOf(limit), "50", "0");
                break;
            case "3":
                acquired = distributedLimiter.distributedLimit(getKey(key), String.valueOf(limit), "80", "0");
                break;
            default:
                break;
        }

        if (!acquired) {
            throw new RuntimeException("exceeded limit");

        }
        boolean acquired1 = true;
        if (acquired) {
            acquired1 = distributedLimiter.telLimit(getTelKey(tels), "1", "1");
        }
        if (!acquired1) {
            throw new RuntimeException("exceeded limit");
        }

    }

    public String getKey(String key) {
        return Constants.RATE_LIMIT_KEY + key;
    }

    public String getTelKey(String key) {
        return Constants.TEL_LIMIT_KEY + key;
    }
}
