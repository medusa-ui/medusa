package io.getmedusa.medusa.core.annotation;

import java.lang.annotation.*;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface UIEvent {
    String value() default "";
}
