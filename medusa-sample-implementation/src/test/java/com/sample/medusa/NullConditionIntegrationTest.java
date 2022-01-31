package com.sample.medusa;

import com.sample.medusa.meta.AbstractSeleniumTest;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class NullConditionIntegrationTest extends AbstractSeleniumTest {

    @Test
    void nullConditionTest() {
        // enter page
        goTo("/null-condition");
        Assertions.assertTrue(driver.getPageSource().contains("Conditions with <code>nulls</code>"));

        // null value on init page
        Assertions.assertEquals("Person is null.", getTextByClass("null_person"));

        // set person via action
        clickById("some-one");
        Assertions.assertEquals("Person with name: Jhon Doe.", getTextByClass("null_person"));
        // reload page
        goTo("/null-condition");
        Assertions.assertEquals("Person with name: Jhon Doe.", getTextByClass("null_person"));

        // reset person via action
        clickById("no-one");
        Assertions.assertEquals("Person is null.", getTextByClass("null_person"));

        // set a person without a name (null)
        clickById("some-null-name");
        Assertions.assertEquals("The person's name is unknown.", getTextByClass("null_person"));
        // reload page
        goTo("/null-condition");
        Assertions.assertEquals("The person's name is unknown.", getTextByClass("null_person"));
    }

}
