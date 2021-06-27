package io.getmedusa.medusa.core.registry;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class IterationRegistry {

    private static final IterationRegistry INSTANCE = new IterationRegistry();
    public static IterationRegistry getInstance() {
        return INSTANCE;
    }
    private final Map<String, List<String>> registry = new HashMap<>();

    public void add(String templateId, String condition){
        List<String> templates = registry.getOrDefault(condition, new ArrayList<>());
        templates.add(templateId);
        registry.put(condition, templates);
    }

    public List<String> findRelatedToValue(String condition) {
        return registry.get(condition);
    }

}
