package com.sample.medusa.bug;

import com.sample.medusa.meta.AbstractSeleniumTest;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class NonLatinCharactersIntegrationTest extends AbstractSeleniumTest {

    @Test
    void testRenderNonLatinVariables() {
        goTo("/test/bug/non-latin-characters");

        Assertions.assertEquals("Medusa in other languages : 메두사", title());

        Assertions.assertEquals("Value1: 메두사", getTextById("val1"));
        Assertions.assertEquals("Value2: 美杜莎", getTextById("val2"));

        clickById("swap");

        Assertions.assertEquals("Medusa in other languages : 美杜莎", title());

        Assertions.assertEquals("Value1: 美杜莎", getTextById("val1"));
        Assertions.assertEquals("Value2: 메두사", getTextById("val2"));
    }

    @Test
    void testPassNonLatinCharsInParam() {
        goTo("/test/bug/non-latin-characters");

        Assertions.assertEquals("Passed value:", getTextById("passed-val").trim());

        clickById("pass");

        Assertions.assertEquals("Passed value: 메두사", getTextById("passed-val"));
    }

    @Test
    void testPassInputNonLatinChars() {
        goTo("/test/bug/non-latin-characters");

        fillFieldById("pass-value-input", "메두사");
        clickById("pass-input");
        Assertions.assertEquals("Passed value from input: 메두사", getTextById("passed-val-from-input"));

    }

}
