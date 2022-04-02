package com.sample.medusa.bug;

import com.sample.medusa.meta.AbstractSeleniumTest;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class ClickAssumeIntIntegrationTest extends AbstractSeleniumTest {

    @Test
    void sendJavaNumber(){
        goTo("/bug/click/42");
        Assertions.assertEquals("Integer: 42", getTextById("number-value"));

        // load a number after an event
        clickById("load-long");
        Assertions.assertEquals("Long: 16481396653467", getTextById("number-value"));
        clickById("load-long2");
        Assertions.assertEquals("Long: 16481396653468", getTextById("number-value"));
        clickById("load-int");
        Assertions.assertEquals("Integer: 1648", getTextById("number-value"));
        clickById("load-double");
        Assertions.assertEquals("Double: 42.1223699988888", getTextById("number-value"));
        clickById("load-double2");
        Assertions.assertEquals("Double: 16481396653468.5", getTextById("number-value"));
        clickById("load-float");
        Assertions.assertEquals("Float: 42.12237", getTextById("number-value")); // rounded

        // load a long on load
        goTo("/bug/click/16481396653467L");
        Assertions.assertEquals("Long: 16481396653467", getTextById("number-value"));
        // load a double on load
        goTo("/bug/click/3.1415d");
        Assertions.assertEquals("Double: 3.1415", getTextById("number-value"));
    }

}
