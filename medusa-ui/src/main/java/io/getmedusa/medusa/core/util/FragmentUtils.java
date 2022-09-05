package io.getmedusa.medusa.core.util;

import io.getmedusa.medusa.core.tags.meta.JSEventAttributeProcessor;
import org.jsoup.nodes.Attribute;
import org.jsoup.nodes.Attributes;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

public class FragmentUtils {

    protected static final String NULL = "null";
    protected static final String SINGLE_QUOTE = "'";

    private FragmentUtils() {}

    public static String addFragmentRefToHTML(String html, String fragmentRef) {
        return html.replace(JSEventAttributeProcessor.FRAGMENT_REPLACEMENT, (fragmentRef == null) ? NULL : SINGLE_QUOTE + fragmentRef + SINGLE_QUOTE);
    }

    public static boolean determineIfFragment(Document document) {
        if(document.head().childNodes().isEmpty()) {
            final Element firstElement = document.body().firstElementChild();
            if (null != firstElement && !firstElement.tagName().endsWith(":fragment")) {
                final Attributes attributes = firstElement.attributes();
                for (Attribute attribute : attributes) {
                    if (attribute.getKey().endsWith(":ref")) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

}
