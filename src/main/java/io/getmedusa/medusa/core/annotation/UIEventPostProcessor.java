package io.getmedusa.medusa.core.annotation;

import io.getmedusa.medusa.core.registry.RouteRegistry;
import io.getmedusa.medusa.core.registry.UIEventRegistry;
import org.springframework.aop.support.AopUtils;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;

@Component
public class UIEventPostProcessor implements BeanPostProcessor {

    private final UIEventRegistry registry = UIEventRegistry.getInstance();

    @Override
    public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
        if(bean instanceof UIEventController) {
            Class<?> clazz = getTargetClass(bean);

            RouteRegistry.getInstance().add(((UIEventController) bean).setupPage());

            for (Method m : clazz.getDeclaredMethods()) {
                if (m.isAnnotationPresent(UIEvent.class)) {
                    UIEvent uiEvent = AnnotationUtils.getAnnotation(m, UIEvent.class);
                    if(uiEvent == null) throw new IllegalStateException("Annotation UIEvent is present, so should never have to deal with a null value here");
                    String uiEventName = uiEvent.value();
                    if("".equals(uiEventName)) uiEventName = m.getName();
                    registry.addMethod(uiEventName, bean, m);
                }
            }

        }
        return null;
    }

    private Class<?> getTargetClass(Object obj) throws BeansException {
        Class<?> clazz = obj.getClass();
        if (AopUtils.isAopProxy(obj)) {
            clazz = AopUtils.getTargetClass(obj);
        }
        return clazz;
    }
}
