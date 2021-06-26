package io.getmedusa.medusa.core.annotation;

import io.getmedusa.medusa.core.registry.EventHandlerRegistry;
import io.getmedusa.medusa.core.registry.RouteRegistry;
import org.springframework.aop.support.AopUtils;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;

@Component
public class UIEventPostProcessor implements BeanPostProcessor {

    @Override
    public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
        if(bean instanceof UIEventController) {
            final UIEventController eventController = (UIEventController) bean;
            final PageSetup pageSetup = eventController.setupPage();
            RouteRegistry.getInstance().add(pageSetup);
            EventHandlerRegistry.getInstance().add(pageSetup.getHtmlFile(), eventController);
        }
        return null;
    }
}
