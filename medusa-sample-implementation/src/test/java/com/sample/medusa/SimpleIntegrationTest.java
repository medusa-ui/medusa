package com.sample.medusa;

import com.sample.medusa.meta.AbstractSeleniumTest;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class SimpleIntegrationTest extends AbstractSeleniumTest {

    @Test
    void testSelenium() {
        String hello = BASE + "/selenium";
        driver.get(hello);
        String pageSource = driver.getPageSource();
        System.out.println(pageSource);
        Assertions.assertTrue(pageSource.contains("Hello Medusa"));
        Assertions.assertTrue(pageSource.contains("SELENIUM !"));
    }

}
