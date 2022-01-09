package com.kesen.demo.messagegateway.module.message.model;

import lombok.Data;
import lombok.ToString;

import java.io.Serializable;
import java.util.Map;

/**
 * @program: messageGateWay
 * @ClassName StatusResponseInfo
 * @description:
 * @author: kesen
 * @create: 2022-01-08 22:44
 * @Version 1.0
 **/
@Data
@ToString
public class StatusResponseInfo implements Serializable {
    private String res_code;

    private String res_message;

    private Map<String, Long> qosSuccessMap;
    private Map<String, Long> qosRejectMap;

    private Integer totalReceived;
}
