package com.kesen.demo.messagegateway.validation.validator;

import com.kesen.demo.messagegateway.validation.ValidationConstants;
import com.kesen.demo.messagegateway.validation.ValidateFactory;
import com.kesen.demo.messagegateway.validation.annotation.CaValidSpecificString;
import lombok.extern.slf4j.Slf4j;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

/**
 * @program: messageGateWay
 * @ClassName CaValidatorSpecificString
 * @description:
 * @author: kesen
 * @create: 2022-01-08 13:54
 * @Version 1.0
 **/
@Slf4j
public class CaValidatorSpecificString implements ConstraintValidator<CaValidSpecificString, String> {
    private String type;


    @Override
    public void initialize(CaValidSpecificString constraintAnnotation) {
        this.type = constraintAnnotation.type();
    }

    @Override
    public boolean isValid(String value, ConstraintValidatorContext constraintValidatorContext) {
        if (null == value) {
            return true;
        }
        switch (type) {
            case ValidationConstants.SpecificStrValidateType.USER:
                return ValidateFactory.validate(ValidateFactory.USENAME_REGULAR_PATTERN, value);
            default:
                return ValidateFactory.validate(type, value);
        }
    }
}
