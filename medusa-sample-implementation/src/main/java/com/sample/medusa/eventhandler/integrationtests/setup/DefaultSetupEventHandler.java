package com.sample.medusa.eventhandler.integrationtests.setup;

import io.getmedusa.medusa.core.annotation.PageAttributes;
import io.getmedusa.medusa.core.annotation.UIEventPage;
import io.getmedusa.medusa.core.injector.DOMChanges;
import io.getmedusa.medusa.core.util.SecurityContext;
import org.springframework.web.reactive.function.server.ServerRequest;

import static io.getmedusa.medusa.core.injector.DOMChanges.of;

@UIEventPage(path = "/test/setup/default", file = "pages/integration-tests/setup/default")
public class DefaultSetupEventHandler {

    public PageAttributes setupAttributes(ServerRequest request, SecurityContext context) {
        return new PageAttributes()
                .with("counter" ,0 )
                .with("setup", "PageAttributes setupAttributes(ServerRequest request, SecurityContext context) {....}")
                .with("name", request.queryParam("name").orElse("query param 'name' not present"))
                .with("principal", (context == null || context.getPrincipal() == null) ? "guest" : context.getPrincipal().getName());
    }

    public DOMChanges increaseCounter(String counterValue, int parameter) {
        Integer counter = Integer.valueOf(counterValue);
        return of("counter", counter + parameter);
    }
}
