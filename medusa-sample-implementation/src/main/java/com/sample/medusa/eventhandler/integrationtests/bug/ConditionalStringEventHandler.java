package com.sample.medusa.eventhandler.integrationtests.bug;

import io.getmedusa.medusa.core.annotation.PageAttributes;
import io.getmedusa.medusa.core.annotation.UIEventPage;
import io.getmedusa.medusa.core.injector.DOMChanges;
import org.springframework.web.reactive.function.server.ServerRequest;

@UIEventPage(path = "/test/bug/conditional/string", file = "pages/integration-tests/bug/conditional-string")
public class ConditionalStringEventHandler {

    public PageAttributes setupAttributes(ServerRequest request) {
        return new PageAttributes()
                .with("word", "");
    }

    public DOMChanges hello() {
        return DOMChanges.of("word", "Hello");
    }

    public DOMChanges world() {
        return DOMChanges.of("word", "World");
    }

    public DOMChanges empty() {
        return DOMChanges.of("word", "");
    }
}
