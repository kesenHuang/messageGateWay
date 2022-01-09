package com.kesen.demo.messagegateway.module.message.controller;

import com.kesen.demo.messagegateway.common.enums.GlobalErrorCode;
import com.kesen.demo.messagegateway.common.exception.BusinessException;
import com.kesen.demo.messagegateway.common.pojo.ResponseInfo;
import com.kesen.demo.messagegateway.module.message.model.SMSResponseInfo;
import com.kesen.demo.messagegateway.module.message.model.SmsRequestBody;
import com.kesen.demo.messagegateway.module.message.model.StatusResponseInfo;
import com.kesen.demo.messagegateway.module.message.model.TitleContent;
import com.kesen.demo.messagegateway.redis.Constants;
import com.kesen.demo.messagegateway.redis.DistributedLimiter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import javax.validation.Valid;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * @program: messageGateWay
 * @ClassName MessageController
 * @description:
 * @author: kesen
 * @create: 2022-01-08 14:10
 * @Version 1.0
 **/
@RestController
@Slf4j
public class MessageController {

    @Autowired
    DistributedLimiter distributedLimiter;

    @Value("${message.sms.host}")
    private String host;
    @Value("${message.sms.port}")
    private String port;
  /*  @Value("$(message.sms.localhost)")
    //private String localhost = "47.113.90.197";*/
    private String sms = "/v2/emp/templateSms/sendSms";
    private RestTemplate restTemplate;

    private StringRedisTemplate stringRedisTemplate;

    SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");


    @Autowired
    public void setRestTemplate(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    @Autowired
    public void setStringRedisTemplate(StringRedisTemplate stringRedisTemplate) {
        this.stringRedisTemplate = stringRedisTemplate;
    }

    // @PreAuth
    // @DistriLimiter 暂时不适用切面，影响性能
    @PostMapping(value = "/directmessage")
    public ResponseInfo directmessage(@RequestParam(required = true) String tels, @RequestParam(required = true) String qos,
                                      @RequestParam(required = true) String userName, @RequestParam(required = true) String sessionId, @RequestBody @Valid TitleContent titleContent) throws InterruptedException {


        // 鉴权，鉴于性能问题，不采用切面
        String o = stringRedisTemplate.opsForValue().get(Constants.userRedisKeyPre + userName);
        if (null == o) {
            throw new BusinessException(GlobalErrorCode.UN_RESIGIGERED.getCode(), GlobalErrorCode.UN_RESIGIGERED.getMessage());
        }
        String session = stringRedisTemplate.opsForValue().get(Constants.userSessionIdRedisKeyPre + userName);
        if (null == session) {
            throw new BusinessException(GlobalErrorCode.UNAUTHORIZED.getCode(), GlobalErrorCode.UNAUTHORIZED.getMessage());
        }
        if (!session.equals(sessionId)) {
            throw new BusinessException(GlobalErrorCode.FORBIDDEN.getCode(), GlobalErrorCode.FORBIDDEN.getMessage());
        }


        // 限流
        String key = "message";
        String limit = "1";
        boolean acquired = true;
        int times = 0;

        // 手机号限流
        boolean acquired1;
        do {     // 一个电话号码只能1秒钟发送一次
            acquired1 = distributedLimiter.telLimit(getTelKey(tels), "1", "1");
            if (!acquired1) {
                Thread.sleep(1000);
                times++;
                continue;
            } else {
                break;
            }
        } while (times < 3);
        if (!acquired1) {
            log.warn("超过1秒一个电话号码请求:{}", tels);
            return ResponseInfo.error(437, "号码限流");
        }
        // 请求频次限流
        switch (qos) {
            case "1":
                acquired = distributedLimiter.distributedLimit(getKey(key), limit, "20", "0");
                break;
            case "2":
                acquired = distributedLimiter.distributedLimit(getKey(key), limit, "50", "0");
                break;
            case "3":
                acquired = distributedLimiter.distributedLimit(getKey(key), limit, "80", "0");
                break;
            default:
                break;
        }
        if (!acquired) {
            log.warn("超过1秒10次请求:{},{}", Thread.currentThread().getName(), qos);
            return ResponseInfo.error(437, "限流");

        }

        SmsRequestBody smsRequestBody = new SmsRequestBody();
        smsRequestBody.setQos(qos);
        smsRequestBody.setAcceptor_tel(tels);
        smsRequestBody.setTemplate_param(titleContent);

        String transformDate = simpleDateFormat.format(new Date());
        smsRequestBody.setTimestamp(transformDate);
        ResponseEntity<SMSResponseInfo> responseInfoResponseEntity = null;
        Long time = System.currentTimeMillis();
        boolean success = true;
        try {
            responseInfoResponseEntity = restTemplate.postForEntity("http://" + host + ":" + port + sms, smsRequestBody, SMSResponseInfo.class);
        } catch (Throwable e) {
            success = false;
            return ResponseInfo.error(429, "当前处于限流中");
        } finally {
            log.info(time + ":" + qos + "[" + success + "]" + ":" + "[" + tels + "}");
        }
        SMSResponseInfo smsResponseInfo = responseInfoResponseEntity.getBody();
        String res_code = smsResponseInfo.getRes_code();
        if ("0".equals(res_code)) {
            return ResponseInfo.success();
        } else {
            return ResponseInfo.error(Integer.parseInt(res_code), smsResponseInfo.getRes_message());
        }

    }

    @PostMapping(value = "/reset")
    public ResponseInfo reset() {
        ResponseEntity<SMSResponseInfo> responseInfoResponseEntity = restTemplate.postForEntity("http://" + host + ":" + port + "/v2/emp/templateSms/reset", null, SMSResponseInfo.class);
        SMSResponseInfo smsResponseInfo = responseInfoResponseEntity.getBody();
        String res_code = smsResponseInfo.getRes_code();
        if ("0".equals(res_code)) {
            return ResponseInfo.success();
        } else {
            return ResponseInfo.error(Integer.parseInt(res_code), smsResponseInfo.getRes_message());
        }
    }


    @PostMapping(value = "/status")
    public ResponseInfo status() {
        ResponseEntity<StatusResponseInfo> responseInfoResponseEntity = restTemplate.getForEntity("http://" + host + ":" + port + "/v2/emp/templateSms/currentStatus", StatusResponseInfo.class);
        StatusResponseInfo StatusResponseInfo = responseInfoResponseEntity.getBody();
        String res_code = StatusResponseInfo.getRes_code();
        if ("0".equals(res_code)) {
            log.info(StatusResponseInfo.toString());
            return ResponseInfo.success();
        } else {
            return ResponseInfo.error(Integer.parseInt(res_code), StatusResponseInfo.getRes_message());
        }
    }


    public String getKey(String key) {
        return Constants.RATE_LIMIT_KEY + key;
    }

    public String getTelKey(String key) {
        return Constants.TEL_LIMIT_KEY + key;
    }
}
