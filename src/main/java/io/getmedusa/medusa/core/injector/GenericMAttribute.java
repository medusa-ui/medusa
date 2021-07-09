package io.getmedusa.medusa.core.injector;

public enum GenericMAttribute {

    DISABLED("disabled=\"true\"", "disabled=\"false\""),
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
