package com.sample.medusa;

import com.sample.medusa.meta.AbstractSeleniumTest;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class ReferenceIntegrationTest extends AbstractSeleniumTest {

    private static final String page = "/test/reference";

    protected boolean isHeadless() {
        return false;
    }

    @Test
    @DisplayName("It should be possible to get an attribute value when referring to an element by its id")
    public void testAdd(){
        goTo(page);

        fillFieldById("_first","13");
        fillFieldById("_second","29");

        clickById("btn_sum");
        clickById("btn_sum");
        String result = getTextById("result");

        Assertions.assertTrue(
                result.contains("13 + 29 = 42"),
                "Result should contain: '13 + 29 = 42'");
    }

    @Test
    @DisplayName("Combining value of attribute from element by its id and attribute on element itself, should be possible")
    public void testMinus(){
        goTo(page);

        fillFieldById("_first","65");
        fillFieldById("_second","23");

        clickById("btn_min");
        String result = getTextById("result");

        Assertions.assertTrue(
                result.contains("65 - 23 = 42"),
                "Result should contain: '65 - 23 = 42'");
    }

    @Test
    @DisplayName("Wrapping referenced values as String should be possible")
    public void testMultiply(){
        goTo(page);

        clickById("btn_42m");
        String result = getTextById("result");

        Assertions.assertTrue(
                result.contains("21 x 2 = 42"),
                "Result should contain: '21 x 2 = 42'");
    }

}
