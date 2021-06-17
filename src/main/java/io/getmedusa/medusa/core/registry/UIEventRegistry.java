package io.getmedusa.medusa.core.registry;

import io.getmedusa.medusa.core.injector.DOMChange;
import org.springframework.beans.BeanInstantiationException;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class UIEventRegistry {

    private static final UIEventRegistry INSTANCE = new UIEventRegistry();

    public static UIEventRegistry getInstance() {
        return INSTANCE;
    }

    private final Map<String, RegistryItem> registry = new HashMap<>();

    public void addMethod(String key, Object bean, Method method) {
        if(registry.containsKey(key)) throw new BeanInstantiationException(UIEventRegistry.class, "You cannot implement an event '" + key + "' twice.");
        this.registry.put(key, new RegistryItem(bean, method));
    }

    public List<DOMChange> execute(String key, List<String> parameters) throws Exception {
        RegistryItem registryItem = registry.get(key);
        return new ArrayList<>((List<DOMChange>) registryItem.getMethod().invoke(registryItem.getBean(), parameters));
    }
}
