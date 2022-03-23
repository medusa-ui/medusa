package com.sample.medusa;

import com.sample.medusa.meta.AbstractSeleniumTest;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT /*, properties = "headless:false" */)
public class ApplicableDOMChangesIntegrationTest extends AbstractSeleniumTest {

    @Test
    public void testApplicableChanges() throws Exception {
        // tab 0
        goTo("/test/applicable");
        Assertions.assertEquals("1",getTextByClass("sess-cnt-value"));
        Assertions.assertEquals("1",getTextByClass("glob-cnt-value"));
        clickById("btn-increase");
        Assertions.assertEquals("2",getTextByClass("sess-cnt-value"));
        Assertions.assertEquals("2",getTextByClass("glob-cnt-value"));

        // new tab
        int tab = openNewTab();
        Assertions.assertTrue(tab > 0);
        goTo("/test/applicable");
        Assertions.assertEquals("1",getTextByClass("sess-cnt-value"));
        Assertions.assertEquals("2",getTextByClass("glob-cnt-value"));
        clickById("btn-increase");
        clickById("btn-increase");
        Assertions.assertEquals("3",getTextByClass("sess-cnt-value"));
        Assertions.assertEquals("4",getTextByClass("glob-cnt-value"));

        // open first tab
        switchToTab(0);
        Assertions.assertEquals("2",getTextByClass("sess-cnt-value"));
        Assertions.assertEquals("4",getTextByClass("glob-cnt-value"));
    }
}
