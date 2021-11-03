package com.sample.medusa.bug;

import com.sample.medusa.meta.AbstractSeleniumTest;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class WrappingDivIntegrationTest extends AbstractSeleniumTest {

    @Test
    void testInitialRender() {
        goTo("/test/bug/js-wrapping-div");

        List<String> peopleByClass = getAttributeByCss("section#people>div", "template-id");
        Assertions.assertEquals(1, peopleByClass.size());
        System.out.println(peopleByClass);
    }

    @Test
    void testAfterReRender() {
        goTo("/test/bug/js-wrapping-div");
        clickById("render");

        List<String> peopleByClass = getAttributeByCss("section#people>div", "template-id");
        Assertions.assertEquals(1, peopleByClass.size());
        System.out.println(peopleByClass);
    }

}
