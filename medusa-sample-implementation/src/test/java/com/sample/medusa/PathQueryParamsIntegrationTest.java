package com.sample.medusa;

import com.sample.medusa.meta.AbstractSeleniumTest;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class PathQueryParamsIntegrationTest extends AbstractSeleniumTest {

    @Test
    public void testAllProvided() {
        driver.get(BASE + "/test/upper/selenium/normal/test?value=12&person=2");
        String pageSource = driver.getPageSource();

        Assertions.assertTrue(pageSource.contains("SELENIUM"));
        Assertions.assertTrue(pageSource.contains("test"));
        Assertions.assertTrue(pageSource.contains("美杜莎"));
        Assertions.assertTrue(pageSource.contains("12"));
    }

    @Test
    public void testDefaultQueryParam() {
        driver.get(BASE + "/test/upper/test/normal/other?person=1");
        String pageSource = driver.getPageSource();

        Assertions.assertTrue(pageSource.contains("TEST"));
        Assertions.assertTrue(pageSource.contains("other"));
        Assertions.assertTrue(pageSource.contains("메두사"));
        Assertions.assertTrue(pageSource.contains("nothing"));
    }
}
