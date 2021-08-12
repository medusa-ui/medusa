package com.sample.medusa.eventhandler.integrationtests;

import io.getmedusa.medusa.core.annotation.PageAttributes;
import io.getmedusa.medusa.core.annotation.UIEventPage;
import io.getmedusa.medusa.core.injector.DOMChanges;

import java.util.ArrayList;
import java.util.List;

@UIEventPage(path = "/test/history", file = "pages/integration-tests/template-m-id")
public class TemplateMIdEventHandler {

    List<String> history = new ArrayList<>();

    public PageAttributes setupAttributes() {
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
