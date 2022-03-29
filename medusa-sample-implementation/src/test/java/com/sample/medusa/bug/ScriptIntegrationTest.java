package com.sample.medusa.bug;

import com.sample.medusa.meta.AbstractSeleniumTest;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class ScriptIntegrationTest extends AbstractSeleniumTest {

    @Test
    void scriptTest(){
        goTo("/test/bug/script");
        Assertions.assertEquals("Hello World!", getTextById("message"));
    }

}
