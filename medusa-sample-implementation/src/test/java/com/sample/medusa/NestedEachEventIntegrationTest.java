package com.sample.medusa;

import com.sample.medusa.meta.AbstractSeleniumTest;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Arrays;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class NestedEachEventIntegrationTest extends AbstractSeleniumTest {

    @Override
    protected boolean isHeadless() {
        return false;
    }

    @Test
    void testRendering() {
        goTo("/nested-each");

        Assertions.assertEquals("Medusa nested each", driver.getTitle());
        Assertions.assertEquals(toList(1, 2, 3), getTextByCss(".outer-1").toString());
        Assertions.assertEquals(toList(1, 2, 3), getTextByCss(".outer-2").toString());

        Assertions.assertEquals(toList(10, 20, 30), getTextByCss(".mid-1").toString());


        System.out.println(driver.getPageSource());
    }

    private String toList(Integer ... i) {
        return Arrays.asList(i).toString();
    }

}
