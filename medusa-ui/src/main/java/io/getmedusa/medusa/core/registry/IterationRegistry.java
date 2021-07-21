package io.getmedusa.medusa.core.registry;

import java.util.*;

public class IterationRegistry {

    private static final IterationRegistry INSTANCE = new IterationRegistry();
    public static IterationRegistry getInstance() {
        return INSTANCE;
    }
    private final Map<String, Set<String>> registry = new HashMap<>();

    public void add(String templateId, String condition){
        Set<String> templates = registry.getOrDefault(condition, new HashSet<>());
        templates.add(templateId);
        registry.put(condition, templates);
    }

    public Set<String> findRelatedToValue(String condition) {
        return registry.get(condition);
    }

}
