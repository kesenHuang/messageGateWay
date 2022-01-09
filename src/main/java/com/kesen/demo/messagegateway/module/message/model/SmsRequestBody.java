package com.kesen.demo.messagegateway.module.message.model;

import lombok.Data;
import lombok.ToString;

import java.io.Serializable;

/**
 * @program: messageGateWay
 * @ClassName SmsRequestBody
 * @description:
 * @author: kesen
 * @create: 2022-01-08 14:38
 * @Version 1.0
 **/
@Data
@ToString
public class SmsRequestBody implements Serializable {

    private String qos;

    private String acceptor_tel;

    private TitleContent template_param;

    private String timestamp;
}
