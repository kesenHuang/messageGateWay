package com.kesen.demo.messagegateway.module.auth.model;

import com.kesen.demo.messagegateway.common.pojo.ResponseInfo;
import lombok.Data;
import lombok.ToString;

/**
 * @program: messageGateWay
 * @ClassName UserLoginResponse
 * @description:
 * @author: kesen
 * @create: 2022-01-08 10:38
 * @Version 1.0
 **/
@Data
@ToString
public class UserLoginResponse extends ResponseInfo {

   private String sessionId;
}
