package com.kesen.demo.messagegateway.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.script.RedisScript;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.scripting.ScriptSource;
import org.springframework.scripting.support.ResourceScriptSource;

/**
 * Redis 配置类
 * @author KESEN
 */
@Configuration
@Slf4j
public class RedisAutoConfiguration {

    /**
     * 创建 RedisTemplate Bean，使用 JSON 序列化方式
     */
    @Bean
    public RedisTemplate<String, Object> redisTemplate(RedisConnectionFactory factory) {
        // 创建 RedisTemplate 对象
        RedisTemplate<String, Object> template = new RedisTemplate<>();
        // 设置 RedisConnection 工厂
        template.setConnectionFactory(factory);
        // 使用 String 序列化方式，序列化 KEY 。
        template.setKeySerializer(RedisSerializer.string());
        template.setHashKeySerializer(RedisSerializer.string());
        // 使用 JSON 序列化方式（库是 Jackson ），序列化 VALUE 。
        template.setValueSerializer(RedisSerializer.json());
        template.setHashValueSerializer(RedisSerializer.json());
        return template;
    }

 /*   @Bean
    public StringRedisTemplate stringRedisTemplate(RedisConnectionFactory factory) {
        StringRedisTemplate stringRedisTemplate = new StringRedisTemplate();
        // 设置 RedisConnection 工厂
        stringRedisTemplate.setConnectionFactory(factory);
        // 使用 String 序列化方式，序列化 KEY 。
        stringRedisTemplate.setKeySerializer(RedisSerializer.string());
        stringRedisTemplate.setHashKeySerializer(RedisSerializer.string());
        // 使用 JSON 序列化方式（库是 Jackson ），序列化 VALUE 。
        stringRedisTemplate.setValueSerializer(RedisSerializer.json());
        stringRedisTemplate.setHashValueSerializer(RedisSerializer.json());
        return stringRedisTemplate;
    }*/

    @Bean("rateLimitInitLua")
    public RedisScript<Long> getInitRedisScript() {
        RedisScript redisScript = null;
        try {
            ScriptSource scriptSource = new ResourceScriptSource(new ClassPathResource("/scripts/rate_limiter_init.lua"));
            redisScript = RedisScript.of(scriptSource.getScriptAsString(), Long.class);
        } catch (Exception e) {
            log.error("error", e);
        }
        return redisScript;

    }

    @Bean("rateLimitLua")
    public RedisScript<Long> getRedisScript() {
        RedisScript redisScript = null;
        try {
            ScriptSource scriptSource = new ResourceScriptSource(new ClassPathResource("/scripts/rate_limiter.lua"));
            redisScript = RedisScript.of(scriptSource.getScriptAsString(), Long.class);
        } catch (Exception e) {
            log.error("error", e);
        }
        return redisScript;

    }

    @Bean("telLimitLua")
    public RedisScript<Long> getTelLimiterScript() {
        RedisScript<Long> redisScript = null;
        try {
            ScriptSource scriptSource = new ResourceScriptSource(new ClassPathResource("/scripts/tel_limiter.lua"));
            redisScript = RedisScript.of(scriptSource.getScriptAsString(), Long.class);
        } catch (Exception e) {
            log.error("error", e);
        }
        return redisScript;
    }

}
