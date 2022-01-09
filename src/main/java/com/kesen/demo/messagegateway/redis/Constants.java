package com.kesen.demo.messagegateway.redis;

/**
 * @program: messageGateWay
 * @ClassName Constants
 * @description:
 * @author: kesen
 * @create: 2022-01-08 11:10
 * @Version 1.0
 **/
public class Constants {

    public static String userRedisKeyPre = "userInfo_";

    public static String userSessionIdRedisKeyPre = "userSessionId_";

    public static String RATE_LIMIT_KEY = "rate_limiter_";
    public static String TEL_LIMIT_KEY = "tel_limiter_";
}
