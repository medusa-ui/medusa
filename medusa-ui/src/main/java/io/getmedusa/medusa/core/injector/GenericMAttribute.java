package io.getmedusa.medusa.core.injector;

/**
 * Listing of generic m-attributes, which are generic replacement tags based on a true/false value.
 * These are attributes such as m-disabled. If the applied expression is true, the disabled flag is added in HTML, if it is false, it is removed.
 * This is partially configured here (for the server-side handling of things) and partially handled in websocket.js, _M.handleMAttributeChange()
 */
public enum GenericMAttribute {

    DISABLED("disabled=\"true\""),
    HIDE("style=\"display:none;\"");

    private final String valueWhenTrue;
    private final String valueWhenFalse;

    GenericMAttribute(String valueWhenTrue) {
        this.valueWhenFalse = "";
        this.valueWhenTrue = valueWhenTrue;
    }

    GenericMAttribute(String valueWhenTrue, String valueWhenFalse) {
        this.valueWhenFalse = valueWhenFalse;
        this.valueWhenTrue = valueWhenTrue;
    }

    public String getValueWhenTrue() {
        return valueWhenTrue;
    }

    public String getValueWhenFalse() {
        return valueWhenFalse;
    }

    public static GenericMAttribute findValueByTagName(String tagNameWithM) {
        return GenericMAttribute.valueOf(tagNameWithM.substring(2).toUpperCase());
    }
}
