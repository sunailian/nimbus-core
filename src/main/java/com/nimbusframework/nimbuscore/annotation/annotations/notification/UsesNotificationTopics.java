package com.nimbusframework.nimbuscore.annotation.annotations.notification;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface UsesNotificationTopics {
    UsesNotificationTopic[] value();
}
