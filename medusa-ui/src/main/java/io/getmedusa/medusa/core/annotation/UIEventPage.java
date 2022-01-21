package io.getmedusa.medusa.core.annotation;

import org.springframework.stereotype.Component;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotation used to mark a class as an event handler for Medusa.
 * This annotation will implicitly make this class a Spring @Component.
 * <br><br>
 * You <i>must</i> define the following attributes: <br><ul>
 * <li><strong>path</strong> = The controller path this event handler's page can be called on </li>
 * <li><strong>file</strong> = The HTML file associated with this event handler </li>
 * </ul>
 * You may optionally add the following attribute: <br><ul>
 * <li><strong>setup</strong> = defines the method used to provide initial {@link PageAttributes} (defaults to setupAttributes)</li>
 * </ul>
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Component
public @interface UIEventPage {

    String path();
    String file();
    String setup() default "setupAttributes";
}
