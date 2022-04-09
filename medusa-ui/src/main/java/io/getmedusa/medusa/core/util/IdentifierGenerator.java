package io.getmedusa.medusa.core.util;

import io.getmedusa.medusa.core.injector.tag.TagConstants;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

public class IdentifierGenerator {

    private IdentifierGenerator() {}

    //TODO: Not ideal, IDs should be unique but also reproducible
    //Hash is not guaranteed unique, especially based on content (same content = same id)

    public static String generateIfID(String suffix, String html) {
        final Document document = Jsoup.parse(html);
        for(Element textSpans : document.getElementsByAttribute(TagConstants.FROM_VALUE)) {
            textSpans.text("");
        }
        String id = "if-" + Math.abs(document.body().html().hashCode()) + suffix;
        return id;
    }

    public static String generateClassConditionalID(String value) {
        return "c-" + Math.abs(value.hashCode());
    }

    public static String generateGenericMId(String value) {
        return "m-" + Math.abs(value.hashCode());
    }
}
