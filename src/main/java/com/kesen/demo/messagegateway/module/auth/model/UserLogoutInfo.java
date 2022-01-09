package com.kesen.demo.messagegateway.module.auth.model;

import com.kesen.demo.messagegateway.validation.annotation.CaValidSpecificString;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.io.Serializable;

/**
 * @program: messageGateWay
 * @ClassName UserLogoutInfo
 * @description:
 * @author: kesen
 * @create: 2022-01-08 13:32
 * @Version 1.0
 **/
@Data
public class UserLogoutInfo implements Serializable {

    @NotBlank(message = "用户名不能为空")
    @Size(min = 3, max = 32, message = "用户名长度最小长度为3个字符，最大长度不能超过32个字符")
    @CaValidSpecificString(message = "用户登录名，只允许数字，大小写字母")
    private String userName;

    @NotBlank(message = "会话ID不能为空")
    private String sessionId;
}
