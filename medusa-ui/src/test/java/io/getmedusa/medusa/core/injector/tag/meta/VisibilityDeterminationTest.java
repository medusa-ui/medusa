package io.getmedusa.medusa.core.injector.tag.meta;

import io.getmedusa.medusa.core.injector.tag.TagConstants;
import org.jsoup.nodes.Element;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.reactive.function.server.MockServerRequest;
import org.springframework.web.reactive.function.server.ServerRequest;

import java.util.HashMap;

class VisibilityDeterminationTest {

    protected final ServerRequest request = MockServerRequest.builder().build();

    private static final String HTML_EQ_SIMPLE_NO_QUOTES = """
                    <med:if item="some-variable" eq="a">
                        <p>A</p>
                    </med:if>
            """;

    @Test
    void testNoQuotes() {
        final Element element = new InjectionResult(HTML_EQ_SIMPLE_NO_QUOTES).getDocument().select(TagConstants.CONDITIONAL_TAG).get(0);
        final VisibilityDetermination.ConditionResult result = VisibilityDetermination.getInstance().determine(new HashMap<>(), element, request);
        Assertions.assertEquals("some-variable === 'a'", result.condition());
    }

    private static final String HTML_EQ_SIMPLE = """
                    <med:if item="some-variable" eq="'a'">
                        <p>A</p>
                    </med:if>
            """;

    @Test
    void testQuotes() {
        final Element element = new InjectionResult(HTML_EQ_SIMPLE).getDocument().select(TagConstants.CONDITIONAL_TAG).get(0);
        final VisibilityDetermination.ConditionResult result = VisibilityDetermination.getInstance().determine(new HashMap<>(), element, request);
        Assertions.assertEquals("some-variable === 'a'", result.condition());
    }

    private static final String HTML_EQ_SIMPLE_INT = """
                    <med:if item="some-variable" eq="1">
                        <p>A</p>
                    </med:if>
            """;

    @Test
    void testInteger() {
        final Element element = new InjectionResult(HTML_EQ_SIMPLE_INT).getDocument().select(TagConstants.CONDITIONAL_TAG).get(0);
        final VisibilityDetermination.ConditionResult result = VisibilityDetermination.getInstance().determine(new HashMap<>(), element, request);
        Assertions.assertEquals("some-variable === 1", result.condition());
    }
}
