package com.sample.medusa.bug;

import com.sample.medusa.meta.AbstractSeleniumTest;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class ClickAssumeIntIntegrationTest extends AbstractSeleniumTest {

    @Test
    void sendJavaNumber(){
        goTo("/bug/click/42");
        Assertions.assertEquals("Long: 42", getTextById("number-value"));

        // load a number after an event
        clickById("load-long");
        Assertions.assertEquals("Long: 16481396653467", getTextById("number-value"));
        clickById("load-double");
        Assertions.assertEquals("Double: 42.1223699988888", getTextById("number-value"));
        clickById("load-float");
        Assertions.assertEquals("Float: 42.12237", getTextById("number-value")); // rounded

        // load a long on load
        goTo("/bug/click/16481396653467");
        Assertions.assertEquals("Long: 16481396653467", getTextById("number-value"));
    }

}
