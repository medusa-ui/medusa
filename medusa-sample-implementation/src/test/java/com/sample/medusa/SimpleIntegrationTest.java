package com.sample.medusa;

import com.sample.medusa.meta.AbstractSeleniumTest;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT /*, properties = "headless:false" */ )
class SimpleIntegrationTest extends AbstractSeleniumTest {

    @Test
    void testSelenium() {
        driver.get(BASE);
        System.out.println(driver.getPageSource());
        Assertions.assertTrue(driver.getPageSource().contains("Hello Medusa"));
    }

    @Test
    @DisplayName("open a second tab")
    void testOpenTab() {
        String original = goTo("/");
        Assertions.assertTrue(driver.getPageSource().contains("Hello Medusa"));

        String window = openInNewTab("/test/click-event");
        Assertions.assertTrue(driver.getPageSource().contains("Hello click event"));

        switchToWindowOrTab(original);
        Assertions.assertTrue(driver.getPageSource().contains("Hello Medusa"));

        switchToWindowOrTab(window);
        Assertions.assertTrue(driver.getPageSource().contains("Hello click event"));

    }

    @Test
    @DisplayName("open a second window")
    void testOpenWindow() {
        String original = goTo("/");
        Assertions.assertTrue(driver.getPageSource().contains("Hello Medusa"));

        String window = openInNewWindow("/test/click-event");
        Assertions.assertTrue(driver.getPageSource().contains("Hello click event"));

        switchToWindowOrTab(original);
        Assertions.assertTrue(driver.getPageSource().contains("Hello Medusa"));

        switchToWindowOrTab(window);
        Assertions.assertTrue(driver.getPageSource().contains("Hello click event"));

    }
}
