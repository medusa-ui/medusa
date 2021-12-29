package io.getmedusa.medusa.core.injector.tag;

public class TagConstants {

    //iteration - tags
    public static final String ITERATION_TAG = "m:foreach"; //each on div
    public static final String ITERATION_TAG_COLLECTION_ATTR = "collection";
    public static final String ITERATION_TAG_EACH_ATTR = "eachName";

    //iteration - attributes
    public static final String M_EACH = "m-each"; //each on div
    public static final String TEMPLATE_ID = "template-id"; //template id on div
    public static final String INDEX = "index"; //index on div
    public static final String M_ID = "m-id"; //template id on template itself
    public static final Object TEMPLATE_TAG = "template";

    //text - tags
    public static final String TEXT_TAG = "m:text";
    public static final String TEXT_TAG_ITEM_ATTR = "item";
    public static final String M_VALUE = "m:value";
    public static final String FROM_VALUE = "from-value";

    //conditional - tags
    public static final String CONDITIONAL_TAG = "m:if"; //each on div
    public static final String CONDITIONAL_TAG_CONDITION_ATTR = "condition";
    public static final String CONDITIONAL_TAG_EQUALS = "eq";
    public static final String CONDITIONAL_TAG_NOT = "not";
    public static final String CONDITIONAL_TAG_GREATER_THAN = "gt";
    public static final String CONDITIONAL_TAG_GREATER_THAN_OR_EQ = "gte";
    public static final String CONDITIONAL_TAG_LESS_THAN = "lt";
    public static final String CONDITIONAL_TAG_LESS_THAN_OR_EQ = "lte";

    //conditional - attributes
    public static final String M_IF = "m-if";
    public static final String M_ELSE = "m:else";
    public static final String M_ELSEIF = "m:elseif";

    //mclick - attributes
    public static final String M_CLICK = "m:click";
    public static final String M_CLICK_REPLACEMENT = "onclick";

    //mchange - attributes;
    public static final String M_CHANGE = "m:change";
    public static final String M_CHANGE_REPLACEMENT = "oninput";

    //monenter - attributes
    public static final String M_ONENTER = "m:onenter";
    public static final String M_ONENTER_REPLACEMENT = "onkeyup";

    public static final String M_ONENTER_ADDITIONAL = "onkeydown";
    public static final String PREVENT_DEFAULT = "_M.preventDefault(event)";


    private TagConstants() {}
}
