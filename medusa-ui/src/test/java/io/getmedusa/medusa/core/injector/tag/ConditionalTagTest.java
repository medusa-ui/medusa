package io.getmedusa.medusa.core.injector.tag;

import io.getmedusa.medusa.core.injector.tag.meta.InjectionResult;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.Collections;

class ConditionalTagTest {

    private final String conditionalHTMLSingle =
            """
                    <h1>Hello Medusa 1</h1>
                    [$if($counter-value > 5)]
                        <p>Visible?</p>
                    [$end if]
                    <p>Hello Medusa 1</p>""";

    //if($counter-value < 5)
    private final String conditionalHTMLMulti = """
            <p>test</p>
            [$if($counter-value > 5)]
                <p>Counter is above 5</p>
            [$else]
                <p>Counter is under 5</p>
            [$end if]""";

    private final ConditionalTag conditionalTag = new ConditionalTag();

    @Test
    void testConditionalSimpleInvisible() {
        InjectionResult parsedHTML = conditionalTag.injectWithVariables(new InjectionResult(conditionalHTMLSingle), Collections.singletonMap("counter-value", "1"));
        System.out.println(parsedHTML.getHtml());

        Assertions.assertTrue(parsedHTML.getHtml().startsWith("<h1>Hello Medusa 1</h1>\n" + "<div class=\"if-"));
        Assertions.assertTrue(parsedHTML.getHtml().endsWith("\" style=\"display:none;\">\n    <p>Visible?</p>\n</div>\n<p>Hello Medusa 1</p>"));
    }

    @Test
    void testConditionalSimpleVisible() {
        InjectionResult parsedHTML = conditionalTag.injectWithVariables(new InjectionResult(conditionalHTMLSingle), Collections.singletonMap("counter-value", "10"));
        System.out.println(parsedHTML.getHtml());

        Assertions.assertTrue(parsedHTML.getHtml().startsWith("<h1>Hello Medusa 1</h1>\n" + "<div class=\"if-"));
        Assertions.assertTrue(parsedHTML.getHtml().endsWith("\">\n    <p>Visible?</p>\n</div>\n<p>Hello Medusa 1</p>"));
    }

    @Test
    void testElse() {
        InjectionResult parsedHTML = conditionalTag.injectWithVariables(new InjectionResult(conditionalHTMLMulti), Collections.singletonMap("counter-value", "10"));
        System.out.println(parsedHTML.getHtml());
        Assertions.assertFalse(parsedHTML.getHtml().contains("$else"));
    }
}
