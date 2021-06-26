package io.getmedusa.medusa.core.registry;

import io.getmedusa.medusa.core.util.ExpressionEval;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ConditionalRegistry {

    private static final ConditionalRegistry INSTANCE = new ConditionalRegistry();
    private static final String PREFIX = "$";
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
        return ExpressionEval.eval(parseCondition(condition, variables));

    }

    private String parseCondition(String conditionParsed, Map<String, Object> variables) {
        for(Map.Entry<String, Object> variableEntrySet : variables.entrySet()) {
            conditionParsed = conditionParsed.replace(PREFIX + variableEntrySet.getKey(), variableEntrySet.getValue().toString());
        }
        return conditionParsed;
    }

    public List<String> findByConditionField(String fieldWithChange) {
        final String prefixedField = PREFIX + fieldWithChange;
        final List<String> ids = new ArrayList<>();

        registry.forEach((key, value) -> {
            if (value.contains(prefixedField)) {
                ids.add(key);
            }
        });
        return ids;
    }
}
