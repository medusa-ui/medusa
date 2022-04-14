package com.sample.medusa.bug;

import com.sample.medusa.meta.AbstractSeleniumTest;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;


@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT /*, properties = "headless=false" */)
class ConditionalTableIntegrationTest extends AbstractSeleniumTest {

    public static final String CLASS_EXPR_LINE = "tr_expr_line";
    public static final String ID_BTN_ALL = "btn_all";
    public static final String ID_BTN_TRUE = "btn_true";
    public static final List<String> expressionResults = List.of("true","true","false","false","true","false");

    @Test
    @DisplayName("Table should show only valid expressions.")
    void renderFiltered() {
        // only valid expressions
        goTo("/test/bug/conditional-table?filtered=true");
        List<String> expressions = getAllTextByClass(CLASS_EXPR_LINE);
        Assertions.assertEquals(3, expressions.size());
        expressions
                .forEach(
                        line -> Assertions.assertTrue(line.endsWith("true"))
                );
    }

    @Test
    @DisplayName("Table should show all expressions.")
    void renderAll() {
        // all expressions
        goTo("/test/bug/conditional-table?filtered=false");
        List<String> expressions = getAllTextByClass(CLASS_EXPR_LINE);
        Assertions.assertEquals(6, expressions.size());
        AtomicInteger index = new AtomicInteger();
        expressions
                .forEach(
                        line -> Assertions.assertTrue(line.endsWith(expressionResults.get(index.getAndIncrement())))
                );
    }

    @Test
    @DisplayName("JS `Unexpected identifier` error should not occur after DOMChanges")
    void fromAllToOnlyTrue() throws Exception{
        goTo("/test/bug/conditional-table?filtered=false");

        clickById(ID_BTN_TRUE);
        List<String> expressions = getAllTextByClass(CLASS_EXPR_LINE);
        Assertions.assertEquals(3, expressions.size());
        expressions
                .forEach(
                        line -> Assertions.assertTrue(line.endsWith("true"))
                );

        clickById(ID_BTN_ALL);
        expressions = getAllTextByClass(CLASS_EXPR_LINE);
        Assertions.assertEquals(6, expressions.size());

        AtomicInteger index = new AtomicInteger();
        expressions
                .forEach(
                        line -> Assertions.assertTrue(line.endsWith(expressionResults.get(index.getAndIncrement())))
                );
    }

}
