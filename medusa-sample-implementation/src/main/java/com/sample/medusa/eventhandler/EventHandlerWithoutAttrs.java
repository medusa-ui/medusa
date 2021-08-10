package com.sample.medusa.eventhandler;

import io.getmedusa.medusa.core.annotation.UIEventPage;
import io.getmedusa.medusa.core.injector.DOMChanges;

import java.util.UUID;

@UIEventPage(path = "/page3", file = "pages/page3.html")
public class EventHandlerWithoutAttrs {

    public void printText(String text) {
        System.out.println("Hello " + text);
    }

    public String dance() {
        System.out.println("dancing");
        return UUID.randomUUID().toString();
    }

    public DOMChanges run() {
        System.out.println("running");
        return DOMChanges.of("doing","running");
    }
}
