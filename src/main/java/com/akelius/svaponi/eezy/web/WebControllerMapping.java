package com.akelius.svaponi.eezy.web;

import java.lang.annotation.*;

@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface WebControllerMapping {

    String value() default "";
}
