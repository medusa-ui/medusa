package io.getmedusa.medusa.core.injector.tag;

import io.getmedusa.medusa.core.injector.tag.meta.InjectionResult;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.Collections;

class ConditionalTagTest {

    private String conditionalHTMLSingle =
            "<h1>Hello Medusa 1</h1>\n" +
            "[$if($counter-value > 5)]\n" +
            "    <p>Visible?</p>\n" +
            "[$end]\n" +
            "<p>Hello Medusa 1</p>";

    private String complexerHTML =
            "<h1>Hello Medusa 1</h1>\n" +
            "<p>Hello Mesuda 2</p>\n" +
            "[$if($counter-value > 5)]\n" +
            "    <p>Counter is above 5</p>\n" +
            "[$end]\n" +
            "<p>Hello Mesuda 3</p>\n";

    private String conditionalHTMLMulti = "<p>test</p>\n" +
            "[$if($counter-value > 5)]\n" +
            "    <p>Counter is above 5</p>\n" +
            "[$else]\n" + //if($counter-value < 5)
            "    <p>Counter is under 5</p>\n" +
            "[$end]";

    private final ConditionalTag conditionalTag = new ConditionalTag();

    @Test
    void testPatternMatch() {
        String html = conditionalTag.patternMatch(ConditionalTag.patternFullIf, complexerHTML);
        System.out.println(html);
    }

    @Test
    void testConditionalSimpleInvisible() {
        InjectionResult parsedHTML = conditionalTag.injectWithVariables(new InjectionResult(conditionalHTMLSingle), Collections.singletonMap("counter-value", "1"));
        System.out.println(parsedHTML.getHtml());

        Assertions.assertTrue(parsedHTML.getHtml().startsWith("<h1>Hello Medusa 1</h1>\n" + "<div id=\"if-"));
        Assertions.assertTrue(parsedHTML.getHtml().endsWith("\" style=\"display:none;\">\n    <p>Visible?</p>\n</div>\n<p>Hello Medusa 1</p>"));
    }

    @Test
    void testConditionalSimpleVisible() {
        InjectionResult parsedHTML = conditionalTag.injectWithVariables(new InjectionResult(conditionalHTMLSingle), Collections.singletonMap("counter-value", "10"));
        System.out.println(parsedHTML.getHtml());

        Assertions.assertTrue(parsedHTML.getHtml().startsWith("<h1>Hello Medusa 1</h1>\n" + "<div id=\"if-"));
        Assertions.assertTrue(parsedHTML.getHtml().endsWith("\">\n    <p>Visible?</p>\n</div>\n<p>Hello Medusa 1</p>"));
    }

    @Test
    void testElse() {
        InjectionResult parsedHTML = conditionalTag.injectWithVariables(new InjectionResult(conditionalHTMLMulti), Collections.singletonMap("counter-value", "10"));
        System.out.println(parsedHTML.getHtml());
        Assertions.assertFalse(parsedHTML.getHtml().contains("$else"));
    }
}
