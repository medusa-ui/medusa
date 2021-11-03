package com.sample.medusa;

import com.sample.medusa.meta.AbstractSeleniumTest;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class CustomSetupIntegrationTest extends AbstractSeleniumTest {

    @Test
    @DisplayName("By default, PageAttributes are initialised in the setupAttributes-method")
    void defaultPage(){
        goTo("/test/setup/default?name=Hello World!");

        String pageSource = driver.getPageSource();
        Assertions.assertTrue(pageSource.contains("guest") || pageSource.contains("user"));
        Assertions.assertTrue(pageSource.contains("Hello World!"));
        Assertions.assertTrue(pageSource.contains("0")); //counter

        clickById("btn_count");
        pageSource = driver.getPageSource();
        Assertions.assertTrue(pageSource.contains("1")); //counter
    }

    @Test
    @DisplayName("Only using ServerRequest to initialise PageAttributes should be possible")
    void requestPage(){
        goTo("/test/setup/request?name=Hello Medusa!");

        String pageSource = driver.getPageSource();
        Assertions.assertTrue(pageSource.contains("Hello Medusa!"));
        Assertions.assertTrue(pageSource.contains("0")); //counter

        clickById("btn_count");
        pageSource = driver.getPageSource();
        Assertions.assertTrue(pageSource.contains("1")); //counter
    }

    @Test
    @DisplayName("Setting up PageAttributes should not be mandatory")
    void emptyPage(){
        goTo("/test/setup/empty");

        String pageSource = driver.getPageSource();
        Assertions.assertTrue(pageSource.contains("counter"));

        clickById("btn_count");
        pageSource = driver.getPageSource();
        Assertions.assertTrue(pageSource.contains("1")); //counter
    }

    @Test
    @DisplayName("Only using SecurityContext to initialise PageAttributes should be possible")
    void securePage(){
        goTo("/test/setup/secure");

        String pageSource = driver.getPageSource();
        Assertions.assertTrue(pageSource.contains("guest") || pageSource.contains("user"));
        Assertions.assertTrue(pageSource.contains("0")); //counter

        clickById("btn_count");
        pageSource = driver.getPageSource();
        Assertions.assertTrue(pageSource.contains("1")); //counter
    }
}
