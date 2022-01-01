package io.getmedusa.medusa.core.util;

import io.getmedusa.medusa.core.injector.tag.TagConstants;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class ElementUtils {

    public static boolean hasTemplateAsParent(Element element) {
        Elements parents = element.parents();
        for(Element parent : parents) {
            if(TagConstants.TEMPLATE_TAG.equals(parent.tagName())) return true;
        }
        return false;
    }

}
