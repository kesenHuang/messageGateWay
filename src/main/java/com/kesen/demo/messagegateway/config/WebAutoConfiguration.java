package com.kesen.demo.messagegateway.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

/**
 * @program: messageGateWay
 * @ClassName WebAutoConfiguration
 * @description:
 * @author: kesen
 * @create: 2022-01-08 14:30
 * @Version 1.0
 **/
@Configuration
@Slf4j
public class WebAutoConfiguration {

    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }
}
