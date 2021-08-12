package com.sample.medusa.eventhandler.integrationtests.bug;

import io.getmedusa.medusa.core.annotation.PageAttributes;
import io.getmedusa.medusa.core.annotation.UIEventPage;
import io.getmedusa.medusa.core.injector.DOMChanges;

import static io.getmedusa.medusa.core.injector.DOMChanges.of;

@UIEventPage(path="/test/bug/welcome", file = "pages/integration-tests/bug/hello")
public class WelcomeUIEventHandler {

    public PageAttributes setupAttributes() {
        return new PageAttributes()
                .with("controller","WelcomeUIEventHandler")
                .with("counter-value", counter)
                .with("numberFromPath", 0);
    }

    int counter = 0;

    public DOMChanges increaseCounter() {
        counter += 1;
        return of("counter-value", counter);
    }
}
