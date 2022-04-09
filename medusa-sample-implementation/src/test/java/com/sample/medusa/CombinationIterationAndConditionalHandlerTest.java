package com.sample.medusa;

import com.sample.medusa.meta.AbstractSeleniumTest;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class CombinationIterationAndConditionalHandlerTest extends AbstractSeleniumTest {

    @Test
    void testCombinations(){
        goTo("/test/combo-if-conditional");
        Assertions.assertTrue(driver.getPageSource().contains("Combo iteration and conditionals"));

        Assertions.assertEquals("Phase A (1, 2 / A, B / JANE, JOHN)", getTextById("phase"));
        Assertions.assertEquals(4*4, getAllTextByClass("conditional-ok").size());

        clickById("phase-b-btn");
        Assertions.assertEquals("Phase B (3, 4 / C, D / PETER, JEANETTE)", getTextById("phase"));
        Assertions.assertEquals(4*4, getAllTextByClass("conditional-ok").size());

        clickById("phase-a-btn");
        Assertions.assertEquals("Phase A (1, 2 / A, B / JANE, JOHN)", getTextById("phase"));
        Assertions.assertEquals(4*4, getAllTextByClass("conditional-ok").size());
    }
}
