package com.kesen.demo.messagegateway.redis;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.connection.RedisServerCommands;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.RedisScript;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.util.Collections;

/**
 * @program: messageGateWay
 * @ClassName RedisLimiterClient
 * @description:
 * @author: kesen
 * @create: 2022-01-08 17:51
 * @Version 1.0
 **/
@Component
@Slf4j
public class RedisLimiterClient {


    private StringRedisTemplate stringRedisTemplate;

    @Autowired
    public void setStringRedisTemplate(StringRedisTemplate stringRedisTemplate) {
        this.stringRedisTemplate = stringRedisTemplate;
    }

    @Qualifier("getInitRedisScript")
    @Resource
    private RedisScript<Long> rateLimitInitLua;

    @PostConstruct
    public Token initToken() {
        Token token;
        Long currMillSecond = stringRedisTemplate.execute(
                RedisServerCommands::time
        );

        Long acquire = stringRedisTemplate.execute(rateLimitInitLua,
                Collections.singletonList(getKey("message")), currMillSecond.toString(), "1", "10", "10", "rate");
        if (null == acquire) {
            token = Token.FAILED;
            return token;
        }
        if (acquire == 1) {
            token = Token.SUCCESS;
        } else if (acquire == 0) {
            token = Token.SUCCESS;
        } else {
            token = Token.FAILED;
        }
        return token;
    }


    public String getKey(String key) {
        return Constants.RATE_LIMIT_KEY + key;
    }
}
