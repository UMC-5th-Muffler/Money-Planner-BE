package com.umc5th.muffler.global.swagger;

import com.umc5th.muffler.global.response.code.ErrorCode;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface ErrorResponses {
    ErrorCode[] value() default {};
}
