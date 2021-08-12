package com.sample.medusa.eventhandler;

import io.getmedusa.medusa.core.annotation.PageAttributes;
import io.getmedusa.medusa.core.annotation.UIEventPage;
import io.getmedusa.medusa.core.injector.DOMChanges;

import java.util.UUID;

@UIEventPage(path = "/page2", file = "pages/page2.html")
public class Page2EventHandler {

    public PageAttributes setupAttributes() {
        return new PageAttributes().with("example-value", UUID.randomUUID().toString());
    }

    //second method with same signature to check if page switching works correctly
    public DOMChanges buy(Object... parameters) {
        return DOMChanges.of("example-value", "this store is closed!");
    }

}
