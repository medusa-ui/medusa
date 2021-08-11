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
        final UIEventPage uiEventPage = bean.getClass().getAnnotation(UIEventPage.class);
        if(uiEventPage != null) {
            final String htmlFile = FilenameHandler.removeExtension(FilenameHandler.normalize(uiEventPage.file()));
            RouteRegistry.getInstance().add(uiEventPage.path(), htmlFile);
            EventHandlerRegistry.getInstance().add(uiEventPage.path(), bean);
        }
        return BeanPostProcessor.super.postProcessBeforeInitialization(bean, beanName);
    }

    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
        return BeanPostProcessor.super.postProcessAfterInitialization(bean, beanName);
    }
}
