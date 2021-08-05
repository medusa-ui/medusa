package com.sample.medusa.eventhandler.integrationtests;

import io.getmedusa.medusa.core.annotation.PageAttributes;
import io.getmedusa.medusa.core.annotation.UIEventController;
import io.getmedusa.medusa.core.annotation.UIEventPage;
import io.getmedusa.medusa.core.injector.DOMChanges;
import io.getmedusa.medusa.core.util.SecurityContext;
import org.springframework.web.reactive.function.server.ServerRequest;

import java.util.ArrayList;
import java.util.List;

@UIEventPage(path = "/test/history", file = "pages/integration-tests/template-m-id")
public class TemplateMIdEventHandler implements UIEventController {

    List<String> history = new ArrayList<>();

    @Override
    public PageAttributes setupAttributes(ServerRequest request, SecurityContext securityContext) {
        return new PageAttributes()
                .with("event", "")
                .with("history", history);
    }

    public DOMChanges setData(String event) {
        history.add(event);
        return DOMChanges
                .of("event", event)
                .and("history", history);
    }
}
