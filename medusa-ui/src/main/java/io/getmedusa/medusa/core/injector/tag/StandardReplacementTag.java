package io.getmedusa.medusa.core.injector.tag;

import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.Collections;
import java.util.Map;

public interface StandardReplacementTag extends Tag {

    String replacement(String attrContent);

    default void replaceAttribute(Elements items, String attribute, String replacementAttribute, Map<String, String> additional) {
        for(Element item : items) {
            item.attr(replacementAttribute, replacement(cleanAttribute(item.attr(attribute))));
            item.removeAttr(attribute);
            for(Map.Entry<String, String> entrySet : additional.entrySet()) {
                item.attr(entrySet.getKey(), entrySet.getValue());
            }
        }
    }

    default void replaceAttribute(Elements items, String attribute, String replacementAttribute) {
        replaceAttribute(items, attribute, replacementAttribute, Collections.emptyMap());
    }

    default String cleanAttribute(String attr) {
        return attr
                .replace("'", "\\'")
                .replace("\"", "\\'");
    }
}
