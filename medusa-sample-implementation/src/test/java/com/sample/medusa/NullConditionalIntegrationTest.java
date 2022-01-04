package com.sample.medusa;

import com.sample.medusa.meta.AbstractSeleniumTest;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class NullConditionalIntegrationTest extends AbstractSeleniumTest {

    @Test
    void testNullConditional() {
        goTo("/test/null-condition");
        // on page open
        Assertions.assertTrue(driver.getPageSource().contains("Hello <code>null</code>-condition"));
        Assertions.assertEquals("", getFromValue("null-condition"));
        Assertions.assertEquals("YES", getTextByClass("conditional-result"));

        // set actual value
        clickById("set_btn");
        Assertions.assertEquals("'some data...'", getFromValue("null-condition"));
        Assertions.assertEquals("NO", getTextByClass("conditional-result"));

        // set null value
        clickById("reset_btn");
        Assertions.assertEquals("", getFromValue("null-condition"));
        Assertions.assertEquals("YES", getTextByClass("conditional-result"));
    }
}