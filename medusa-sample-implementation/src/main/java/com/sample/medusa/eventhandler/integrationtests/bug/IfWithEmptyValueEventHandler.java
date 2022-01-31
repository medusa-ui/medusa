package com.sample.medusa.eventhandler.integrationtests.bug;

import io.getmedusa.medusa.core.annotation.PageAttributes;
import io.getmedusa.medusa.core.annotation.UIEventPage;
import io.getmedusa.medusa.core.injector.DOMChanges;
import org.springframework.web.reactive.function.server.ServerRequest;

@UIEventPage(path="/test/bug/empty", file = "pages/integration-tests/bug/if-with-empty-value")
public class IfWithEmptyValueEventHandler {

    private String text = "";

    public PageAttributes setupAttributes(ServerRequest request) {
        return new PageAttributes()
                .with("text", text);
    }

    public DOMChanges concat(String additionalText) {
        this.text = text.concat(additionalText);
        return DOMChanges.of("text", text);
    }

    public DOMChanges hello() {
        this.text = "hello";
        return DOMChanges.of("text", text);
    }
}
