package io.getmedusa.medusa.core.registry;

import io.getmedusa.medusa.core.util.ExpressionEval;

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
    private static final String PREFIX = "$";

    private ConditionalRegistry() { }

    public static ConditionalRegistry getInstance() {
        return INSTANCE;
    }
    private final Map<String, String> registry = new HashMap<>();

    public void add(String divId, String condition){
        registry.put(divId, condition);
    }
    public String get(String divId) { return registry.get(divId); }

    public boolean evaluate(String divId, Map<String, Object> variables) {
        String condition = registry.get(divId);
        if(null == condition) return false;
        return Boolean.parseBoolean(ExpressionEval.eval(condition, variables));
    }

    public List<String> findByConditionField(String fieldWithChange) {
        final String prefixedField = PREFIX + fieldWithChange;
        final List<String> ids = new ArrayList<>();

        for (Map.Entry<String, String> entry : registry.entrySet()) {
            String key = entry.getKey();
            String value = entry.getValue();
            if (value.contains(prefixedField)) {
                ids.add(key);
            }
        }
        return ids;
    }
}
