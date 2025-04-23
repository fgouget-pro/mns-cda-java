package com.fgouget.framework.server.annotations;

import java.lang.annotation.Retention;

import static java.lang.annotation.RetentionPolicy.RUNTIME;

@Retention(RUNTIME)
public @interface Controller {
    String route() default "";
    boolean autoStart = true;
}
