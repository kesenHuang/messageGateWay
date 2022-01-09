package com.kesen.demo.messagegateway.redis;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.connection.RedisServerCommands;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.script.RedisScript;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.Collections;

/**
 * @program: messageGateWay
 * @ClassName DistributedLimiter
 * @description:
 * @author: kesen
 * @create: 2022-01-08 18:12
 * @Version 1.0
 **/
@Slf4j
@Component
public class DistributedLimiter {
    @Autowired
    private RedisTemplate<String, String> redisTemplate;

    @Qualifier("getRedisScript")
    @Resource
    RedisScript<Long> rateLimitLua;

    @Qualifier("getTelLimiterScript")
    @Resource
    RedisScript<Long> telLimitLua;


    public boolean distributedLimit(String key, String limit, String reserved_percent_s, String max_wait_mill_second_s) {
        Long id = 0L;
        Long currMillSecond = redisTemplate.execute(
                RedisServerCommands::time
        );


        try {
            id = redisTemplate.execute(rateLimitLua, Collections.singletonList(key), limit + "", currMillSecond + "", reserved_percent_s, max_wait_mill_second_s);
        } catch (Exception e) {
            log.error("error", e);
        }

        if (id == 0L) {
            return true;
        } else {
            return false;
        }
    }

    public boolean telLimit(String key, String limit, String length) {
        Long id = 0L;
        try {
            id = redisTemplate.execute(telLimitLua, Collections.singletonList(key), limit, length);
        } catch (Exception e) {
            log.error("error", e);
        }

        if (id == 1L) {
            return true;
        } else {
            return false;
        }
    }
}
