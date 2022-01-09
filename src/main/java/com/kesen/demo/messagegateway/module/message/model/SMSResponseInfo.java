package com.kesen.demo.messagegateway.module.message.model;

import lombok.Data;
import lombok.ToString;

import java.io.Serializable;

/**
 * @program: messageGateWay
 * @ClassName ResponseInfo
 * @description:
 * @author: kesen
 * @create: 2022-01-08 14:42
 * @Version 1.0
 **/
@Data
@ToString
public class SMSResponseInfo implements Serializable {

    private String res_code;

    private String res_message;
}
