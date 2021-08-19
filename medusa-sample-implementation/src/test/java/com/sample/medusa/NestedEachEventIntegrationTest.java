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
        Assertions.assertEquals(Arrays.asList(1, 2, 3).toString(), getTextByCss(".outer-1").toString());
        Assertions.assertEquals(Arrays.asList(1, 2, 3).toString(), getTextByCss(".outer-2").toString());

        System.out.println(driver.getPageSource());
    }

}
