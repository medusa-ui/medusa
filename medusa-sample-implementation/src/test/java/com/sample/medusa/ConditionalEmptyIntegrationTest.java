package com.sample.medusa;

import com.sample.medusa.meta.AbstractSeleniumTest;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class ConditionalEmptyIntegrationTest extends AbstractSeleniumTest {

    @Test
    void testEmpty(){
        // init
        goTo("/test/conditional-empty");
        Assertions.assertTrue(driver.getPageSource().contains("Hello empty conditionals"));

        // empty on start
        Assertions.assertEquals("Text is empty.",getTextByClass("text-result"));
        Assertions.assertEquals("List is empty.",getTextByClass("list-result"));
        Assertions.assertEquals("Set is empty.",getTextByClass("set-result"));
        Assertions.assertEquals("Map is empty.",getTextByClass("map-result"));

        // set values
        clickById("values_btn");
        Assertions.assertEquals("Text is some text.",getTextByClass("text-result"));
        Assertions.assertEquals("List is a list item.",getTextByClass("list-result"));
        Assertions.assertEquals("Set is a set item.",getTextByClass("set-result"));
        Assertions.assertEquals("Map is a map value.",getTextByClass("map-result"));
        refreshPage();
        Assertions.assertEquals("Text is some text.",getTextByClass("text-result"));
        Assertions.assertEquals("List is a list item.",getTextByClass("list-result"));
        Assertions.assertEquals("Set is a set item.",getTextByClass("set-result"));
        Assertions.assertEquals("Map is a map value.",getTextByClass("map-result"));

        // clear values
        clickById("clear_btn");
        Assertions.assertEquals("Text is empty.",getTextByClass("text-result"));
        Assertions.assertEquals("List is empty.",getTextByClass("list-result"));
        Assertions.assertEquals("Set is empty.",getTextByClass("set-result"));
        Assertions.assertEquals("Map is empty.",getTextByClass("map-result"));
    }
}
