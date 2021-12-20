package io.getmedusa.medusa.core.injector.tag;

import io.getmedusa.medusa.core.injector.tag.meta.InjectionResult;
import org.jsoup.nodes.Document;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.reactive.function.server.MockServerRequest;
import org.springframework.web.reactive.function.server.ServerRequest;

import java.util.Map;

class ConditionalTagTest {

    private static final String HTML_EQ_SIMPLE = """
                    <m:if condition="some-variable" eq="a">
                        <p>A</p>
                    </m:if>
            """;

    private static final String HTML_EQ_ELSE = """
                    <m:if condition="some-variable" eq="a">
                        <p>A</p>
                        
                        <m:else>
                            <p>B</p>
                        </m:else>
                    </m:if>
            """;

    private static final String HTML_EQ_ELSE_IF = """
                    <m:if condition="some-variable" eq="a">
                        <p>A</p>
                        <m:elseif condition="innerItem" eq="b">
                            <p>B</p>
                        </m:elseif>
                        <m:else>
                            <p>C</p>
                        </m:else>
                    </m:if>
            """;

    //TODO eq w/ variable
    //TODO nested
    //TODO as part of iteration w/ each
    //TODO gt
    //TODO lt
    //TODO gte
    //TODO lte
    //TODO not
    //TODO complex objects

    private static final ConditionalTag TAG = new ConditionalTag();
    private final ServerRequest request = MockServerRequest.builder().build();

    @Test
    void testSimpleHTML() {
        Document htmlA = TAG.inject(new InjectionResult(HTML_EQ_SIMPLE), Map.of("some-variable", "a"), request).getDocument();
        Document htmlB = TAG.inject(new InjectionResult(HTML_EQ_SIMPLE), Map.of("some-variable", "b"), request).getDocument();

        removeNonDisplayedElements(htmlA);
        removeNonDisplayedElements(htmlB);

        System.out.println(htmlA.html());
        System.out.println(htmlB.html());

        Assertions.assertEquals("A", htmlA.text());
        Assertions.assertEquals("", htmlB.text());
    }

    private void removeNonDisplayedElements(Document doc) {
        doc.getElementsByAttributeValue("style", "display:none;").remove();
    }
}
