package com.sample.medusa;

import com.sample.medusa.meta.AbstractSeleniumTest;
import io.getmedusa.medusa.core.registry.hydra.HydraRegistry;
import org.junit.jupiter.api.Assertions;
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
        HydraRegistry.INSTANCE.addRoute("other-service/other-page", "/test-123");
        goTo("/test/hydra-link");

        //hidden-link
        Assertions.assertTrue(getAttributeByCss("#hidden-link", "href").get(0).endsWith("/test-123"));
        Assertions.assertTrue(getAttributeByCss("#inactive-link", "href").get(0).endsWith("#"));

        System.out.println(driver.getPageSource());
    }

}
