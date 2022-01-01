package io.getmedusa.medusa.core.injector;

import java.util.Collections;
import java.util.Map;

/**
 * Listing of generic m-attributes, which are generic replacement tags based on a true/false value.
 * These are attributes such as m-disabled. If the applied expression is true, the disabled flag is added in HTML, if it is false, it is removed.
 * This is partially configured here (for the server-side handling of things) and partially handled in websocket.js, _M.handleMAttributeChange()
 */
public enum GenericMAttribute {

    DISABLED(Map.of("disabled", "true")),
    HIDE(Map.of("style", "display:none;"));

    private final Map<String, String> valueWhenTrue;
    private final Map<String, String> valueWhenFalse;

    GenericMAttribute(Map<String, String> valueWhenTrue) {
        this.valueWhenFalse = Collections.emptyMap();
        this.valueWhenTrue = valueWhenTrue;
    }

    GenericMAttribute(Map<String, String> valueWhenTrue, Map<String, String> valueWhenFalse) {
        this.valueWhenFalse = valueWhenFalse;
        this.valueWhenTrue = valueWhenTrue;
    }

    public Map<String, String> getValueWhenTrue() {
        return valueWhenTrue;
    }

    public Map<String, String> getValueWhenFalse() {
        return valueWhenFalse;
    }

    public Map<String, String> determineValue(Object variableValue) {
        boolean val = Boolean.parseBoolean(variableValue.toString());
        if(val) {
            return getValueWhenTrue();
        } else {
            return getValueWhenFalse();
        }
    }
}
