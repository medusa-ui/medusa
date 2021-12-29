package io.getmedusa.medusa.core.injector.tag;

import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public interface StandardReplacementTag extends Tag {

    String replacement(String attrContent);

    default void replaceAttribute(Elements mClickItems, String attribute, String replacementAttribute) {
        for(Element mClickItem : mClickItems) {
            mClickItem.attr(replacementAttribute, replacement(cleanAttribute(mClickItem.attr(attribute))));
            mClickItem.removeAttr(attribute);
        }
    }

    default String cleanAttribute(String attr) {
        return attr
                .replace("'", "\\'")
                .replace("\"", "\\'");
    }
}
