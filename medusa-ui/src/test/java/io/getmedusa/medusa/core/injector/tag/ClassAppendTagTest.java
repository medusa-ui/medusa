package io.getmedusa.medusa.core.injector.tag;

import org.jsoup.nodes.Document;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.Map;

class ClassAppendTagTest extends AbstractTest {

    public static final String HTML_CLASS_APPEND = "<p class=\"existing-class another-existing-class\" m:class-append=\"a\">Hello world</p>";

    @Test
    void testSimple() {
        Document doc = inject(HTML_CLASS_APPEND, Map.of("a", "abcd"));
        System.out.println(doc.html());
        Assertions.assertEquals("<p class=\"existing-class another-existing-class abcd\" data-base-class=\"existing-class another-existing-class\" data-from=\"a\">Hello world</p>", doc.getElementsByTag("p").outerHtml());
    }

}
