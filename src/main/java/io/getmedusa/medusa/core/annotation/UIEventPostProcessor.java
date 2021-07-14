package io.getmedusa.medusa.core.annotation;

import io.getmedusa.medusa.core.registry.EventHandlerRegistry;
import io.getmedusa.medusa.core.registry.RouteRegistry;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.stereotype.Component;

@Component
public class UIEventPostProcessor implements BeanPostProcessor {

    @Override
    public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
        if(bean instanceof UIEventController) {
            final UIEventController eventController = (UIEventController) bean;
            final PageSetup pageSetup = eventController.setupPage();
            RouteRegistry.getInstance().add(pageSetup.getGetPath(), pageSetup.getHtmlFile());
            EventHandlerRegistry.getInstance().add(pageSetup.getHtmlFile(), eventController);
        } else if(bean instanceof UIEventPage) {
            final UIEventPage controller = (UIEventPage) bean;
            RouteRegistry.getInstance().add(controller.getPath(), controller.getHtmlFile());
            EventHandlerRegistry.getInstance().add(controller.getHtmlFile(), controller);
        }
        return BeanPostProcessor.super.postProcessBeforeInitialization(bean, beanName);
    }

    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
        return BeanPostProcessor.super.postProcessAfterInitialization(bean, beanName);
    }
}
