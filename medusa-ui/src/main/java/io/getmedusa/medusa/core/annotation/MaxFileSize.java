package io.getmedusa.medusa.core.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Validation annotation used to define what the size restrictions are. <br>
 * <strong>Use a string to specify the size and unit, e.g., "1MB", "500KB", "2GB", etc.</strong> <br>
 * You can use a Spring property as well, for example ${files.max-size:1MB} <br>
 * As with other validation annotations, this affects both front-end and backend validation.
 * Because this is a validation, @Valid is needed just like other validations. <br>
 * Not using this, instead makes the default of 10MB apply
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.PARAMETER})
public @interface MaxFileSize {
    String value();

    String message() default "File size exceeds the maximum allowed size";
}