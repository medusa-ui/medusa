package io.getmedusa.medusa.core.annotation;

import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;

public class MEventPageHandler {
    private final ConfigurableListableBeanFactory beanFactory;
    private final Class clazz;

    public MEventPageHandler(ConfigurableListableBeanFactory beanFactory, Class clazz) {
        this.beanFactory = beanFactory;
        this.clazz = clazz;
    }

    public Object get() {
        return beanFactory.createBean(clazz, ConfigurableListableBeanFactory.AUTOWIRE_CONSTRUCTOR, true);
    }
}
