package com.nimbusframework.nimbuscore.annotation.annotations.function;

import com.nimbusframework.nimbuscore.annotation.annotations.NimbusConstants;
import com.nimbusframework.nimbuscore.annotation.annotations.function.repeatable.BasicServerlessFunctions;

import java.lang.annotation.*;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Repeatable(BasicServerlessFunctions.class)
public @interface BasicServerlessFunction {
    String cron() default "";
    int timeout() default 10;
    int memory() default 1024;
    String[] stages() default {NimbusConstants.stage};
}
