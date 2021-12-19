package com.sample.medusa.eventhandler;

import io.getmedusa.medusa.core.annotation.PageAttributes;
import io.getmedusa.medusa.core.annotation.UIEventPage;
import io.getmedusa.medusa.core.injector.DOMChanges;

import java.security.SecureRandom;
import java.util.List;
import java.util.UUID;

@UIEventPage(path = "/xml", file = "pages/page-xml.html")
public class TempPageWithXMLEventHandler {

    public PageAttributes setupAttributes() {
        return new PageAttributes()
                .with("title", "Medusa demo :: " + UUID.randomUUID() + ":: Page XML")
                .with("example-value", UUID.randomUUID().toString())
                .with("someVariable", new SecureRandom().nextInt(9999))
                .with("outerLoop", List.of(1, 2))
                .with("innerLoop", List.of("a", "b", "c"));
    }

    //second method with same signature to check if page switching works correctly
    public DOMChanges buy(Object... parameters) {
        return DOMChanges.of("example-value", "this store is closed!");
    }

}
