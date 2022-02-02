package com.sample.medusa.bug;

import com.sample.medusa.meta.AbstractSeleniumTest;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class ConditionalStringIntegrationTest extends AbstractSeleniumTest {

    @Test
    public void testWordsEqualTo(){
        // initial value
        goTo("/test/bug/conditional/string");
        Assertions.assertTrue(driver.getPageSource().contains("Medusa in trouble"));
        Assertions.assertEquals("", getFromValue("word"));
        Assertions.assertEquals("word is empty.", getTextByClass("empty-result"));
        Assertions.assertEquals("\"\" != \"Hello\"", getTextByClass("hello-result"));

        // say Hello
        clickById("hello_btn");
        Assertions.assertEquals("Hello", getFromValue("word"));
        Assertions.assertEquals("word is Hello.", getTextByClass("empty-result"));
        Assertions.assertEquals("\"Hello\" == \"Hello\"", getTextByClass("hello-result"));
        Assertions.assertEquals("\"Hello\" != \"World\"", getTextByClass("world-result"));

        // say World
        clickById("world_btn");
        Assertions.assertEquals("World", getFromValue("word"));
        Assertions.assertEquals("word is World.", getTextByClass("empty-result"));
        Assertions.assertEquals("\"World\" != \"Hello\"", getTextByClass("hello-result"));
        Assertions.assertEquals("\"World\" == \"World\"", getTextByClass("world-result"));

        // clear
        clickById("empty_btn");
        Assertions.assertEquals("", getFromValue("word"));
        Assertions.assertEquals("word is empty.", getTextByClass("empty-result"));
        Assertions.assertEquals("\"\" != \"Hello\"", getTextByClass("hello-result"));
        Assertions.assertEquals("\"\" != \"World\"", getTextByClass("world-result"));
    }

}


