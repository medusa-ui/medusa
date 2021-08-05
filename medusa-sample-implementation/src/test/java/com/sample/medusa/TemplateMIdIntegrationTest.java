package com.sample.medusa;

import com.sample.medusa.meta.AbstractSeleniumTest;
import org.junit.jupiter.api.*;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Arrays;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class TemplateMIdIntegrationTest extends AbstractSeleniumTest {

    private static final String page = "/test/history";

    @Test
    @Order(1)
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

    @Test
    @Order(2)
    void testAddAnHistoricEvent() {
        driver.get(BASE + page);

        setValue("input-event","Hello World");
        mOnEnter("input-event");

        String pageSource = driver.getPageSource();

        Assertions.assertEquals(
                2,
                countOccurrence( pageSource, "<p>Hello World</p>"),
                "Since the history repeat itself, one input should output 2 times that input"
        );

        // repeat
        mOnEnter("input-event");
        pageSource = driver.getPageSource();

        Assertions.assertEquals(
                4,
                countOccurrence( pageSource, "<p>Hello World</p>"),
                "Repeating the same input, should double the output"
        );
    }

    @Test
    @Order(3)
    void testReloadAndAddAnHistoricEvent() {
        driver.get(BASE + page);

        setValue("input-event","Hello World");
        mOnEnter("input-event");

        String pageSource = driver.getPageSource();

        Assertions.assertEquals(
                6,
                countOccurrence( pageSource, "<p>Hello World</p>" ),
                "Adding an extra 'Hello World'? That should be six times now!"
        );
    }

    private int countOccurrence(String source, String search) {
        return source.split(search).length - 1;
    }
}
