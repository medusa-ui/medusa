package com.sample.medusa;

import com.sample.medusa.meta.AbstractSeleniumTest;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.Keys;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class TemplateMIdIntegrationTest extends AbstractSeleniumTest {

    private static final String page = "/test/history";

    @Test
    @DisplayName("Using template with m-id should allow the usage of identical foreach-blocks")
    void test() {
        testInitPage();

        testAddAnHistoricEvent();

        testReloadAndAddAnHistoricEvent();
    }

    void testInitPage() {
        driver.get(BASE + page);

        String pageSource = driver.getPageSource();
        Assertions.assertTrue(pageSource.contains("History has a tendency"));

        Assertions.assertEquals(
                2,
                countOccurrence( pageSource, "t-1515128164"),
                "templates with the same m-id should be possible"
        );
    }

    void testAddAnHistoricEvent() {
        driver.get(BASE + page);

        fillFieldById("input-event","Hello World");
        pressKeyById("input-event", Keys.ENTER);

        String pageSource = driver.getPageSource();

        Assertions.assertEquals(
                2,
                countOccurrence( pageSource, "<p>Hello World</p>"),
                "Since the history repeat itself, one input should output 2 times that input"
        );

        // repeat
        pressKeyById("input-event", Keys.ENTER);
        pageSource = driver.getPageSource();

        Assertions.assertEquals(
                4,
                countOccurrence( pageSource, "<p>Hello World</p>"),
                "Repeating the same input, should double the output"
        );
    }

    void testReloadAndAddAnHistoricEvent() {
        driver.get(BASE + page);

        Assertions.assertEquals(
                4,
                countOccurrence( driver.getPageSource(), "<p>Hello World</p>"),
                "Repeating the same input, should double the output"
        );

        fillFieldById("input-event","Hello World");
        pressKeyById("input-event", Keys.ENTER);


        Assertions.assertEquals(
                6,
                countOccurrence( driver.getPageSource(), "<p>Hello World</p>" ),
                "Adding an extra 'Hello World'? That should be six times now!"
        );
    }
}
