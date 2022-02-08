package com.sample.medusa.bug;

import com.sample.medusa.meta.AbstractSeleniumTest;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class ConditionalElseIfEvalIntegrationTest extends AbstractSeleniumTest {

    @Test
    void testPageLoadValues() {
        goTo("/test/bug/elseif-eval/-1");
        Assertions.assertEquals("-1", getFromValue("counter"));
        Assertions.assertEquals("Counter is < 3.", getTextByClass("counter-eval"));
        Assertions.assertEquals(1 , getAllTextByClass("counter-eval").size());

        goTo("/test/bug/elseif-eval/0");
        Assertions.assertEquals("0", getFromValue("counter"));
        Assertions.assertEquals("Counter is < 3.", getTextByClass("counter-eval"));
        Assertions.assertEquals(1 , getAllTextByClass("counter-eval").size());

        goTo("/test/bug/elseif-eval/1");
        Assertions.assertEquals("1", getFromValue("counter"));
        Assertions.assertEquals("Counter is 1.", getTextByClass("counter-eval"));
        Assertions.assertEquals(1 , getAllTextByClass("counter-eval").size());

        goTo("/test/bug/elseif-eval/2");
        Assertions.assertEquals("2", getFromValue("counter"));
        Assertions.assertEquals("Counter is < 3.", getTextByClass("counter-eval"));
        Assertions.assertEquals(1 , getAllTextByClass("counter-eval").size());

        goTo("/test/bug/elseif-eval/3");
        Assertions.assertEquals("3", getFromValue("counter"));
        Assertions.assertEquals("Counter is 3, 4 or 5.", getTextByClass("counter-eval"));
        Assertions.assertEquals(1 , getAllTextByClass("counter-eval").size());

        goTo("/test/bug/elseif-eval/4");
        Assertions.assertEquals("4", getFromValue("counter"));
        Assertions.assertEquals("Counter is 3, 4 or 5.", getTextByClass("counter-eval"));
        Assertions.assertEquals(1 , getAllTextByClass("counter-eval").size());

        goTo("/test/bug/elseif-eval/5");
        Assertions.assertEquals("5", getFromValue("counter"));
        Assertions.assertEquals("Counter is 3, 4 or 5.", getTextByClass("counter-eval"));
        Assertions.assertEquals(1 , getAllTextByClass("counter-eval").size());

        goTo("/test/bug/elseif-eval/6");
        Assertions.assertEquals("6", getFromValue("counter"));
        Assertions.assertEquals("Counter is > 5.", getTextByClass("counter-eval"));
        Assertions.assertEquals(1 , getAllTextByClass("counter-eval").size());
    }

    @Test
    void testJSEvalAfterDomChange() {
        goTo("/test/bug/elseif-eval/0");
        Assertions.assertEquals("0", getFromValue("counter"));
        Assertions.assertEquals("Counter is < 3.", getTextByClass("counter-eval"));
        clickById("plus");
        Assertions.assertEquals("1", getFromValue("counter"));
        Assertions.assertEquals("Counter is 1.", getAllTextByClass("counter-eval").get(0));
        // "Counter is < 3" is also visible 
        Assertions.assertEquals( 1 , getAllTextByClass("counter-eval").size());
    }
}
