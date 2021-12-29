package io.getmedusa.medusa.core.registry;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Keeps a singleton instance with all conditions and their respective ids.
 * As such we can do a lookup if certain variable change would have impact on an if-condition, and by consequence require re-interpretation of the if statement.
 * Gets instantiated and filled up with values during beforeInitialization post-processing, like most registries.
 */
public class ConditionalRegistry {
    private static final ConditionalRegistry INSTANCE = new ConditionalRegistry();

    private ConditionalRegistry() { }

    public static ConditionalRegistry getInstance() {
        return INSTANCE;
    }
    private final Map<String, String> registry = new HashMap<>();

    public void add(String divId, String condition){
        registry.put(divId, condition);
    }
    public String get(String divId) { return registry.get(divId); }

    public List<String> findByConditionField(String fieldWithChange) {
        final List<String> ids = new ArrayList<>();
        for (Map.Entry<String, String> entry : registry.entrySet()) {
            if (entry.getValue().contains(fieldWithChange)) ids.add(entry.getKey());
        }
        return ids;
    }
}
