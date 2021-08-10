package com.sample.medusa.eventhandler.integrationtests.setup;

import io.getmedusa.medusa.core.annotation.PageAttributes;
import io.getmedusa.medusa.core.annotation.UIEventPage;
import org.springframework.web.reactive.function.server.ServerRequest;

@UIEventPage(path = "/test/setup/request", file = "pages/integration-tests/custom-setup", setup = "custom")
public class CustomSetupRequestEventHandler {

    public PageAttributes custom(ServerRequest request) {
        return new PageAttributes()
                .with("message", "Custom setup")
                .with("name", request.queryParam("name").orElse("query param 'name' not present"));
    }
}
