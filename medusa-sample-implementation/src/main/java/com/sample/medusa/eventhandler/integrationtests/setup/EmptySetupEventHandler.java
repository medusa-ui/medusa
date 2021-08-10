package com.sample.medusa.eventhandler.integrationtests.setup;

import io.getmedusa.medusa.core.annotation.PageAttributes;
import io.getmedusa.medusa.core.annotation.UIEventPage;
import io.getmedusa.medusa.core.injector.DOMChanges;
import io.getmedusa.medusa.core.util.SecurityContext;
import org.springframework.web.reactive.function.server.ServerRequest;

import java.util.HashMap;
import java.util.Map;

import static io.getmedusa.medusa.core.injector.DOMChanges.of;

@UIEventPage(path = "/test/setup/empty", file = "pages/integration-tests/custom-setup")
public class EmptySetupEventHandler {

    public DOMChanges increaseCounter(String counterValue, int parameter) {
        Integer counter = (counterValue == null || counterValue.equals("counter")) ? 0 : Integer.valueOf(counterValue);
        return of("counter", counter + parameter);
    }
}
