package com.kesen.demo.messagegateway.validation.annotation;

import com.kesen.demo.messagegateway.validation.ValidationConstants;
import com.kesen.demo.messagegateway.validation.validator.CaValidatorSpecificString;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.*;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * @program: messageGateWay
 * @ClassName CaValidSpecificString
 * @description:
 * @author: kesen
 * @create: 2022-01-08 13:49
 * @Version 1.0
 **/
@Target({METHOD, FIELD, ANNOTATION_TYPE, CONSTRUCTOR, PARAMETER})
@Retention(RUNTIME)
@Constraint(validatedBy = CaValidatorSpecificString.class)
@Documented
public @interface CaValidSpecificString {
    String type() default ValidationConstants.SpecificStrValidateType.USER;

    String message() default "只允许数字，大小写字母";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};


    /**
     * Defines several {@link CaValidSpecificString} annotations on the same element.
     *
     * @see CaValidSpecificString
     */
    @Target({ METHOD, FIELD, ANNOTATION_TYPE, CONSTRUCTOR, PARAMETER })
    @Retention(RUNTIME)
    @Documented
    @interface List {
        CaValidSpecificString[] value();
    }
}
