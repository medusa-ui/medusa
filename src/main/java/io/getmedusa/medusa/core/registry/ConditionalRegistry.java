package io.getmedusa.medusa.core.registry;

import io.getmedusa.medusa.core.util.ExpressionEval;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ConditionalRegistry {

    private static final ConditionalRegistry INSTANCE = new ConditionalRegistry();

    public static ConditionalRegistry getInstance() {
        return INSTANCE;
    }

    private Map<String, String> registry = new HashMap<>();

    public void add(String divId, String condition){
        registry.put(divId, condition);
    }

    public String get(String divId) { return registry.get(divId); }

    public boolean evaluate(String divId, Map<String, Object> variables) {
        String condition = registry.get(divId);
        if(null == condition) return false;
        return ExpressionEval.eval(parseCondition(condition, variables));

    }

    public String parseCondition(String conditionParsed, Map<String, Object> variables) {
        for(Map.Entry<String, Object> variableEntryset : variables.entrySet()) {
            conditionParsed = conditionParsed.replace("$" + variableEntryset.getKey(), variableEntryset.getValue().toString());
        }
        return conditionParsed;
    }

    public List<String> findByConditionField(String fieldWithChange) {
        List<String> ids = new ArrayList<>();
        for(Map.Entry<String, String> entrySet : registry.entrySet()) {
            if(entrySet.getValue().contains("$" + fieldWithChange)) {
                ids.add(entrySet.getKey());
            }
        }
        return ids;
    }
}
