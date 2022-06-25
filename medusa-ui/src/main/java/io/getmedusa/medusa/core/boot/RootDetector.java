package io.getmedusa.medusa.core.boot;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.stereotype.Component;

@Component
public class RootDetector implements BeanPostProcessor {

    @Override
    public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
        RouteDetection.INSTANCE.consider(bean);
        RefDetection.INSTANCE.consider(bean);
        return BeanPostProcessor.super.postProcessBeforeInitialization(bean, beanName);
    }

}
