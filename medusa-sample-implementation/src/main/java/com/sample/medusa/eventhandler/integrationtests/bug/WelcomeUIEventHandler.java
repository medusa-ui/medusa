package com.sample.medusa.eventhandler.integrationtests.bug;

import io.getmedusa.medusa.core.annotation.PageAttributes;
import io.getmedusa.medusa.core.annotation.UIEventPage;
import io.getmedusa.medusa.core.annotation.UIEventWithAttributes;
import io.getmedusa.medusa.core.injector.DOMChanges;
import io.getmedusa.medusa.core.util.SecurityContext;
import org.springframework.web.reactive.function.server.ServerRequest;

import static io.getmedusa.medusa.core.injector.DOMChanges.of;

@UIEventPage(path="/test/bug/welcome", file = "pages/integration-tests/bug/hello")
public class WelcomeUIEventHandler  implements UIEventWithAttributes {

    @Override
    public PageAttributes setupAttributes(ServerRequest request, SecurityContext securityContext) {
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
