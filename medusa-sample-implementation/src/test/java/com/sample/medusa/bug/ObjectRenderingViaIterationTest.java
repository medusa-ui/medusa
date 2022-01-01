package com.sample.medusa.bug;

import com.sample.medusa.meta.AbstractSeleniumTest;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class ObjectRenderingViaIterationTest extends AbstractSeleniumTest {

    @Test
    void testInitialRender() {
        goTo("/test/bug/js-wrapping-div");

        List<String> peopleByClass = getTextByCss("section#people .person");
        Assertions.assertEquals(2, peopleByClass.size());
        System.out.println(peopleByClass);
        Assertions.assertEquals("[A, B]", peopleByClass.toString());

        List<String> people2ByClass = getAttributeByCss("section#people2 .person input", "value");
        Assertions.assertEquals(2, people2ByClass.size());
        System.out.println(people2ByClass);
        Assertions.assertEquals("[A, B]", people2ByClass.toString());
    }

    @Test
    void testAfterReRender() {
        goTo("/test/bug/js-wrapping-div");
        clickById("render");

        List<String> peopleByClass = getTextByCss("section#people .person");
        Assertions.assertEquals(2, peopleByClass.size());
        System.out.println(peopleByClass);
        Assertions.assertEquals("[C, D]", peopleByClass.toString(), "JS failed to interpret object: from-value span");

        List<String> people2ByClass = getAttributeByCss("section#people2 .person input", "value");
        Assertions.assertEquals(2, people2ByClass.size());
        System.out.println(people2ByClass);
        Assertions.assertEquals("[C, D]", people2ByClass.toString(), "JS failed to interpret object: input m:value");
    }

}
