package com.sample.medusa;

import com.sample.medusa.meta.AbstractSeleniumTest;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class ClickEventIntegrationTest extends AbstractSeleniumTest {

    @Test
    void testClickEventWorks() {
        driver.get(BASE);
        Assertions.assertTrue(driver.getPageSource().contains("Hello Medusa"));

        Assertions.assertEquals("0", getFromValue("my-counter"));
        clickById("my-counter-btn");
        String counter = getTextById("my-counter-btn").replace("Increase my counter by ", "").trim();
        Assertions.assertEquals(counter, getFromValue("my-counter"));
    }

}
