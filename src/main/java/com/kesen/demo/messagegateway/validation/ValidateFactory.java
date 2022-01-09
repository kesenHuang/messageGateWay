package com.kesen.demo.messagegateway.validation;

import java.util.regex.Pattern;

/**
 * @program: messageGateWay
 * @ClassName ValidateFactory
 * @description:
 * @author: kesen
 * @create: 2022-01-08 13:48
 * @Version 1.0
 **/
public class ValidateFactory {
    public static final Pattern PHONE_REGULAR_PATTERN = Pattern.compile("^[1][34578][0-9]{9}$");
    public static final Pattern USENAME_REGULAR_PATTERN = Pattern.compile("^[A-Za-z0-9]+$");


    public static boolean validate(String reg, String val) {
        return Pattern.compile(reg).matcher(val).find();
    }

    public static boolean validate(Pattern pattern, String val) {
        return pattern.matcher(val).find();
    }
}
