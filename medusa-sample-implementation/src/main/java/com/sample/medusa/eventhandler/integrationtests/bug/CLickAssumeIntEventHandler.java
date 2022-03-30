package com.sample.medusa.eventhandler.integrationtests.bug;

import io.getmedusa.medusa.core.annotation.PageAttributes;
import io.getmedusa.medusa.core.annotation.UIEventPage;
import io.getmedusa.medusa.core.injector.DOMChanges;
import org.springframework.web.reactive.function.server.ServerRequest;

@UIEventPage(file = "pages/integration-tests/bug/click-assume-int.html", path = "/bug/click/{value}")
public class CLickAssumeIntEventHandler {

    public PageAttributes setupAttributes(ServerRequest serverRequest) {
        Long value = Long.valueOf(serverRequest.pathVariable("value"));
        return new PageAttributes()
                .with("value", value)
                .with("type","Long");
    }

    public DOMChanges load(long value) {
        return DOMChanges.of("value", value).and("type","Long");
    }

    public DOMChanges load(double value) {
        return DOMChanges.of("value", value).and("type","Double");
    }

    public DOMChanges load(float value) {
        return DOMChanges.of("value", value).and("type","Float");
    }

}
