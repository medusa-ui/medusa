package io.getmedusa.medusa.core.annotation;

import io.getmedusa.medusa.core.registry.EventHandlerRegistry;
import io.getmedusa.medusa.core.registry.RouteRegistry;
import io.getmedusa.medusa.core.util.FilenameHandler;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.stereotype.Component;

@Component
public class UIEventPostProcessor implements BeanPostProcessor {

    @Override
    public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
        if(bean instanceof UIEventController) {
            final UIEventController eventController = (UIEventController) bean;
            final UIEventPage UIEventPage = eventController.getClass().getAnnotation(UIEventPage.class);
            final String htmlFile = FilenameHandler.removeExtension(FilenameHandler.normalize(UIEventPage.file()));
            RouteRegistry.getInstance().add(UIEventPage.path(), htmlFile);
            EventHandlerRegistry.getInstance().add(htmlFile, eventController);
        }
        return BeanPostProcessor.super.postProcessBeforeInitialization(bean, beanName);
    }

    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
        return BeanPostProcessor.super.postProcessAfterInitialization(bean, beanName);
    }
}
