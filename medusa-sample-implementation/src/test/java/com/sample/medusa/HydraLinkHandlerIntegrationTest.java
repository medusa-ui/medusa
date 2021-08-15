package com.sample.medusa;

import com.sample.medusa.meta.AbstractSeleniumTest;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class HydraLinkHandlerIntegrationTest extends AbstractSeleniumTest {

    @Override
    protected boolean isHeadless() {
        return true;
    }

    @Test
    void testLink() {
        goTo("/test/hydra-link");

        //hidden-link

        System.out.println(driver.getPageSource());
    }

}
