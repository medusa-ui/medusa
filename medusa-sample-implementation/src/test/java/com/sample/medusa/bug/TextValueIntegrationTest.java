package com.sample.medusa.bug;

import com.sample.medusa.meta.AbstractSeleniumTest;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class TextValueIntegrationTest extends AbstractSeleniumTest {

    @Test
    void counterTest() {
        goTo("/bug/text-value");
        // initial value
        Assertions.assertEquals("0", getFromValue("counter.value"));
        Assertions.assertEquals("0", getFromValue("counter.count"));
        Assertions.assertEquals("a", getFromValue("counter.key"));

        // increment
        clickById("increment");
        Assertions.assertEquals("1", getFromValue("counter.value"));
        Assertions.assertEquals("1", getFromValue("counter.count"));
        Assertions.assertEquals("b", getFromValue("counter.key"));
        clickById("increment");
        clickById("increment");
        clickById("increment");
        clickById("increment");
        Assertions.assertEquals("5", getFromValue("counter.value"));
        Assertions.assertEquals("5", getFromValue("counter.count"));
        Assertions.assertEquals("f", getFromValue("counter.key"));

        // reset
        clickById("reset");
        Assertions.assertEquals("0", getFromValue("counter.value"));
        Assertions.assertEquals("0", getFromValue("counter.count"));
        Assertions.assertEquals("a", getFromValue("counter.key"));
    }

}
