package io.getmedusa.medusa.core.tags.annotation;

import org.springframework.stereotype.Component;
import org.thymeleaf.templatemode.TemplateMode;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Component
public @interface MedusaTag {
    int precedence = 99999;
    TemplateMode templateMode = TemplateMode.HTML;
    String prefix = "m";
    String namespace= "http://www.getmedusa.io";
}