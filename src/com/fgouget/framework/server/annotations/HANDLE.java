package com.fgouget.framework.server.annotations;

import com.fgouget.framework.server.enums.HttpMethods;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.ANNOTATION_TYPE, ElementType.METHOD})
public @interface HANDLE {

    HttpMethods httpMethod() default HttpMethods.GET;

}
