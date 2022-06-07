package com.sample.medusa.eventhandler.integrationtests;

import io.getmedusa.medusa.core.annotation.PageAttributes;
import io.getmedusa.medusa.core.annotation.UIEventPage;
import io.getmedusa.medusa.core.injector.Applicable;
import io.getmedusa.medusa.core.injector.DOMChanges;


@UIEventPage(path = "/test/applicable", file = "pages/integration-tests/applicable-dom-changes")
public class ApplicableDOMChangesEventHandler {

    private static int SES = 1;
    private static int GLB = 1;

    public PageAttributes setupAttributes() {
        return new PageAttributes()
                .with("session-counter", SES)
                .with("global-counter", GLB);
    }

    public DOMChanges increase(String counter) {
        int sessionCounter = Integer.valueOf(counter) + 1;
        int globalCounter = ++GLB;
        return DOMChanges
                .of("session-counter", sessionCounter)
                .and("global-counter", globalCounter, Applicable.TRUE);  // applicable for all sessions
    }

}
