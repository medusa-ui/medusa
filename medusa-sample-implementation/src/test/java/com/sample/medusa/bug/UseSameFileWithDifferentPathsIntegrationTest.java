package com.sample.medusa.bug;

import com.sample.medusa.meta.AbstractSeleniumTest;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class UseSameFileWithDifferentPathsIntegrationTest extends AbstractSeleniumTest {

    @Test
    void testBothEventHandlersWork() {
        goTo("/test/bug/hello/123");
        Assertions.assertTrue(driver.getPageSource().contains("HelloUIEventHandler"));
        Assertions.assertTrue(driver.getPageSource().contains("123"));

        Assertions.assertEquals("0", getTextById("counter-value"));
        Assertions.assertEquals("Medusa in trouble 0", driver.getTitle());
        clickById("interact");
        Assertions.assertEquals("1", getTextById("counter-value"));
        Assertions.assertEquals("Medusa in trouble 1", driver.getTitle());

        goTo("/test/bug/hello/321");
        Assertions.assertTrue(driver.getPageSource().contains("HelloUIEventHandler"));
        Assertions.assertTrue(driver.getPageSource().contains("321"));
        Assertions.assertEquals("Medusa in trouble 1", driver.getTitle());

        Assertions.assertEquals("1", getTextById("counter-value"));
        clickById("interact");
        Assertions.assertEquals("2", getTextById("counter-value"));
        Assertions.assertEquals("Medusa in trouble 2", driver.getTitle());

        goTo("/test/bug/welcome");

        Assertions.assertTrue(driver.getPageSource().contains("WelcomeUIEventHandler"));
        Assertions.assertTrue(driver.getPageSource().contains("0"));

        Assertions.assertEquals("0", getTextById("counter-value"));
        Assertions.assertEquals("Medusa in trouble 0", driver.getTitle());
        clickById("interact");
        Assertions.assertEquals("1", getTextById("counter-value"));
        Assertions.assertEquals("Medusa in trouble 1", driver.getTitle());
    }

}
