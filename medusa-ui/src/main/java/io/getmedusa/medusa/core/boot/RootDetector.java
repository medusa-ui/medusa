package io.getmedusa.medusa.core.boot;

import io.getmedusa.medusa.core.boot.hydra.HydraConnectionController;
import io.getmedusa.medusa.core.router.request.Route;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.stereotype.Component;

@Component
public class RootDetector implements BeanPostProcessor {

    private final HydraConnectionController hydraConnectionController;

    public RootDetector(HydraConnectionController hydraConnectionController) {
        this.hydraConnectionController = hydraConnectionController;
    }

    private String lastClass = null;

    @Override
    public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
        RouteDetection.INSTANCE.consider(bean);
        RefDetection.INSTANCE.consider(bean);
        this.lastClass = bean.getClass().getName();
        return BeanPostProcessor.super.postProcessBeforeInitialization(bean, beanName);
    }

    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
        if(lastClass.equals(bean.getClass().getName())) {
            hydraConnectionController
                    .getActiveService()
                    .getEndpoints().addAll(
                            RouteDetection.INSTANCE.getDetectedRoutes()
                                    .stream()
                                    .map(Route::getPath)
                                    .toList());
            hydraConnectionController.sendRegistration();
        }
        return BeanPostProcessor.super.postProcessAfterInitialization(bean, beanName);
    }
}
