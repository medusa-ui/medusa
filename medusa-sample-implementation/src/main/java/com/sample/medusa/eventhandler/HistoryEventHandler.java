package com.sample.medusa.eventhandler;

import io.getmedusa.medusa.core.annotation.PageAttributes;
import io.getmedusa.medusa.core.annotation.UIEventController;
import io.getmedusa.medusa.core.annotation.UIEventPage;
import io.getmedusa.medusa.core.injector.DOMChanges;
import io.getmedusa.medusa.core.util.SecurityContext;
import org.springframework.web.reactive.function.server.ServerRequest;

import java.util.ArrayList;
import java.util.List;

@UIEventPage(path = "/history", file = "pages/history")
public class HistoryEventHandler implements UIEventController {

    List<String> history = new ArrayList<>();

    @Override
    public PageAttributes setupAttributes(ServerRequest request, SecurityContext securityContext) {
        String message = "Hello World";
        history.add(message);

        return new PageAttributes()
                .with("event", message)
                .with("data", "")
                .with("history", history);
    }

    public DOMChanges setData(String event) {
        history.add(event);
        return DOMChanges
                .of("event", event)
                .and("history", history)
                .and("data","");
    }
}
