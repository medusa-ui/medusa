package com.sample.medusa.eventhandler.integrationtests.bug;

import io.getmedusa.medusa.core.annotation.PageAttributes;
import io.getmedusa.medusa.core.annotation.UIEventPage;
import io.getmedusa.medusa.core.injector.DOMChanges;
import org.springframework.web.reactive.function.server.ServerRequest;

import static io.getmedusa.medusa.core.injector.DOMChanges.of;

@UIEventPage(path="/test/bug/hello/{number}", file = "pages/integration-tests/bug/hello")
public class HelloUIEventHandler {

    int counter = 0;

    public PageAttributes setupAttributes(ServerRequest request) {
        return new PageAttributes()
                .with("title", "Medusa in trouble " + counter)
                .with("controller","HelloUIEventHandler")
                .with("counter-value", counter)
                .with("numberFromPath", request.pathVariable("number"));
    }

    public DOMChanges increaseCounter() {
        counter += 1;
        return of("counter-value", counter)
                .and("title", "Medusa in trouble " + counter);
    }
}
