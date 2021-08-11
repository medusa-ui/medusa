package com.sample.medusa.eventhandler.integrationtests.bug;

import io.getmedusa.medusa.core.annotation.PageAttributes;
import io.getmedusa.medusa.core.annotation.UIEventPage;
import io.getmedusa.medusa.core.annotation.UIEventWithAttributes;
import io.getmedusa.medusa.core.injector.DOMChanges;
import io.getmedusa.medusa.core.util.SecurityContext;
import org.springframework.web.reactive.function.server.ServerRequest;

import static io.getmedusa.medusa.core.injector.DOMChanges.of;

@UIEventPage(path="/test/bug/hello/{number}", file = "pages/integration-tests/bug/hello")
public class HelloUIEventHandler implements UIEventWithAttributes {

    @Override
    public PageAttributes setupAttributes(ServerRequest request, SecurityContext securityContext) {
        return new PageAttributes()
                .with("controller","HelloUIEventHandler")
                .with("counter-value", counter)
                .with("numberFromPath", request.pathVariable("number"));
    }

    int counter = 0;

    public DOMChanges increaseCounter() {
        counter += 1;
        return of("counter-value", counter);
    }
}
