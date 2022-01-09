package com.kesen.demo.messagegateway.module.auth.controller;

import cn.hutool.core.util.StrUtil;
import com.kesen.demo.messagegateway.common.enums.GlobalErrorCode;
import com.kesen.demo.messagegateway.common.exception.BusinessException;
import com.kesen.demo.messagegateway.common.pojo.ResponseInfo;

import com.kesen.demo.messagegateway.module.auth.model.UserLoginInfo;
import com.kesen.demo.messagegateway.module.auth.model.UserLoginResponse;
import com.kesen.demo.messagegateway.module.auth.model.UserLogoutInfo;
import com.kesen.demo.messagegateway.module.auth.model.UserRegisterInfo;
import com.kesen.demo.messagegateway.redis.Constants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

/**
 * @program: messageGateWay
 * @ClassName AuthController
 * @description:
 * @author: kesen
 * @create: 2022-01-08 10:45
 * @Version 1.0
 **/
@RestController
@RequestMapping("/auth/user")
public class AuthController {

    @Autowired
    RedisTemplate<String, Object> redisTemplate;
    @Autowired
    StringRedisTemplate  stringRedisTemplate;

    @PostMapping("/register")
    private ResponseInfo register(@RequestBody @Valid UserRegisterInfo userRegisterInfo) {

        String key = Constants.userRedisKeyPre + userRegisterInfo.getUserName();
        Object user = redisTemplate.opsForValue().get(key);
        if (null != user) {
            throw new BusinessException(GlobalErrorCode.RESIGIGERED.getCode(), GlobalErrorCode.RESIGIGERED.getMessage());
        } else {
            // 插入redis
            redisTemplate.opsForValue().set(key, userRegisterInfo);
            return ResponseInfo.success();
        }

    }


    @PostMapping("/login")
    private ResponseInfo login(@RequestBody @Valid UserLoginInfo userLoginInfo) {

        String key = Constants.userRedisKeyPre + userLoginInfo.getUserName();
        Object user = redisTemplate.opsForValue().get(key);
        if (null == user) {
            throw new BusinessException(GlobalErrorCode.USER_NOT_FOUND.getCode(), GlobalErrorCode.USER_NOT_FOUND.getMessage());
        } else {

            String sessionKey = Constants.userSessionIdRedisKeyPre + userLoginInfo.getUserName();
            // 校验密码
            UserRegisterInfo userRegisterInfo = (UserRegisterInfo) user;
            String se = stringRedisTemplate.opsForValue().get(sessionKey);
            if (null != se) {
                throw new BusinessException(GlobalErrorCode.LOGINED.getCode(), GlobalErrorCode.LOGINED.getMessage());
            }
            // 校验密码
            if (!userRegisterInfo.getPassword().equals(userLoginInfo.getPassword())) {
                return ResponseInfo.error(426, "密码校验失败");
            }
            // 生成会话ID
            String sessionId = StrUtil.uuid();

            stringRedisTemplate.opsForValue().set(sessionKey, sessionId);
            UserLoginResponse userLoginResponse = new UserLoginResponse();
            userLoginResponse.setCode(200);
            userLoginResponse.setMessage("success");
            userLoginResponse.setSessionId(sessionId);
            return userLoginResponse;
        }

    }

    @PostMapping("/logout")
    private ResponseInfo logout(@RequestBody @Valid UserLogoutInfo userLoginInfo) {
        String sessionKey = Constants.userSessionIdRedisKeyPre + userLoginInfo.getUserName();
        String sessionId = stringRedisTemplate.opsForValue().get(sessionKey);
        if (null == sessionId) {
            throw new BusinessException(GlobalErrorCode.UNAUTHORIZED.getCode(), GlobalErrorCode.UNAUTHORIZED.getMessage());
        }
        if( !sessionId.equals(userLoginInfo.getSessionId())) {
            throw new BusinessException(GlobalErrorCode.FORBIDDEN.getCode(), GlobalErrorCode.FORBIDDEN.getMessage());
        }
        redisTemplate.delete(sessionKey);
        return ResponseInfo.success();
    }
}
