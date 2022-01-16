package io.getmedusa.medusa.core.registry;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Keeps a singleton instance with all iteration conditions and their iteration id
 * As such we can do a lookup if certain variable change would have impact on a condition,
 * and by consequence require re-interpretation of the iteration itself.
 * Gets instantiated and filled up with values during beforeInitialization post-processing, like most registries.
 */
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
        Set<String> result = new HashSet<>();
        Set<String> templateIds = registry.get(condition);
        if(null != templateIds) {
            for (String templateId : templateIds) {
                result.add(templateId.split("#")[0]);
            }
        }
        return result;
    }

    public String findByTemplateId(String templateId) {
        for(Map.Entry<String, Set<String>> entry : registry.entrySet()) {
            for(String entrySubset : entry.getValue()) {
                if(entrySubset.equals(templateId)) {
                    return entry.getKey();
                }
            }
        }
        return null;
    }

    public Map<String, String> listConditions() {
        Map<String, String> map = new HashMap<>();
        for(Map.Entry<String, Set<String>> entry : registry.entrySet()) {
            for(String entrySubset : entry.getValue()) {
                map.put(entrySubset, entry.getKey());
            }
        }
        return map;
    }

}
