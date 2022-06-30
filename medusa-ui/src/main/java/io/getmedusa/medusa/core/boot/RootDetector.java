package io.getmedusa.medusa.core.boot;

import io.getmedusa.medusa.core.boot.hydra.HydraConnectionController;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.stereotype.Component;

@Component
public class RootDetector implements BeanPostProcessor {

    private final HydraConnectionController hydraConnectionController;

    public RootDetector(@Autowired(required = false) HydraConnectionController hydraConnectionController) {
        this.hydraConnectionController = hydraConnectionController;
    }

    @Override
    public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
        RouteDetection.INSTANCE.consider(bean);
        RefDetection.INSTANCE.consider(bean);
        return BeanPostProcessor.super.postProcessBeforeInitialization(bean, beanName);
    }

    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
        return BeanPostProcessor.super.postProcessAfterInitialization(bean, beanName);
    }
}
