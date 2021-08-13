package io.getmedusa.medusa.core.registry;

import io.getmedusa.medusa.core.injector.GenericMAttribute;
import io.getmedusa.medusa.core.util.IdentifierGenerator;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Keeps a singleton instance with all generic m-attributes and their conditions
 * As such we can do a lookup if certain variable change would have impact on a condition,
 * and by consequence require re-interpretation of the true/false condition of a generic m-attribute.
 * Gets instantiated and filled up with values during beforeInitialization post-processing, like most registries.
 */
public class GenericMRegistry {

    private static final GenericMRegistry INSTANCE = new GenericMRegistry();
    private static final String PREFIX = "$";

    private GenericMRegistry() { }
    public static GenericMRegistry getInstance() {
        return INSTANCE;
    }

    private final Map<String, RegistryItem> registry = new HashMap<>();

    public String add(String expression, GenericMAttribute attribute) {
        final String key = IdentifierGenerator.generateGenericMId(expression);
        registry.put(key, new RegistryItem(expression, attribute));
        return key;
    }

    public List<String> findByConditionField(String fieldWithChange) {
        final String prefixedField = PREFIX + fieldWithChange;
        final List<String> ids = new ArrayList<>();

        for (Map.Entry<String, RegistryItem> entry : registry.entrySet()) {
            String key = entry.getKey();
            RegistryItem value = entry.getValue();
            if (value.expression.contains(prefixedField)) {
                ids.add(key);
            }
        }
        return ids;
    }

    public RegistryItem get(String impactedDivId) {
        return registry.get(impactedDivId);
    }

    public static class RegistryItem {
        public final String expression;
        public final GenericMAttribute attribute;

        public RegistryItem(String expression, GenericMAttribute attribute) {
            this.expression = expression;
            this.attribute = attribute;
        }
    }
}
