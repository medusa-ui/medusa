package io.getmedusa.medusa.core.util;

import io.getmedusa.medusa.core.injector.tag.meta.ForEachElement;

public class IdentifierGenerator {

    private IdentifierGenerator() {}

    //TODO: Not ideal, IDs should be unique but also reproducable
    //Hash is not guaranteed unique, especially based on content (same content = same id)

    public static String generateIfID(String value) {
        return "if-" + Math.abs(value.hashCode());
    }

    public static String generateTemplateID(ForEachElement value) {
        String prefix = "";
        if(value.getParent() != null) {
            prefix = generateTemplateID(value.getParent()) + "#";
        }

        return prefix + "t-" + Math.abs(value.blockHTML.hashCode());
    }

    public static String generateClassConditionalID(String value) {
        return "c-" + Math.abs(value.hashCode());
    }

    public static String generateGenericMId(String value) {
        return "m-" + Math.abs(value.hashCode());
    }

}
