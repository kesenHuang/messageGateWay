package com.kesen.demo.messagegateway.aop;

import com.kesen.demo.messagegateway.common.exception.BusinessException;
import com.kesen.demo.messagegateway.redis.Constants;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.Signature;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

/**
 * @program: messageGateWay
 * @ClassName AuthenticationAspect
 * @description: 消息网关转发消息鉴权切面
 * @author: kesen
 * @create: 2022-01-08 15:27
 * @Version 1.0
 **/
@Component
@Slf4j
@Aspect
public class AuthenticationAspect {

    @Autowired
    RedisTemplate redisTemplate;

    @Around("@annotation(com.kesen.demo.messagegateway.aop.annotation.PreAuth)")
    public Object around(ProceedingJoinPoint point) throws Throwable {
        Signature signature = point.getSignature();
        if (signature instanceof MethodSignature) {

            MethodSignature methodSignature = (MethodSignature) signature;
            // 方法参数名
            String[] paramsNames = methodSignature.getParameterNames();

            // 参数获取
            Object[] params = point.getArgs();
            if (methodSignature.getName().equals("directmessage")) {
                String sessionId = "";
                String userName = "";
                for (int i = 0; i < paramsNames.length; i++) {
                    if (paramsNames[i].equals("sessionId")) {
                        sessionId = (String) params[i];
                    }
                    if (paramsNames[i].equals("userName")) {
                        userName = (String) params[i];
                    }
                }
                Object o = redisTemplate.opsForValue().get(Constants.userRedisKeyPre + userName);
                if (null == o) {
                    throw new BusinessException(401, "用户未注册");
                }
                Object session = redisTemplate.opsForValue().get(Constants.userSessionIdRedisKeyPre + userName);
                if (null == session) {
                    throw new BusinessException(401, "用户未登录");
                }
                if (!((String) session) .equals(sessionId))  {
                    throw new BusinessException(401, "用户和会话ID不匹配");
                }
            }
            return point.proceed();
        }
        return point.proceed();
    }
}
