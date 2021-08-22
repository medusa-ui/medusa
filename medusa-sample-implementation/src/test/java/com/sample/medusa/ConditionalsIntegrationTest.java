package com.sample.medusa;

import com.sample.medusa.meta.AbstractSeleniumTest;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class ConditionalsIntegrationTest extends AbstractSeleniumTest {

    @Test
    void testConditionals() {
        goTo("/test/conditionals");
        Assertions.assertTrue(driver.getPageSource().contains("Hello conditionals"));

        Assertions.assertEquals("0", getFromValue("counter-value"));
        Assertions.assertEquals("Always true", getTextById("always-true"));
        Assertions.assertEquals("", getTextById("always-false"));

        clickById("increase-counter-button"); //1
        Assertions.assertEquals("1", getFromValue("counter-value"));

        clickById("increase-counter-button"); //2
        Assertions.assertEquals("Counter is below 2 and 5", getTextByClass("conditional-result"));

        clickById("increase-counter-button"); //3
        Assertions.assertEquals("Counter is above 2", getTextByClass("conditional-result"));

        clickById("increase-counter-button"); //4
        Assertions.assertEquals("Counter is above 2", getTextByClass("conditional-result"));

        clickById("increase-counter-button"); //5
        clickById("increase-counter-button"); //6
        Assertions.assertEquals("Counter is above 5", getTextByClass("conditional-result"));

        refreshPage();

        Assertions.assertEquals("6", getFromValue("counter-value"));
        Assertions.assertEquals("Counter is above 5", getTextByClass("conditional-result"));
    }

}
