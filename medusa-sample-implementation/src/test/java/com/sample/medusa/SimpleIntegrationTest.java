package com.sample.medusa;

import com.sample.medusa.meta.AbstractSeleniumTest;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class SimpleIntegrationTest extends AbstractSeleniumTest {

    @Test
    void testSelenium() {
        driver.get(BASE);
        System.out.println(driver.getPageSource());
        Assertions.assertTrue(driver.getPageSource().contains("Hello Medusa"));
    }

}
