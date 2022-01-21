package io.getmedusa.medusa.core.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotation used to mark a class as a menu item for Hydra.
 * If the class also contains a @UIEventPage, that page gets propagated to the menu item marked under value
 * It can then be used in html as such: &lt;nav h-menu=&quot;top-menu&quot;&gt;&lt;/nav&gt;
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface HydraMenu {

    String value();
    String label() default "";
}
