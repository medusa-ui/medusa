package io.getmedusa.medusa.core.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/* Opposite to UIEventPage, MEventPage is NOT a @Component, */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface MEventPage {

    String path();
    String file();

}
