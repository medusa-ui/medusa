package com.sample.medusa.bug;

import com.sample.medusa.meta.AbstractSeleniumTest;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.stream.Collectors;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class ElseIfDomChangesIntegrationTest extends AbstractSeleniumTest {

    @Test
    @DisplayName("JS expression evaluation with an array should work correctly")
    void filtered(){
        goTo("/test/bug/else-if?filtered=false");
        clickById("true_btn");
        Assertions.assertEquals(3, getAllTextByClass("valid").size());
        Assertions.assertEquals(0, getAllTextByClass("valid").stream().filter(it -> it.equals("false")).collect(Collectors.toList()).size());

        clickById("all_btn");
        Assertions.assertEquals(5, getAllTextByClass("valid").size());
        Assertions.assertEquals(2, getAllTextByClass("valid").stream().filter(it -> it.equals("false")).collect(Collectors.toList()).size());
        Assertions.assertEquals(3, getAllTextByClass("valid").stream().filter(it -> it.equals("true")).collect(Collectors.toList()).size());

        clickById("true_btn");
        Assertions.assertEquals(3, getAllTextByClass("valid").size());
        Assertions.assertEquals(3, getAllTextByClass("valid").size());
        Assertions.assertEquals(0, getAllTextByClass("valid").stream().filter(it -> it.equals("false")).collect(Collectors.toList()).size());

        goTo("/test/bug/else-if?filtered=true");
        clickById("all_btn");
        Assertions.assertEquals(5, getAllTextByClass("valid").size());
        Assertions.assertEquals(2, getAllTextByClass("valid").stream().filter(it -> it.equals("false")).collect(Collectors.toList()).size());
        Assertions.assertEquals(3, getAllTextByClass("valid").stream().filter(it -> it.equals("true")).collect(Collectors.toList()).size());

        clickById("true_btn");
        Assertions.assertEquals(3, getAllTextByClass("valid").size());
        Assertions.assertEquals(3, getAllTextByClass("valid").size());
        Assertions.assertEquals(0, getAllTextByClass("valid").stream().filter(it -> it.equals("false")).collect(Collectors.toList()).size());

        clickById("all_btn");
        Assertions.assertEquals(5, getAllTextByClass("valid").size());
        Assertions.assertEquals(2, getAllTextByClass("valid").stream().filter(it -> it.equals("false")).collect(Collectors.toList()).size());
        Assertions.assertEquals(3, getAllTextByClass("valid").stream().filter(it -> it.equals("true")).collect(Collectors.toList()).size());
    }
}
