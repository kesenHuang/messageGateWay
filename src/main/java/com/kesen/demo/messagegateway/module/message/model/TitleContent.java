package com.kesen.demo.messagegateway.module.message.model;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

/**
 * @program: messageGateWay
 * @ClassName TitleContent
 * @description:
 * @author: kesen
 * @create: 2022-01-08 14:21
 * @Version 1.0
 **/
@Data

public class TitleContent {


    @Size(min = 1, max = 64, message = "标题长度最小长度为1个字符，最大长度不能超过64个字符")
    @NotBlank
    private String title;

    @NotBlank
    private String content;
}
