package com.sample.medusa.eventhandler.integrationtests.bug;

import io.getmedusa.medusa.core.annotation.PageAttributes;
import io.getmedusa.medusa.core.annotation.UIEventPage;
import io.getmedusa.medusa.core.injector.DOMChanges;
import org.springframework.web.reactive.function.server.ServerRequest;

import static io.getmedusa.medusa.core.injector.DOMChanges.of;

@UIEventPage(path="/test/bug/elseif-eval/{value}", file = "pages/integration-tests/bug/elseif-eval")
public class ConditionalElseIfEvalEventHandler {

    int counter;

    public PageAttributes setupAttributes(ServerRequest request) {
        counter =  Integer.valueOf(request.pathVariable("value"));
        return new PageAttributes().with("counter", counter);
    }

    public DOMChanges plus() {
        return DOMChanges.of("counter", ++counter);
    }

    public DOMChanges minus() {
        return DOMChanges.of("counter", --counter);
    }
}
