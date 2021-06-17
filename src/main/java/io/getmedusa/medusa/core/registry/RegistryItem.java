package io.getmedusa.medusa.core.registry;

import java.lang.reflect.Method;

public class RegistryItem {

    private final Object bean;
    private final Method method;

    public RegistryItem(Object bean, Method method) {
        this.bean = bean;
        this.method = method;
    }

    public Object getBean() {
        return bean;
    }

    public Method getMethod() {
        return method;
    }
}
