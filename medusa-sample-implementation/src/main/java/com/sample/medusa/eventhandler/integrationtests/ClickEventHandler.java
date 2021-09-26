package com.sample.medusa.eventhandler.integrationtests;

import io.getmedusa.medusa.core.annotation.PageAttributes;
import io.getmedusa.medusa.core.annotation.UIEventPage;
import io.getmedusa.medusa.core.injector.DOMChanges;

import java.util.HashMap;
import java.util.Map;

import static io.getmedusa.medusa.core.injector.DOMChanges.of;

@UIEventPage(path = "/test/click-event", file = "pages/integration-tests/click-event.html")
public class ClickEventHandler {

    int counter = 0;

    public PageAttributes setupAttributes() {
        Map<String, Object> modelMap = new HashMap<>();
        modelMap.put("my-counter", counter);
        return new PageAttributes(modelMap);
    }

    public DOMChanges increaseCounter(int parameter) {
        counter += parameter;
        if(counter > 10) {
            counter = 0;
        }
        return of("my-counter", counter);
    }
}
