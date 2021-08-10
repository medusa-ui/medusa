package com.sample.medusa.eventhandler.integrationtests.setup;

import io.getmedusa.medusa.core.annotation.PageAttributes;
import io.getmedusa.medusa.core.annotation.UIEventPage;
import io.getmedusa.medusa.core.injector.DOMChanges;
import org.springframework.web.reactive.function.server.ServerRequest;

import static io.getmedusa.medusa.core.injector.DOMChanges.of;

@UIEventPage(path = "/test/setup/request", file = "pages/integration-tests/setup/request", setup = "custom")
public class CustomSetupRequestEventHandler {

    public PageAttributes custom(ServerRequest request) {
        return new PageAttributes()
                .with("setup", "PageAttributes custom(ServerRequest request) {...}")
                .with("counter", 0)
                .with("name", request.queryParam("name").orElse("query param 'name' not present"));
    }

    public DOMChanges increaseCounter(String counterValue, int parameter) {
        Integer counter = Integer.valueOf(counterValue) + parameter;
        return of("counter", counter);
    }
}
