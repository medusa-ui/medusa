package com.sample.medusa.bug;

import com.sample.medusa.meta.AbstractSeleniumTest;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;


@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT /*, properties = "headless=false" */)
class ConditionalIdClashIntegrationTest extends AbstractSeleniumTest {

    public static final String CLASS_EXPR_LINE = "expr-line";
    public static final String ID_BTN_ALL = "btn-all";
    public static final String ID_BTN_TRUE = "btn-true";
    public static final List<String> expressionResults = List.of("true","true","false","true","false");

    @Test
    @DisplayName("DOMChanges: from 'all true expressions' to 'all expressions'")
    void fromTrueToAll() {
        goTo("/test/bug/conditional-id-clash?allTrue=true");
        List<String> expressions = getAllTextByClass(CLASS_EXPR_LINE);
        Assertions.assertEquals(3, expressions.size());
        expressions
            .forEach(
                    line -> Assertions.assertTrue(line.endsWith("true"))
            );

        clickById(ID_BTN_ALL);
        expressions = getAllTextByClass(CLASS_EXPR_LINE);
        Assertions.assertEquals(5, expressions.size());

        AtomicInteger index = new AtomicInteger();
        expressions
            .forEach(
                    line -> Assertions.assertTrue(line.endsWith(expressionResults.get(index.getAndIncrement())))
            );
    }

    @Test
    @DisplayName("DOMChanges: from 'all expressions' to 'only those that are true'")
    void fromAllToOnlyTrue() throws Exception{
        goTo("/test/bug/conditional-id-clash?allTrue=false");
        List<String> expressions = getAllTextByClass(CLASS_EXPR_LINE);
        Assertions.assertEquals(5, expressions.size());
        AtomicInteger index = new AtomicInteger();
        expressions
                .forEach(
                        line -> Assertions.assertTrue(line.endsWith(expressionResults.get(index.getAndIncrement())))
                );

        clickById(ID_BTN_TRUE);
        expressions = getAllTextByClass(CLASS_EXPR_LINE);
        Assertions.assertEquals(3, expressions.size());
        expressions
                .forEach(
                        line -> Assertions.assertTrue(line.endsWith("true"))
                );

    }

}
