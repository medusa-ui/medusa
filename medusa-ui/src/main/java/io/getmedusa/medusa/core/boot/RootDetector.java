package io.getmedusa.medusa.core.boot;

import io.getmedusa.medusa.core.boot.hydra.HydraConnectionController;
import io.getmedusa.medusa.core.config.MedusaAutoConfiguration;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

@Component
@Order(1)
public class RootDetector implements BeanPostProcessor {

    private final HydraConnectionController hydraConnectionController;

    public RootDetector(@Autowired(required = false) HydraConnectionController hydraConnectionController) {
        this.hydraConnectionController = hydraConnectionController;
        StaticResourcesDetection.INSTANCE.detectAvailableStaticResources();
    }

    @Override
    public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
        RouteDetection.INSTANCE.consider(bean);
        RefDetection.INSTANCE.consider(bean);
        MethodDetection.INSTANCE.consider(bean);
        return BeanPostProcessor.super.postProcessBeforeInitialization(bean, beanName);
    }

    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
        if(beanName.equalsIgnoreCase(MedusaAutoConfiguration.class.getName()) && hydraConnectionController != null) { //execute post init, as last
            //hydra should execute last, because it depends on routes being initialized
            hydraConnectionController.enableHydraConnectivity();
        }
        return BeanPostProcessor.super.postProcessAfterInitialization(bean, beanName);
    }
}
